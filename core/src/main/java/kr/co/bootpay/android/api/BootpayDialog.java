package kr.co.bootpay.android.api;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.BootpayExtEventListener;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.webview.BootpayWebView;
import kr.co.bootpay.core.R;

public class BootpayDialog extends DialogFragment implements BootpayDialogInterface, BootpayInterface {
    BootpayWebView mWebView = null;
    RelativeLayout mLayoutProgress = null;
    ProgressBar mProgressBar = null;

    Payload mPayload = null;
    BootpayEventListener mEventListener = null;
    boolean doubleBackToExitPressedOnce = false;
    private int mRequestType = BootpayConstant.REQUEST_TYPE_PAYMENT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME,  android.R.style.Theme_Holo_Light);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        boolean quickPopup = false;
//        if(mPayload != null && mPayload.getExtra() != null && mPayload.getExtra().getPopup() == 1) quickPopup = true;
        View view = inflater.inflate(R.layout.layout_bootpay_dialog, container, false);
        if(mWebView == null) mWebView = view.findViewById(R.id.webview);
        mLayoutProgress = view.findViewById(R.id.layout_progress);
        mWebView.setExtEventListener(isShow -> {
            getActivity().runOnUiThread(() -> {
                if(mLayoutProgress != null) mLayoutProgress.setVisibility(isShow == true ? View.VISIBLE : View.GONE);
            });
        });
        mProgressBar = view.findViewById(R.id.progress);
//        mWebView.o


//        inflater.in

//        if(mWebView == null) mWebView = new BootpayWebView(inflater.getContext());
        if(mEventListener != null) mWebView.setEventListener(mEventListener);
//        mWebView.addCloseEvent();
        if(mPayload != null) {
            mWebView.setInjectedJS(BootpayConstant.getJSPay(mPayload, mRequestType));
            mWebView.setPayload(mPayload);
        }
        mWebView.setInjectedJSBeforePayStart(BootpayConstant.getJSBeforePayStart(inflater.getContext()));
        backButtonEventBind();
        mWebView.startBootpay();
        return view;
    }

    void backButtonEventBind() {
        Dialog dialog = getDialog();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;

                if(keyCode == KeyEvent.KEYCODE_BACK) {
                    if (doubleBackToExitPressedOnce) {
                        BootpayEventListener listener = mWebView.getEventListener();
                        if(listener != null) listener.onClose("???????????? ?????? ???????????????");
                        return true;
                    }
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(dialog.getContext(), "????????? ?????????????????? '??????' ????????? ?????? ??? ???????????????.", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
                return false;
            }
        });
    }

    public Payload getPayload() {
        return mPayload;
    }

    public void requestPayment(android.app.FragmentManager fragmentManager) throws IllegalArgumentException {
        if(mPayload == null) throw new IllegalArgumentException("payload??? null ??? ??? ??? ????????????.");
        mRequestType = BootpayConstant.REQUEST_TYPE_PAYMENT;
        show(fragmentManager, mPayload.getOrderId());
    }

    public void requestSubscription(android.app.FragmentManager fragmentManager) throws IllegalArgumentException {
        if(mPayload == null) throw new IllegalArgumentException("payload??? null ??? ??? ??? ????????????.");
        mRequestType = BootpayConstant.REQUEST_TYPE_SUBSCRIPT;
        show(fragmentManager, mPayload.getOrderId());
    }

    public void requestAuthentication(android.app.FragmentManager fragmentManager) throws IllegalArgumentException {
        if(mPayload == null) throw new IllegalArgumentException("payload??? null ??? ??? ??? ????????????.");
        mRequestType = BootpayConstant.REQUEST_TYPE_AUTH;
        show(fragmentManager, mPayload.getOrderId());
    }

    public void transactionConfirm(String data) {
        if (mWebView != null) mWebView.transactionConfirm(data);
    }

    @Override
    public void removePaymentWindow() {
        if(mWebView != null) mWebView.removePaymentWindow();
        dismiss();
    }

    @Override
    public void setEventListener(BootpayEventListener listener) {
        this.mEventListener = listener;
    }

    @Override
    public void setPayload(Payload payload) {
        this.mPayload = payload;
    }
}
