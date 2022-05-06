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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import kr.co.bootpay.android.api.BootpayDialog;
import kr.co.bootpay.android.api.BootpayDialogX;
import kr.co.bootpay.android.api.BootpayInterface;
import kr.co.bootpay.android.constants.BootpayBuildConfig;
import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.JSInterfaceBridge;


public class BootpayWebView extends WebView implements BootpayInterface {
    BootpayDialog mDialog;
    BootpayDialogX mDialogX;

    BootpayWebViewClient mWebViewClient;
    BootpayEventListener mEventListener;


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
        addJavascriptInterface(new BootpayJavascriptBridge(), "Android");
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
        load("Bootpay.removePaymentWindow();");
        if(mDialog != null) mDialog.removePaymentWindow();
        else if(mDialogX != null) mDialogX.removePaymentWindow();
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
            if (mEventListener != null) mEventListener.onError(data);
        }

        @JavascriptInterface
        @Override
        public void close(String data) {
            if (mEventListener != null) mEventListener.onClose(data);
        }

        @JavascriptInterface
        @Override
        public void cancel(String data) {
            if (mEventListener != null) mEventListener.onCancel(data);
        }

        @JavascriptInterface
        @Override
        public void issued(String data) {
            if (mEventListener != null) mEventListener.onIssued(data);
        }

        @JavascriptInterface
        @Override
        public String confirm(String data) {
            boolean goTransaction = false;
            if (mEventListener != null) goTransaction = mEventListener.onConfirm(data);
            if(goTransaction) transactionConfirm(data);
            return String.valueOf(goTransaction);
        }

        @JavascriptInterface
        @Override
        public void done(String data) {
            if (mEventListener != null) mEventListener.onDone(data);
        }
    }

    public void connectBootpay() {
        loadUrl(BootpayConstant.CDN_URL);
    }


    public void backPressed() {
        if(canGoBack()) goBack();
        else if(mDialog != null) mDialog.dismiss();
        else if(mDialogX != null) mDialogX.dismiss();
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

    public void callInjectedJavaScript() {
        callJavaScript(injectedJS);
    }

    public void callInjectedJavaScriptBeforePayStart() {
        if(injectedJSBeforePayStart == null) return;
        for(String js : injectedJSBeforePayStart) {
            callJavaScript(js);
        }
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
}

