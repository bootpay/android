package kr.co.bootpay.android.models;


public class BootExtra {
    private String cardQuota; //카드 결제시 할부 기간 설정 (5만원 이상 구매시)
    private String sellerName; //노출되는 판매자명 설정
    private int deliveryDay = 1; //배송일자
    private String locale = "ko"; //결제창 언어지원
    private String offerPeriod; //결제창 제공기간에 해당하는 string 값, 지원하는 PG만 적용됨
    private boolean displayCashReceipt = true; // 현금영수증 보일지 말지.. 가상계좌 KCP 옵션
    private String depositExpiration; //가상계좌 입금 만료일자 설정, yyyy-MM-dd

    private String appScheme; //모바일 앱에서 결제 완료 후 돌아오는 옵션 ( 아이폰만 적용 )
    private boolean useCardPoint = true; //카드 포인트 사용 여부 (토스만 가능)
    private String directCard = "ko"; //해당 카드로 바로 결제창 (토스만 가능)

    private boolean useOrderId = false; //가맹점 order_id로 PG로 전송
    private boolean internationalCardOnly = false; //해외 결제카드 선택 여부 (토스만 가능)
    private String phoneCarrier;  //본인인증 시 고정할 통신사명, SKT,KT,LGT 중 1개만 가능
    private boolean directAppCard = false; //카드사앱으로 direct 호출
    private boolean directSamsungpay = false; //삼성페이 바로 띄우기
    private boolean testDeposit = false;  //가상계좌 모의 입금
    private boolean enableErrorWebhook = false;  //결제 오류시 Feedback URL로 webhook
    private boolean separatelyConfirmed = true; // confirm 이벤트를 호출할지 말지, false일 경우 자동승인
    private boolean confirmOnlyRestApi = false; // REST API로만 승인 처리
    private String openType = "iframe"; //페이지 오픈 type [iframe, popup, redirect] 중 택 1
    private String redirectUrl; //open_type이 redirect일 경우 페이지 이동할 URL ( 오류 및 결제 완료 모두 수신 가능 )
    private boolean displaySuccessResult = false; // 결제 완료되면 부트페이가 제공하는 완료창으로 보여주기 ( open_type이 iframe, popup 일때만 가능 )
    private boolean displayErrorResult = true; // 결제가 실패하면 부트페이가 제공하는 실패창으로 보여주기 ( open_type이 iframe, popup 일때만 가능 )

    public String getCardQuota() {
        return cardQuota;
    }

    public BootExtra setCardQuota(String cardQuota) {
        this.cardQuota = cardQuota;
        return this;
    }

    public String getSellerName() {
        return sellerName;
    }

    public BootExtra setSellerName(String sellerName) {
        this.sellerName = sellerName;
        return this;
    }

    public int getDeliveryDay() {
        return deliveryDay;
    }

    public BootExtra setDeliveryDay(int deliveryDay) {
        this.deliveryDay = deliveryDay;
        return this;
    }

    public String getLocale() {
        return locale;
    }

    public BootExtra setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public String getOfferPeriod() {
        return offerPeriod;
    }

    public BootExtra setOfferPeriod(String offerPeriod) {
        this.offerPeriod = offerPeriod;
        return this;
    }

    public boolean isDisplayCashReceipt() {
        return displayCashReceipt;
    }

    public BootExtra setDisplayCashReceipt(boolean displayCashReceipt) {
        this.displayCashReceipt = displayCashReceipt;
        return this;
    }

    public String getDepositExpiration() {
        return depositExpiration;
    }

    public BootExtra setDepositExpiration(String depositExpiration) {
        this.depositExpiration = depositExpiration;
        return this;
    }

    public String getAppScheme() {
        return appScheme;
    }

    public BootExtra setAppScheme(String appScheme) {
        this.appScheme = appScheme;
        return this;
    }

    public boolean isUseCardPoint() {
        return useCardPoint;
    }

    public BootExtra setUseCardPoint(boolean useCardPoint) {
        this.useCardPoint = useCardPoint;
        return this;
    }

    public String getDirectCard() {
        return directCard;
    }

    public BootExtra setDirectCard(String directCard) {
        this.directCard = directCard;
        return this;
    }

    public boolean isUseOrderId() {
        return useOrderId;
    }

    public BootExtra setUseOrderId(boolean useOrderId) {
        this.useOrderId = useOrderId;
        return this;
    }

    public boolean isInternationalCardOnly() {
        return internationalCardOnly;
    }

    public BootExtra setInternationalCardOnly(boolean internationalCardOnly) {
        this.internationalCardOnly = internationalCardOnly;
        return this;
    }

    public String getPhoneCarrier() {
        return phoneCarrier;
    }

    public BootExtra setPhoneCarrier(String phoneCarrier) {
        this.phoneCarrier = phoneCarrier;
        return this;
    }

    public boolean getDirectAppCard() {
        return directAppCard;
    }

    public BootExtra setDirectAppCard(boolean directAppCard) {
        this.directAppCard = directAppCard;
        return this;
    }

    public boolean getDirectSamsungpay() {
        return directSamsungpay;
    }

    public BootExtra setDirectSamsungpay(boolean directSamsungpay) {
        this.directSamsungpay = directSamsungpay;
        return this;
    }

    public boolean getTestDeposit() {
        return testDeposit;
    }

    public BootExtra setTestDeposit(boolean testDeposit) {
        this.testDeposit = testDeposit;
        return this;
    }

    public boolean getEnableErrorWebhook() {
        return enableErrorWebhook;
    }

    public BootExtra setEnableErrorWebhook(boolean enableErrorWebhook) {
        this.enableErrorWebhook = enableErrorWebhook;
        return this;
    }

    public boolean isSeparatelyConfirmed() {
        return separatelyConfirmed;
    }

    public BootExtra setSeparatelyConfirmed(boolean separatelyConfirmed) {
        this.separatelyConfirmed = separatelyConfirmed;
        return this;
    }

    public boolean isConfirmOnlyRestApi() {
        return confirmOnlyRestApi;
    }

    public BootExtra setConfirmOnlyRestApi(boolean confirmOnlyRestApi) {
        this.confirmOnlyRestApi = confirmOnlyRestApi;
        return this;
    }

    public String getOpenType() {
        return openType;
    }

    public BootExtra setOpenType(String openType) {
        this.openType = openType;
        return this;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public BootExtra setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public boolean isDisplaySuccessResult() {
        return displaySuccessResult;
    }

    public BootExtra setDisplaySuccessResult(boolean displaySuccessResult) {
        this.displaySuccessResult = displaySuccessResult;
        return this;
    }

    public boolean isDisplayErrorResult() {
        return displayErrorResult;
    }

    public BootExtra setDisplayErrorResult(boolean displayErrorResult) {
        this.displayErrorResult = displayErrorResult;
        return this;
    }
}
