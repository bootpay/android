package kr.co.bootpay.android.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
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
import java.util.List;
import java.util.Locale;

import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.api.BootpayDialog;
import kr.co.bootpay.android.api.BootpayDialogX;
import kr.co.bootpay.android.api.BootpayInterface;
import kr.co.bootpay.android.constants.BootpayBuildConfig;
import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.BootpayExtEventListener;
import kr.co.bootpay.android.events.JSInterfaceBridge;
import kr.co.bootpay.android.models.Payload;


public class BootpayWebView extends WebView implements BootpayInterface {
//    BootpayDialog mDialog;
//    BootpayDialogX mDialogX;

    Payload payload;

    BootpayWebViewClient mWebViewClient;
    BootpayEventListener mEventListener;
    BootpayExtEventListener mExtEventListener;


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
        if(payload != null) {
            addJavascriptInterface(new BootpayJavascriptBridge(), BootpayBuildConfig.JSInterfaceBridgeName);
        }

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
//        load("Bootpay.removePaymentWindow();");
        load("Bootpay.destroy();");
//        if(mDialog != null) mDialog.removePaymentWindow();
//        else if(mDialogX != null) mDialogX.removePaymentWindow();
    }

    void closeIfWebApp() {
        if(payload == null) { //webapp
            removePaymentWindow();
            Bootpay.dismissWindow();

//            postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            }, 200);
        }
    }

    public void startBootpay() {
        connectBootpay();
    }

    public void transactionConfirm(String data) {
        String scriptList = BootpayConstant.loadParams(
//                "var data = JSON.parse('" + data + "');",
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
            if (mExtEventListener != null) mExtEventListener.onProgressShow(false);
            if (mEventListener != null) mEventListener.onError(data);
        }

        @JavascriptInterface
        @Override
        public void close(String data) {
            if (mExtEventListener != null) mExtEventListener.onProgressShow(false);
            if (mEventListener != null) mEventListener.onClose();

        }

        @JavascriptInterface
        @Override
        public void cancel(String data) {
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
                        error(data);
                        if(!isDisplayError()) close(data);
                        break;
                    case "close":
                        removePaymentWindow();
                        close(data);
                        break;
                    case "cancel":
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            Log.d("bootpay", script);
            evaluateJavascriptWithFallback("(function() {\n" + script + ";\n})();");
        }
    }

    public void setEventListener(BootpayEventListener listener) {
        this.mEventListener = listener;
    }

    public void setExtEventListener(BootpayExtEventListener listener) {
        this.mExtEventListener = listener;
    }

    public void callInjectedJavaScript() {
        callJavaScript(injectedJS);
    }

    public void callInjectedJavaScriptBeforePayStart() {
        if(injectedJSBeforePayStart == null) return;
        for(String js : injectedJSBeforePayStart) {
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
}

