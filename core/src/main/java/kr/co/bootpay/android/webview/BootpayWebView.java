package kr.co.bootpay.android.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kr.co.bootpay.android.BootpayPaymentResult;
import kr.co.bootpay.android.BootpayWidget;
import kr.co.bootpay.android.api.BootpayInterface;
import kr.co.bootpay.android.api.BootpayWidgetInterface;
import kr.co.bootpay.android.constants.BootpayBuildConfig;
import kr.co.bootpay.android.enums.BootpayWidgetEvent;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.BootpayExtEventListener;
import kr.co.bootpay.android.events.BootpayWidgetEventListener;
import kr.co.bootpay.android.events.BootpayWidgetPrivateEventListener;
import kr.co.bootpay.android.events.JSInterfaceBridge;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.models.widget.WidgetData;
import kr.co.bootpay.android.widget.BootpayWidgetController;

public class BootpayWebView extends WebView implements BootpayInterface, BootpayWidgetInterface {

    private Activity mActivity;
    Payload payload;

    protected boolean isWidget = false;
    protected boolean isFullScreen = false;

    Double mHeight = 400.0;


    BootpayWebViewClient mWebViewClient;
    BootpayEventListener mEventListener;
    BootpayExtEventListener mExtEventListener;
    BootpayWidgetEventListener mWidgetEventListener;
    BootpayWidgetPrivateEventListener mWidgetPrivateEventListener;
    @Nullable BootpayWidgetController mWidgetController;

    protected @Nullable
    String injectedJS;

    protected @Nullable
    List<String> injectedJSBeforePayStart;

    private BootpayPaymentResult paymentResult = BootpayPaymentResult.NONE;

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

    void payWebSettings(Context context) {
        BootpayWebViewHandler.payWebSettings(this, context);
    }

    @Override
    public void removePaymentWindow() {
        BootpayWebViewHandler.removePaymentWindow(this);
    }

    public void setInjectedJS(@Nullable String injectedJS) {
        this.injectedJS = injectedJS;
    }

    public void setInjectedJSBeforePayStart(@Nullable List<String> injectedJSBeforePayStart) {
        this.injectedJSBeforePayStart = injectedJSBeforePayStart;
    }

    public void startPayment() {
        this.isWidget = false;
        BootpayWebViewHandler.startPayment(this);
    }

    public void startWidget() {
        this.isWidget = true;
        BootpayWebViewHandler.startWidget(this);
    }

    public void transactionConfirm() {
        BootpayWebViewHandler.transactionConfirm(this);
    }

    public void setEventListener(BootpayEventListener listener) {
        this.mEventListener = listener;
        this.mWebViewClient.setEventListener(listener);
    }

    public void setExtEventListener(BootpayExtEventListener listener) {
        this.mExtEventListener = listener;
    }

    public void callInjectedJavaScript() {
        BootpayWebViewHandler.callInjectedJavaScript(this);
    }

    public void callInjectedJavaScriptBeforePayStart() {
        BootpayWebViewHandler.callInjectedJavaScriptBeforePayStart(this);
    }

//    public void receivePostMessage() {
//        BootpayWebViewHandler.receivePostMessage(this);
//    }

    public void setIgnoreErrFailedForThisURL(@Nullable String url) {
        if (mWebViewClient != null) mWebViewClient.setIgnoreErrFailedForThisURL(url);
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
        if (payload != null) {
            addJavascriptInterface(new BootpayJavascriptBridge(), BootpayBuildConfig.JSInterfaceBridgeName);
        }
        this.payload = payload;
    }

    @Override
    public void renderWidget(Activity activity, Payload payload, BootpayWidgetEventListener listener) {
        this.isWidget = true;
        this.mActivity = activity;
        this.mWidgetEventListener = listener;
        this.setPayload(payload);
        BootpayWebViewHandler.renderWidget(this, payload);
    }

    @Override
    public void requestWidgetPayment(Payload payload, BootpayEventListener listener) {
        this.isWidget = true;
        this.mEventListener = listener;
        if (payload == null) return;
        BootpayWebViewHandler.requestWidgetPayment(this, payload);
    }

    public void removeFromParent(Activity activity) {
        BootpayWebViewHandler.removeFromParent(this, activity);
    }

    public void addToParent(Activity activity, ViewGroup parent) {
        BootpayWebViewHandler.addToParent(this, activity, parent);
    }

    public void pauseWebView() {
        BootpayWebViewHandler.pauseWebView(this);
    }

