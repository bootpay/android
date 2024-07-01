package kr.co.bootpay.android;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.BootpayWidgetEventListener;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.models.widget.WidgetData;
import kr.co.bootpay.android.webview.BootpayWebView;

public class WidgetActivity extends AppCompatActivity {

    Payload payload = new Payload();

    void initPayload() {
        payload.setApplicationId("5b8f6a4d396fa665fdc2b5e8")
                .setOrderName("부트페이 결제테스트")
//                .setPg("다날")
//                .setMethod("본인인증")
//                .setOrderId("1234")
//                .setAuthenticationId("1234")
                .setPrice(1000d);
//                .setUser(user)
//                .setExtra(extra)
//                .setItems(items);


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);

        BootpayWebView webView = findViewById(R.id.webview);
        //link your domain

        initPayload();

        webView.renderWidget(payload,
                new BootpayWidgetEventListener() {
                    @Override
                    public void onWidgetResize(double height) {

                    }

                    @Override
                    public void onWidgetReady() {

                    }

                    @Override
                    public void onWidgetChangePayment(WidgetData data) {

                    }

                    @Override
                    public void onWidgetChangeAgreeTerm(WidgetData data) {

                    }
                });

//        webView.rendorWidget(payload)
//                .onWidgetResize((height) -> {
//                    //resize your widget
//                })
//                .onWidgetChangePayment((data) -> {
//                    //change your payment
//                })
//                .onWidgetChangeAgreeTerms((data) -> {
//                    //change your agree terms
//                })
//                .onWidgetReady(() -> {
//                    //widget is ready
//                });
//
//
//        webView.requestPayment(payload,
//           (data) -> {
//            //payment success
//        }, (data) -> {
//            //payment fail
//        }, (data) -> {
//            //payment cancel
//        }, (data) -> {
//            //payment confirm
//        }, (data) -> {
//            //payment close
//        });


    }

    void goPayment() {
        BootpayWebView webView = findViewById(R.id.webview);
        webView.requestPayment(payload,
                new BootpayEventListener() {
                    @Override
                    public void onCancel(String data) {

                    }

                    @Override
                    public void onError(String data) {

                    }

                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onIssued(String data) {

                    }

                    @Override
                    public boolean onConfirm(String data) {
                        return false;
                    }

                    @Override
                    public void onDone(String data) {

                    }
                });
        //webView.requestPayment(payload, success, fail, cancel, confirm, close);
    }
}