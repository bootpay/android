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

import kr.co.bootpay.android.constants.BootpayBuildConfig;
import kr.co.bootpay.android.deprecated.BootpayRest;
import kr.co.bootpay.android.deprecated.BootpayRestImplement;
import kr.co.bootpay.android.deprecated.EasyPayUserTokenData;
import kr.co.bootpay.android.deprecated.TokenData;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootItem;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

@Deprecated
public class PasswordPaymentActivity extends AppCompatActivity implements BootpayRestImplement {
    @Deprecated
    String restApplicationId = BootpayConstants.rest_application_id;
    // 주의: server_key (secret) 는 클라이언트에 절대 포함하지 말 것 — 서버 SDK 에서만 사용. 아래 호출은 서버에서 받은 토큰을 직접 주입하도록 변경하세요.
    String serverKey = "";
    String clientKey = BootpayConstants.client_key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_payment);
    }


    public void PaymentTest(View v) {

        BootpayRest.getRestTokenWithClientKey(this, this, clientKey, serverKey);
    }

    public BootUser getBootUser() {
        String userId = "123411aaaaaaaaaaaabd4ss121";

        BootUser user = new BootUser();
        user.setId(userId);
        user.setArea("서울");
        user.setGender(1); //1: 남자, 0: 여자
        user.setEmail("test1234@gmail.com");
        user.setPhone("010-1234-4567");
        user.setBirth("1988-06-10");
        user.setUsername("홍길동");
        return user;
    }

    void BootpayTest(String userToken) {
        BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보

        BootExtra extra = new BootExtra();


        List<BootItem> items = new ArrayList<>();
        BootItem item1 = new BootItem().setName("마우's 스").setId("ITEM_CODE_MOUSE").setQty(1).setPrice(500d);
        BootItem item2 = new BootItem().setName("키보드").setId("ITEM_KEYBOARD_MOUSE").setQty(1).setPrice(500d);
        items.add(item1);
        items.add(item2);

        Payload payload = new Payload();
        payload.setClientKey(clientKey)
                .setOrderName("부트페이 결제테스트")
                .setPg("나이스페이")
                .setMethod("카드간편")
                .setOrderId("1234")
                .setUserToken(userToken)
                .setPrice(1000d)
                .setUser(user)
                .setExtra(extra)
                .setItems(items);

        Map<String, Object> map = new HashMap<>();
        map.put("1", "abcdef");
        map.put("2", "abcdef55");
//        map.put("3", 1234);
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

    @Override
    public void callbackRestToken(TokenData token) {
        BootpayRest.getEasyPayUserToken(this, this, token.access_token, getBootUser());
    }

    @Override
    public void callbackEasyPayUserToken(EasyPayUserTokenData userToken) {
        BootpayTest(userToken.user_token);
    }
}