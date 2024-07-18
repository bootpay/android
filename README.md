# Bootpay Android

부트페이에서 지원하는 공식 Android 라이브러리 입니다
* Android SDK 16부터 사용 가능합니다.

### Gradle을 통한 설치
 

#### build.gradle (module)
```groovy
android {
    compileSdk 34 //Android 11 지원을 위한 30 이상 버전을 추천 

    defaultConfig {
        ...
        minSdk 16 //16 이상 버전 이상부터 지원  
        targetSdk 34 //Android 11 지원을 위한 30 이상 버전을 추천 
    }
}

dependencies {
    ...
    implementation 'io.github.bootpay:android:+' //최신 버전 추천, + 는 항상 최신 버전을 의미합니다. 
}
```

### AndroidManifest.xml 수정 

```markup
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```


#### 실행시 Webpage not available. net:ERR_CLEARTEXT_NOT_PERMITTED 에러가 나온 경우 

Android [네트워크 보안 구성](https://developer.android.com/training/articles/security-config#CleartextTrafficPermitted)에 따르면 안드로이드 9\(API28\)부터 cleartext traffic을 기본적으로를 비활성화한다고 합니다. 따라서 API 28 이후에서 Http에 접근하려면 cleartext traffic을 활성화 시켜야 합니다.

```markup
<application
        android:label="@string/app_name"
        ...
        android:usesCleartextTraffic="true">
```

## 위젯 설정 
[부트페이 관리자](https://developers.bootpay.co.kr/pg/guides/widget)에서 위젯을 생성하셔야만 사용이 가능합니다.

## 위젯 렌더링
```java
private FrameLayout webViewContainer; //위젯을 담을 레이아웃
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_widget); 
    webViewContainer = findViewById(R.id.webViewContainer);
 
    initPayload();
    bindWidgetView();
    renderWidget();
}

void initPayload() {
    BootExtra extra = new BootExtra();
    extra.setDisplaySuccessResult(true);

    payload.setApplicationId("5b9f51264457636ab9a07cdc")
            .setOrderName("부트페이 결제테스트")
            .setWidgetSandbox(true)
            .setWidgetKey("default-widget")
            .setWidgetUseTerms(true)
            .setOrderId("1234")
            .setUserToken("6667b08b04ab6d03f274d32e")
            .setPrice(1000d)
            .setExtra(extra);
}

//위젯을 webViewContainer에 바인딩 합니다. 
private void bindWidgetView() {
    BootpayWidget.bindViewUpdate(this, getSupportFragmentManager(), webViewContainer);
}

double mWidgetHeight = 300.0;
//위젯을 렌더링 합니다 
void renderWidget() {
    if(BootpayWidget.getView(this, getSupportFragmentManager()).getUrl() == null) {
        BootpayWidget.renderWidget(this, payload, new BootpayWidgetEventListener() {
            @Override
            public void onWidgetResize(double height) {
                //위젯 사이즈 변경 이벤트 
                Log.d("bootpay", "onWidgetResize: " + height);
                mWidgetHeight = height;
            }

            @Override
            public void onWidgetReady() {
                //위젯이 렌더링되면 호출되는 이벤트
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
```

## 위젯으로 결제 요청하기 
이 방법은 위젯을 사용하여 결제하는 방법입니다. 위젯을 사용하지 않고 결제를 요청하는 방법은 별도로 제공합니다.
```java
public void goPayment(View v) {
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
                    bindWidgetView();
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
```

## 결제 요청하기
이 방법은 위젯을 사용하지 않고 결제를 요청하는 방법입니다.
```java
public void PaymentTest(View v) {
    BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보

    BootExtra extra = new BootExtra()
            .setCardQuota("0"); // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)

    List<BootItem> items = new ArrayList<>();
    BootItem item1 = new BootItem().setName("마우's 스").setId("ITEM_CODE_MOUSE").setQty(1).setPrice(500d);
    BootItem item2 = new BootItem().setName("키보드").setId("ITEM_KEYBOARD_MOUSE").setQty(1).setPrice(500d);
    items.add(item1);
    items.add(item2);

    Payload payload = new Payload();
    payload.setApplicationId(BootpayConstants.application_id)
            .setOrderName("부트페이 결제테스트")
            .setPg("페이앱")
            .setMethod("네이버페이")
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
```

## 자동결제 - 빌링키 발급 요청하기 

```java 
Payload payload = new Payload();
payload.setApplicationId(application_id)
    .setOrderName("부트페이 결제테스트")
    .setPg("나이스페이")
    .setMethod("카드자동")
    .setSubscriptionId("1234") //정기결제용 orderId
    .setMethod(method)
//    .setPrice(price) 가격이 0원 이상일 경우 빌링키 발급 후 결제가 진행됩니다.
    .setUser(user)
    .setExtra(extra)
    .setItems(items);
    
Bootpay.init(getSupportFragmentManager(), getApplicationContext())
            .setPayload(payload)
            .requestSubscription(); //정기결제 실행함수 
```



## 본인인증 요청하기 

```java
Payload payload = new Payload();
payload.setApplicationId(application_id)
    .setOrderName("부트페이 결제테스트")
    .setPg("다날")
    .setMethod("본인인증")
    .setAuthenticationId("1234") //본인인증용 orderId
    .setMethod(method)
    .setPrice(price)
    .setUser(user)
    .setExtra(extra)
    .setItems(items);
    
Bootpay.init(getSupportFragmentManager(), getApplicationContext())
            .setPayload(payload)
            .requestAuthentication(); //본인인증 실행함수
```


## Bootpay Event Listener
결제 진행 상태에 따라 이벤트 함수가 실행됩니다. 각 이벤트에 대한 상세 설명은 아래를 참고하세요.

### onError 함수
결제 진행 중 오류가 발생된 경우 호출되는 함수입니다. 진행중 에러가 발생되는 경우는 다음과 같습니다.

1. **부트페이 관리자에서 활성화 하지 않은 PG, 결제수단을 사용하고자 할 때**
2. **PG에서 보내온 결제 정보를 부트페이 관리자에 잘못 입력하거나 입력하지 않은 경우**
3. **결제 진행 도중 한도초과, 카드정지, 휴대폰소액결제 막힘, 계좌이체 불가 등의 사유로 결제가 안되는 경우**
4. **PG에서 리턴된 값이 다른 Client에 의해 변조된 경우**

에러가 난 경우 해당 함수를 통해 관련 에러 메세지를 사용자에게 보여줄 수 있습니다.

 data 포맷은 아래와 같습니다.

```text
{
  action: "BootpayError",
  message: "카드사 거절",
  receipt_id: "5fffab350c20b903e88a2cff"
}
```


### onCancel 함수
결제 진행 중 사용자가 PG 결제창에서 취소 혹은 닫기 버튼을 눌러 나온 경우 입니다. ****

 data 포맷은 아래와 같습니다.

```text
{
  action: "BootpayCancel",
  message: "사용자가 결제를 취소하였습니다.",
  receipt_id: "5fffab350c20b903e88a2cff"
}
```


### onIssued 함수
가상계좌 발급이 완료되면 호출되는 함수입니다(가상계좌를 위한 Done). 가상계좌는 다른 결제와 다르게 입금할 계좌 번호 발급 이후 입금 후에 Feedback URL을 통해 통지가 됩니다. 발급된 가상계좌 정보를 issued 함수를 통해 확인하실 수 있습니다.

  data 포맷은 아래와 같습니다.

```text
{
  account: "T0309260001169"
  accounthodler: "한국사이버결제"
  action: "BootpayBankReady"
  bankcode: "BK03"
  bankname: "기업은행"
  expiredate: "2021-01-17 00:00:00"
  item_name: "테스트 아이템"
  method: "vbank"
  method_name: "가상계좌"
  order_id: "1610591554856"
  metadata: null
  payment_group: "vbank"
  payment_group_name: "가상계좌"
  payment_name: "가상계좌"
  pg: "kcp"
  pg_name: "KCP"
  price: 3000
  purchased_at: null
  ready_url: "https://dev-app.bootpay.co.kr/bank/7o044QyX7p"
  receipt_id: "5fffad430c20b903e88a2d17"
  requested_at: "2021-01-14 11:32:35"
  status: 2
  tax_free: 0
  url: "https://d-cdn.bootapi.com"
  username: "홍길동"
}
```


### onConfirm 함수
결제 승인이 되기 전 호출되는 함수입니다. 승인 이전 관련 로직을 서버 혹은 클라이언트에서 수행 후 결제를 승인해도 될 경우`BootPay.transactionConfirm(data); 또는 return true;`

코드를 실행해주시면 PG에서 결제 승인이 진행이 됩니다.

**\* 페이앱, 페이레터 PG는 이 함수가 실행되지 않고 바로 결제가 승인되는 PG 입니다. 참고해주시기 바랍니다.**

 data 포맷은 아래와 같습니다.

```text
{
  receipt_id: "5fffc0460c20b903e88a2d2c",
  action: "BootpayConfirm"
}
```


### onDone 함수
PG에서 거래 승인 이후에 호출 되는 함수입니다. 결제 완료 후 다음 결제 결과를 호출 할 수 있는 함수 입니다.

이 함수가 호출 된 후 반드시 REST API를 통해 [결제검증](https://developers.bootpay.co.kr/pg/server/receipt)을 수행해야합니다. data 포맷은 아래와 같습니다.

```text
{
  action: "BootpayDone"
  card_code: "CCKM",
  card_name: "KB국민카드",
  card_no: "0000120000000014",
  card_quota: "00",
  item_name: "테스트 아이템",
  method: "card",
  method_name: "카드결제",
  order_id: "1610596422328",
  payment_group: "card",
  payment_group_name: "신용카드",
  payment_name: "카드결제",
  pg: "kcp",
  pg_name: "KCP",
  price: 100,
  purchased_at: "2021-01-14 12:54:53",
  receipt_id: "5fffc0460c20b903e88a2d2c",
  receipt_url: "https://app.bootpay.co.kr/bill/UFMvZzJqSWNDNU9ERWh1YmUycU9hdnBkV29DVlJqdzUxRzZyNXRXbkNVZW81%0AQT09LS1XYlNJN1VoMDI4Q1hRdDh1LS10MEtZVmE4c1dyWHNHTXpZTVVLUk1R%0APT0%3D%0A",
  requested_at: "2021-01-14 12:53:42",
  status: 1,
  tax_free: 0,
  url: "https://d-cdn.bootapi.com"
}
```

## Documentation

[부트페이 개발매뉴얼](https://developer.bootpay.co.kr/)을 참조해주세요

## 기술문의

[채팅](https://bootpay.channel.io/)으로 문의

## License

[MIT License](https://opensource.org/licenses/MIT).

