package kr.co.bootpay.android.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.bootpay.android.models.widget.Oopay;
import kr.co.bootpay.android.models.widget.WidgetData;
import kr.co.bootpay.android.models.widget.WidgetExtra;
import kr.co.bootpay.android.models.widget.WidgetTerm;

public class Payload {
    private String applicationId = "";
    private String pg = "";
    private String method = "";
    private List<String> methods = new ArrayList<>();
    private String orderName = "";
    private Double price = 0.0;
    private Double taxFree = 0.0;

    private Double depositPrice = 0.0;

    private String currency;

    private String orderId = "";
    private String subscriptionId = "";
    private String authenticationId = "";

//    private int useOrderId = 0; // 1: 사용, 0: 미사용
    private Map<String, Object> metadata = new HashMap<>();
//    private boolean showAgreeWindow = false;
    private String userToken = "";

//    private String bootKey = "";

//    private String easyPayUserToken;

    private BootExtra extra = new BootExtra();
    private BootUser user = new BootUser();
    private List<BootItem> items = new ArrayList<>();

    //widget 관련
    private String widgetKey = "";
    private boolean widgetUseTerms;
    private boolean widgetSandbox;
    private Oopay widgetOopay = new Oopay();

    //widget data response 관련
    private String _widgetWalletId;
    private WidgetData _widgetData = new WidgetData();
    private List<WidgetTerm> _widgetSelectTerms = new ArrayList<>();
    private boolean _widgetTermPassed;
    private boolean _widgetCompleted;

    boolean WidgetIsCompleted() {
        return (_widgetTermPassed || false) && (_widgetCompleted || false);
    }

    public Payload() { }

    public static Payload fromJson(String json) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Payload payload = gson.fromJson(json, Payload.class);
//        if(payload.paramJson != null && payload.paramJson.length() > 0) {
//            payload.params = new Gson().fromJson(
//                    payload.paramJson, new TypeToken<HashMap<String, Object>>() {}.getType()
//            );
//        }

        return payload;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public Payload setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getPg() {
        return pg;
    }

    public Payload setPg(String pg) {
        this.pg = pg;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Payload setMethod(String method) {
        this.method = method;
        return this;
    }

    public List<String> getMethods() {
        return methods;
    }

    public Payload setMethods(List<String> methods) {
        this.methods = methods;
        return this;
    }

    public String getOrderName() {
        return orderName;
    }

    public Payload setOrderName(String orderName) {
        this.orderName = orderName;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public Payload setPrice(Double price) {
        this.price = price;
        return this;
    }

    public Double getTaxFree() {
        return taxFree;
    }

    public Payload setTaxFree(Double taxFree) {
        this.taxFree = taxFree;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public Payload setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

//    public int getUseOrderId() {
//        return useOrderId;
//    }

//    public Payload setUseOrderId(int useOrderId) {
//        this.useOrderId = useOrderId;
//        return this;
//    }

//    public Map<String, Object> getParams() {
//        return params;
//    }
//
//    public Payload setParams(Map<String, Object> params) {
//        this.params = params;
//        return this;
//    }
    public Map<String, Object> getMetadata() {
    return metadata;
}

    public Payload setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

//    public String getAccountExpireAt() {
//        return accountExpireAt;
//    }
//
//    public Payload setAccountExpireAt(String accountExpireAt) {
//        this.accountExpireAt = accountExpireAt;
//        return this;
//    }

//    public boolean getShowAgreeWindow() {
//        return showAgreeWindow;
//    }
//
//    public Payload setShowAgreeWindow(boolean showAgreeWindow) {
//        this.showAgreeWindow = showAgreeWindow;
//        return this;
//    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public Payload setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public String getAuthenticationId() {
        return authenticationId;
    }

    public Payload setAuthenticationId(String authenticationId) {
        this.authenticationId = authenticationId;
        return this;
    }


    public String getUserToken() {
        return userToken;
    }

    public Payload setUserToken(String userToken) {
        this.userToken = userToken;
        return this;
    }

    //    public String getBootKey() {
//        return bootKey;
//    }
//
//    public Payload setBootKey(String bootKey) {
//        this.bootKey = bootKey;
//        return this;
//    }

//    public String getParamJson() {
//        return paramJson;
//    }
//
//    public void setParamJson(String paramJson) {
//        this.paramJson = paramJson;
//    }

//    public String getEasyPayUserToken() {
//        return easyPayUserToken;
//    }
//
//    public Payload setEasyPayUserToken(String easyPayUserToken) {
//        this.easyPayUserToken = easyPayUserToken;
//        return this;
//    }



    public BootExtra getExtra() {
        return extra;
    }

    public Payload setExtra(BootExtra extra) {
        this.extra = extra;
        return this;
    }

    public BootUser getUser() {
        return user;
    }

    public Payload setUser(BootUser user) {
        this.user = user;
        return this;
    }

    public List<BootItem> getItems() {
        return items;
    }

    public Payload setItems(List<BootItem> items) {
        this.items = items;
        return this;
    }

    public String toJsonUnderscore() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("application_id", applicationId);
            jsonObject.put("pg", pg);
            if(methods.size() > 0) {
                jsonObject.put("method", new JSONArray(methods));
            } else {
                jsonObject.put("method", method);
            }
            jsonObject.put("order_name", orderName);
            jsonObject.put("price", price);
            jsonObject.put("tax_free", taxFree);

            jsonObject.put("order_id", orderId);
            jsonObject.put("subscription_id", subscriptionId);
            jsonObject.put("authentication_id", authenticationId);
            jsonObject.put("metadata", new JSONObject(metadata));

            jsonObject.put("extra", extra.toJsonObject());
            jsonObject.put("user", user.toJsonObject());

            if(items.size() > 0) {
                List<JSONObject> itemList = new ArrayList<>();
                for(BootItem item : items) {
                    itemList.add(item.toJsonObject());
                }
                jsonObject.put("items", new JSONArray(itemList));
            }


            if(widgetKey != null && widgetKey.length() > 0) {
                jsonObject.put("widget", 1);
                jsonObject.put("use_bootpay_inapp_sdk", true);
            }
            jsonObject.put("key", widgetKey);
            jsonObject.put("use_terms", widgetUseTerms);
            jsonObject.put("sandbox", widgetSandbox);

            jsonObject.put("currency", currency);
            jsonObject.put("wallet_id", _widgetWalletId);
            jsonObject.put("terms", new JSONArray(_widgetSelectTerms));
            jsonObject.put("user_token", userToken);


            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            return gson.toJson(this);
        }
    }


