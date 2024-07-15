package kr.co.bootpay.android.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import kr.co.bootpay.android.constants.BootpayBuildConfig;
import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.script.BootpayScript;

public class BootpayWebViewHandler {

    static void payWebSettings(WebView webView, Context context) {
        WebSettings settings = webView.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportMultipleWindows(true);

        if (BootpayBuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getApplicationInfo().flags &= context.getApplicationInfo().FLAG_DEBUGGABLE;
            if (0 != context.getApplicationInfo().flags) webView.setWebContentsDebuggingEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptCookie(true);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
    }

    static void evaluateJavascriptWithFallback(WebView webView, String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(script, null);
            return;
        }

        try {
            webView.loadUrl("javascript:" + URLEncoder.encode(script, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // UTF-8 should always be supported
            throw new RuntimeException(e);
        }
    }

    static void doScript(WebView webView, String script) {
//        webView.post(() -> webView.loadUrl(String.format(Locale.KOREA, "javascript:(function(){%s})()", script)));
        if (webView.getSettings().getJavaScriptEnabled() &&
                script != null &&
                !TextUtils.isEmpty(script)) {
            Log.d("bootpay", "callJavaScript:" + script);
            evaluateJavascriptWithFallback(webView, "(function() {\n" + script + ";\n})();");
        }

//        get
//        webView.getactivi

    }

    static void removePaymentWindow(WebView webView) {
        doScript(webView, BootpayScript.removePaymentWindow());
    }

    static void startPayment(WebView webView) {
//        webView.post(() -> webView.loadUrl(String.format(Locale.KOREA, "javascript:(function(){%s})()", BootpayConstant.CDN_URL)));
//        webView.post(() -> webView.loadUrl(String.format(Locale.KOREA, "javascript:(function(){%s})()", "https://www.naver.com")));
        webView.loadUrl(BootpayConstant.CDN_URL);
    }

    static void startWidget(WebView webView) {
//        webView.post(() -> webView.loadUrl(String.format(Locale.KOREA, "javascript:(function(){%s})()", BootpayConstant.WIDGET_URL)));
        webView.loadUrl(BootpayConstant.WIDGET_URL);
    }

    static void transactionConfirm(WebView webView) {
        doScript(webView, BootpayScript.transactionConfirm());
    }

    static void callInjectedJavaScript(BootpayWebView webView) {
        doScript(webView, webView.getInjectedJS());
    }

    static void callInjectedJavaScriptBeforePayStart(BootpayWebView webView) {
        List<String> injectedJSBeforePayStart = webView.getInjectedJSBeforePayStart();
        if (injectedJSBeforePayStart != null) {
            for (String js : injectedJSBeforePayStart) {
                doScript(webView, js);
            }
        }
    }

//    static void receivePostMessage(WebView webView) {
//        BootpayScript.callJavaScript(webView, BootpayConstant.message());
//    }

    static void renderWidget(BootpayWebView webView, Payload payload) {
//        String script = BootpayScript.createRenderWidgetScript(payload);
        webView.setInjectedJS(BootpayScript.renderWidget(payload));
        startWidget(webView);
    }

    static void requestWidgetPayment(BootpayWebView webView, Payload payload) {
        String updateScript = BootpayScript.updateWidget(payload, false);
        String requestScript = BootpayScript.requestPayment(payload);

//        BootpayScript.load(webView, updateScript);
//        BootpayScript.load(webView, requestScript);
        doScript(webView, updateScript);
        doScript(webView, requestScript);
    }

    static void removeFromParent(WebView webView, Activity activity) {
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            pauseWebView(webView);
            if (webView.getParent() != null) {
                ((ViewGroup) webView.getParent()).removeView(webView);
            }
        });
    }

