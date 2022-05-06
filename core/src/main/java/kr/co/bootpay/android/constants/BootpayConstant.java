package kr.co.bootpay.android.constants;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.pref.UserInfo;

public class BootpayConstant {
    public static final String CDN_URL = "https://webview.bootpay.co.kr/4.0.0";

    public static final int REQUEST_TYPE_PAYMENT = 1; //일반 결제
    public static final int REQUEST_TYPE_SUBSCRIPT = 2; //정기 결제
    public static final int REQUEST_TYPE_AUTH = 3; //본인 인증

    public static final String getJSPay(@Nullable Payload payload, int requestType) {
        if(payload == null) return "";

        String requestMethod = "requestPayment";
        if(requestType == REQUEST_TYPE_PAYMENT) {
            requestMethod = "requestPayment";
        } else if(requestType == REQUEST_TYPE_SUBSCRIPT) {
            requestMethod = "requestSubscription";
            if("".equals(payload.getSubscriptionId())) {  payload.setSubscriptionId(payload.getOrderId()); }
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
                "}, function (res) {",
                error(),
                cancel(),
                "})"
        );
    }

    public static String loadParams(String... script) {
        StringBuilder builder = new StringBuilder();
        for (String s : script) builder.append(s);
        builder.append(";");
        return builder.toString();
    }

    public static String confirm() { return "if (res.event === 'confirm') { Android.confirm(JSON.stringify(res)); }"; }

    public static String done()  { return "else if (res.event === 'done') { Android.done(JSON.stringify(res)); }"; }

    public static String issued() { return "else if (res.event === 'issued') { Android.issued(JSON.stringify(res)); }"; }

    public static String error() { return "if (res.event === 'error') { Android.error(JSON.stringify(res)); }"; }

    public static String cancel() { return  "else if (res.event === 'cancel') { Android.cancel(JSON.stringify(res)); }"; }

    public static String close() { return  "document.addEventListener('bootpayclose', function (e) { Android.close('결제창이 닫혔습니다'); });"; }

//    protected static String confirm() { return  ".confirm(function(data){Android.confirm(JSON.stringify(data));})"; }
//    protected static String cancel() { return  ".cancel(function(data){Android.cancel(JSON.stringify(data));})"; }

//    protected static String done() { return  ".done(function(data){Android.done(JSON.stringify(data));})"; }

    public static final List<String> getJSBeforePayStart(Context context) {
        List<String> scripts = new ArrayList<>();
        scripts.add("Bootpay.setDevice('ANDROID');");
        scripts.add(getAnalyticsData(context));
        if(BootpayBuildConfig.DEBUG) scripts.add("Bootpay.setEnvironmentMode('development');");
        scripts.add(close());
//        if(quickPopup) scripts.add("BootPay.startQuickPopup();");
        return scripts;
    }

    private static String getAnalyticsData(Context context) {
        return String.format(Locale.KOREA,
                "Bootpay.setAnalyticsData({uuid:'%s',sk:'%s',sk_time:'%d',time:'%d'});"
                , UserInfo.getInstance(context).getBootpayUuid()
                , UserInfo.getInstance(context).getBootpaySk()
                , UserInfo.getInstance(context).getBootpayLastTime()
                , System.currentTimeMillis() - UserInfo.getInstance(context).getBootpayLastTime());
    }
}
