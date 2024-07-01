package kr.co.bootpay.android.api;

import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.BootpayWidgetEventListener;
import kr.co.bootpay.android.models.Payload;

public interface BootpayWidgetInterface {
    void renderWidget(Payload payload, BootpayWidgetEventListener listener);

    void requestPayment(Payload payload, BootpayEventListener listener);

//    void setEventListeners(BootpayEventListener listener);
}
