package kr.co.bootpay.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootItem;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

public class DefaultPaymentActivity extends AppCompatActivity {
//    BootpayWebView bootpayWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_payment);
    }


    public void PaymentTest(View v) {
        BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보

        BootExtra extra = new BootExtra()
                .setOpenType("iframe")
                .setCardQuota("0,2,3"); // 일시불, 2개월, 3개월 할부 허용

        // 상품 정보 (화면에 표시된 상품)
        List<BootItem> items = new ArrayList<>();
        BootItem item = new BootItem()
                .setName("프리미엄 레더 토트백")
                .setId("ITEM_PREMIUM_LEATHER_BAG")
                .setQty(1)
                .setPrice(80100d);
        items.add(item);

        Payload payload = new Payload();
        payload.setApplicationId(BootpayConstants.application_id)
                .setOrderName("프리미엄 레더 토트백")
                .setPg("나이스페이")
                .setMethod("카드")
                .setOrderId("ORDER_" + System.currentTimeMillis())
                .setPrice(80100d)
                .setUser(user)
                .setExtra(extra)
                .setItems(items);

        Map<String, Object> map = new HashMap<>();
        map.put("1", "abcdef");
        map.put("2", "abcdef55");
        map.put("3", 1234);
        payload.setMetadata(map);
//        payload.setMetadata(new Gson().toJson(map));

        Bootpay.init(getSupportFragmentManager())
                .setPayload(payload)
                .setEventListener(new BootpayEventListener() {
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
//                        Bootpay.removePaymentWindow();
                        Bootpay.dismiss();
                    }

                    @Override
                    public void onIssued(String data) {
                        Log.d("bootpay", "issued: " +data);
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        if(checkClientValidation(data)) {
                            // Bootpay().transactionConfirm() // 승인 요청(방법 1), 이때는 return false로 해야함
                            return true; //승인 요청(방법 2), return true시 내부적으로 승인을 요청함
                        } else {
                            Bootpay.dismiss(); // 결제창 닫기
                            return false; //승인하지 않음
                        }
                    }

                    @Override
                    public void onDone(String data) {
                        Log.d("done", data);
                    }
                }).requestPayment();

    }

    private boolean checkClientValidation(String data) {
        // 유효성 검사 로직을 작성하세요
        return true;
    }

    private boolean proceedServerConfirm(String data) {
        // 서버 통신 - 서버에서 유효성 검사 후 승인
        return true;
    }
}