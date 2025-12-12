package kr.co.bootpay.android.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.bootpay.android.BootpayWidget;
import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.core.R;

/**
 * Activity for fullscreen widget payment flow.
 * Replaces BootpayWidgetDialogX for better lifecycle management when transitioning to/from external apps (card company apps).
 */
public class BootpayWidgetPaymentActivity extends AppCompatActivity {

    public static final String EXTRA_REQUEST_TYPE = "request_type";

    private static Payload sPayload;
    private static BootpayEventListener sEventListener;
    private static Activity sCallerActivity;

    private FrameLayout webViewContainer;
    private BootpayWebView mWebView;

    private int mRequestType = BootpayConstant.REQUEST_TYPE_PAYMENT;

    /**
     * Launch the widget payment activity
     */
    public static void launch(Activity callerActivity, Payload payload, BootpayEventListener listener, int requestType) {
        sPayload = payload;
        sEventListener = listener;
        sCallerActivity = callerActivity;

        Intent intent = new Intent(callerActivity, BootpayWidgetPaymentActivity.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, requestType);
        callerActivity.startActivity(intent);
    }

    public static void clearStaticReferences() {
        sPayload = null;
        sEventListener = null;
        sCallerActivity = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bootpay_widget_dialog);

        Log.d("bootpay", "[WidgetPaymentActivity] onCreate");

        mRequestType = getIntent().getIntExtra(EXTRA_REQUEST_TYPE, BootpayConstant.REQUEST_TYPE_PAYMENT);

        webViewContainer = findViewById(R.id.webViewContainer);

        // Get the shared WebView from BootpayWidget
        mWebView = BootpayWidget.getView(this, (androidx.fragment.app.FragmentManager) null);

        if (mWebView != null) {
            // Remove from previous parent and add to this activity's container
            mWebView.removeFromParent(this);
            mWebView.addToParent(this, webViewContainer);
            mWebView.fullSizeWebView();
            mWebView.fadeInWebView(300);

            Log.d("bootpay", "[WidgetPaymentActivity] WebView attached to container");

            if (mRequestType == BootpayConstant.REQUEST_TYPE_PAYMENT && sPayload != null) {
                mWebView.requestWidgetPayment(sPayload, sEventListener);
            }
        } else {
            Log.e("bootpay", "[WidgetPaymentActivity] WebView is null!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("bootpay", "[WidgetPaymentActivity] onResume");
        if (mWebView != null) {
            mWebView.onResume();
            mWebView.resumeTimers();
            // Ensure WebView is visible when returning from external app
            mWebView.setAlpha(1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("bootpay", "[WidgetPaymentActivity] onPause");
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.pauseTimers();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("bootpay", "[WidgetPaymentActivity] onDestroy");
        // Don't destroy or remove WebView here - it's shared with BootpayWidget
        // and already moved to new parent in closeAndNotify()
        // Removing here would detach it from WidgetControllerActivity's container
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("bootpay", "[WidgetPaymentActivity] Back button pressed");
            closeAndNotify();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Close this activity and notify BootpayWidget to handle the widget state
     * Called when user presses back button - should rerender widget, not close the parent activity
     */
    private void closeAndNotify() {
        Log.d("bootpay", "[WidgetPaymentActivity] closeAndNotify - handling back button");

        // Remove WebView from this activity's container before finishing
        if (mWebView != null) {
            mWebView.removeFromParent(this);
        }

        // Tell BootpayWidget to handle the close and trigger rerender
        // Note: Do NOT call listener.onClose() here - that would close the parent activity
        BootpayWidget.closeActivity(this);

        // Delay finish to allow WebView to be properly attached to new parent and URL to load
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.d("bootpay", "[WidgetPaymentActivity] closeAndNotify - finishing activity");
            finish();
        }, 100);
    }

    /**
     * Remove the payment window and finish the activity
     */
    public void removePaymentWindow() {
        Log.d("bootpay", "[WidgetPaymentActivity] removePaymentWindow");
        finish();
    }

    /**
     * Finish activity after payment completion (done/error/cancel)
     * This should be called after the payment result is handled
     */
    public void finishAfterPayment() {
        Log.d("bootpay", "[WidgetPaymentActivity] finishAfterPayment");
        // Remove WebView from this activity's container before finishing
        if (mWebView != null) {
            mWebView.removeFromParent(this);
        }
        finish();
    }

    // Static reference to current activity for external access
    private static BootpayWidgetPaymentActivity sCurrentActivity;

    public static BootpayWidgetPaymentActivity getCurrentActivity() {
        return sCurrentActivity;
    }

    @Override
    protected void onStart() {
        super.onStart();
        sCurrentActivity = this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sCurrentActivity == this) {
            sCurrentActivity = null;
        }
    }
}
