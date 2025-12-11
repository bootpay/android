package kr.co.bootpay.android;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import kr.co.bootpay.android.webview.BootpayWebViewHandler;
import kr.co.bootpay.android.webview.BootpayWidgetDialog;
import kr.co.bootpay.android.webview.BootpayWidgetDialogX;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.BootpayWidgetEventListener;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.webview.BootpayWebView;
import kr.co.bootpay.android.widget.BootpayWidgetController;

public class BootpayWidget {

    private static final double DEFAULT_WIDGET_HEIGHT = 300.0;

    private static BootpayWidgetDialog mDialog;
    private static BootpayWidgetDialogX mDialogX;
    private static BootpayWebView mWebView;
    private static Payload mPayload;
    private static BootpayEventListener mListener;
    private static BootpayWidgetEventListener mWidgetListener;
    private static BootpayWidgetController mWidgetController;
    private static androidx.fragment.app.FragmentManager mFragmentManagerX;
    private static android.app.FragmentManager mFragmentManager;

    public static BootpayWebView getView(Context context, androidx.fragment.app.FragmentManager fragmentManagerX) {
        mFragmentManagerX = fragmentManagerX;
        if (mWebView == null) {
            mWebView = new BootpayWebView(context);
        }
        return mWebView;
    }

    public static BootpayWebView getView(Context context, android.app.FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
        if (mWebView == null) {
            mWebView = new BootpayWebView(context);
        }
        return mWebView;
    }

    public static void destroyView() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
    }

    private static void widgetStatusReset() {
        if (mWebView == null) return;

        mWebView.startWidget();
        if (mWebView.getPaymentResult() == BootpayPaymentResult.NONE) {
            if (mListener != null) {
                mListener.onCancel("{'action':'BootpayCancel','status':-100,'message':'사용자에 의한 취소'}");
            }
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
        if (mWebView == null) {
            Log.e("bootpay", "WebView is not initialized. Call getView() first.");
            return;
        }
        mWidgetListener = listener;
        mWebView.renderWidget(activity, payload, listener);
    }

    /**
     * Controller 기반 위젯 렌더링 (iOS 패리티)
     * @param activity Activity
     * @param payload Payload
     * @param controller BootpayWidgetController
     */
    public static void renderWidget(Activity activity, Payload payload, BootpayWidgetController controller) {
        if (mWebView == null) {
            Log.e("bootpay", "WebView is not initialized. Call getView() first.");
            return;
        }
        mWidgetController = controller;
        mWebView.setWidgetController(controller);
        mWebView.renderWidget(activity, payload, null);
    }

    /**
     * Controller 가져오기
     * @return BootpayWidgetController
     */
    public static BootpayWidgetController getController() {
        return mWidgetController;
    }

    /**
     * Controller 설정하기
     * @param controller BootpayWidgetController
     */
    public static void setController(BootpayWidgetController controller) {
        mWidgetController = controller;
        if (mWebView != null) {
            mWebView.setWidgetController(controller);
        }
    }

    public static void showDialog(Activity activity) {
        if (mFragmentManagerX != null) {
            if (mDialogX == null) mDialogX = new BootpayWidgetDialogX();
            mDialogX.fullScreenDialog(activity, mFragmentManagerX);
        }
        if (mFragmentManager != null) {
            if (mDialog == null) mDialog = new BootpayWidgetDialog();
            mDialog.fullScreenDialog(activity, mFragmentManager);
        }
    }

    public static void closeDialog(Activity activity) {
        Log.d("bootpay", "closeDialog");

        if (activity == null) return;

        activity.runOnUiThread(() -> {
            BootpayWebView webView = null;

            if (mDialogX != null) webView = mDialogX.getWebView();
            if (mDialog != null) webView = mDialog.getWebView();

            if (webView != null) {
                webView.invisibleWebView();
                BootpayWebViewHandler.resizeWebView(webView, DEFAULT_WIDGET_HEIGHT);
                webView.removeFromParent(activity);

                BootpayPaymentResult result = webView.getPaymentResult();
                if (result == BootpayPaymentResult.NONE) {
                    if (mListener != null) mListener.onClose();
                }
            }

            if (mDialogX != null) mDialogX.dismiss();
            if (mDialog != null) mDialog.dismiss();

            widgetStatusReset();
            if (mWidgetListener != null) mWidgetListener.needReloadWidget();
        });
    }

    public static void bindViewUpdate(Activity activity, androidx.fragment.app.FragmentManager fragmentManager, ViewGroup group) {
        if (activity == null || fragmentManager == null || group == null) return;

        mWebView = getView(activity, fragmentManager);
        if (mWebView != null) {
            mWebView.removeFromParent(activity);
            mWebView.addToParent(activity, group);
        }
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
        if (mDialog != null) mDialog.removePaymentWindow();
        if (mDialogX != null) mDialogX.removePaymentWindow();
    }

    public static void destroy() {
        if (mDialogX != null) {
            mDialogX.dismiss();
            mDialogX = null;
        }
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        destroyView();
        mFragmentManager = null;
        mFragmentManagerX = null;
        mListener = null;
        mWidgetListener = null;
        if (mWidgetController != null) {
            mWidgetController.reset();
            mWidgetController = null;
        }
        mPayload = null;
    }

    public static void resizeWidget(double height) {
        if (mWebView != null) {
            BootpayWebViewHandler.resizeWebView(mWebView, height);
        }
    }

    /**
     * 위젯 재로드 (iOS 패리티)
     * 에러 발생 후 위젯을 다시 로드할 때 사용
     */
    public static void reloadWidget() {
        if (mWebView != null && mPayload != null) {
            mWebView.startWidget();
        }
    }

    /**
     * 위젯 업데이트 (iOS 패리티)
     * @param payload 업데이트할 Payload
     * @param refresh true면 위젯 새로고침
     */
    public static void updateWidget(Payload payload, boolean refresh) {
        if (mWebView != null) {
            mPayload = payload;
            BootpayWebViewHandler.updateWidget(mWebView, payload, refresh);
        }
    }
}
