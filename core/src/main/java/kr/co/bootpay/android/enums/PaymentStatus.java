package kr.co.bootpay.android.enums;

public enum PaymentStatus {
    DONE("DONE"),
    ERROR("ERROR"),
    CANCEL("CANCEL"),
    ISSUED("ISSUED"),
    NONE("NONE");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return this.status;
    }
}

