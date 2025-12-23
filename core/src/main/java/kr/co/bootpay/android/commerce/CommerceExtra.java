package kr.co.bootpay.android.commerce;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Commerce 추가 옵션
 */
public class CommerceExtra {
    /** 분리 확인 여부 (기본값: false) */
    private boolean separatelyConfirmed = false;

    /** 즉시 주문 생성 여부 (기본값: true) */
    private boolean createOrderImmediately = true;

    public CommerceExtra() {}

    public boolean isSeparatelyConfirmed() {
        return separatelyConfirmed;
    }

    public CommerceExtra setSeparatelyConfirmed(boolean separatelyConfirmed) {
        this.separatelyConfirmed = separatelyConfirmed;
        return this;
    }

    public boolean isCreateOrderImmediately() {
        return createOrderImmediately;
    }

    public CommerceExtra setCreateOrderImmediately(boolean createOrderImmediately) {
        this.createOrderImmediately = createOrderImmediately;
        return this;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("separately_confirmed", separatelyConfirmed);
            json.put("create_order_immediately", createOrderImmediately);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
