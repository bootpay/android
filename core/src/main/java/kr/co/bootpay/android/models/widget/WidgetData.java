package kr.co.bootpay.android.models.widget;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class WidgetData {
    private String pg;
    private String method;
    private String walletId;
    private List<WidgetTerm> selectTerms = new ArrayList<>();
    private String currency;
    private boolean termPassed;
    private boolean completed;
    private WidgetExtra extra = new WidgetExtra();

    public WidgetData() {
        this.selectTerms = new ArrayList<>();
        this.extra = new WidgetExtra();
    }

//    {"pg":"nicepay","method":"card","select_terms":[],"term_passed":true,"extra":{"direct_card_company":"국민","direct_card_quota":0,"direct_card_interest":false},"completed":true}
    public static WidgetData fromJson(String json) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        return gson.fromJson(json, WidgetData.class);
    }

    public String toJsonUnderscore() {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        return gson.toJson(this);
    }

    public String getPg() {
        return pg;
    }

    public void setPg(String pg) {
        this.pg = pg;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public List<WidgetTerm> getSelectTerms() {
        return selectTerms;
    }

    public void setSelectTerms(List<WidgetTerm> selectTerms) {
        this.selectTerms = selectTerms;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean getTermPassed() {
        return termPassed;
    }

    public void setTermPassed(boolean termPassed) {
        this.termPassed = termPassed;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public WidgetExtra getExtra() {
        return extra;
    }

    public void setExtra(WidgetExtra extra) {
        this.extra = extra;
    }
}
