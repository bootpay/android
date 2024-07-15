package kr.co.bootpay.android.webview;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import kr.co.bootpay.android.BootpayWidget;
import kr.co.bootpay.android.api.BootpayDialogInterface;
import kr.co.bootpay.android.api.BootpayInterface;
import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.core.R;


public class BootpayWidgetDialogX extends DialogFragment implements BootpayDialogInterface, BootpayInterface {
//    BootpayWebView mWebView = null;

    FrameLayout webViewContainer;

    boolean isDismiss = false;
    Payload mPayload = null;
    BootpayEventListener mEventListener = null;
    boolean doubleBackToExitPressedOnce = false;
    private int mRequestType = BootpayConstant.REQUEST_TYPE_PAYMENT;
    BootpayWebView mWebView = null;

    Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME,  android.R.style.Theme_Holo_Light);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_bootpay_widget_dialog, container, false);
        webViewContainer = view.findViewById(R.id.webViewContainer);

        mWebView = BootpayWidget.getView(getContext(), getFragmentManager());
        mWebView.removeFromParent(mActivity);
        mWebView.addToParent(mActivity, webViewContainer);
        mWebView.fullSizeWebView();
        mWebView.fadeInWebView(300);

        if(mRequestType == BootpayConstant.REQUEST_TYPE_PAYMENT) {
            mWebView.requestWidgetPayment(mPayload, mEventListener);
        }

        backButtonEventBind();
        return view;
    }


    void backButtonEventBind() {
        Dialog dialog = getDialog();
        if(dialog == null) return;
        dialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
            if (keyEvent.getAction()!=KeyEvent.ACTION_DOWN)
                return true;

            if(keyCode == KeyEvent.KEYCODE_BACK) {
                BootpayEventListener listener = BootpayWidget.getEventListener();
                if(listener != null) listener.onClose();
                BootpayWidget.closeDialog(mActivity);
                return false;

            }
            return false;
        });
    }

    public Payload getPayload() {
        return mPayload;
    }

    public BootpayWebView getWebView() {
        return BootpayWidget.getView(getContext(), mFragmentManager);
    }

    private androidx.fragment.app.FragmentManager mFragmentManager;
    public void requestPayment(androidx.fragment.app.FragmentManager fragmentManager) throws IllegalArgumentException {
        this.mFragmentManager = fragmentManager;
        if(mPayload == null) throw new IllegalArgumentException("payload는 null 이 될 수 없습니다.");
        mRequestType = BootpayConstant.REQUEST_TYPE_PAYMENT;
        show(fragmentManager, mPayload.getOrderId());
    }


//    private Bundle widgetBundle;
    public void requestWidgetPayment(Activity activity, androidx.fragment.app.FragmentManager fragmentManager) throws IllegalArgumentException {
        this.mActivity = activity;
        this.mFragmentManager = fragmentManager;
        if(mPayload == null) throw new IllegalArgumentException("payload는 null 이 될 수 없습니다.");
        mRequestType = BootpayConstant.REQUEST_TYPE_PAYMENT;

//        this.widgetBundle = widgetBundle;
        show(fragmentManager, mPayload.getOrderId());
    }


    @Override
    public void removePaymentWindow() {
//        if(mWebView != null) {
//            mWebView.removePaymentWindow();
//        }
        if(getShowsDialog()) {
            isDismiss = true;
            dismiss();
        }
    }

    @Override
    public void setEventListener(BootpayEventListener listener) {
        this.mEventListener = listener;
    }

    @Override
    public void setPayload(Payload payload) {
        this.mPayload = payload;
    }


    public void transactionConfirm(String data) {
//        if (mWebView != null) mWebView.transactionConfirm(data);
    }

    public void fullScreenDialog(Activity activity, androidx.fragment.app.FragmentManager fragmentManager) {
        mRequestType = BootpayConstant.REQUEST_TYPE_NONE;
        this.mFragmentManager = fragmentManager;
        this.mActivity = activity;

        this.show(fragmentManager, "BootpayWidgetDialogX");
    }

}
