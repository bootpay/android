//package kr.co.bootpay.android;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import kr.co.bootpay.android.constants.BootpayConstant;
//import kr.co.bootpay.android.events.BootpayEventListener;
//import kr.co.bootpay.android.models.Payload;
//import kr.co.bootpay.android.webview.BootpayWebView;
//import kr.co.bootpay.core.R;
//
//public class BootpayActivity extends AppCompatActivity {
//    public static final int REQUEST_PAYMENT = 1;
//    public static final int REQUEST_SUBSCRIPTION = 2;
//    public static final int REQUEST_AUTHENTICATION = 3;
//
//    private Payload mPayload;
//    private BootpayEventListener mBootpayEventListener;
//    BootpayWebView mWebView = null;
//    RelativeLayout mLayoutProgress = null;
//    ProgressBar mProgressBar = null;
//    int mRequestType = -1;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // Set your layout here
//        setContentView(R.layout.layout_bootpay_dialog);
//
//        initResources();
//        loadParameters();
//        startBootpayRequest();
//    }
//
//    void initResources() {
//        if(mWebView == null) mWebView = findViewById(R.id.webview);
//        mWebView.setExtEventListener(isShow -> {
//            runOnUiThread(() -> {
//                if(mLayoutProgress != null) mLayoutProgress.setVisibility(isShow == true ? View.VISIBLE : View.GONE);
//            });
//        });
//
//        mLayoutProgress = findViewById(R.id.layout_progress);
//        mProgressBar = findViewById(R.id.progress);
//    }
//
//    void loadParameters() {
//        mPayload = Bootpay.getBuilder().getPayload();
//        mBootpayEventListener = Bootpay.getBuilder().getBootpayEventListener();
//        mRequestType = Bootpay.getBuilder().getRequestType();
//    }
//
//    private void startBootpayRequest() {
////        switch (requestType) {
////            case REQUEST_PAYMENT:
////                // Handle payment request
////                break;
////            case REQUEST_SUBSCRIPTION:
////                // Handle subscription request
////                break;
////            case REQUEST_AUTHENTICATION:
////                // Handle authentication request
////                break;
////        }
//
//        if(mBootpayEventListener != null) mWebView.setEventListener(mBootpayEventListener);
//        if(mPayload != null) {
//            mWebView.setInjectedJS(BootpayConstant.getJSPay(mPayload, mRequestType));
//            mWebView.setPayload(mPayload);
//        }
//        mWebView.startBootpay();
//    }
//
//    private boolean doubleBackToExitPressedOnce = false;
//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
//    }
//    // Define your other methods to handle Bootpay processes
//}
//
