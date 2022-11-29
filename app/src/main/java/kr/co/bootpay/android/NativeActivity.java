package kr.co.bootpay.android;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.bootpay.android.constants.Browser;
import kr.co.bootpay.android.constants.OpenType;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootItem;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.BrowserOpenType;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.models.statistics.BootStatItem;

public class NativeActivity extends AppCompatActivity {
//    private String application_id = "5b8f6a4d396fa665fdc2b5e8"; //production
//    private String application_id = "5b9f51264457636ab9a07cdc"; //development


    Context context;

    Spinner spinner_pg;
    Spinner spinner_method;
    EditText edit_price;
    EditText edit_non_tax;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        this.context = this;
        spinner_pg = findViewById(R.id.spinner_pg);
        spinner_method = findViewById(R.id.spinner_method);
        edit_price = findViewById(R.id.edit_price);
        edit_non_tax = findViewById(R.id.edit_non_tax);

        bootpayInit();
//        bootpayAnalyticsUserTrace();
//        bootpayAnalyticsPageTrace();
    }

    void bootpayInit() {
        BootpayAnalytics.init(this, BootpayConstants.application_id); //this는 context
    }

    public void goTraceUser(View v) {
        BootpayAnalytics.userTrace(
                "user_1234", //user_id
                "test1234@gmail.com", //email
                "홍길동", //user name
                1, //성별 남자:1, 여자:0
                "19941014", //생년월일
                "01012345678", //고객 전화번호
                "서울" //ex) 서울|인천|대구|광주|부산|울산|경기|강원|충청북도|충북|충청남도|충남|전라북도|전북|전라남도|전남|경상북도|경북|경상남도|경남|제주|세종|대전 중 1
        );
    }

    public void goTracePage(View v) {
        //통계용 데이터 추가
        List<BootStatItem> items = new ArrayList<>();
        BootStatItem item1 = new BootStatItem().setItemName("마우's 스").setUnique("ITEM_CODE_MOUSE").setPrice(500d);
        BootStatItem item2 = new BootStatItem().setItemName("키보드").setUnique("ITEM_KEYBOARD_MOUSE").setPrice(500d);
        items.add(item1);
        items.add(item2);

        BootpayAnalytics.pageTrace(
                "url", // 앱 페이지 url 또는 화면이름
                "abcde",  // 분류에 해당하지만 아직 기능 미지원
                items //상품정보
        );
    }

    public void goRequest(View v) {
        BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보

        BootExtra extra = new BootExtra()
                .setCardQuota("0,2,3"); // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)

//                .setOpenType("popup");

        Double price = 1000d;

        try {
            price = Double.parseDouble(edit_price.getText().toString());
        } catch (Exception e){}

        String pg = BootpayValueHelper.pgToString(spinner_pg.getSelectedItem().toString());
        String method = BootpayValueHelper.methodToString(spinner_method.getSelectedItem().toString());

        List<BootItem> items = new ArrayList<>();
        BootItem item1 = new BootItem().setName("마우's 스").setId("ITEM_CODE_MOUSE").setQty(1).setPrice(500d);
        BootItem item2 = new BootItem().setName("키보드").setId("ITEM_KEYBOARD_MOUSE").setQty(1).setPrice(500d);
        items.add(item1);
        items.add(item2);

        Payload payload = new Payload();
        payload.setApplicationId(BootpayConstants.application_id)
                .setOrderName("부트페이 결제테스트")
                .setPg(pg)
                .setOrderId("1234")
                .setMethod(method)
                .setPrice(price)
                .setUser(user)
                .setExtra(extra)
                .setItems(items);

        Map<String, Object> map = new HashMap<>();
        map.put("1", "abcdef");
        map.put("2", "abcdef55");
        map.put("3", 1234);
        payload.setMetadata(map);
//        payload.setMetadata(new Gson().toJson(map));

        Bootpay.init(getSupportFragmentManager(), getApplicationContext())
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
                        Bootpay.removePaymentWindow();
                    }

                    @Override
                    public void onIssued(String data) {
                        Log.d("bootpay", "issued: " +data);
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        Log.d("bootpay", "confirm: " + data);
//                        Bootpay.transactionConfirm(data); //재고가 있어서 결제를 진행하려 할때 true (방법 1)
                        return true; //재고가 있어서 결제를 진행하려 할때 true (방법 2)
//                        return false; //결제를 진행하지 않을때 false
                    }

                    @Override
                    public void onDone(String data) {
                        Log.d("done", data);
                    }
                }).requestPayment();
    }

    public void goTotalRequest(View v) {
        BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보
        BootExtra extra = new BootExtra().setCardQuota("0,2,3");  // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)
        Double price = 1000d;



        List<BootItem> items = new ArrayList<>();
        BootItem item1 = new BootItem().setName("마우's 스").setId("ITEM_CODE_MOUSE").setQty(1).setPrice(500d);
        BootItem item2 = new BootItem().setName("키보드").setId("ITEM_KEYBOARD_MOUSE").setQty(1).setPrice(500d);
        items.add(item1);
        items.add(item2);

        Payload payload = new Payload();

        Map<String, Object> map = new HashMap<>();
        map.put("1", "abcdef");
        map.put("2", "abcdef55");
        map.put("3", 1234);
        payload.setMetadata(map);

        payload.setApplicationId(BootpayConstants.application_id)
                .setOrderName("맥\"북프로's 임다")
                .setOrderId("1234")
                .setPrice(price)
                .setUser(user)
                .setExtra(extra)
                .setItems(items);

        Bootpay.init(getSupportFragmentManager(), getApplicationContext())
                .setPayload(payload)
                .setEventListener(new BootpayEventListener() {
                    @Override
                    public void onCancel(String data) {
                        Log.d("cancel", data);
                    }

                    @Override
                    public void onError(String data) {
                        Log.d("error", data);
                    }

                    @Override
                    public void onClose() {
                        Log.d("bootpay", "close");
                        Bootpay.dismissWindow();
                    }

                    @Override
                    public void onIssued(String data) {
                        Log.d("issued", data);
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        Log.d("confirm", data);
                        //Bootpay.transactionConfirm(data); //재고가 있어서 결제를 진행하려 할때 true (방법 1)
                        return true; //재고가 있어서 결제를 진행하려 할때 true (방법 2)
//                        return false; //결제를 진행하지 않을때 false
                    }

                    @Override
                    public void onDone(String data) {
                        Log.d("done", data);
                    }
                }).requestPayment();
    }


    public void goSubscriptionRequest(View v) {
        BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보

        BootExtra extra = new BootExtra()
                .setCardQuota("0,2,3");  // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)


        Double price = 1000d;

        String pg = "나이스페이";
        String method = "카드자동";

        List<BootItem> items = new ArrayList<>();
        BootItem item1 = new BootItem().setName("마우's 스").setId("ITEM_CODE_MOUSE").setQty(1).setPrice(500d);
        BootItem item2 = new BootItem().setName("키보드").setId("ITEM_KEYBOARD_MOUSE").setQty(1).setPrice(500d);
        items.add(item1);
        items.add(item2);

        Payload payload = new Payload();
        payload.setApplicationId(BootpayConstants.application_id)
                .setOrderName("부트페이 결제테스트")
                .setPg(pg)
                .setSubscriptionId("1234") //정기결제용 orderId
                .setMethod(method)
                .setPrice(price)
                .setUser(user)
                .setExtra(extra)
                .setItems(items);

        Map<String, Object> map = new HashMap<>();
        map.put("1", "abcdef");
        map.put("2", "abcdef55");
        map.put("3", 1234);
        payload.setMetadata(map);
//        payload.setMetadata(new Gson().toJson(map));

        Bootpay.init(getSupportFragmentManager(), getApplicationContext())
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
                        Log.d("bootpay", "close ");
                        Bootpay.removePaymentWindow();
                    }

                    @Override
                    public void onIssued(String data) {
                        Log.d("bootpay", "issued: " +data);
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        Log.d("bootpay", "confirm: " + data);
//                        Bootpay.transactionConfirm(data); //재고가 있어서 결제를 진행하려 할때 true (방법 1)
                        return true; //재고가 있어서 결제를 진행하려 할때 true (방법 2)
//                        return false; //결제를 진행하지 않을때 false
                    }

                    @Override
                    public void onDone(String data) {
                        Log.d("done", data);
                    }
                }).requestSubscription(); //정기결제 실행함수
    }


    public void goAuthenticationRequest(View v) {
        BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보

        BootExtra extra = new BootExtra()
                .setCardQuota("0,2,3");  // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)


        Double price = 1000d;

        String pg = "danal";
        String method = "auth";

        List<BootItem> items = new ArrayList<>();
        BootItem item1 = new BootItem().setName("마우's 스").setId("ITEM_CODE_MOUSE").setQty(1).setPrice(500d);
        BootItem item2 = new BootItem().setName("키보드").setId("ITEM_KEYBOARD_MOUSE").setQty(1).setPrice(500d);
        items.add(item1);
        items.add(item2);

        Payload payload = new Payload();
        payload.setApplicationId(BootpayConstants.application_id)
                .setOrderName("부트페이 결제테스트")
                .setPg(pg)
                .setAuthenticationId("1234")
                .setMethod(method)
                .setPrice(price)
                .setUser(user)
                .setExtra(extra)
                .setItems(items);

        Map<String, Object> map = new HashMap<>();
        map.put("1", "abcdef");
        map.put("2", "abcdef55");
        map.put("3", 1234);
        payload.setMetadata(map);
//        payload.setMetadata(new Gson().toJson(map));

        Bootpay.init(getSupportFragmentManager(), getApplicationContext())
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
                        Bootpay.removePaymentWindow();
                    }

                    //본인인증에선 호출되지 않음
                    @Override
                    public void onIssued(String data) {
                        Log.d("bootpay", "issued: " +data);
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        return true; //본인인증에선 호출되지 않음
                    }

                    @Override
                    public void onDone(String data) {
                        Log.d("done", data);
                    }
                }).requestAuthentication(); //본인인증 실행함수
    }
}