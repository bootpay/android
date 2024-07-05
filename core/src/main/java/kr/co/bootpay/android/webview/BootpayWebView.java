package kr.co.bootpay.android.webview;

import static kr.co.bootpay.android.constants.BootpayBuildConfig.VERSION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import kr.co.bootpay.android.BootpayPaymentResult;
import kr.co.bootpay.android.BootpayWidget;
import kr.co.bootpay.android.api.BootpayInterface;
import kr.co.bootpay.android.api.BootpayWidgetInterface;
import kr.co.bootpay.android.constants.BootpayBuildConfig;
import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.BootpayExtEventListener;
import kr.co.bootpay.android.events.BootpayWidgetEventListener;
import kr.co.bootpay.android.events.BootpayWidgetPrivateEventListener;
import kr.co.bootpay.android.events.JSInterfaceBridge;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.models.widget.WidgetData;

public class BootpayWebView extends WebView implements BootpayInterface, BootpayWidgetInterface {
//    BootpayDialog mDialog;
//    BootpayDialogX mDialogX;


    private Activity mActivity;
    Payload payload;

    private boolean isProcessingEventResize = false;
    private boolean isProcessingEventReady = false;
    private boolean isProcessingEventChangePayment = false;
    private boolean isProcessingEventChangeAgreeTerm = false;

    private final int WIDGET_EVENT_RESIZE = 1;
    private final int WIDGET_EVENT_READY = 2;
    private final int WIDGET_EVENT_CHANGE_PAYMENT = 3;
    private final int WIDGET_EVENT_CHANGE_AGREE_TERM = 4;

    BootpayWebViewClient mWebViewClient;
    BootpayEventListener mEventListener;
    BootpayExtEventListener mExtEventListener;
    BootpayWidgetEventListener mWidgetEventListener;
    BootpayWidgetPrivateEventListener mWidgetPrivateEventListener;

//    androidx.fragment.app.FragmentManager mFragmentManagerX;
//    android.app.FragmentManager mFragmentManager;

    private Runnable widgetResize = () -> this.isProcessingEventResize = false;
    private Runnable widgetReady = () -> this.isProcessingEventReady = false;
    private Runnable widgetChangePayment = () -> this.isProcessingEventChangePayment = false;
    private Runnable widgetChangeAgreeTerm = () -> this.isProcessingEventChangeAgreeTerm = false;


    private BootpayPaymentResult paymentResult = BootpayPaymentResult.NONE;

    protected @Nullable
    String injectedJS;

    protected @Nullable
    List<String> injectedJSBeforePayStart;

    public BootpayWebView(@NonNull Context context) {
        super(context);
        constructInit(context);
    }

