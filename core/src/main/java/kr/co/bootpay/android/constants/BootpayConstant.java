package kr.co.bootpay.android.constants;

import static kr.co.bootpay.android.constants.BootpayBuildConfig.VERSION;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.pref.UserInfo;

public class BootpayConstant {
    public static final String CDN_URL = "https://webview.bootpay.co.kr/5.0.0-rc.13/";
    public static final String WIDGET_URL = CDN_URL + "widget.html";
//    public static final String CDN_URL = "https://staging-webview.bootpay.co.kr/4.2.7";


    public static final int REQUEST_TYPE_NONE = 0; //일반 결제
    public static final int REQUEST_TYPE_PAYMENT = 1; //일반 결제
    public static final int REQUEST_TYPE_SUBSCRIPT = 2; //정기 결제
    public static final int REQUEST_TYPE_AUTH = 3; //본인 인증

    public static final String getJSPay(@Nullable Payload payload, int requestType) {
        if(payload == null) return "";
//        String payloadJson = payload.toJsonUnderscore();


        String requestMethod = "requestPayment";
        if(requestType == REQUEST_TYPE_PAYMENT) {
            requestMethod = "requestPayment";
        } else if(requestType == REQUEST_TYPE_SUBSCRIPT) {
            requestMethod = "requestSubscription";
            if("".equals(payload.getSubscriptionId())) {payload.setSubscriptionId(payload.getOrderId());}
        } else if(requestType == REQUEST_TYPE_AUTH) {
            requestMethod = "requestAuthentication";
            if("".equals(payload.getAuthenticationId())) {  payload.setAuthenticationId(payload.getOrderId()); }
        }

        return loadParams(
                "Bootpay." + requestMethod + "(",
                payload.toJsonUnderscore(),
                ")",
                ".then( function (res) {",
                confirm(),
                issued(),
                done(),
                redirect(), //success_result 닫기 이벤트가 일로 간다
                "}, function (res) {",
                error(),
                cancel(),
                redirect(),
                "})"
        );
    }

    public static String loadParams(String... script) {
        StringBuilder builder = new StringBuilder();
        for (String s : script) builder.append(s);
        builder.append(";");
        return builder.toString();
    }

    public static String confirm() { return "if (res.event === 'confirm') { " + BootpayBuildConfig.JSInterfaceBridgeName + ".confirm(JSON.stringify(res)); }"; }

    public static String done()  { return "else if (res.event === 'done') { " + BootpayBuildConfig.JSInterfaceBridgeName + ".done(JSON.stringify(res)); }"; }

    public static String issued() { return "else if (res.event === 'issued') { " + BootpayBuildConfig.JSInterfaceBridgeName + ".issued(JSON.stringify(res)); }"; }

    public static String error() { return "if (res.event === 'error') { " + BootpayBuildConfig.JSInterfaceBridgeName + ".error(JSON.stringify(res)); }"; }

    public static String cancel() { return  "else if (res.event === 'cancel') { " + BootpayBuildConfig.JSInterfaceBridgeName + ".cancel(JSON.stringify(res)); }"; }

    public static String close() { return  ""; }
//    public static String close() { return  "document.addEventListener('bootpayclose', function (e) { A.close('결제창이 닫혔습니다'); });"; }
//public static String close() { return  "document.addEventListener('bootpayclose', function (e) { " + BootpayBuildConfig.JSInterfaceBridgeName + ".close('결제창이 닫혔습니다');  });"; }

    public static String redirect() { return "else if (res.event === 'redirectEvent') { " + BootpayBuildConfig.JSInterfaceBridgeName + ".redirectEvent(JSON.stringify(res)); }"; }

    public static String message() { return  "document.addEventListener('message', function (e) { " + BootpayBuildConfig.JSInterfaceBridgeName + ".message(JSON.stringify(e)); });"; }

//    public static String readyWatch() { return "document.addEventListener('bootpay-widget-ready', function (e) { " + BootpayBuildConfig.JSInterfaceBridgeName + ".readyWatch(JSON.stringify(e.detail)); });"; }
    public static String readyWatch() { return "document.addEventListener('bootpay-widget-ready', function (e) { " +  BootpayBuildConfig.JSInterfaceBridgeName + ".readyWatch(); });"; }
//    public static String readyWatch() { return "document.addEventListener('bootpay-widget-ready', function (e) { alert('readyWatch') });"; }
//    public static String readyWatch() { return "document.addEventListener('bootpay-widget-ready', function (e) { alert(e.detail); });"; }

//    public static String resizeWatch() { return "document.addEventListener('bootpay-widget-resize', function (e) { alert('resizeWatch') });"; }
    public static String resizeWatch() { return "document.addEventListener('bootpay-widget-resize', function (e) { " + BootpayBuildConfig.JSInterfaceBridgeName + ".resizeWatch(JSON.stringify(e.detail));  });"; }

//    public static String changeMethodWatch() { return "document.addEventListener('bootpay-widget-change-payment', function (e) { alert('changeMethodWatch') });"; }
    public static String changeMethodWatch() { return "document.addEventListener('bootpay-widget-change-payment', function (e) { " + BootpayBuildConfig.JSInterfaceBridgeName + ".changeMethodWatch(JSON.stringify(e.detail)); });"; }
//    public static String changeMethodWatch() { return ""; }

//    public static String changeTermsWatch() { return "document.addEventListener('bootpay-widget-change-terms', function (e) { alert('changeTermsWatch') });"; }
    public static String changeTermsWatch() { return "document.addEventListener('bootpay-widget-change-terms', function (e) { " + BootpayBuildConfig.JSInterfaceBridgeName + ".changeTermsWatch(JSON.stringify(e.detail)); });"; }



//    public static String message() { return  "window.BootpayError = function (e) {  Bootpay.error(JSON.stringify(e)); };"; }

//    protected static String confirm() { return  ".confirm(function(data){Android.confirm(JSON.stringify(data));})"; }
//    protected static String cancel() { return  ".cancel(function(data){Android.cancel(JSON.stringify(data));})"; }

//    protected static String done() { return  ".done(function(data){Android.done(JSON.stringify(data));})"; }

    /**
     *         android: 100,
     *         android_react: 101,
     *         android_flutter: 102,
     *         android_unity: 103,
     *         ios: 200,
     *         ios_react: 201,
     *         ios_flutter: 202,
     *         ios_unity: 203
     * @param context
     * @return
     */
    public static final List<String> getJSBeforePayStart(Context context) {
        List<String> scripts = new ArrayList<>();
        scripts.add("Bootpay.setVersion('" + VERSION + "', 'android');");
        scripts.add("Bootpay.setDevice('ANDROID');");
        scripts.add(getAnalyticsData(context));
        if(BootpayBuildConfig.DEBUG) scripts.add("Bootpay.setEnvironmentMode('development');");
        return scripts;
    }

    private static String getAnalyticsData(Context context) {
        return String.format(Locale.KOREA,
                "window.Bootpay.\\$analytics.setAnalyticsData({uuid:'%s',sk:'%s',sk_time:'%d',time:'%d'});"
                , UserInfo.getInstance(context).getBootpayUuid()
                , UserInfo.getInstance(context).getBootpaySk()
                , UserInfo.getInstance(context).getBootpayLastTime()
                , System.currentTimeMillis() - UserInfo.getInstance(context).getBootpayLastTime());
    }
}
