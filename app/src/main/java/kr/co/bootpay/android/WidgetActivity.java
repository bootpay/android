package kr.co.bootpay.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.BootpayWidgetEventListener;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.models.widget.WidgetData;
import kr.co.bootpay.android.webview.BootpayWebView;

public class WidgetActivity extends AppCompatActivity {

    private FrameLayout webViewContainer;
    Payload payload = new Payload();
    Button button;
//    BootpayWebView webView;

    void initPayload() {
//        BootExtra extra = new BootExtra();
//        extra.setDisplaySuccessResult(true);

        payload.setApplicationId("5b9f51264457636ab9a07cdc")
                .setOrderName("부트페이 결제테스트")
                .setWidgetSandbox(true)
                .setWidgetKey("default-widget")
                .setWidgetSandbox(true)
                .setWidgetUseTerms(true)
//                .setPg("다날")
//                .setMethod("본인인증")
                .setOrderId("1234")
                .setUserToken("6667b08b04ab6d03f274d32e")
//                .setAuthenticationId("1234")
                .setPrice(1000d);
//                .setUser(user)
//                .setExtra(extra);
//                .setItems(items);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BootpayWidget.destroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        button = findViewById(R.id.button);
        webViewContainer = findViewById(R.id.webViewContainer);

        initPayload();
        loadWidgetView();
        renderWidget();
    }



    BootpayWebView mWebView;
    private void loadWidgetView() {
        BootpayWidget.bindViewUpdate(this, getSupportFragmentManager(), webViewContainer);
    }

    Double widgetHeight = 0.0;
    void renderWidget() {
        if(BootpayWidget.getView(this, getSupportFragmentManager()).getUrl() == null) {
            BootpayWidget.renderWidget(this, payload, new BootpayWidgetEventListener() {
                @Override
                public void onWidgetResize(double height) {
                    Log.d("bootpay", "onWidgetResize: " + height);
                }

                @Override
                public void onWidgetReady() {
                    Log.d("bootpay", "onWidgetReady: ");

                }

                @Override
                public void onWidgetChangePayment(WidgetData data) {
                    Log.d("bootpay", "onWidgetChangePayment: " + data);
                    payload.mergeWidgetData(data);
                    updatePaymentButtonState();
                }

                @Override
                public void onWidgetChangeAgreeTerm(WidgetData data) {
                    Log.d("bootpay", "onWidgetChangeAgreeTerm: " + data);
                    payload.mergeWidgetData(data);
                    updatePaymentButtonState();
                }

                @Override
                public void needReloadWidget() {
                    Log.d("bootpay", "needReloadWidget ");
                    widgetStatusReset();
                }
            });
        }
    }

    void updatePaymentButtonState() {
        Log.d("bootpay", "updatePaymentButtonState: " + payload.getWidgetIsCompleted());
        runOnUiThread(() -> {
            button.setEnabled(payload.getWidgetIsCompleted());
//            button.setBackgroundColor(payload.getWidgetIsCompleted() ? Color.GREEN : 0xFFAAAAAA);
        });
//        button.setEnabled(payload.getWidgetIsCompleted());
//        button.setBackgroundColor(payload.getWidgetIsCompleted() ? Color.GREEN : 0xFFAAAAAA);
    }

    void widgetStatusReset() {
        BootpayWidget.bindViewUpdate(this, getSupportFragmentManager(), webViewContainer);
        BootpayWidget.resizeWidget(widgetHeight);
    }

    public void goPayment(View v) {

        Log.d("bootpay", "goPayment");

        BootpayWidget.requestPayment(
                this,
                getSupportFragmentManager(),
                payload,
                new BootpayEventListener() {
                    @Override
                    public void onCancel(String data) {
                        Log.d("bootpay", "cancel: " + data);
                    }

                    @Override
                    public void onError(String data) {
                        Log.d("bootpay", "error: " + data);
                    }

                    @Override
                    public void onClose() {
                        Log.d("bootpay", "close");
                        BootpayWidget.removePaymentWindow();
                        loadWidgetView();
                    }

                    @Override
                    public void onIssued(String data) {
                        Log.d("bootpay", "issued: " + data);
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        Log.d("bootpay", "confirm: " + data);
                        return true;
                    }

                    @Override
                    public void onDone(String data) {
                        Log.d("bootpay", "done: " + data);
                    }
                });

    }
}