    public String targetJsonUnderscore(Object object) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

//        gson.fr

        return gson.toJson(object);
    }


    public String getWidgetKey() {
        return widgetKey;
    }

    public Payload setWidgetKey(String widgetKey) {
        this.widgetKey = widgetKey;
        return this;
    }

    public boolean getWidgetUseTerms() {
        return widgetUseTerms;
    }

    public Payload setWidgetUseTerms(boolean widgetUseTerms) {
        this.widgetUseTerms = widgetUseTerms;
        return this;
    }

    public boolean getWidgetSandbox() {
        return widgetSandbox;
    }

    public Payload setWidgetSandbox(boolean widgetSandbox) {
        this.widgetSandbox = widgetSandbox;
        return this;
    }

    public Oopay getWidgetOopay() {
        return widgetOopay;
    }

    public Payload setWidgetOopay(Oopay widgetOopay) {
        this.widgetOopay = widgetOopay;
        return this;
    }

    public String getWidgetWalletId() {
        return _widgetWalletId;
    }

    public Payload setWidgetWalletId(String _widgetWalletId) {
        this._widgetWalletId = _widgetWalletId;
        return this;
    }

    public WidgetData getWidgetData() {
        return _widgetData;
    }

    public Payload setWidgetData(WidgetData _widgetData) {
        this._widgetData = _widgetData;
        return this;
    }

    public List<WidgetTerm> getWidgetSelectTerms() {
        return _widgetSelectTerms;
    }

    public Payload setWidgetSelectTerms(List<WidgetTerm> _widgetSelectTerms) {
        this._widgetSelectTerms = _widgetSelectTerms;
        return this;
    }

    public boolean getWidgetTermPassed() {
        return _widgetTermPassed;
    }

    public Payload setWidgetTermPassed(boolean _widgetTermPassed) {
        this._widgetTermPassed = _widgetTermPassed;
        return this;
    }

    public boolean getWidgetCompleted() {
        return _widgetCompleted;
    }

    public Payload setWidgetCompleted(boolean _widgetCompleted) {
        this._widgetCompleted = _widgetCompleted;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public Payload setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public void mergeWidgetData(WidgetData data) {
        if(data == null) return;
        this._widgetData = data;
        if(data.getPg() != null) this.pg = data.getPg();
        if(data.getMethod() != null) this.method = data.getMethod();

        this._widgetCompleted = data.isCompleted();
        this._widgetTermPassed = data.isTermPassed();
        if(data.getCurrency() != null) this.currency = data.getCurrency();
        if(data.getSelectTerms() != null) this._widgetSelectTerms = data.getSelectTerms();
        if(data.getWalletId() != null) this._widgetWalletId = data.getWalletId();

        if(this.extra == null) this.extra = new BootExtra();

        if(data.getExtra() != null) {
            WidgetExtra widgetExtra = data.getExtra();
            if(widgetExtra.getDirectCardCompany() != null) this.extra.setDirectCardCompany(widgetExtra.getDirectCardCompany());
            if(widgetExtra.getDirectCardQuota() != null) this.extra.setDirectCardQuota(widgetExtra.getDirectCardQuota());
            if(widgetExtra.getCardQuota() != null) this.extra.setCardQuota(widgetExtra.getCardQuota());
        }
    }
}
