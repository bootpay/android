package kr.co.bootpay.android.commerce;

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

import kr.co.bootpay.android.core.R;

/**
 * Commerce 결제 Activity
 */
public class CommerceActivity extends AppCompatActivity {
    private static CommerceActivity sCurrentActivity;
    private static CommercePayload sPayload;

    private CommerceWebView commerceWebView;
    private RelativeLayout layoutProgress;
    private ProgressBar progressBar;
    private boolean doubleBackToExitPressedOnce = false;

    /**
     * Commerce Activity 실행
     */
    public static void launch(Context context, CommercePayload payload) {
        sPayload = payload;

        Intent intent = new Intent(context, CommerceActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static CommerceActivity getCurrentActivity() {
        return sCurrentActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bootpay_dialog);

        // 기존 WebView를 찾아서 CommerceWebView로 교체
        View existingWebView = findViewById(R.id.webview);
        layoutProgress = findViewById(R.id.layout_progress);
        progressBar = findViewById(R.id.progress);

        // WebView를 CommerceWebView로 교체
        if (existingWebView != null) {
            RelativeLayout parent = (RelativeLayout) existingWebView.getParent();
            int index = parent.indexOfChild(existingWebView);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) existingWebView.getLayoutParams();
            parent.removeView(existingWebView);

            commerceWebView = new CommerceWebView(this);
            commerceWebView.setId(R.id.webview);
            commerceWebView.setLayoutParams(params);
            parent.addView(commerceWebView, index);
        }

        // Progress 리스너 설정
        if (commerceWebView != null) {
            commerceWebView.setProgressListener(show -> {
                runOnUiThread(() -> {
                    if (layoutProgress != null) {
                        layoutProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            });
        }

        // 이벤트 리스너 설정
        if (commerceWebView != null && BootpayCommerce.getInstance().getEventListener() != null) {
            commerceWebView.setEventListener(BootpayCommerce.getInstance().getEventListener());
        }

        // Payload 설정 및 Commerce 시작
        if (sPayload != null && commerceWebView != null) {
            commerceWebView.setPayload(sPayload);
            commerceWebView.startCommerce();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sCurrentActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (commerceWebView != null) {
            commerceWebView.onResume();
            commerceWebView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (commerceWebView != null) {
            commerceWebView.onPause();
            commerceWebView.pauseTimers();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sCurrentActivity == this) {
            sCurrentActivity = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (commerceWebView != null) {
            commerceWebView.destroy();
            commerceWebView = null;
        }
        sPayload = null;
        BootpayCommerce.clearInstance();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (doubleBackToExitPressedOnce) {
                CommerceEventListener listener = BootpayCommerce.getInstance().getEventListener();
                if (listener != null) {
                    listener.onClose();
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
}
