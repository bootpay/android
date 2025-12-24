package kr.co.bootpay.android.commerce;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.co.bootpay.android.constants.BootpayBuildConfig;
import kr.co.bootpay.android.constants.BootpayConstant;

/**
 * Commerce 결제용 WebView
 */
public class CommerceWebView extends WebView {
    private static final String TAG = "CommerceWebView";
    private static final String BRIDGE_NAME = "Android";

    private CommercePayload payload;
    private CommerceEventListener eventListener;
    private OnProgressListener progressListener;

    private Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable closeRunnable;

    public CommerceWebView(Context context) {
        super(context);
        initWebView(context);
    }

    public CommerceWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebView(context);
    }

    public CommerceWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebView(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(Context context) {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }

        // 웹 디버깅 활성화 (개발용)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BootpayBuildConfig.DEBUG);
        }

        setWebViewClient(new CommerceWebViewClient());
        setWebChromeClient(new WebChromeClient());

        addJavascriptInterface(new CommerceJSBridge(), BRIDGE_NAME);
    }

    public void setPayload(CommercePayload payload) {
        this.payload = payload;
    }

    public void setEventListener(CommerceEventListener listener) {
        this.eventListener = listener;
    }

    public void setProgressListener(OnProgressListener listener) {
        this.progressListener = listener;
    }

    /**
     * Commerce 페이지 로드 시작
     */
    public void startCommerce() {
        Log.d(TAG, "Loading Commerce URL: " + BootpayConstant.COMMERCE_URL);
        loadUrl(BootpayConstant.COMMERCE_URL);
    }

    /**
     * Commerce SDK JavaScript 생성
     */
    private String getJSCommerceCheckout(CommercePayload payload) {
        StringBuilder scripts = new StringBuilder();

        // 환경 모드 설정
        String envMode = BootpayCommerce.getInstance().getEnvironmentMode();
        scripts.append("BootpayCommerce.setEnvironmentMode('").append(envMode).append("');");

        // 로그 레벨 설정 (개발 환경에서만)
        if ("development".equals(envMode)) {
            scripts.append("BootpayCommerce.setLogLevel(1);");
        }

        // close 이벤트 리스너 등록
        scripts.append("document.addEventListener('bootpayclose', function(e) { Android.close(); });");

        // payload JSON
        String payloadJSON = payload.toJSONString();
        Log.d(TAG, "Payload JSON: " + payloadJSON);

        // requestCheckout 호출
        scripts.append("BootpayCommerce.requestCheckout(")
                .append(payloadJSON)
                .append(")")
                .append(".then(function(res) {")
                .append("  console.log('[BootpayCommerce] requestCheckout response:', res);")
                .append("  Android.handleResponse(JSON.stringify(res));")
                .append("}).catch(function(err) {")
                .append("  console.error('[BootpayCommerce] requestCheckout error:', err);")
                .append("  Android.handleError(JSON.stringify({event: 'error', data: err}));")
                .append("});");

        String fullScript = scripts.toString();
        Log.d(TAG, "Full JS Script: " + fullScript);

        return fullScript;
    }

    /**
     * JavaScript 주입
     */
    private void injectJavaScript() {
        if (payload == null) return;

        String script = getJSCommerceCheckout(payload);
        evaluateJavascript(script, null);
        Log.d(TAG, "requestCheckout executed");
    }

    /**
     * Debounce된 close 호출
     */
    private void debounceClose() {
        if (closeRunnable != null) {
            debounceHandler.removeCallbacks(closeRunnable);
        }
        closeRunnable = () -> {
            if (eventListener != null) {
                eventListener.onClose();
            }
            BootpayCommerce.clearInstance();
        };
        debounceHandler.postDelayed(closeRunnable, 500);
    }

    /**
     * WebView Client
     */
    private class CommerceWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (progressListener != null) {
                progressListener.onProgress(true);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "Page finished: " + url);

            // Commerce 페이지 로드 완료 시 JavaScript 주입
            if (url != null && url.contains("webview.bootpay.co.kr/commerce")) {
                injectJavaScript();
            }

            if (progressListener != null) {
                progressListener.onProgress(false);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            Log.d(TAG, "shouldOverrideUrlLoading: " + url);

            // redirect_url 콜백 처리
            if (url.contains("api.bootpay.co.kr/v2/callback")) {
                Log.d(TAG, "Callback URL detected, handling...");
                handleCallbackURL(request.getUrl());
                return true;
            }

            // iTunes URL (iOS에서 사용)
            if (isItunesURL(url)) {
                startAppToApp(Uri.parse(url));
                return true;
            }

            // about:blank
            if (url.startsWith("about:blank")) {
                return false;
            }

            // 비 HTTP 스킴 (앱 스킴)
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                startAppToApp(Uri.parse(url));
                return true;
            }

            return false;
        }

        private boolean isItunesURL(String url) {
            return url != null && url.contains("//itunes.apple.com/");
        }
    }

    /**
     * Callback URL 처리
     */
    private void handleCallbackURL(Uri uri) {
        Map<String, Object> data = new HashMap<>();

        // Query parameters 파싱
        for (String paramName : uri.getQueryParameterNames()) {
            String value = uri.getQueryParameter(paramName);
            if (value != null) {
                // metadata는 JSON 문자열이므로 파싱 시도
                if ("metadata".equals(paramName)) {
                    try {
                        String decodedValue = URLDecoder.decode(value, "UTF-8");
                        JSONObject metadataJson = new JSONObject(decodedValue);
                        data.put("metadata", jsonToMap(metadataJson));
                    } catch (Exception e) {
                        data.put(paramName, value);
                    }
                } else {
                    data.put(paramName, value);
                }
            }
        }

        Log.d(TAG, "Callback data: " + data);

        // event에 따라 콜백 호출
        String event = (String) data.get("event");
        if (event == null) event = "";

        if (progressListener != null) {
            progressListener.onProgress(false);
        }

        switch (event) {
            case "done":
                if (eventListener != null) eventListener.onDone(data);
                debounceClose();
                BootpayCommerce.removePaymentWindow();
                break;
            case "issued":
                if (eventListener != null) eventListener.onIssued(data);
                debounceClose();
                BootpayCommerce.removePaymentWindow();
                break;
            case "cancel":
                if (eventListener != null) eventListener.onCancel(data);
                debounceClose();
                BootpayCommerce.removePaymentWindow();
                break;
            case "error":
                if (eventListener != null) eventListener.onError(data);
                debounceClose();
                BootpayCommerce.removePaymentWindow();
                break;
            default:
                // event가 없으면 receipt_id로 판단
                if (data.containsKey("receipt_id")) {
                    if (eventListener != null) eventListener.onDone(data);
                } else {
                    if (eventListener != null) eventListener.onCancel(data);
                }
                debounceClose();
                BootpayCommerce.removePaymentWindow();
                break;
        }
    }

    /**
     * JSON을 Map으로 변환
     */
    private Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = json.get(key);
            if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * JavaScript Bridge
     */
    private class CommerceJSBridge {
        @JavascriptInterface
        public void handleResponse(String data) {
            Log.d(TAG, "handleResponse: " + data);
            if (data == null || data.isEmpty()) return;

            try {
                JSONObject json = new JSONObject(data);
                Map<String, Object> dataMap = jsonToMap(json);

                String event = json.optString("event", "");

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressListener != null) {
                        progressListener.onProgress(false);
                    }

                    switch (event) {
                        case "done":
                            if (eventListener != null) eventListener.onDone(dataMap);
                            debounceClose();
                            BootpayCommerce.removePaymentWindow();
                            break;
                        case "issued":
                            if (eventListener != null) eventListener.onIssued(dataMap);
                            debounceClose();
                            BootpayCommerce.removePaymentWindow();
                            break;
                        case "cancel":
                            if (eventListener != null) eventListener.onCancel(dataMap);
                            debounceClose();
                            BootpayCommerce.removePaymentWindow();
                            break;
                        case "error":
                            if (eventListener != null) eventListener.onError(dataMap);
                            debounceClose();
                            BootpayCommerce.removePaymentWindow();
                            break;
                        default:
                            // event가 없으면 receipt_id로 판단
                            if (dataMap.containsKey("receipt_id")) {
                                if (eventListener != null) eventListener.onDone(dataMap);
                                debounceClose();
                                BootpayCommerce.removePaymentWindow();
                            }
                            break;
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse response: " + e.getMessage());
            }
        }

        @JavascriptInterface
        public void handleError(String data) {
            Log.e(TAG, "handleError: " + data);
            if (data == null || data.isEmpty()) return;

            try {
                JSONObject json = new JSONObject(data);
                Map<String, Object> dataMap = jsonToMap(json);

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (progressListener != null) {
                        progressListener.onProgress(false);
                    }

                    if (eventListener != null) {
                        eventListener.onError(dataMap);
                    }
                    debounceClose();
                    BootpayCommerce.removePaymentWindow();
                });
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse error: " + e.getMessage());
            }
        }

        @JavascriptInterface
        public void close() {
            Log.d(TAG, "close called from JS");
            new Handler(Looper.getMainLooper()).post(() -> {
                debounceClose();
                BootpayCommerce.removePaymentWindow();
            });
        }
    }

    /**
     * Progress 리스너 인터페이스
     */
    public interface OnProgressListener {
        void onProgress(boolean show);
    }

    /**
     * 외부 앱 실행 (앱 스킴 처리)
     */
    private void startAppToApp(Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start app: " + e.getMessage());
            // 앱이 설치되어 있지 않으면 Play Store로 이동 시도
            String packageName = getPackageNameFromScheme(uri.getScheme());
            if (packageName != null) {
                try {
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + packageName));
                    marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(marketIntent);
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to open Play Store: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * URL 스킴에서 패키지명 추출
     */
    private String getPackageNameFromScheme(String scheme) {
        if (scheme == null) return null;
        switch (scheme) {
            case "shinhan-sr-ansimclick": return "com.shcard.smartpay";
            case "kftc-bankpay": return "com.kftc.bankpay.android";
            case "ispmobile": return "kvp.jjy.MispAndroid320";
            case "hdcardappcardansimclick": return "com.hyundaicard.appcard";
            case "kb-acp": return "com.kbcard.kbkookmincard";
            case "mpocket.online.ansimclick": return "kr.co.samsungcard.mpocket";
            case "lotteappcard": return "com.lcacApp";
            case "cloudpay": return "com.hanaskcard.paycla";
            case "nhappvardansimclick": return "nh.smart.nhallonepay";
            case "citispay": return "kr.co.citibank.citimobile";
            case "kakaotalk": return "com.kakao.talk";
            case "supertoss": return "viva.republica.toss";
            case "payco": return "com.nhnent.payapp";
            default: return null;
        }
    }
}
