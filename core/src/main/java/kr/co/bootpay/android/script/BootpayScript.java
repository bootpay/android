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
        if (BootpayBuildConfig.DEBUG) {
            return BootpayConstant.loadParams(
                    "BootpayWidget.setEnvironmentMode('development');",
                    BootpayConstant.readyWatch(),
                    BootpayConstant.resizeWatch(),
                    BootpayConstant.changeMethodWatch(),
                    BootpayConstant.changeTermsWatch(),
                    BootpayConstant.close(),
                    "BootpayWidget.render('#bootpay-widget', ",
                    payload.toJsonUnderscore(),
                    ")"
            );
        } else {
            return BootpayConstant.loadParams(
                    "BootpayWidget.render('#bootpay-widget', ",
                    payload.toJsonUnderscore(),
                    ")"
            );
        }
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
