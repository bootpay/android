package kr.co.bootpay.android;

import android.app.Activity;
import android.content.Context;

import kr.co.bootpay.android.webview.BootpayDialog;
import kr.co.bootpay.android.webview.BootpayDialogX;
import kr.co.bootpay.android.api.BootpayInterface;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.Payload;

public class BootpayBuilder implements BootpayInterface {
    private Activity mActivity;
    private android.app.FragmentManager mFragmentManager;
    private androidx.fragment.app.FragmentManager mFragmentManagerX;

    private Context mContext;
    private BootpayDialog mDialog;
    private BootpayDialogX mDialogX;

    private Payload mPayload;
    private BootpayEventListener mBootpayEventListener;
//    private int mRequestType = BootpayConstantV2.REQUEST_TYPE_PAYMENT;

    public BootpayBuilder(Activity activity, Context context) {
        this.mActivity = activity;
        this.mFragmentManager = activity.getFragmentManager();
        this.mFragmentManagerX = null;
        this.mDialog = new BootpayDialog();
        this.mDialogX = null;
        this.mContext = context;
    }

    public BootpayBuilder(android.app.FragmentManager fragmentManager, Context context) {
        this.mActivity = null;
        this.mFragmentManager = fragmentManager;
        this.mFragmentManagerX = null;
        this.mDialog = new BootpayDialog();
        this.mDialogX = null;
        this.mContext = context;
    }

    public BootpayBuilder(androidx.fragment.app.FragmentManager fragmentManager, Context context) {
        this.mActivity = null;
        this.mFragmentManager = null;
        this.mFragmentManagerX = fragmentManager;
        this.mDialog = null;
        this.mDialogX = new BootpayDialogX();
        this.mContext = context;
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


    public void transactionConfirm(String data) {
        if (mDialog != null) mDialog.transactionConfirm(data);
        if (mDialogX != null) mDialogX.transactionConfirm(data);
    }

    @Override
    public void removePaymentWindow() {
        if(mDialog  != null) mDialog.removePaymentWindow();
        if(mDialogX != null) mDialogX.removePaymentWindow();
    }

    public void dismissWindow() {
        if(mDialog  != null) mDialog.dismiss();
        if(mDialogX != null) mDialogX.dismiss();
    }
}
