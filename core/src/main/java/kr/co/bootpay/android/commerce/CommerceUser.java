package kr.co.bootpay.android.commerce;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Commerce 사용자 정보
 */
public class CommerceUser {
    /** 회원 타입 (guest, member 등) */
    private String membershipType = "guest";

    /** 사용자 ID */
    private String userId;

    /** 사용자 이름 */
    private String name;

    /** 전화번호 */
    private String phone;

    /** 이메일 */
    private String email;

    public CommerceUser() {}

    public String getMembershipType() {
        return membershipType;
    }

    public CommerceUser setMembershipType(String membershipType) {
        this.membershipType = membershipType;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public CommerceUser setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public CommerceUser setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public CommerceUser setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CommerceUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("membership_type", membershipType);
            if (userId != null) json.put("user_id", userId);
            if (name != null) json.put("name", name);
            if (phone != null) json.put("phone", phone);
            if (email != null) json.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
