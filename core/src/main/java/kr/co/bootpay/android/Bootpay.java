package kr.co.bootpay.android;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;


public class Bootpay {
    protected  static BootpayBuilder builder;

//    public static BootpayBuilder init(Context context) {
//        return builder = new BootpayBuilder(context);
//    }

    /**
     * Initialize Bootpay with an Activity.
     * For AppCompatActivity/FragmentActivity, uses Activity-based payment flow (recommended).
     * For regular Activity, falls back to Dialog-based flow.
     */
    public static BootpayBuilder init(Activity activity) {
        // FragmentActivity 인스턴스인 경우 Activity 기반 결제 사용
        if (activity instanceof FragmentActivity) {
            return builder = new BootpayBuilder((FragmentActivity) activity);
        }
        return builder = new BootpayBuilder(activity);
    }

    /**
     * @deprecated Use {@link #init(Activity)} instead.
     * Dialog-based approach has lifecycle issues with external app transitions.
     */
    @Deprecated
    public static BootpayBuilder init(android.app.FragmentManager fragmentManager) {
        return builder = new BootpayBuilder(fragmentManager);
    }

    /**
     * @deprecated Use {@link #init(Activity)} instead.
     * Dialog-based approach has lifecycle issues with external app transitions.
     */
    @Deprecated
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

