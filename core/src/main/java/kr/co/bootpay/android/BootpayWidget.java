package kr.co.bootpay.android;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.webview.BootpayWebViewHandler;
import kr.co.bootpay.android.webview.BootpayWidgetDialog;
import kr.co.bootpay.android.webview.BootpayWidgetDialogX;
import kr.co.bootpay.android.webview.BootpayWidgetPaymentActivity;
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

    private static Activity mCallerActivity;

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
        Log.d("bootpay", "[BootpayWidget] renderWidget with controller called, mWebView=" + mWebView);
        if (mWebView == null) {
            Log.e("bootpay", "WebView is not initialized. Call getView() first.");
            return;
        }
        mCallerActivity = activity;  // Store caller activity for closeActivity callback
        mPayload = payload;
        mWidgetController = controller;
        mWebView.setWidgetController(controller);
        Log.d("bootpay", "[BootpayWidget] Calling mWebView.renderWidget(), mCallerActivity=" + mCallerActivity);
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

    /**
     * 전체화면으로 확장 (iOS parity - expandToFullscreen)
     * Activity/Dialog 전환 없이 같은 WebView를 전체화면으로 확장
     */
    public static void showDialog(Activity activity) {
        mCallerActivity = activity;

        // iOS 스타일: WebView를 전체화면으로 확장
        if (mWebView != null) {
            Log.d("bootpay", "[BootpayWidget] showDialog using expandToFullscreen approach");
            activity.runOnUiThread(() -> {
                mWebView.expandToFullscreen(true);
            });
        }
    }

    /**
     * 전체화면 축소 (iOS parity - collapseToOriginal)
     * WebView를 원래 위치로 복원
     */
    public static void closeDialog(Activity activity) {
        Log.d("bootpay", "[BootpayWidget] closeDialog called (collapseToOriginal), mWebView=" + mWebView);

        if (activity == null) return;

        activity.runOnUiThread(() -> {
            // iOS 스타일: WebView를 원래 위치로 축소
            if (mWebView != null && mWebView.isExpanded()) {
                Log.d("bootpay", "[BootpayWidget] closeDialog - collapsing to original");
                mWebView.collapseToOriginal(true);
            }
        });
    }

    /**
     * Close activity callback (legacy support for BootpayWidgetPaymentActivity)
     * Activity 기반 결제에서 뒤로가기 시 호출됨
     * iOS 방식에서는 collapseAndReload를 사용하는 것이 권장됨
     */
    public static void closeActivity(Activity paymentActivity) {
        Log.d("bootpay", "[BootpayWidget] closeActivity called (legacy)");
        collapseAndReload(mCallerActivity != null ? mCallerActivity : paymentActivity);
    }

    /**
     * 전체화면 축소 후 위젯 재로드 (결제 취소/에러 시)
     * iOS cancel/error 후 reloadWidget 호출하는 것과 동일
     * 축소 시작 전에 위젯 URL을 먼저 로드하여 자연스러운 전환
     */
    public static void collapseAndReload(Activity activity) {
        Log.d("bootpay", "[BootpayWidget] collapseAndReload called");

        if (activity == null) return;

        activity.runOnUiThread(() -> {
            if (mWebView != null && mWebView.isExpanded()) {
                // 축소하면서 동시에 위젯 URL 로드 (reloadWidget=true)
                mWebView.collapseToOriginal(true, true);
            }
        });
    }

    /**
     * 전체화면 축소 후 Activity 닫기 (결제 완료 시)
     * displaySuccessResult = false 일 때 사용
     */
    public static void collapseAndFinish(Activity activity) {
        Log.d("bootpay", "[BootpayWidget] collapseAndFinish called");

        if (activity == null) return;

        activity.runOnUiThread(() -> {
            if (mWebView != null && mWebView.isExpanded()) {
                mWebView.collapseToOriginal(false); // 애니메이션 없이 즉시 축소
            }
            // closeAction에 따라 Activity 종료 (WidgetControllerActivity에서 처리)
        });
    }

    public static void bindViewUpdate(Activity activity, androidx.fragment.app.FragmentManager fragmentManager, ViewGroup group) {
        Log.d("bootpay", "[BootpayWidget] bindViewUpdate called, activity=" + activity + ", group=" + group + ", mWebView=" + mWebView);
        if (activity == null || fragmentManager == null || group == null) {
            Log.e("bootpay", "[BootpayWidget] bindViewUpdate - null parameter! activity=" + activity + ", fm=" + fragmentManager + ", group=" + group);
            return;
        }

        mWebView = getView(activity, fragmentManager);
        Log.d("bootpay", "[BootpayWidget] bindViewUpdate - got mWebView=" + mWebView);
        if (mWebView != null) {
            Log.d("bootpay", "[BootpayWidget] bindViewUpdate - removing from parent and adding to group");
            mWebView.removeFromParent(activity);
            mWebView.addToParent(activity, group);
            Log.d("bootpay", "[BootpayWidget] bindViewUpdate - WebView added to container");
        }
    }

    /**
     * 결제 요청 (iOS parity - widgetRequestPayment)
     * Activity 전환 없이 같은 WebView를 전체화면으로 확장 후 결제 요청
     */
    public static void requestPayment(Activity activity, android.app.FragmentManager fragmentManager, Payload payload, BootpayEventListener listener) {
        mFragmentManager = fragmentManager;
        mPayload = payload;
        mListener = listener;
        mCallerActivity = activity;

        if (mWebView == null) {
            Log.e("bootpay", "[BootpayWidget] requestPayment - WebView is null");
            return;
        }

        Log.d("bootpay", "[BootpayWidget] requestPayment using expandToFullscreen approach");
        activity.runOnUiThread(() -> {
            // 전체화면으로 확장
            mWebView.expandToFullscreen(true);

            // 약간의 딜레이 후 결제 요청 (애니메이션 완료 후)
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                BootpayWebViewHandler.requestWidgetPayment(mWebView, payload);
            }, 400);
        });
    }

    /**
     * 결제 요청 (iOS parity - widgetRequestPayment)
     * Activity 전환 없이 같은 WebView를 전체화면으로 확장 후 결제 요청
     */
    public static void requestPayment(Activity activity, androidx.fragment.app.FragmentManager fragmentManager, Payload payload, BootpayEventListener listener) {
        mFragmentManagerX = fragmentManager;
        mPayload = payload;
        mListener = listener;
        mCallerActivity = activity;

        if (mWebView == null) {
            Log.e("bootpay", "[BootpayWidget] requestPayment - WebView is null");
            return;
        }

        Log.d("bootpay", "[BootpayWidget] requestPayment using expandToFullscreen approach");
        activity.runOnUiThread(() -> {
            // 전체화면으로 확장
            mWebView.expandToFullscreen(true);

            // 약간의 딜레이 후 결제 요청 (애니메이션 완료 후)
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                BootpayWebViewHandler.requestWidgetPayment(mWebView, payload);
            }, 400);
        });
    }

    public static void removePaymentWindow() {
        // Activity-based approach
        BootpayWidgetPaymentActivity currentActivity = BootpayWidgetPaymentActivity.getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.removePaymentWindow();
            return;
        }

        // Dialog fallback
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
     * 위젯 재로드 (iOS parity - reloadWidget)
     * 에러/취소 발생 후 위젯을 다시 로드할 때 사용
     */
    public static void reloadWidget() {
        if (mWebView != null) {
            Log.d("bootpay", "[BootpayWidget] reloadWidget called");
            mWebView.reloadWidget();
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

    /**
     * 위젯 리렌더링 (뒤로가기 후 재렌더링)
     * widget.html 페이지로 먼저 이동 후 JS 주입하여 위젯 렌더링
     */
    public static void rerenderWidget(Activity activity, Payload payload, BootpayWidgetController controller) {
        Log.d("bootpay", "[BootpayWidget] rerenderWidget called");
        if (mWebView == null) {
            Log.e("bootpay", "WebView is not initialized. Call getView() first.");
            return;
        }
        mCallerActivity = activity;
        mPayload = payload;
        mWidgetController = controller;
        mWebView.setWidgetController(controller);

        // 위젯 페이지로 이동하면서 렌더링 (startWidget이 widget.html 로드 후 JS 주입)
        Log.d("bootpay", "[BootpayWidget] rerenderWidget - calling rerenderWidget on WebView");
        mWebView.rerenderWidget(activity, payload);
    }
}
