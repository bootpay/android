package kr.co.bootpay.android.enums;

public enum BootpayWidgetEvent {
    READY("BootpayWidgetReady"),
    RESIZE("BootpayWidgetResize"),
    CHANGE_PAYMENT("BootpayWidgetChangePayment"),
    CHANGE_TERMS_WATCH("BootpayWidgetChangeTermsWatch");

    private final String event;

    BootpayWidgetEvent(String event) {
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

