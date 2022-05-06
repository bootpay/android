package kr.co.bootpay.android.constants;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.pref.UserInfo;

@Deprecated
public class BootpayConstantDeprecated {
    public static final String CDN_URL = "https://inapp.bootpay.co.kr/3.3.3/production.html";

    public static final String getJSPay(@Nullable Payload payload) {
        if(payload == null) return "";

        return loadParams(
                "BootPay.request(",
                payload.toJsonUnderscore(),
                ")",
                error(),
                ready(),
                close(),
                confirm(),
                cancel(),
                done()
        );
    }

    protected static String loadParams(String... script) {
        StringBuilder builder = new StringBuilder();
        for (String s : script) builder.append(s);
        builder.append(";");
        return builder.toString();
    }

    protected static String error() { return ".error(function(data){Android.error(JSON.stringify(data));})"; }

    protected static String ready() { return ".ready(function(data){Android.ready(JSON.stringify(data));})"; }

    protected static String close() { return  ".close(function(data){Android.close('결제창이 닫혔습니다');})"; }

    protected static String confirm() { return  ".confirm(function(data){Android.confirm(JSON.stringify(data));})"; }

    protected static String cancel() { return  ".cancel(function(data){Android.cancel(JSON.stringify(data));})"; }

    protected static String done() { return  ".done(function(data){Android.done(JSON.stringify(data));})"; }

    public static final List<String> getJSBeforePayStart(Context context, boolean quickPopup) {
        List<String> scripts = new ArrayList<>();
        scripts.add("BootPay.setDevice('ANDROID');");
        scripts.add(getAnalyticsData(context));
        if(BootpayBuildConfig.DEBUG) scripts.add("BootPay.setMode('development');");
        if(quickPopup) scripts.add("BootPay.startQuickPopup();");
        return scripts;
    }

    private static String getAnalyticsData(Context context) {
        return String.format(Locale.KOREA,
                "BootPay.setAnalyticsData({uuid:'%s',sk:'%s',sk_time:'%d',time:'%d'});"
                , UserInfo.getInstance(context).getBootpayUuid()
                , UserInfo.getInstance(context).getBootpaySk()
                , UserInfo.getInstance(context).getBootpayLastTime()
                , System.currentTimeMillis() - UserInfo.getInstance(context).getBootpayLastTime());
    }
}
