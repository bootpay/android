package kr.co.bootpay.android;

import android.app.Activity;
import android.content.Context;

import kr.co.bootpay.android.webview.BootpayWidgetDialogX;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.BootpayWidgetEventListener;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.webview.BootpayWebView;


public class BootpayWidget {

    private static BootpayWidgetDialogX mDialogX;
    private static BootpayWebView mWebView;
    private static Payload mPayload;
    private static BootpayEventListener mListener;

    private static BootpayWidgetEventListener mWidgetListener;

    private static androidx.fragment.app.FragmentManager mFragmentManagerX;
    private static android.app.FragmentManager mFragmentManager;




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
        if(mWebView != null) {
//            mWebView.loadUrl("https://www.naver.com");
            mWebView.startWidget();
        }
        if(mWebView.getPaymentResult() == BootpayPaymentResult.NONE) {
            if(mListener != null) mListener.onCancel("{'action':'BootpayCancel','status':-100,'message':'사용자에 의한 취소'}");

//            if(mListener != null) mListener.onClose();
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
            if(mDialogX == null)
                mDialogX = new BootpayWidgetDialogX();
            mDialogX.showDialog(activity, mFragmentManagerX);
//            mDialogX.show(mFragmentManagerX, "BootpayWidgetDialogX");
        }
    }

    public static void closeDialog(Activity activity) {

        if(activity == null) return;
        activity.runOnUiThread(() -> {
            BootpayWebView webView = mDialogX.getWebView();
            webView.removeFromParent(activity);

            if(mDialogX != null) mDialogX.dismiss();

            BootpayPaymentResult result = webView.getPaymentResult();
            if(result == BootpayPaymentResult.NONE) {
                if(mListener != null) mListener.onClose();
            }

            widgetStatusReset();
            if(mWidgetListener != null) mWidgetListener.needReloadWidget();
        });
    }



    public static void requestPayment(androidx.fragment.app.FragmentManager fragmentManager, Payload payload, BootpayEventListener listener) {
        mPayload = payload;
        mListener = listener;

        mDialogX = new BootpayWidgetDialogX();
        mDialogX.setPayload(payload);
        mDialogX.setEventListener(listener);
//
        mDialogX.requestWidgetPayment(fragmentManager);
//        setPrivateWidgetEvent();

//        setPrivateWidgetEvent(fragmentManager());
//        setPrivateWidgetEvent(fragmentManager.getContext());
    }

//    public static void setPrivateWidgetEvent() {
//        BootpayWebView webView = mDialogX.getWebView(); //BootpayWidget.getView(context); 와 동일 코드
//        webView.setWidgetPrivateEventListener(new kr.co.bootpay.android.events.BootpayWidgetPrivateEventListener() {
////            @Override
////            public void onFullSizeScreen(String data) {
////                //dialog 시작
////
////                Log.d("bootpay", "onFullSizeScreen: " + data);
//////                if(mEventListener != null) mEventListener.onClose();
////            }
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
//    }

    public static void removePaymentWindow() {
        if(mDialogX != null) mDialogX.removePaymentWindow();
    }

    public static void destroy() {
        if(mDialogX != null) {
            mDialogX.dismiss();
            mDialogX = null;
        }
        destroyView();
    }

    public static void resizeWidget(double height) {
        if(mWebView != null) mWebView.resizeWebView(height);
    }

//    public static void fullSizeWidget() {
//        if (mWebView != null) mWebView.fullSizeWebView();
//    }

//    protected  static BootpayBuilder builder;

//    public static BootpayBuilder init(Context context) {
//        return builder = new BootpayBuilder(context);
//    }

//    public static BootpayBuilder init(Activity activity, Context context) {
//        return builder = new BootpayBuilder(activity, context);
//    }
//
//    public static BootpayBuilder init(android.app.FragmentManager fragmentManager, Context context) {
//        return builder = new BootpayBuilder(fragmentManager, context);
//    }
//
//    public static BootpayBuilder init(androidx.fragment.app.FragmentManager fragmentManagerX, Context context) {
//        return builder = new BootpayBuilder(fragmentManagerX, context);
//    }
//
//
//    public static void transactionConfirm(String data) {
//        if (builder != null) builder.transactionConfirm(data);
//    }
//
//    public static void removePaymentWindow() {
//        if (builder != null) builder.removePaymentWindow();
//    }
//
//    public static void dismissWindow() {
//        if (builder != null) builder.dismissWindow();
//    }
}

