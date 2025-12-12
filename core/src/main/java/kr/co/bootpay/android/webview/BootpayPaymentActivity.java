package kr.co.bootpay.android.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.bootpay.android.constants.BootpayConstant;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.core.R;

/**
 * Activity for fullscreen payment flow.
 * Replaces BootpayDialogX for better lifecycle management when transitioning to/from external apps.
 */
public class BootpayPaymentActivity extends AppCompatActivity {

    public static final String EXTRA_REQUEST_TYPE = "request_type";

    private static Payload sPayload;
    private static BootpayEventListener sEventListener;

    private BootpayWebView mWebView;
    private RelativeLayout mLayoutProgress;
    private ProgressBar mProgressBar;

    private int mRequestType = BootpayConstant.REQUEST_TYPE_PAYMENT;
    private boolean doubleBackToExitPressedOnce = false;

    /**
     * Launch the payment activity
     */
    public static void launch(Context context, Payload payload, BootpayEventListener listener, int requestType) {
        sPayload = payload;
        sEventListener = listener;

        Intent intent = new Intent(context, BootpayPaymentActivity.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, requestType);

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void clearStaticReferences() {
        sPayload = null;
        sEventListener = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bootpay_dialog);

        mRequestType = getIntent().getIntExtra(EXTRA_REQUEST_TYPE, BootpayConstant.REQUEST_TYPE_PAYMENT);

        mWebView = findViewById(R.id.webview);
        mLayoutProgress = findViewById(R.id.layout_progress);
        mProgressBar = findViewById(R.id.progress);

        mWebView.setExtEventListener(isShow -> {
            runOnUiThread(() -> {
                if (mLayoutProgress != null) {
                    mLayoutProgress.setVisibility(isShow ? View.VISIBLE : View.GONE);
                }
            });
        });

        if (sEventListener != null) {
            mWebView.setEventListener(sEventListener);
        }

        if (sPayload != null) {
            mWebView.setInjectedJS(BootpayConstant.getJSPay(sPayload, mRequestType));
            mWebView.setPayload(sPayload);

            if (sPayload.getWidgetKey() == null || sPayload.getWidgetKey().isEmpty()) {
                mWebView.setInjectedJSBeforePayStart(BootpayConstant.getJSBeforePayStart(this));
                mWebView.startPayment();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
            mWebView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.pauseTimers();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        clearStaticReferences();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (doubleBackToExitPressedOnce) {
                if (sEventListener != null) {
                    sEventListener.onClose();
                }
                finish();
                return true;
            }

            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "결제를 종료하시려면 '뒤로' 버튼을 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Remove the payment window and finish the activity
     */
    public void removePaymentWindow() {
        if (mWebView != null) {
            mWebView.removePaymentWindow();
        }
        finish();
    }

    /**
     * Confirm the transaction
     */
    public void transactionConfirm() {
        if (mWebView != null) {
            mWebView.transactionConfirm();
        }
    }

    // Static reference to current activity for external access
    private static BootpayPaymentActivity sCurrentActivity;

    public static BootpayPaymentActivity getCurrentActivity() {
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