    static void addToParent(WebView webView, Activity activity, ViewGroup parent) {
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            if (parent != null) parent.addView(webView);
            resumeWebView(webView);
        });
    }

    static void pauseWebView(WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.onPause();
        }
        webView.pauseTimers();
    }

    static void resumeWebView(WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.onResume();
        }
        webView.resumeTimers();
    }

    static void fadeOutWebView(WebView webView, long duration) {
        ViewPropertyAnimator animator = webView.animate();
        if (animator != null) animator.alpha(0).setDuration(duration);
    }

    static void invisibleWebView(WebView webView) {
        webView.setAlpha(0);
    }

    static void fadeInWebView(WebView webView, long duration) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ViewPropertyAnimator animator = webView.animate();
            if (animator != null) animator.alpha(1).setDuration(duration);
        }, 1000);
    }

    static public void resizeWebView(WebView webView, Double height) {
        Activity activity = (Activity) webView.getContext();
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            Log.d("bootpay", "resizeWebView: " + height);
            DisplayMetrics displayMetrics = webView.getResources().getDisplayMetrics();
            int heightInPx = (int) (height * displayMetrics.density);

            ViewGroup.LayoutParams params = webView.getLayoutParams();
            params.height = heightInPx;
            webView.setLayoutParams(params);
        });
    }

    static void fullSizeWebView(WebView webView) {
        Activity activity = (Activity) webView.getContext();
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            Log.d("bootpay", "fullSizeWebView");
            ViewGroup.LayoutParams params = webView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            webView.setLayoutParams(params);
        });
    }