    public BootpayWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        constructInit(context);
    }

    public BootpayWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructInit(context);
    }

    void constructInit(Context context) {
        payWebSettings(context);
        mWebViewClient = new BootpayWebViewClient();
        setWebViewClient(mWebViewClient);
        setWebChromeClient(new BootpayWebViewChromeClient(context));
    }

    void setIgnoreSSLError() {
        
    }

    public void setInjectedJS(@Nullable String injectedJS) {
        this.injectedJS = injectedJS;
    }

    public void setInjectedJSBeforePayStart(@Nullable List<String> injectedJSBeforePayStart) {
        this.injectedJSBeforePayStart = injectedJSBeforePayStart;
    }



    @SuppressLint("JavascriptInterface")
    void payWebSettings(Context context) {
        getSettings().setAppCacheEnabled(true);
        getSettings().setAllowFileAccess(false);
        getSettings().setAllowContentAccess(false);
        getSettings().setBuiltInZoomControls(true);
        getSettings().setDisplayZoomControls(false);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        getSettings().setDomStorageEnabled(true);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setLoadsImagesAutomatically(true);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setSupportMultipleWindows(true);

        if (BootpayBuildConfig.DEBUG == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getApplicationInfo().flags &=  context.getApplicationInfo().FLAG_DEBUGGABLE;
            if (0 != context.getApplicationInfo().flags)  setWebContentsDebuggingEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getSettings().setAllowFileAccessFromFileURLs(false);
            getSettings().setAllowUniversalAccessFromFileURLs(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptCookie(true);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }
    }

    @Override
    public void removePaymentWindow() {
        load("Bootpay.destroy();");
    }


    boolean isWidget = false;
    public void startPayment() {
        this.isWidget = false;
        loadUrl(BootpayConstant.CDN_URL);
    }

    public void startWidget() {
        this.isWidget = true;
        loadUrl(BootpayConstant.WIDGET_URL);
    }

    public void transactionConfirm(String data) {
        String scriptList = BootpayConstant.loadParams(
                "Bootpay.confirm()",
                ".then( function (res) {",
                BootpayConstant.confirm(),
                BootpayConstant.issued(),
                BootpayConstant.done(),
                "}, function (res) {",
                BootpayConstant.error(),
                BootpayConstant.cancel(),
                "})"
        );

        load(scriptList);
    }

    private void load(String script) {
        post(() -> loadUrl(String.format(Locale.KOREA, "javascript:(function(){%s})()", script)));
    }

    private class BootpayJavascriptBridge implements JSInterfaceBridge {

        @JavascriptInterface
        @Override
        public void error(String data) {
            paymentResult = BootpayPaymentResult.ERROR;
            if (mExtEventListener != null) mExtEventListener.onProgressShow(false);
            if (mEventListener != null) mEventListener.onError(data);
        }

        @JavascriptInterface
        @Override
        public void close(String data) {
            if (mExtEventListener != null) mExtEventListener.onProgressShow(false);
            if(isWidget) {
                if(mWidgetPrivateEventListener != null) mWidgetPrivateEventListener.onCloseWidget();
            } else {
                if (mEventListener != null) mEventListener.onClose();
            }
        }

        @JavascriptInterface
        @Override
        public void cancel(String data) {
            paymentResult = BootpayPaymentResult.CANCEL;
            if (mExtEventListener != null) mExtEventListener.onProgressShow(false);
            if (mEventListener != null) mEventListener.onCancel(data);


            if(payload != null && payload.getExtra() != null && "popup".equals(payload.getExtra().getOpenType())) {
                close("");
            }
        }

        @JavascriptInterface
        @Override
        public void issued(String data) {
            if (mExtEventListener != null) mExtEventListener.onProgressShow(false);
            if (mEventListener != null) mEventListener.onIssued(data);
        }

        @JavascriptInterface
        @Override
        public String confirm(String data) {
            if (mExtEventListener != null) mExtEventListener.onProgressShow(true);
            boolean goTransaction = false;
            if (mEventListener != null) goTransaction = mEventListener.onConfirm(data);
            if(goTransaction) transactionConfirm(data);
            return String.valueOf(goTransaction);
        }

        @JavascriptInterface
        @Override
        public void done(String data) {
            paymentResult = BootpayPaymentResult.DONE;
            if (mExtEventListener != null) mExtEventListener.onProgressShow(false);
            if (mEventListener != null) mEventListener.onDone(data);
        }


        @JavascriptInterface
        @Override
        public void redirectEvent(String data) {
//          redirect 또는 success_result 닫기
            Log.d("bootpay", "redirectEvent: " + data);
            if("undefined".equals(data)) return;
            try {
                JSONObject json = new JSONObject(data);
                String event = String.valueOf(json.get("event"));
                switch (event) {
                    case "error":
                        paymentResult = BootpayPaymentResult.ERROR;
                        error(data);
                        if(!isDisplayError()) close(data);
                        break;
                    case "close":
                        removePaymentWindow();
                        close(data);
                        break;
                    case "cancel":
                        paymentResult = BootpayPaymentResult.CANCEL;
                        cancel(data);
                        close(data);
//                        closeIfWebApp();
                        break;
                    case "issued":
                        issued(data);
                        if(!isDisplaySuccess()) close(data);
                        break;
                    case "confirm":
                        confirm(data);
                        break;
                    case "done":
                        paymentResult = BootpayPaymentResult.DONE;
                        done(data);
                        if(!isDisplaySuccess()) close(data);
                        else {
                            String dataString = String.valueOf(json.get("data"));
                            JSONObject dataJson = new JSONObject(dataString);
                            String methodOriginSymbol = String.valueOf(dataJson.get("method_origin_symbol"));
                            if("card_rebill_rest".equals(methodOriginSymbol)) {
                                close(data);
                            }
                        }
//                        closeIfWebApp();
                        break;
                    case "bootpayWidgetFullSizeScreen":
                          //dialog를 띄워야 함
                        invisibleWebView();
                        showWidgetDialog();
//                        if(mWidgetPrivateEventListener != null) mWidgetPrivateEventListener.onFullSizeScreen(data);
                        break;
                    case "bootpayWidgetRevertScreen":
//                        fadeOutWebView(300);\
                        invisibleWebView();
                        closeWidgetDialog();
//                        if(mWidgetPrivateEventListener != null) mWidgetPrivateEventListener.onRevertScreen(data);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private void showWidgetDialog() {
            BootpayWidget.showDialog(mActivity);
        }

        private void closeWidgetDialog() {
            BootpayWidget.closeDialog(mActivity);
        }

        //widget debounce event 관련 처리 코드
        private Handler handlerResize = new Handler(Looper.getMainLooper());
        private Handler handlerReady = new Handler(Looper.getMainLooper());
        private Handler handlerChangePayment = new Handler(Looper.getMainLooper());
        private Handler handlerChangeAgreeTerm = new Handler(Looper.getMainLooper());

        private long DEBOUNCE_DELAY = 400;
        private void debounceEvent(Handler handler, int eventId, String data) {
            Runnable resetEventRunnable = null;
            boolean isProcessingEvent;

            switch (eventId) {
                case WIDGET_EVENT_RESIZE:
                    isProcessingEvent = isProcessingEventResize;
                    resetEventRunnable = widgetResize;
                    break;
                case WIDGET_EVENT_READY:
                    isProcessingEvent = isProcessingEventReady;
                    resetEventRunnable = widgetReady;
                    break;
                case WIDGET_EVENT_CHANGE_PAYMENT:
                    isProcessingEvent = isProcessingEventChangePayment;
                    resetEventRunnable = widgetChangePayment;
                    break;
                case WIDGET_EVENT_CHANGE_AGREE_TERM:
                    isProcessingEvent = isProcessingEventChangeAgreeTerm;
                    resetEventRunnable = widgetChangeAgreeTerm;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid event ID");
            }

            if (!isProcessingEvent) {
                handleEvent(eventId, data);
                setProcessingEvent(eventId, true);
            }

            handler.removeCallbacks(resetEventRunnable);
            handler.postDelayed(resetEventRunnable, DEBOUNCE_DELAY);
        }



//        Double webViewHeight = 0.0;
        private void handleEvent(int eventId, String data) {
            if(data == null || data.length() == 0) {
                if(eventId == WIDGET_EVENT_READY) {
                    if(mWidgetEventListener != null) mWidgetEventListener.onWidgetReady();
                }
                return;
            }
            try {
                JSONObject obj = new JSONObject(data);
                switch (eventId) {
                    case WIDGET_EVENT_RESIZE:
                        Double height = obj.getDouble("height");
                        if(mWidgetEventListener != null) mWidgetEventListener.onWidgetResize(height);

//                        if(mActivity == null) return;
//                        mActivity.runOnUiThread(() -> {
//                            // DisplayMetrics를 사용하여 dp를 px로 변환
//                            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//                            int heightInPx = (int) (height * displayMetrics.density);
//
//                            // WebView의 레이아웃 파라미터 업데이트
//                            ViewGroup.LayoutParams params = getLayoutParams();
//                            params.height = heightInPx;
//                            setLayoutParams(params);
//
//                            if(mWidgetEventListener != null) mWidgetEventListener.onWidgetResize(height);
//                        });

                        break;
                    case WIDGET_EVENT_READY:
                        if(mWidgetEventListener != null) mWidgetEventListener.onWidgetReady();
                        break;
                    case WIDGET_EVENT_CHANGE_PAYMENT:
                        if(mWidgetEventListener != null) mWidgetEventListener.onWidgetChangePayment(WidgetData.fromJson(data));
                        break;
                    case WIDGET_EVENT_CHANGE_AGREE_TERM:
                        if(mWidgetEventListener != null) mWidgetEventListener.onWidgetChangeAgreeTerm(WidgetData.fromJson(data));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid event ID");
                }
            } catch (Throwable t) {
                Log.e("bootpay", "Could not parse malformed JSON: \"" + data + "\"");
            }
        }

        private void setProcessingEvent(int eventId, boolean isProcessing) {
            switch (eventId) {
                case WIDGET_EVENT_RESIZE:
                    isProcessingEventResize = isProcessing;
                    break;
                case WIDGET_EVENT_READY:
                    isProcessingEventReady = isProcessing;
                    break;
                case WIDGET_EVENT_CHANGE_PAYMENT:
                    isProcessingEventChangePayment = isProcessing;
                    break;
                case WIDGET_EVENT_CHANGE_AGREE_TERM:
                    isProcessingEventChangeAgreeTerm = isProcessing;
                    break;
            }
        }


        @JavascriptInterface
        @Override
        public void readyWatch() {
            debounceEvent(handlerReady, WIDGET_EVENT_READY, "");
        }

        @JavascriptInterface
        @Override
        public void resizeWatch(String data) {
            debounceEvent(handlerResize, WIDGET_EVENT_RESIZE, data);
        }

        @JavascriptInterface
        @Override
        public void changeMethodWatch(String data) {
            debounceEvent(handlerChangePayment, WIDGET_EVENT_CHANGE_PAYMENT, data);
        }

        @JavascriptInterface
        @Override
        public void changeTermsWatch(String data) {
            debounceEvent(handlerChangeAgreeTerm, WIDGET_EVENT_CHANGE_AGREE_TERM, data);
        }
    }

    public void connectBootpay() {
        loadUrl(BootpayConstant.CDN_URL);
    }


    public void backPressed() {
        if(canGoBack()) goBack();
//        else if(mDialog != null) mDialog.dismiss();
//        else if(mDialogX != null) mDialogX.dismiss();
    }

    void evaluateJavascriptWithFallback(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(script, null);
            return;
        }

        try {
            loadUrl("javascript:" + URLEncoder.encode(script, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // UTF-8 should always be supported
            throw new RuntimeException(e);
        }
    }

    void callJavaScript(@Nullable String script) {
        if (getSettings().getJavaScriptEnabled() &&
                script != null &&
                !TextUtils.isEmpty(script)) {
            Log.d("bootpay", "callJavaScript:" + script);
            evaluateJavascriptWithFallback("(function() {\n" + script + ";\n})();");
        }
    }

    public void setEventListener(BootpayEventListener listener) {
        this.mEventListener = listener;
        this.mWebViewClient.setEventListener(listener);
    }

    public void setExtEventListener(BootpayExtEventListener listener) {
        this.mExtEventListener = listener;
    }

    public void callInjectedJavaScript() {
        Log.d("bootpay", "callInjectedJavaScript: " + injectedJS);
        callJavaScript(injectedJS);
    }

    public void callInjectedJavaScriptBeforePayStart() {
        if(injectedJSBeforePayStart == null) return;
        for(String js : injectedJSBeforePayStart) {
            Log.d("bootpay", "callInjectedJavaScriptBeforePayStart: " + js);
            callJavaScript(js);
        }
    }

    public void receivePostMessage() {
        callJavaScript(BootpayConstant.message());
    }

    public void setIgnoreErrFailedForThisURL(@Nullable String url) {
        if(mWebViewClient != null) mWebViewClient.setIgnoreErrFailedForThisURL(url);
    }

    public BootpayEventListener getEventListener() {
        return mEventListener;
    }

    @Nullable
    public String getInjectedJS() {
        return injectedJS;
    }

    @Nullable
    public List<String> getInjectedJSBeforePayStart() {
        return injectedJSBeforePayStart;
    }

    public void setPayload(Payload payload) {
        if(payload != null) {
            addJavascriptInterface(new BootpayJavascriptBridge(), BootpayBuildConfig.JSInterfaceBridgeName);
        }
        this.payload = payload;
    }

    boolean isDisplaySuccess() {
        if(payload == null) return false;
        if(payload.getExtra() == null) return false;
        return payload.getExtra().isDisplaySuccessResult();
    }

    boolean isDisplayError() {
        if(payload == null) return false;
        if(payload.getExtra() == null) return false;
        return payload.getExtra().isDisplayErrorResult();
    }

    @Override
    public void renderWidget(Activity activity, Payload payload, BootpayWidgetEventListener listener) {
        this.mActivity = activity;

        this.mWidgetEventListener = listener;

        this.setPayload(payload);

        if(BootpayBuildConfig.DEBUG) {
            this.injectedJSBeforePayStart = Arrays.asList(
                    "BootpayWidget.setEnvironmentMode('development');",
                    BootpayConstant.readyWatch(),
                    BootpayConstant.resizeWatch(),
                    BootpayConstant.changeMethodWatch(),
                    BootpayConstant.changeTermsWatch(),
                    BootpayConstant.close()
            );
        }

        String script = BootpayConstant.loadParams(
                "BootpayWidget.render('#bootpay-widget', ",
                payload.toJsonUnderscore(),
                ")"
        );
        this.setInjectedJS(script);
        this.startWidget();
    }

//    @Nullable
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        return super.onSaveInstanceState();
//        this.save
//    }


//    @Nullable
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        return super.onSaveInstanceState();
//    }

    @Override
    public void requestPayment(Payload payload, BootpayEventListener listener) {
        this.mEventListener = listener;

        if(payload == null) return;

        String requestScript = BootpayConstant.loadParams(
                "BootpayWidget.requestPayment(",
                payload.toJsonUnderscore(),
                ")",
                ".then( function (res) {",
                BootpayConstant.confirm(),
                BootpayConstant.issued(),
                BootpayConstant.done(),
                BootpayConstant.redirect(), //success_result 닫기 이벤트가 일로 간다
                "}, function (res) {",
                BootpayConstant.error(),
                BootpayConstant.cancel(),
                BootpayConstant.redirect(),
                "})"
        );

        boolean refresh = false;
        String updateScript = BootpayConstant.loadParams(
                "Bootpay.setDevice('ANDROID');",
                "Bootpay.setVersion('" + VERSION + "', 'android');",
                "BootpayWidget.update(" + "",
                payload.toJsonUnderscore(),
                String.format(", '%s');", refresh ? "true" : "false")
        );

        Log.d("bootpay", "updateScript: " + updateScript);
        Log.d("bootpay", "requestPayment: " + requestScript);

//        post(() -> loadUrl(String.format(Locale.KOREA, "javascript:(function(){%s})()", script)));

//        load("alert(1);");
        //update 후에 request
        load(updateScript);
        load(requestScript);


//        load(requestScript);
    }

    public void removeFromParent(Activity activity) {
        mActivity = activity;
        activity.runOnUiThread(() -> {
//            fadeOutWebView(300);
            pauseWebView();
            if(getParent() != null) {
                ((android.view.ViewGroup) getParent()).removeView(this);
            }
        });
    }

    public void addToParent(Activity activity, ViewGroup parent) {
        mActivity = activity;
        activity.runOnUiThread(() -> {
            if(parent != null) parent.addView(this);
//            fadeInWebView(2000);
            resumeWebView();
        });
    }

    private void pauseWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            onPause();
        }
        pauseTimers();
    }

    private void resumeWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            onResume();
        }
        resumeTimers();
    }

    protected void fadeOutWebView(long duration) {
        ViewPropertyAnimator animator = animate();
        if(animator != null) animator.alpha(0).setDuration(duration);
    }

    protected void invisibleWebView() {
        setAlpha(0);
    }

    protected void visibleWebView() {
        setAlpha(1);
    }

    protected void fadeInWebView(long duration) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ViewPropertyAnimator animator = animate();
            if(animator != null) animator.alpha(1).setDuration(duration);
        }, 1000);
    }

    public void setWidgetPrivateEventListener(BootpayWidgetPrivateEventListener listener) {
        this.mWidgetPrivateEventListener = listener;
    }

    public BootpayPaymentResult getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(BootpayPaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public void resizeWebView(Double height) {
        if(mActivity == null) return;
        mActivity.runOnUiThread(() -> {
            // DisplayMetrics를 사용하여 dp를 px로 변환
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int heightInPx = (int) (height * displayMetrics.density);

            // WebView의 레이아웃 파라미터 업데이트
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = heightInPx;
            setLayoutParams(params);

            if(mWidgetEventListener != null) mWidgetEventListener.onWidgetResize(height);
        });
    }

    public void fullSizeWebView() {
        mActivity.runOnUiThread(() -> {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;  // MATCH_PARENT로 설정
            setLayoutParams(params);
        });
    }
}

