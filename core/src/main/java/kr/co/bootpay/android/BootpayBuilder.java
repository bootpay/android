package kr.co.bootpay.android;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import kr.co.bootpay.android.webview.BootpayDialog;
import kr.co.bootpay.android.webview.BootpayDialogX;
import kr.co.bootpay.android.webview.BootpayPaymentActivity;
import kr.co.bootpay.android.api.BootpayInterface;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.Payload;

public class BootpayBuilder implements BootpayInterface {
    private Activity mActivity;
    private android.app.FragmentManager mFragmentManager;
    private androidx.fragment.app.FragmentManager mFragmentManagerX;

    private BootpayDialog mDialog;
    private BootpayDialogX mDialogX;

    private Payload mPayload;
    private BootpayEventListener mBootpayEventListener;

    // Activity-based approach flag (default: true)
    private boolean useActivity = true;

    public BootpayBuilder(Activity activity) {
        this.mActivity = activity;
        this.mFragmentManager = activity.getFragmentManager();
        this.mFragmentManagerX = null;
        this.mDialog = new BootpayDialog();
        this.mDialogX = null;
    }

    /**
     * @deprecated Use {@link #BootpayBuilder(Activity)} or {@link #BootpayBuilder(FragmentActivity)} instead.
     * Dialog-based approach has lifecycle issues with external app transitions.
     */
    @Deprecated
    public BootpayBuilder(android.app.FragmentManager fragmentManager) {
        this.mActivity = null;
        this.mFragmentManager = fragmentManager;
        this.mFragmentManagerX = null;
        this.mDialog = new BootpayDialog();
        this.mDialogX = null;
    }

    /**
     * Recommended constructor for FragmentActivity (AppCompatActivity).
     * Uses Activity-based payment flow for better lifecycle management.
     */
    public BootpayBuilder(FragmentActivity activity) {
        this.mActivity = activity;
        this.mFragmentManager = null;
        this.mFragmentManagerX = activity.getSupportFragmentManager();
        this.mDialog = null;
        this.mDialogX = new BootpayDialogX();
    }

    /**
     * @deprecated Use {@link #BootpayBuilder(FragmentActivity)} instead.
     * Dialog-based approach has lifecycle issues with external app transitions.
     */
    @Deprecated
    public BootpayBuilder(androidx.fragment.app.FragmentManager fragmentManager) {
        // Try to extract Activity from FragmentManager for Activity-based approach
        this.mActivity = null;
        this.mFragmentManager = null;
        this.mFragmentManagerX = fragmentManager;
        this.mDialog = null;
        this.mDialogX = new BootpayDialogX();
    }

    /**
     * Set whether to use Activity-based payment flow (recommended for better lifecycle management)
     * Default is true
     */
    public BootpayBuilder setUseActivity(boolean useActivity) {
        this.useActivity = useActivity;
        return this;
    }


    private Object getHostUIComponent() {
        if(mActivity != null) return mActivity;
        if(mFragmentManager != null) return mFragmentManager;
        if(mFragmentManagerX != null) return mFragmentManagerX;
        throw new IllegalStateException("activity, fragment 중 하나의 UI에서 실행되어야 합니다.");
    }

    public BootpayBuilder setPayload(Payload payload) {
        this.mPayload = payload;
        return this;
    }

    public BootpayBuilder setEventListener(BootpayEventListener listener) {
        this.mBootpayEventListener = listener;
        return this;
    }

    public void requestPayment() {
        // Use Activity-based approach
        if (useActivity && getActivityContext() != null) {
            BootpayPaymentActivity.launch(
                getActivityContext(),
                mPayload,
                mBootpayEventListener,
                kr.co.bootpay.android.constants.BootpayConstant.REQUEST_TYPE_PAYMENT
            );
            return;
        }

        // Fallback to Dialog approach
        if(mDialog != null) {
            if(mPayload != null) mDialog.setPayload(mPayload);
            if(mBootpayEventListener != null) mDialog.setEventListener(mBootpayEventListener);
            mDialog.requestPayment(mFragmentManager);
            return;
        }
        if(mDialogX != null) {
            if(mPayload != null) mDialogX.setPayload(mPayload);
            if(mBootpayEventListener != null) mDialogX.setEventListener(mBootpayEventListener);
            mDialogX.requestPayment(mFragmentManagerX);
            return;
        }
    }

    public void requestSubscription() {
        // Use Activity-based approach
        if (useActivity && getActivityContext() != null) {
            BootpayPaymentActivity.launch(
                getActivityContext(),
                mPayload,
                mBootpayEventListener,
                kr.co.bootpay.android.constants.BootpayConstant.REQUEST_TYPE_SUBSCRIPT
            );
            return;
        }

        // Fallback to Dialog approach
        if(mDialog != null) {
            if(mPayload != null) mDialog.setPayload(mPayload);
            if(mBootpayEventListener != null) mDialog.setEventListener(mBootpayEventListener);
            mDialog.requestSubscription(mFragmentManager);
            return;
        }
        if(mDialogX != null) {
            if(mPayload != null) mDialogX.setPayload(mPayload);
            if(mBootpayEventListener != null) mDialogX.setEventListener(mBootpayEventListener);
            mDialogX.requestSubscription(mFragmentManagerX);
            return;
        }
    }

    public void requestAuthentication() {
        // Use Activity-based approach
        if (useActivity && getActivityContext() != null) {
            BootpayPaymentActivity.launch(
                getActivityContext(),
                mPayload,
                mBootpayEventListener,
                kr.co.bootpay.android.constants.BootpayConstant.REQUEST_TYPE_AUTH
            );
            return;
        }

        // Fallback to Dialog approach
        if(mDialog != null) {
            if(mPayload != null) mDialog.setPayload(mPayload);
            if(mBootpayEventListener != null) mDialog.setEventListener(mBootpayEventListener);
            mDialog.requestAuthentication(mFragmentManager);
            return;
        }
        if(mDialogX != null) {
            if(mPayload != null) mDialogX.setPayload(mPayload);
            if(mBootpayEventListener != null) mDialogX.setEventListener(mBootpayEventListener);
            mDialogX.requestAuthentication(mFragmentManagerX);
            return;
        }
    }

    /**
     * Get Activity context for launching payment activity
     */
    private Context getActivityContext() {
        return mActivity;
    }

    public void transactionConfirm() {
        // Activity-based approach
        BootpayPaymentActivity currentActivity = BootpayPaymentActivity.getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.transactionConfirm();
            return;
        }

        // Dialog fallback
        if (mDialog != null) mDialog.transactionConfirm();
        if (mDialogX != null) mDialogX.transactionConfirm();
    }

    @Override
    public void removePaymentWindow() {
        // Activity-based approach
        BootpayPaymentActivity currentActivity = BootpayPaymentActivity.getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.removePaymentWindow();
            return;
        }

        // Dialog fallback
        if(mDialog  != null) mDialog.removePaymentWindow();
        if(mDialogX != null) mDialogX.removePaymentWindow();
    }

    public void dismissWindow() {
        // Activity-based approach
        BootpayPaymentActivity currentActivity = BootpayPaymentActivity.getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.finish();
            return;
        }

        // Dialog fallback
        if(mDialog  != null) mDialog.dismiss();
        if(mDialogX != null) mDialogX.dismiss();
    }
}
