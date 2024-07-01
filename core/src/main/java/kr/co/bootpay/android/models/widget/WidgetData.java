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

}
