package kr.co.bootpay.android.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BootItem {
    private String name; //아이템 이름
    private int qty; //상품 판매된 수량
    private String id; //상품의 고유 PK
    private Double price; //상품 하나당 판매 가격
    private String cat1; //카테고리 상
    private String cat2; //카테고리 중
    private String cat3; //카테고리 하

    public BootItem() {}
    public BootItem(String itemName, int qty, String unique, Double price, String cat1, String cat2, String cat3) {
        this.name = itemName;
        this.qty = qty;
        this.id = unique;
        this.price = price;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
    }

    public String getName() {
        return name;
    }

    public BootItem setName(String name) {
        this.name = name;
        return this;
    }

    public int getQty() {
        return qty;
    }

    public BootItem setQty(int qty) {
        this.qty = qty;
        return this;
    }

    public String getId() {
        return id;
    }

    public BootItem setId(String id) {
        this.id = id;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public BootItem setPrice(Double price) {
        this.price = price;
        return this;
    }

    public String getCat1() {
        return cat1;
    }

    public BootItem setCat1(String cat1) {
        this.cat1 = cat1;
        return this;
    }

    public String getCat2() {
        return cat2;
    }

    public BootItem setCat2(String cat2) {
        this.cat2 = cat2;
        return this;
    }

    public String getCat3() {
        return cat3;
    }

    public BootItem setCat3(String cat3) {
        this.cat3 = cat3;
        return this;
    }

    public String toJsonUnderscore() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.toJson(this);
    }
}
