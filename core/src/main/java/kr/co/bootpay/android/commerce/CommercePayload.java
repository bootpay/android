package kr.co.bootpay.android.commerce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Commerce 결제 요청 Payload
 */
public class CommercePayload {
    /** 클라이언트 키 (Commerce 대시보드에서 발급) */
    private String clientKey = "";

    /** 청구서/주문 이름 */
    private String name;

    /** 메모 */
    private String memo;

    /** 사용자 정보 */
    private CommerceUser user;

    /** 결제 금액 */
    private double price = 0;

    /** 결제 완료 후 리다이렉트 URL */
    private String redirectUrl;

    /** 사용량 API URL (구독 결제 시) */
    private String usageApiUrl;

    /** 자동 로그인 사용 여부 */
    private boolean useAutoLogin = false;

    /** 요청 ID (주문 ID) */
    private String requestId;

    /** 알림 사용 여부 */
    private boolean useNotification = false;

    /** 상품 목록 */
    private List<CommerceProduct> products;

    /** 메타데이터 (추가 정보) */
    private Map<String, Object> metadata;

    /** 추가 옵션 */
    private CommerceExtra extra;

    public CommercePayload() {}

    // Getters and Setters
    public String getClientKey() {
        return clientKey;
    }

    public CommercePayload setClientKey(String clientKey) {
        this.clientKey = clientKey;
        return this;
    }

    public String getName() {
        return name;
    }

    public CommercePayload setName(String name) {
        this.name = name;
        return this;
    }

    public String getMemo() {
        return memo;
    }

    public CommercePayload setMemo(String memo) {
        this.memo = memo;
        return this;
    }

    public CommerceUser getUser() {
        return user;
    }

    public CommercePayload setUser(CommerceUser user) {
        this.user = user;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public CommercePayload setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public CommercePayload setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public String getUsageApiUrl() {
        return usageApiUrl;
    }

    public CommercePayload setUsageApiUrl(String usageApiUrl) {
        this.usageApiUrl = usageApiUrl;
        return this;
    }

    public boolean isUseAutoLogin() {
        return useAutoLogin;
    }

    public CommercePayload setUseAutoLogin(boolean useAutoLogin) {
        this.useAutoLogin = useAutoLogin;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public CommercePayload setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public boolean isUseNotification() {
        return useNotification;
    }

    public CommercePayload setUseNotification(boolean useNotification) {
        this.useNotification = useNotification;
        return this;
    }

    public List<CommerceProduct> getProducts() {
        return products;
    }

    public CommercePayload setProducts(List<CommerceProduct> products) {
        this.products = products;
        return this;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public CommercePayload setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public CommerceExtra getExtra() {
        return extra;
    }

    public CommercePayload setExtra(CommerceExtra extra) {
        this.extra = extra;
        return this;
    }

    /**
     * JSON Object로 변환
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("client_key", clientKey);
            if (name != null) json.put("name", name);
            if (memo != null) json.put("memo", memo);
            if (user != null) json.put("user", user.toJSON());
            if (price > 0) json.put("price", price);
            if (redirectUrl != null) json.put("redirect_url", redirectUrl);
            if (usageApiUrl != null) json.put("usage_api_url", usageApiUrl);
            json.put("use_auto_login", useAutoLogin);
            if (requestId != null) json.put("request_id", requestId);
            json.put("use_notification", useNotification);

            if (products != null && !products.isEmpty()) {
                JSONArray productsArray = new JSONArray();
                for (CommerceProduct product : products) {
                    productsArray.put(product.toJSON());
                }
                json.put("products", productsArray);
            }

            if (metadata != null && !metadata.isEmpty()) {
                json.put("metadata", new JSONObject(metadata));
            }

            if (extra != null) json.put("extra", extra.toJSON());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * JSON 문자열로 변환
     */
    public String toJSONString() {
        return toJSON().toString();
    }
}
