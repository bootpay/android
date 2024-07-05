package kr.co.bootpay.android.events;


import kr.co.bootpay.android.models.widget.WidgetData;

public interface BootpayWidgetPrivateEventListener {
//    void onFullSizeScreen(String data);
    void onRevertScreen(String data);
    void onCloseWidget();
}
