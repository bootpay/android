package kr.co.bootpay.android.enums;

public enum BootpayEvent {
    DEFAULT_EVENT("Bootpay"),
    DONE("BootpayDone"),
    CONFIRM("BootpayConfirm"),
    ISSUED("BootpayIssued"),
    CANCEL("BootpayCancel"),
    ERROR("BootpayError"),
    CLOSE("CLOSE");

    private final String event;

    BootpayEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return this.event;
    }

    @Override
    public String toString() {
        return this.event;
    }
}

