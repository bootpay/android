package kr.co.bootpay.android.webview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import kr.co.bootpay.android.BootpayWidget;
import kr.co.bootpay.android.api.BootpayDialogInterface;
import kr.co.bootpay.android.api.BootpayInterface;
import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.webview.BootpayWebView;
import kr.co.bootpay.core.R;


public class BootpayWidgetDialogX extends DialogFragment implements BootpayDialogInterface, BootpayInterface {
//    BootpayWebView mWebView = null;

    FrameLayout webViewContainer;
    RelativeLayout mLayoutProgress = null;
    ProgressBar mProgressBar = null;

    boolean isDismiss = false;
    Payload mPayload = null;
    BootpayEventListener mEventListener = null;
    boolean doubleBackToExitPressedOnce = false;
    private int mRequestType = BootpayConstant.REQUEST_TYPE_PAYMENT;
    BootpayWebView mWebView = null;

    Activity mActivity;

//    @Override
//    public void onResume() {
//        super.onResume();
//        if(mWebView != null) {
//            mWebView.onResume();
//            mWebView.resumeTimers();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if(mWebView != null) {
//            if(isDismiss) {
////                BootpayWidget.getView(getContext()).destroy();
//            } else {
//                mWebView.onPause();
//                mWebView.pauseTimers();
//            }
//        }
//    }

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

        backButtonEventBind();
        mWebView.fadeInWebView(300);


        return view;
    }

    void setPrivateWidgetEvent() {
//        BootpayWebView webView = BootpayWidget.getView(getContext());
//        webView.setWidgetPrivateEventListener(new kr.co.bootpay.android.events.BootpayWidgetPrivateEventListener() {
//            @Override
//            public void onFullSizeScreen(String data) {
//                //dialog 시작
//
//                Log.d("bootpay", "onFullSizeScreen: " + data);
////                if(mEventListener != null) mEventListener.onClose();
//            }
//
//            @Override
//            public void onRevertScreen(String data) {
//                Log.d("bootpay", "onRevertScreen: " + data);
//                //dialog 종료, 위젯 리로드
////                if(mEventListener != null) mEventListener.onClose();
//            }
//
//            @Override
//            public void onCloseWidget() {
//                Log.d("bootpay", "onCloseWidget: ");
//                //paymentResult 가 none이 아니면 dialog 종료, 위젯 리로드
////               onClose 호출해야함
//            }
//        });
    }

    void backButtonEventBind() {
        Dialog dialog = getDialog();
        dialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
            if (keyEvent.getAction()!=KeyEvent.ACTION_DOWN)
                return true;

            if(keyCode == KeyEvent.KEYCODE_BACK) {
                BootpayEventListener listener = BootpayWidget.getEventListener();
                if(listener != null) listener.onClose();
                return true;

//                if (doubleBackToExitPressedOnce) {
//                    BootpayEventListener listener = BootpayWidget.getEventListener();
//                    if(listener != null) listener.onClose();
//                    return true;
//                }
//                doubleBackToExitPressedOnce = true;
//                Toast.makeText(getContext(), "결제를 종료하시려면 '뒤로' 버튼을 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        doubleBackToExitPressedOnce = false;
//                    }
//                }, 2000);
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

    private Bundle widgetBundle;
    public void requestWidgetPayment(androidx.fragment.app.FragmentManager fragmentManager) throws IllegalArgumentException {
        this.mFragmentManager = fragmentManager;
//        if(mPayload == null) throw new IllegalArgumentException("payload는 null 이 될 수 없습니다.");
//        mRequestType = BootpayConstant.REQUEST_TYPE_WIDGET;

//        this.widgetBundle = widgetBundle;
        show(fragmentManager, mPayload.getOrderId());
    }

    public void requestSubscription(androidx.fragment.app.FragmentManager fragmentManager) throws IllegalArgumentException {
        this.mFragmentManager = fragmentManager;
        if(mPayload == null) throw new IllegalArgumentException("payload는 null 이 될 수 없습니다.");
        mRequestType = BootpayConstant.REQUEST_TYPE_SUBSCRIPT;
        show(fragmentManager, mPayload.getOrderId());
    }

    public void requestAuthentication(androidx.fragment.app.FragmentManager fragmentManager) throws IllegalArgumentException {
        this.mFragmentManager = fragmentManager;
        if(mPayload == null) throw new IllegalArgumentException("payload는 null 이 될 수 없습니다.");
        mRequestType = BootpayConstant.REQUEST_TYPE_AUTH;
        show(fragmentManager, mPayload.getOrderId());
    }

//    public void pushInternalWebView(androidx.fragment.app.FragmentManager fragmentManager) {
//        this.mFragmentManager = fragmentManager;
//        mWebView.fullSizeWebView();
//        show(fragmentManager, System.currentTimeMillis() + "");
//    }

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

    public void showDialog(Activity activity, androidx.fragment.app.FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
        this.mActivity = activity;

//        this.mWebView.fullSizeWebView();
        this.show(fragmentManager, "BootpayWidgetDialogX");
    }

}
