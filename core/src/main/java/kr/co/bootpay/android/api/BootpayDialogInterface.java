package kr.co.bootpay.android.api;


import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.Payload;

public interface BootpayDialogInterface {
    void setEventListener(BootpayEventListener listener);
    void setPayload(Payload payload);
}
