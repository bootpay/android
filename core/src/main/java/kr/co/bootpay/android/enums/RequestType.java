package kr.co.bootpay.android.enums;

public enum RequestType {
    PAYMENT("requestPayment"),
    SUBSCRIPTION("requestSubscription"),
    AUTH("requestAuthentication"),
    PASSWORD("requestPassword");

    private final String type;

    RequestType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}

