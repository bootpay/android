package kr.co.bootpay.android;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import kr.co.bootpay.android.webview.BootpayWidgetDialog;
import kr.co.bootpay.android.webview.BootpayWidgetDialogX;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.BootpayWidgetEventListener;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.webview.BootpayWebView;


// 위젯 사용을 위한 인터페이스
// webview는 싱글턴으로 관리한다
// 사용자 화면에서 webview를 제공하고, 결제시 전체화면 dialog에서 해당 웹뷰를 재사용하고, 다시 화면으로 돌아올때 다시 제공한다
// webview에서도 내부적으로 BootpayWidget을 사용하고, BootpayWidget에서는 BootpayWebView를 사용한다

public class BootpayWidget {

    private static BootpayWidgetDialog mDialog;
    private static BootpayWidgetDialogX mDialogX;
    private static BootpayWebView mWebView;
    private static Payload mPayload;
    private static BootpayEventListener mListener;

    private static BootpayWidgetEventListener mWidgetListener;

    private static androidx.fragment.app.FragmentManager mFragmentManagerX;
    private static android.app.FragmentManager mFragmentManager;


//    public static BootpayWebView getView(Context context, android.app.FragmentManager fragmentManager) {
//        fragmentManager = fragmentManager;
//        if(mWebView == null) {
//            mWebView = new BootpayWebView(context);
//        }
//        return mWebView;
//    }


    public static BootpayWebView getView(Context context, androidx.fragment.app.FragmentManager fragmentManagerX) {
        mFragmentManagerX = fragmentManagerX;
        if(mWebView == null) {
            mWebView = new BootpayWebView(context);
        }
        return mWebView;
    }

    public static BootpayWebView getView(Context context, android.app.FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
        if(mWebView == null) {
            mWebView = new BootpayWebView(context);
        }
        return mWebView;
    }

    public static void destroyView() {
        if(mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
    }

    private static void widgetStatusReset() {
        if(mWebView != null)  mWebView.startWidget();
        if(mWebView.getPaymentResult() == BootpayPaymentResult.NONE) {
            if(mListener != null) mListener.onCancel("{'action':'BootpayCancel','status':-100,'message':'사용자에 의한 취소'}");
        }

        mWebView.setPaymentResult(BootpayPaymentResult.NONE);
    }

    public static Payload getPayload() {
        return mPayload;
    }

    public static BootpayEventListener getEventListener() {
        return mListener;
    }

    public static void renderWidget(Activity activity, Payload payload, BootpayWidgetEventListener listener) {
//        if (builder != null) builder.renderWidget(payload, listener);
        mWidgetListener = listener;
        mWebView.renderWidget(activity, payload, listener);
    }

    public static void showDialog(Activity activity) {
        if(mFragmentManagerX != null) {
            if(mDialogX == null) mDialogX = new BootpayWidgetDialogX();
            mDialogX.fullScreenDialog(activity, mFragmentManagerX);
        }
        if(mFragmentManager != null) {
            if(mDialog == null) mDialog = new BootpayWidgetDialog();
            mDialog.fullScreenDialog(activity, mFragmentManager);
        }
    }

    public static void closeDialog(Activity activity) {

        if(activity == null) return;
        activity.runOnUiThread(() -> {
            BootpayWebView webView = null;

            if(mDialogX != null) webView = mDialogX.getWebView();
            if(mDialog != null) webView = mDialog.getWebView();

            webView.invisibleWebView();
            webView.removeFromParent(activity);

            if(mDialogX != null) mDialogX.dismiss();
            if(mDialog != null) mDialog.dismiss();

            BootpayPaymentResult result = webView.getPaymentResult();
            if(result == BootpayPaymentResult.NONE) {
                if(mListener != null) mListener.onClose();
            }

            widgetStatusReset();
            if(mWidgetListener != null) mWidgetListener.needReloadWidget();
        });
    }

    public static void bindViewUpdate(Activity activity, androidx.fragment.app.FragmentManager fragmentManager, ViewGroup group) {
        if(activity == null) return;
        if(fragmentManager == null) return;
        if(group == null) return;
        mWebView = getView(activity, fragmentManager);
        mWebView.removeFromParent(activity);
        mWebView.addToParent(activity, group);
    }

    public static void requestPayment(Activity activity, android.app.FragmentManager fragmentManager, Payload payload, BootpayEventListener listener) {
        mFragmentManager = fragmentManager;
        mPayload = payload;
        mListener = listener;

        mDialog = new BootpayWidgetDialog();
        mDialog.setPayload(payload);
        mDialog.setEventListener(listener);
        mDialog.requestWidgetPayment(activity, fragmentManager);
    }

    public static void requestPayment(Activity activity, androidx.fragment.app.FragmentManager fragmentManager, Payload payload, BootpayEventListener listener) {
        mFragmentManagerX = fragmentManager;
        mPayload = payload;
        mListener = listener;

        mDialogX = new BootpayWidgetDialogX();
        mDialogX.setPayload(payload);
        mDialogX.setEventListener(listener);
        mDialogX.requestWidgetPayment(activity, fragmentManager);
    }


    public static void removePaymentWindow() {
        if(mDialog != null) mDialog.removePaymentWindow();
        if(mDialogX != null) mDialogX.removePaymentWindow();
    }

    public static void destroy() {
        if(mDialogX != null) {
            mDialogX.dismiss();
            mDialogX = null;
        }
        if(mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        destroyView();
    }

    public static void resizeWidget(double height) {
        if(mWebView != null) mWebView.resizeWebView(height);
    }

}

