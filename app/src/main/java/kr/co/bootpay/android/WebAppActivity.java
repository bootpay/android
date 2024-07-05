package kr.co.bootpay.android;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import kr.co.bootpay.android.webview.BootpayDialogX;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.webview.BootpayWebView;

public class WebAppActivity extends AppCompatActivity {

    BootpayWebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webapp);

        webView = findViewById(R.id.webview);
        //link your domain
//        webView.loadUrl("https://dev-js.bootapi.com/test/payment/");
        webView.loadUrl("https://www.naver.com/");
    }

    public void goPayment(View v) {

        Bundle webViewBundle = new Bundle();
        webView.saveState(webViewBundle);

        BootpayDialogX mDialogX = new BootpayDialogX();
        Payload payload = new Payload();
        payload.setApplicationId("5b9f51264457636ab9a07cdc")
                .setOrderName("부트페이 결제테스트")
                .setWidgetSandbox(true)
                .setWidgetKey("default-widget")
                        .setOrderId("1234");
        mDialogX.setPayload(payload);
//        mDialogX.show(getSupportFragmentManager(), "bootpay");
//        mDialogX.requestWidgetPayment(getSupportFragmentManager(), webViewBundle);

//        webView.save
//        BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보
//
//        BootExtra extra = new BootExtra()
//                .setCardQuota("0"); // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//        webView.saveState(outState);
//    }
}