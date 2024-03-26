package kr.co.bootpay.android.webview;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;

import kr.co.bootpay.android.events.BootpayEventListener;

public class BootpayWebViewClient extends WebViewClient {

//    protected boolean isCDNLoaded = false;

    protected @Nullable
    BootpayEventListener mEventListener;

    protected @Nullable
    String ignoreErrFailedForThisURL = null;

    public BootpayWebViewClient() {
    }
//    public BootpayWebViewClient(@Nullable BootpayEventListener eventListener) {
//        this.mEventListener = eventListener;
//    }

    public void setIgnoreErrFailedForThisURL(@Nullable String url) {
        ignoreErrFailedForThisURL = url;
    }

    public void setEventListener(@Nullable BootpayEventListener eventListener) {
        this.mEventListener = eventListener;
    }


    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);
        updateBlindViewIfNaverLogin(webView, url);

        if(url.contains("webview.bootpay.co.kr")) {
            BootpayWebView _webView = (BootpayWebView) webView;
            _webView.callInjectedJavaScriptBeforePayStart();
            _webView.callInjectedJavaScript();
        }

//        if (!isCDNLoaded) {
//            BootpayWebView _webView = (BootpayWebView) webView;
//            _webView.callInjectedJavaScriptBeforePayStart();
//            _webView.callInjectedJavaScript();
//            isCDNLoaded = true;
//        }
    }


    private void updateBlindViewIfNaverLogin(WebView webView, String url) {
        if(url.startsWith("https://nid.naver.com")) { //show
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("document.getElementById('back').remove();", null);
            }
        }
    }

//    shouldOv

//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        return super.shouldOverrideUrlLoading(view, url);
//    }
//
//    @Override
//    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//        return super.shouldOverrideKeyEvent(view, event);
//    }
//
//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//        return super.shouldOverrideUrlLoading(view, request);
//    }


    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    //    @Override
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

    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
        if(mEventListener != null) {
            mEventListener.onError("sslerror:" + error.toString());
//            mEventListener.onClose();
        }
//        onErrorHandled("sslerror:" + error.toString());
//        onCloseHandled("sslerror:" + error.toString());

//        webView.mE


        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("SSL Connection Error");
        builder.setMessage("Your device's Android version is outdated and may not securely connect to our service. To continue using the app securely, please update your device's operating system. If you choose to proceed without updating, it may expose you to security vulnerabilities.");
        builder.setPositiveButton("Update", (dialog, which) -> {
            // Redirect the user to the system update settings


            Intent intent = new Intent("android.settings.SYSTEM_UPDATE_SETTINGS");
            if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
                view.getContext().startActivity(intent);
            } else {
                // If the device does not support system update settings intent
                Toast.makeText(view.getContext(), "System update option not available. Please check your device settings manually.", Toast.LENGTH_LONG).show();
            }
            if(mEventListener != null) {
//                    mEventListener.onError("sslerror:" + error.toString());
                mEventListener.onClose();
            }
        });
        builder.setNeutralButton("Cancel", (dialog, which) -> {
            handler.cancel();
            if(mEventListener != null) {
//                    mEventListener.onError("sslerror:" + error.toString());
                mEventListener.onClose();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnKeyListener((dialogInterface, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                dialog.dismiss();
                mEventListener.onClose();
            }
            return true;
        });
        dialog.show();
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
