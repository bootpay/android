package kr.co.bootpay.android.webview;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

public class BootpayWebViewClient extends WebViewClient {

    protected boolean isCDNLoaded = false;


    protected @Nullable
    String ignoreErrFailedForThisURL = null;

    public BootpayWebViewClient() {}

    public void setIgnoreErrFailedForThisURL(@Nullable String url) {
        ignoreErrFailedForThisURL = url;
    }


    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);

        if (!isCDNLoaded) {
            BootpayWebView _webView = (BootpayWebView) webView;
            _webView.callInjectedJavaScriptBeforePayStart();
            _webView.callInjectedJavaScript();
            isCDNLoaded = true;
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return BootpayUrlHelper.shouldOverrideUrlLoading(view, url);
    }


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        final String url = request.getUrl().toString();
        return this.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) back();
        return super.shouldOverrideKeyEvent(view, event);
    }



//    @Override
//    public void onReceivedSslError(final WebView webView, final SslErrorHandler handler, final SslError error) {
//        handler.proceed();
////        String topWindowUrl = webView.getUrl();
////        String failingUrl = error.getUrl();
////
////        handler.cancel();
////
////        if (!topWindowUrl.equalsIgnoreCase(failingUrl)) {
////            // If error is not due to top-level navigation, then do not call onReceivedError()
////            return;
////        }
////
////        int code = error.getPrimaryError();
////        String description = "";
////        String descriptionPrefix = "SSL error: ";
////
////        // https://developer.android.com/reference/android/net/http/SslError.html
////        switch (code) {
////            case SslError.SSL_DATE_INVALID:
////                description = "The date of the certificate is invalid";
////                break;
////            case SslError.SSL_EXPIRED:
////                description = "The certificate has expired";
////                break;
////            case SslError.SSL_IDMISMATCH:
////                description = "Hostname mismatch";
////                break;
////            case SslError.SSL_INVALID:
////                description = "A generic error occurred";
////                break;
////            case SslError.SSL_NOTYETVALID:
////                description = "The certificate is not yet valid";
////                break;
////            case SslError.SSL_UNTRUSTED:
////                description = "The certificate authority is not trusted";
////                break;
////            default:
////                description = "Unknown SSL Error";
////                break;
////        }
////
////        description = descriptionPrefix + description;
////
////        this.onReceivedError(
////                webView,
////                code,
////                description,
////                failingUrl
////        );
//    }

//    protected HashMap createWebViewEvent(WebView webView, String url) {
//        HashMap<String, Object> event = new HashMap<>();
//        event.put("target", webView.getId());
//        event.put("url", url);
////        event.put("loading", !mLastLoadFailed && webView.getProgress() != 100);
//        event.put("title", webView.getTitle());
//        event.put("canGoBack", webView.canGoBack());
//        event.put("canGoForward", webView.canGoForward());
//        return event;
//    }
}
