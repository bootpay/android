package kr.co.bootpay.android.script;

import java.util.List;

import kr.co.bootpay.android.constants.BootpayBuildConfig;
import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.models.Payload;

public class BootpayScript {

    public static String transactionConfirm() {
        return BootpayConstant.loadParams(
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
    }

    public static String renderWidget(Payload payload) {
        // BootpayWidget이 정의될 때까지 기다린 후 실행
        String waitForBootpayWidget = "function waitForBootpayWidget(callback) { " +
                "if (typeof BootpayWidget !== 'undefined') { callback(); } " +
                "else { setTimeout(function() { waitForBootpayWidget(callback); }, 50); } " +
                "} ";

        // 참고: setEnvironmentMode('development')는 dev-widget 서버가 X-Frame-Options deny 설정으로
        // iframe 로드를 거부하므로 사용하지 않음
        return BootpayConstant.loadParams(
                waitForBootpayWidget,
                "waitForBootpayWidget(function() { ",
                BootpayConstant.readyWatch(),
                BootpayConstant.resizeWatch(),
                BootpayConstant.changeMethodWatch(),
                BootpayConstant.changeTermsWatch(),
                BootpayConstant.close(),
                "BootpayWidget.render('#bootpay-widget', ",
                payload.toJsonUnderscore(),
                ");",
                "});"
        );
    }

    public static String requestPayment(Payload payload) {
        return BootpayConstant.loadParams(
                "BootpayWidget.requestPayment(",
                payload.toJsonUnderscore(),
                ")",
                ".then( function (res) {",
                BootpayConstant.confirm(),
                BootpayConstant.issued(),
                BootpayConstant.done(),
                BootpayConstant.redirect(),
                "}, function (res) {",
                BootpayConstant.error(),
                BootpayConstant.cancel(),
                BootpayConstant.redirect(),
                "})"
        );
    }

    public static String updateWidget(Payload payload, boolean refresh) {
        return BootpayConstant.loadParams(
                "Bootpay.setDevice('ANDROID');",
                "Bootpay.setVersion('" + BootpayBuildConfig.VERSION + "', 'android');",
                "BootpayWidget.update(" + "",
                payload.toJsonUnderscore(),
                String.format(", '%s');", refresh ? "true" : "false")
        );
    }

    public static String removePaymentWindow() {
        return BootpayConstant.loadParams(
                "Bootpay.destroy();"
        );
    }
}
