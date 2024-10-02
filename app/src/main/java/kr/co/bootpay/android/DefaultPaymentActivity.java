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
                .setCardQuota("0"); // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)

        List<BootItem> items = new ArrayList<>();
        BootItem item1 = new BootItem().setName("마우's 스").setId("ITEM_CODE_MOUSE").setQty(1).setPrice(500d);
        BootItem item2 = new BootItem().setName("키보드").setId("ITEM_KEYBOARD_MOUSE").setQty(1).setPrice(500d);
        items.add(item1);
        items.add(item2);

        Payload payload = new Payload();
        payload.setApplicationId(BootpayConstants.application_id)
                .setOrderName("부트페이 결제테스트")
                .setPg("나이스페이")

                .setMethod("카드")
                .setOrderId("1234")
                .setPrice(1000d)
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