package kr.co.bootpay.android.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class BootUser {
    private String id; //개발사가 발급한 고유 아이디
    private String username;  // 구매자 명
    private String birth; //생년월일 "1986-10-14"
    private String email; //구매자의 이메일 정보
    private int gender = -1; //1:남자 0:여자
    private String area; // [서울,인천,대구,광주,부산,울산,경기,강원,충청북도,충북,충청남도,충남,전라북도,전북,전라남도,전남,경상북도,경북,경상남도,경남,제주,세종,대전] 중 택 1
    private String phone; //구매자의 전화번호 (페이앱 필수)
    private String addr;

    public String getId() {
        return id;
    }

    public BootUser setId(String id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public BootUser setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getBirth() {
        return birth;
    }

    public BootUser setBirth(String birth) {
        this.birth = birth;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public BootUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public int getGender() {
        return gender;
    }

    public BootUser setGender(int gender) {
        this.gender = gender;
        return this;
    }

    public String getArea() {
        return area;
    }

    public BootUser setArea(String area) {
        this.area = area;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public BootUser setPhone(String phone) {
        if(phone != null) {
            this.phone = phone.replaceAll("-", "");
        }
        return this;
    }

    public String getAddr() {
        return addr;
    }

    public BootUser setAddr(String addr) {
        this.addr = addr;
        return this;
    }

    public String toJsonUnderscore() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.toJson(this);
    }

    public JSONObject toJsonObject() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("username", username);
            jsonObject.put("birth", birth);
            jsonObject.put("email", email);
            jsonObject.put("gender", gender);
            jsonObject.put("area", area);
            jsonObject.put("phone", phone);
            jsonObject.put("addr", addr);

        } catch (JSONException e) {
            e.printStackTrace();

        }
        return jsonObject;
    }

}
