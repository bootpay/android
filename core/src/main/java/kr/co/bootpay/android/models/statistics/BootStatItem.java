package kr.co.bootpay.android.models.statistics;

public class BootStatItem {
    public String unique;
    public Double price;
    public String item_name;
    public String item_img;
    public String cat1;
    public String cat2;
    public String cat3;

    public BootStatItem() {}

    public BootStatItem(String unique, Double price, String itemName, String itemImg, String cat1, String cat2, String cat3) {
        this.unique = unique;
        this.price = price;
        this.item_name = itemName;
        this.item_img = itemImg;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
    }

    public String getItemName() {
        return item_name;
    }

    public BootStatItem setItemName(String item_name) {
        this.item_name = item_name;
        return this;
    }

    public String getItemImg() {
        return item_img;
    }

    public BootStatItem setItemImg(String item_img) {
        this.item_img = item_img;
        return this;
    }

    public String getUnique() {
        return unique;
    }

    public BootStatItem setUnique(String unique) {
        this.unique = unique;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public BootStatItem setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getCat1() {
        return cat1;
    }

    public BootStatItem setCat1(String cat1) {
        this.cat1 = cat1;
        return this;
    }

    public String getCat2() {
        return cat2;
    }

    public BootStatItem setCat2(String cat2) {
        this.cat2 = cat2;
        return this;
    }

    public String getCat3() {
        return cat3;
    }

    public BootStatItem setCat3(String cat3) {
        this.cat3 = cat3;
        return this;
    }
}
