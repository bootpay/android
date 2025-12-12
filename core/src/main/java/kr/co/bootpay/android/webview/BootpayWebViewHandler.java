package kr.co.bootpay.android.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.List;
import java.util.Locale;

import kr.co.bootpay.android.constants.BootpayBuildConfig;
import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.script.BootpayScript;

public class BootpayWebViewHandler {

    static void payWebSettings(WebView webView, Context context) {
        WebSettings settings = webView.getSettings();
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

        if (BootpayBuildConfig.DEBUG) {
            context.getApplicationInfo().flags &= context.getApplicationInfo().FLAG_DEBUGGABLE;
            if (0 != context.getApplicationInfo().flags) webView.setWebContentsDebuggingEnabled(true);
        }

        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);

        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
    }

    static void evaluateJavascriptWithFallback(WebView webView, String script) {
        webView.evaluateJavascript(script, null);
    }

    static void doScript(WebView webView, String script) {
        webView.post(() -> webView.loadUrl(String.format(Locale.KOREA, "javascript:(function(){%s})()", script)));
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
        Log.d("bootpay", "[BootpayWebViewHandler] startWidget called, loading URL: " + BootpayConstant.WIDGET_URL);
        // Use post to ensure WebView is properly attached to window before loading URL
        webView.post(() -> {
            Log.d("bootpay", "[BootpayWebViewHandler] startWidget - posting loadUrl");
            webView.loadUrl(BootpayConstant.WIDGET_URL);
            Log.d("bootpay", "[BootpayWebViewHandler] startWidget - loadUrl posted");
        });
    }

    static void transactionConfirm(WebView webView) {
        doScript(webView, BootpayScript.transactionConfirm());
    }

    static void callInjectedJavaScript(BootpayWebView webView) {
        String js = webView.getInjectedJS();
        Log.d("bootpay", "[BootpayWebViewHandler] callInjectedJavaScript called, js length=" + (js != null ? js.length() : 0));
        if (js != null && !js.isEmpty()) {
            Log.d("bootpay", "[BootpayWebViewHandler] Executing JS script: " + js.substring(0, Math.min(js.length(), 200)) + "...");
            doScript(webView, js);
        } else {
            Log.w("bootpay", "[BootpayWebViewHandler] No JS to inject!");
        }
    }

    static void callInjectedJavaScriptBeforePayStart(BootpayWebView webView) {
        List<String> injectedJSBeforePayStart = webView.getInjectedJSBeforePayStart();
        Log.d("bootpay", "[BootpayWebViewHandler] callInjectedJavaScriptBeforePayStart called, scripts count=" + (injectedJSBeforePayStart != null ? injectedJSBeforePayStart.size() : 0));
        if (injectedJSBeforePayStart != null) {
            for (String js : injectedJSBeforePayStart) {
                Log.d("bootpay", "[BootpayWebViewHandler] Executing BeforePayStart script: " + js.substring(0, Math.min(js.length(), 50)) + "...");
                doScript(webView, js);
            }
        }
    }

//    static void receivePostMessage(WebView webView) {
//        BootpayScript.callJavaScript(webView, BootpayConstant.message());
//    }

    static void renderWidget(BootpayWebView webView, Payload payload) {
        Log.d("bootpay", "[BootpayWebViewHandler] renderWidget called, webView=" + webView);
        String js = BootpayScript.renderWidget(payload);
        webView.setInjectedJS(js);
        // setVersion, setDevice 등 기본 설정 스크립트 설정
        webView.setInjectedJSBeforePayStart(BootpayConstant.getJSBeforePayStart(webView.getContext()));
        Log.d("bootpay", "[BootpayWebViewHandler] calling startWidget");
        startWidget(webView);
    }

    /**
     * 위젯 리렌더링 - widget.html 페이지로 이동 후 JS 주입
     * 결제 페이지에서 뒤로가기 시 사용
     */
    static void rerenderWidget(BootpayWebView webView, Payload payload) {
        Log.d("bootpay", "[BootpayWebViewHandler] rerenderWidget called, webView=" + webView);
        String js = BootpayScript.renderWidget(payload);
        webView.setInjectedJS(js);
        // setVersion, setDevice 등 기본 설정 스크립트 설정
        webView.setInjectedJSBeforePayStart(BootpayConstant.getJSBeforePayStart(webView.getContext()));
        // 위젯용 플래그 설정 (onPageFinished에서 JS 주입용)
        webView.setIsRerendering(true);
        Log.d("bootpay", "[BootpayWebViewHandler] rerenderWidget - loading widget URL");
        // 테스트: www.naver.com 로드
        webView.post(() -> {
            Log.d("bootpay", "[BootpayWebViewHandler] rerenderWidget - posting loadUrl (naver test)");
            webView.loadUrl("https://www.naver.com");
        });
    }

    public static void requestWidgetPayment(BootpayWebView webView, Payload payload) {
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
            if (parent != null) {
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                parent.addView(webView, params);
            }
            resumeWebView(webView);
        });
    }

    static void pauseWebView(WebView webView) {
        webView.onPause();
        webView.pauseTimers();
    }

    static void resumeWebView(WebView webView) {
        webView.onResume();
        webView.resumeTimers();
    }

    static void fadeOutWebView(WebView webView, long duration) {
        ViewPropertyAnimator animator = webView.animate();
        if (animator != null) animator.alpha(0).setDuration(duration);
    }

    static void invisibleWebView(WebView webView) {
        webView.setAlpha(0);
    }

    static void visibleWebView(WebView webView) {
        webView.setAlpha(1);
    }

    static void fadeInWebView(WebView webView, long duration) {
        Handler handler = new Handler(Looper.getMainLooper());
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

    /**
     * 위젯 업데이트 (iOS 패리티)
     * @param webView WebView
     * @param payload Payload
     * @param refresh true면 위젯 새로고침
     */
    public static void updateWidget(BootpayWebView webView, Payload payload, boolean refresh) {
        String updateScript = BootpayScript.updateWidget(payload, refresh);
        doScript(webView, updateScript);
    }
}
