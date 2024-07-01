package kr.co.bootpay.android.events;


import kr.co.bootpay.android.models.widget.WidgetData;

public interface BootpayWidgetEventListener {
    void onWidgetResize(double height);
    void onWidgetReady();
    void onWidgetChangePayment(WidgetData data);
    void onWidgetChangeAgreeTerm(WidgetData data);
}
