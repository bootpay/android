package kr.co.bootpay.android.commerce;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Commerce 상품 정보
 */
public class CommerceProduct {
    /** 상품 ID (Commerce 대시보드에서 생성된 ID) */
    private String productId = "";

    /** 구독 기간 (-1: 무기한) */
    private int duration = -1;

    /** 수량 */
    private int quantity = 1;

    public CommerceProduct() {}

    public String getProductId() {
        return productId;
    }

    public CommerceProduct setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public CommerceProduct setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public CommerceProduct setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("product_id", productId);
            json.put("duration", duration);
            json.put("quantity", quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