//    public static class BootpayJavascriptBridge implements JSInterfaceBridge {
//        private final BootpayWebView webView;
//
//        public BootpayJavascriptBridge(BootpayWebView webView) {
//            this.webView = webView;
//        }
//
//        private BootpayPaymentResult paymentResult = BootpayPaymentResult.NONE;
//
//        @JavascriptInterface
//        @Override
//        public void error(String data) {
//            paymentResult = BootpayPaymentResult.ERROR;
//            if (webView.mExtEventListener != null) webView.mExtEventListener.onProgressShow(false);
//            if (webView.mEventListener != null) webView.mEventListener.onError(data);
//        }
//
//        @JavascriptInterface
//        @Override
//        public void close(String data) {
//            if (webView.mExtEventListener != null) webView.mExtEventListener.onProgressShow(false);
//            if (webView.isWidget) {
//                BootpayWidget.closeDialog((Activity) webView.getContext());
//            } else {
//                if (webView.mEventListener != null) webView.mEventListener.onClose();
//            }
//        }
//
//        @JavascriptInterface
//        @Override
//        public void cancel(String data) {
//            paymentResult = BootpayPaymentResult.CANCEL;
//            if (webView.mExtEventListener != null) webView.mExtEventListener.onProgressShow(false);
//            if (webView.mEventListener != null) webView.mEventListener.onCancel(data);
//
//            if (webView.payload != null && webView.payload.getExtra() != null && "popup".equals(webView.payload.getExtra().getOpenType())) {
//                close("");
//            }
//        }
//
//        @JavascriptInterface
//        @Override
//        public void issued(String data) {
//            if (webView.mExtEventListener != null) webView.mExtEventListener.onProgressShow(false);
//            if (webView.mEventListener != null) webView.mEventListener.onIssued(data);
//        }
//
//        @JavascriptInterface
//        @Override
//        public String confirm(String data) {
//            if (webView.mExtEventListener != null) webView.mExtEventListener.onProgressShow(true);
//            boolean goTransaction = false;
//            if (webView.mEventListener != null) goTransaction = webView.mEventListener.onConfirm(data);
//            if (goTransaction) BootpayWebViewHandler.transactionConfirm(webView);
//            return String.valueOf(goTransaction);
//        }
//
//        @JavascriptInterface
//        @Override
//        public void done(String data) {
//            paymentResult = BootpayPaymentResult.DONE;
//            if (webView.mExtEventListener != null) webView.mExtEventListener.onProgressShow(false);
//            if (webView.mEventListener != null) webView.mEventListener.onDone(data);
//        }
//
//        @JavascriptInterface
//        @Override
//        public void redirectEvent(String data) {
//            Log.d("bootpay", "redirectEvent: " + data);
//            if ("undefined".equals(data)) return;
//            try {
//                JSONObject json = new JSONObject(data);
//                String event = String.valueOf(json.get("event"));
//                switch (event) {
//                    case "error":
//                        paymentResult = BootpayPaymentResult.ERROR;
//                        error(data);
//                        if (!isDisplayError(webView)) close(data);
//                        break;
//                    case "close":
//                        close(data);
//                        removePaymentWindow(webView);
//                        break;
//                    case "cancel":
//                        paymentResult = BootpayPaymentResult.CANCEL;
//                        cancel(data);
//                        close(data);
//                        break;
//                    case "issued":
//                        issued(data);
//                        if (!isDisplaySuccess(webView)) close(data);
//                        break;
//                    case "confirm":
//                        confirm(data);
//                        break;
//                    case "done":
//                        paymentResult = BootpayPaymentResult.DONE;
//                        done(data);
//                        if (!isDisplaySuccess(webView)) close(data);
//                        else if (!webView.isWidget) {
//                            String dataString = String.valueOf(json.get("data"));
//                            JSONObject dataJson = new JSONObject(dataString);
//                            String methodOriginSymbol = String.valueOf(dataJson.get("method_origin_symbol"));
//                            if ("card_rebill_rest".equals(methodOriginSymbol)) {
//                                close(data);
//                            }
//                        }
//                        break;
//                    case "bootpayWidgetFullSizeScreen":
//                        BootpayWebViewHandler.invisibleWebView(webView);
//                        BootpayWidget.showDialog((Activity) webView.getContext());
//                        break;
//                    case "bootpayWidgetRevertScreen":
//                        BootpayWebViewHandler.invisibleWebView(webView);
//                        BootpayWidget.closeDialog((Activity) webView.getContext());
//                        break;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        private boolean isDisplaySuccess(BootpayWebView webView) {
//            if (webView.payload == null) return false;
//            if (webView.payload.getExtra() == null) return false;
//            return webView.payload.getExtra().isDisplaySuccessResult();
//        }
//
//        private boolean isDisplayError(BootpayWebView webView) {
//            if (webView.payload == null) return false;
//            if (webView.payload.getExtra() == null) return false;
//            return webView.payload.getExtra().isDisplayErrorResult();
//        }
//
//        private Handler handlerResize = new Handler(Looper.getMainLooper());
//        private Handler handlerReady = new Handler(Looper.getMainLooper());
//        private Handler handlerChangePayment = new Handler(Looper.getMainLooper());
//        private Handler handlerChangeAgreeTerm = new Handler(Looper.getMainLooper());
//        private boolean isProcessingEventResize = false;
//        private boolean isProcessingEventReady = false;
//        private boolean isProcessingEventChangePayment = false;
//        private boolean isProcessingEventChangeAgreeTerm = false;
//        private Runnable widgetResize = () -> this.isProcessingEventResize = false;
//        private Runnable widgetReady = () -> this.isProcessingEventReady = false;
//        private Runnable widgetChangePayment = () -> this.isProcessingEventChangePayment = false;
//        private Runnable widgetChangeAgreeTerm = () -> this.isProcessingEventChangeAgreeTerm = false;
//
//        private long DEBOUNCE_DELAY = 400;
//
//        private void debounceWidgetEvent(Handler handler, BootpayWidgetEvent widgetEvent, String data) {
//            Runnable resetEventRunnable = null;
//            boolean isProcessingEvent = false;
//
//            switch (widgetEvent) {
//                case RESIZE:
//                    isProcessingEvent = isProcessingEventResize;
//                    resetEventRunnable = widgetResize;
//                    break;
//                case READY:
//                    isProcessingEvent = isProcessingEventReady;
//                    resetEventRunnable = widgetReady;
//                    break;
//                case CHANGE_PAYMENT:
//                    isProcessingEvent = isProcessingEventChangePayment;
//                    resetEventRunnable = widgetChangePayment;
//                    break;
//                case CHANGE_AGREE_TERM:
//                    isProcessingEvent = isProcessingEventChangeAgreeTerm;
//                    resetEventRunnable = widgetChangeAgreeTerm;
//                    break;
//                default:
//                    throw new IllegalArgumentException("Invalid event ID");
//            }
//
//            if (!isProcessingEvent) {
//                handleEvent(widgetEvent, data);
//                setProcessingEvent(widgetEvent, true);
//            }
//
//            handler.removeCallbacks(resetEventRunnable);
//            handler.postDelayed(resetEventRunnable, DEBOUNCE_DELAY);
//        }
//
//        Double webViewHeight = 0.0;
//
//        private void handleEvent(BootpayWidgetEvent widgetEvent, String data) {
//            if (data == null || data.length() == 0) {
//                if (widgetEvent == BootpayWidgetEvent.READY) {
//                    if (webView.mWidgetEventListener != null) webView.mWidgetEventListener.onWidgetReady();
//                }
//                return;
//            }
//            try {
//                JSONObject obj = new JSONObject(data);
//                switch (widgetEvent) {
//                    case RESIZE:
//                        Double height = obj.getDouble("height");
//                        if (webViewHeight.equals(height)) return;
//                        if (webView.mWidgetEventListener != null)
//                            webView.mWidgetEventListener.onWidgetResize(height);
//                        BootpayWebViewHandler.resizeWebView(webView, height);
//                        break;
//                    case READY:
//                        if (webView.mWidgetEventListener != null) webView.mWidgetEventListener.onWidgetReady();
//                        break;
//                    case CHANGE_PAYMENT:
//                        if (webView.mWidgetEventListener != null)
//                            webView.mWidgetEventListener.onWidgetChangePayment(WidgetData.fromJson(data));
//                        break;
//                    case CHANGE_AGREE_TERM:
//                        if (webView.mWidgetEventListener != null)
//                            webView.mWidgetEventListener.onWidgetChangeAgreeTerm(WidgetData.fromJson(data));
//                        break;
//                    default:
//                        throw new IllegalArgumentException("Invalid event ID");
//                }
//            } catch (Throwable t) {
//                Log.e("bootpay", "Could not parse malformed JSON: \"" + data + "\"");
//            }
//        }
//
//        private void setProcessingEvent(BootpayWidgetEvent widgetEvent, boolean isProcessing) {
//            switch (widgetEvent) {
//                case RESIZE:
//                    isProcessingEventResize = isProcessing;
//                    break;
//                case READY:
//                    isProcessingEventReady = isProcessing;
//                    break;
//                case CHANGE_PAYMENT:
//                    isProcessingEventChangePayment = isProcessing;
//                    break;
//                case CHANGE_AGREE_TERM:
//                    isProcessingEventChangeAgreeTerm = isProcessing;
//                    break;
//            }
//        }
//
//        @JavascriptInterface
//        @Override
//        public void readyWatch() {
//            debounceWidgetEvent(handlerReady, BootpayWidgetEvent.READY, "");
//        }
//
//        @JavascriptInterface
//        @Override
//        public void resizeWatch(String data) {
//            debounceWidgetEvent(handlerResize, BootpayWidgetEvent.RESIZE, data);
//        }
//
//        @JavascriptInterface
//        @Override
//        public void changeMethodWatch(String data) {
//            debounceWidgetEvent(handlerChangePayment, BootpayWidgetEvent.CHANGE_PAYMENT, data);
//        }
//
//        @JavascriptInterface
//        @Override
//        public void changeTermsWatch(String data) {
//            debounceWidgetEvent(handlerChangeAgreeTerm, BootpayWidgetEvent.CHANGE_AGREE_TERM, data);
//        }
//    }
}
