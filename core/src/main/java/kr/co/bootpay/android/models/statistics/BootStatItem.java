package kr.co.bootpay.android.models.statistics;

public class BootStatItem {
    public String unique;
    public Double price;
    public String itemName;
    public String itemImg;
    public String cat1;
    public String cat2;
    public String cat3;

    public BootStatItem() {}

    public BootStatItem(String unique, Double price, String itemName, String itemImg, String cat1, String cat2, String cat3) {
        this.unique = unique;
        this.price = price;
        this.itemName = itemName;
        this.itemImg = itemImg;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
    }

    public String getItemName() {
        return itemName;
    }

    public BootStatItem setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public String getItemImg() {
        return itemImg;
    }

    public BootStatItem setItemImg(String itemImg) {
        this.itemImg = itemImg;
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
