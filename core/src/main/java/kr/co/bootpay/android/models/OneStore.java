package kr.co.bootpay.android.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OneStore {
    public String adId;
    public String simOperator;
    public String installerPackageName;

    public String getAdId() {
        return adId;
    }

    public OneStore setAdId(String adId) {
        this.adId = adId;
        return this;
    }

    public String getSimOperator() {
        return simOperator;
    }

    public OneStore setSimOperator(String simOperator) {
        this.simOperator = simOperator;
        return this;
    }

    public String getInstallerPackageName() {
        return installerPackageName;
    }

    public OneStore setInstallerPackageName(String installerPackageName) {
        this.installerPackageName = installerPackageName;
        return this;
    }

    public String toJsonUnderscore() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.toJson(this);
    }
}
