package kr.co.bootpay.android;

import android.app.Activity;

import kr.co.bootpay.android.constants.BootpayConstant;


public class Bootpay {
    protected  static BootpayBuilder builder;

    /**
     * WebView 결제 환경을 설정합니다. 기본값은 production 입니다.
     * @param mode "development" | "stage" | "production" (그 외 값은 production 으로 fallback)
     */
    public static void setEnvironmentMode(String mode) {
        if ("development".equals(mode) || "stage".equals(mode) || "production".equals(mode)) {
            BootpayConstant.ENVIRONMENT_MODE = mode;
        } else {
            BootpayConstant.ENVIRONMENT_MODE = "production";
        }
    }

//    public static BootpayBuilder init(Context context) {
//        return builder = new BootpayBuilder(context);
//    }

    public static BootpayBuilder init(Activity activity) {
        return builder = new BootpayBuilder(activity);
    }

    public static BootpayBuilder init(android.app.FragmentManager fragmentManager) {
        return builder = new BootpayBuilder(fragmentManager);
    }

    public static BootpayBuilder init(androidx.fragment.app.FragmentManager fragmentManagerX) {
        return builder = new BootpayBuilder(fragmentManagerX);
    }


    public static void transactionConfirm() {
        if (builder != null) builder.transactionConfirm();
    }

    public static void removePaymentWindow() {
        if (builder != null) builder.removePaymentWindow();
    }

    public static void dismiss() {
        if (builder != null) builder.dismissWindow();
    }
}

