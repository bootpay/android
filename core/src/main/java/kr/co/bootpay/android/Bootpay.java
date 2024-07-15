package kr.co.bootpay.android;

import android.app.Activity;


public class Bootpay {
    protected  static BootpayBuilder builder;

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