    public void resumeWebView() {
        BootpayWebViewHandler.resumeWebView(this);
    }

    public void fadeOutWebView(long duration) {
        BootpayWebViewHandler.fadeOutWebView(this, duration);
    }

    public void invisibleWebView() {
        BootpayWebViewHandler.invisibleWebView(this);
    }

    public void fadeInWebView(long duration) {
        BootpayWebViewHandler.fadeInWebView(this, duration);
    }

    public void setWidgetPrivateEventListener(BootpayWidgetPrivateEventListener listener) {
        this.mWidgetPrivateEventListener = listener;
    }

    public void setWidgetController(@Nullable BootpayWidgetController controller) {
        this.mWidgetController = controller;
    }

    @Nullable
    public BootpayWidgetController getWidgetController() {
        return mWidgetController;
    }

    public BootpayPaymentResult getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(BootpayPaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public void resizeWebView(Double height) {
        if(isFullScreen) return;
        BootpayWebViewHandler.resizeWebView(this, height);
    }

    public void fullSizeWebView() {
        this.isFullScreen = true;
        BootpayWebViewHandler.fullSizeWebView(this);
    }


    private class BootpayJavascriptBridge implements JSInterfaceBridge {
//        private final BootpayWebView webView;

//        public BootpayJavascriptBridge(BootpayWebView webView) {
//            this.webView = webView;
//        }

        private BootpayPaymentResult paymentResult = BootpayPaymentResult.NONE;

        @JavascriptInterface
        @Override
        public void error(String data) {
            paymentResult = BootpayPaymentResult.ERROR;
            if (mExtEventListener != null && isWidget) mExtEventListener.onProgressShow(false);

            // Widget controller callback
            if (isWidget && mWidgetController != null) {
                mWidgetController.handleError(data);
            }
            // Widget event listener callback
            if (isWidget && mWidgetEventListener != null) {
                mWidgetEventListener.onWidgetError(data);
            }
            // Legacy event listener callback
            if (mEventListener != null) mEventListener.onError(data);

            if (payload != null && payload.getExtra() != null && !"redirect".equals(payload.getExtra().getOpenType())) {
                close("");
            }
        }

        @JavascriptInterface
        @Override
        public void close(String data) {
            if (mExtEventListener != null && isWidget) mExtEventListener.onProgressShow(false);

            // Widget controller callback
            if (isWidget && mWidgetController != null) {
                mWidgetController.handleClose();
            }
            // Widget event listener callback
            if (isWidget && mWidgetEventListener != null) {
                mWidgetEventListener.onWidgetClose();
            }

            if (isWidget) {
                BootpayWidget.closeDialog((Activity) getContext());
            } else {
                if (mEventListener != null) mEventListener.onClose();
            }
            isFullScreen = false;
        }

        @JavascriptInterface
        @Override
        public void cancel(String data) {
            paymentResult = BootpayPaymentResult.CANCEL;
            if (mExtEventListener != null && isWidget) mExtEventListener.onProgressShow(false);

            // Widget controller callback
            if (isWidget && mWidgetController != null) {
                mWidgetController.handleCancel(data);
            }
            // Widget event listener callback
            if (isWidget && mWidgetEventListener != null) {
                mWidgetEventListener.onWidgetCancel(data);
            }
            // Legacy event listener callback
            if (mEventListener != null) mEventListener.onCancel(data);

            if (payload != null && payload.getExtra() != null && !"redirect".equals(payload.getExtra().getOpenType())) {
                close("");
            }
        }

        @JavascriptInterface
        @Override
        public void issued(String data) {
            if (mExtEventListener != null && isWidget) mExtEventListener.onProgressShow(false);

            // Widget controller callback
            if (isWidget && mWidgetController != null) {
                mWidgetController.handleIssued(data);
            }
            // Widget event listener callback
            if (isWidget && mWidgetEventListener != null) {
                mWidgetEventListener.onWidgetIssued(data);
            }
            // Legacy event listener callback
            if (mEventListener != null) mEventListener.onIssued(data);
        }

        @JavascriptInterface
        @Override
        public String confirm(String data) {
            if (mExtEventListener != null && isWidget) mExtEventListener.onProgressShow(true);

            boolean goTransaction = true; // Default to proceed

            // Widget controller callback (takes priority if set)
            if (isWidget && mWidgetController != null && mWidgetController.hasOnConfirm()) {
                goTransaction = mWidgetController.handleConfirm(data);
            }
            // Widget event listener callback
            else if (isWidget && mWidgetEventListener != null) {
                goTransaction = mWidgetEventListener.onWidgetConfirm(data);
            }
            // Legacy event listener callback
            else if (mEventListener != null) {
                goTransaction = mEventListener.onConfirm(data);
            }

            if (goTransaction) {
                if (isWidget && mActivity != null) {
                    mActivity.runOnUiThread(() -> BootpayWebViewHandler.transactionConfirm(BootpayWebView.this));
                } else {
                    transactionConfirm();
                }
            }

            return String.valueOf(goTransaction);
        }

        @JavascriptInterface
        @Override
        public void done(String data) {
            paymentResult = BootpayPaymentResult.DONE;
            if (mExtEventListener != null && isWidget) mExtEventListener.onProgressShow(false);

            // Widget controller callback
            if (isWidget && mWidgetController != null) {
                mWidgetController.handleDone(data);
            }
            // Widget event listener callback
            if (isWidget && mWidgetEventListener != null) {
                mWidgetEventListener.onWidgetDone(data);
            }
            // Legacy event listener callback
            if (mEventListener != null) mEventListener.onDone(data);

            if (payload != null && payload.getExtra() != null && !"redirect".equals(payload.getExtra().getOpenType())) {
                close("");
            }
        }

        @JavascriptInterface
        @Override
        public void redirectEvent(String data) {
            Log.d("bootpay", "redirectEvent: " + data);
            if ("undefined".equals(data)) return;
            try {
                JSONObject json = new JSONObject(data);
                String event = String.valueOf(json.get("event"));
                switch (event) {
                    case "error":
                        paymentResult = BootpayPaymentResult.ERROR;
                        error(data);
                        if (!isDisplayError()) close(data);
                        break;
                    case "close":
                        close(data);
                        BootpayWebViewHandler.removePaymentWindow(BootpayWebView.this);
                        break;
                    case "cancel":
                        paymentResult = BootpayPaymentResult.CANCEL;
                        cancel(data);
                        close(data);
                        break;
                    case "issued":
                        issued(data);
                        if (!isDisplaySuccess()) close(data);
                        break;
                    case "confirm":
                        confirm(data);
                        break;
                    case "done":
                        paymentResult = BootpayPaymentResult.DONE;
                        done(data);
                        if (!isDisplaySuccess()) close(data);
                        else if (!isWidget) {
                            String dataString = String.valueOf(json.get("data"));
                            JSONObject dataJson = new JSONObject(dataString);
                            String methodOriginSymbol = String.valueOf(dataJson.get("method_origin_symbol"));
                            if ("card_rebill_rest".equals(methodOriginSymbol)) {
                                close(data);
                            }
                        }
                        break;
                    case "bootpayWidgetFullSizeScreen":
                        BootpayWebViewHandler.invisibleWebView(BootpayWebView.this);
                        BootpayWidget.showDialog((Activity) getContext());
                        break;
                    case "bootpayWidgetRevertScreen":
                        BootpayWebViewHandler.invisibleWebView(BootpayWebView.this);
                        BootpayWidget.closeDialog((Activity) getContext());
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private boolean isDisplaySuccess() {
            if (payload == null) return false;
            if (payload.getExtra() == null) return false;
            return payload.getExtra().isDisplaySuccessResult();
        }

        private boolean isDisplayError() {
            if (payload == null) return false;
            if (payload.getExtra() == null) return false;
            return payload.getExtra().isDisplayErrorResult();
        }

        private Handler handlerResize = new Handler(Looper.getMainLooper());
        private Handler handlerReady = new Handler(Looper.getMainLooper());
        private Handler handlerChangePayment = new Handler(Looper.getMainLooper());
        private Handler handlerChangeAgreeTerm = new Handler(Looper.getMainLooper());
        private boolean isProcessingEventResize = false;
        private boolean isProcessingEventReady = false;
        private boolean isProcessingEventChangePayment = false;
        private boolean isProcessingEventChangeAgreeTerm = false;
        private Runnable widgetResize = () -> this.isProcessingEventResize = false;
        private Runnable widgetReady = () -> this.isProcessingEventReady = false;
        private Runnable widgetChangePayment = () -> this.isProcessingEventChangePayment = false;
        private Runnable widgetChangeAgreeTerm = () -> this.isProcessingEventChangeAgreeTerm = false;

        private long DEBOUNCE_DELAY = 400;

        private void debounceWidgetEvent(Handler handler, BootpayWidgetEvent widgetEvent, String data) {
            Runnable resetEventRunnable = null;
            boolean isProcessingEvent = false;

            switch (widgetEvent) {
                case RESIZE:
                    isProcessingEvent = isProcessingEventResize;
                    resetEventRunnable = widgetResize;
                    break;
                case READY:
                    isProcessingEvent = isProcessingEventReady;
                    resetEventRunnable = widgetReady;
                    break;
                case CHANGE_PAYMENT:
                    isProcessingEvent = isProcessingEventChangePayment;
                    resetEventRunnable = widgetChangePayment;
                    break;
                case CHANGE_AGREE_TERM:
                    isProcessingEvent = isProcessingEventChangeAgreeTerm;
                    resetEventRunnable = widgetChangeAgreeTerm;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid event ID");
            }

            if (!isProcessingEvent) {
                handleEvent(widgetEvent, data);
                setProcessingEvent(widgetEvent, true);
            }

            handler.removeCallbacks(resetEventRunnable);
            handler.postDelayed(resetEventRunnable, DEBOUNCE_DELAY);
        }

//        Double webViewHeight = 0.0;

        private void handleEvent(BootpayWidgetEvent widgetEvent, String data) {
            if (data == null || data.length() == 0) {
                if (widgetEvent == BootpayWidgetEvent.READY) {
                    // Controller callback
                    if (mWidgetController != null) mWidgetController.handleReady();
                    // Event listener callback
                    if (mWidgetEventListener != null) mWidgetEventListener.onWidgetReady();
                }
                return;
            }
            try {
                JSONObject obj = new JSONObject(data);
                switch (widgetEvent) {
                    case RESIZE:
                        if (isFullScreen) return;
                        Double height = obj.getDouble("height");
                        if (mHeight.equals(height)) return;
                        mHeight = height;
                        // Controller callback
                        if (mWidgetController != null) mWidgetController.handleResize(height);
                        // Event listener callback
                        if (mWidgetEventListener != null) mWidgetEventListener.onWidgetResize(height);
                        BootpayWebViewHandler.resizeWebView(BootpayWebView.this, height);
                        break;
                    case READY:
                        // Controller callback
                        if (mWidgetController != null) mWidgetController.handleReady();
                        // Event listener callback
                        if (mWidgetEventListener != null) mWidgetEventListener.onWidgetReady();
                        break;
                    case CHANGE_PAYMENT:
                        WidgetData paymentData = WidgetData.fromJson(data);
                        // Controller callback
                        if (mWidgetController != null) mWidgetController.handleChangePayment(paymentData);
                        // Event listener callback
                        if (mWidgetEventListener != null) mWidgetEventListener.onWidgetChangePayment(paymentData);
                        break;
                    case CHANGE_AGREE_TERM:
                        WidgetData termData = WidgetData.fromJson(data);
                        // Controller callback
                        if (mWidgetController != null) mWidgetController.handleChangeAgreeTerm(termData);
                        // Event listener callback
                        if (mWidgetEventListener != null) mWidgetEventListener.onWidgetChangeAgreeTerm(termData);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid event ID");
                }
            } catch (Throwable t) {
                Log.e("bootpay", "Could not parse malformed JSON: \"" + data + "\"");
            }
        }

        private void setProcessingEvent(BootpayWidgetEvent widgetEvent, boolean isProcessing) {
            switch (widgetEvent) {
                case RESIZE:
                    isProcessingEventResize = isProcessing;
                    break;
                case READY:
                    isProcessingEventReady = isProcessing;
                    break;
                case CHANGE_PAYMENT:
                    isProcessingEventChangePayment = isProcessing;
                    break;
                case CHANGE_AGREE_TERM:
                    isProcessingEventChangeAgreeTerm = isProcessing;
                    break;
            }
        }

        @JavascriptInterface
        @Override
        public void readyWatch() {
            debounceWidgetEvent(handlerReady, BootpayWidgetEvent.READY, "");
        }

        @JavascriptInterface
        @Override
        public void resizeWatch(String data) {
            debounceWidgetEvent(handlerResize, BootpayWidgetEvent.RESIZE, data);
        }

        @JavascriptInterface
        @Override
        public void changeMethodWatch(String data) {
            debounceWidgetEvent(handlerChangePayment, BootpayWidgetEvent.CHANGE_PAYMENT, data);
        }

        @JavascriptInterface
        @Override
        public void changeTermsWatch(String data) {
            debounceWidgetEvent(handlerChangeAgreeTerm, BootpayWidgetEvent.CHANGE_AGREE_TERM, data);
        }
    }
}
