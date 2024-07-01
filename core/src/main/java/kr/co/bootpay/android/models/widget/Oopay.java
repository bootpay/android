package kr.co.bootpay.android.models.widget;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class Oopay {
    public List<Integer> cardQuota = new ArrayList<>();

    public Oopay() { }

    public String toJsonUnderscore() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.toJson(this);
    }
}
