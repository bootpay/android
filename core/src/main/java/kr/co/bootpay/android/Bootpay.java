package kr.co.bootpay.android;

import android.app.Activity;
import android.content.Context;


public class Bootpay {
    protected  static BootpayBuilder builder;

    public static BootpayBuilder init(Activity activity, Context context) {
        return builder = new BootpayBuilder(activity, context);
    }

    public static BootpayBuilder init(android.app.FragmentManager fragmentManager, Context context) {
        return builder = new BootpayBuilder(fragmentManager, context);
    }

    public static BootpayBuilder init(androidx.fragment.app.FragmentManager fragmentManagerX, Context context) {
        return builder = new BootpayBuilder(fragmentManagerX, context);
    }


    public static void transactionConfirm(String data) {
        if (builder != null) builder.transactionConfirm(data);
    }

    public static void removePaymentWindow() {
        if (builder != null) builder.removePaymentWindow();
    }

    public static void dismissWindow() {
        if (builder != null) builder.dismissWindow();
    }
}

