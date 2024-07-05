package kr.co.bootpay.android.api;

import android.app.Activity;

import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.events.BootpayWidgetEventListener;
import kr.co.bootpay.android.models.Payload;

public interface BootpayWidgetInterface {
    void renderWidget(Activity activity, Payload payload, BootpayWidgetEventListener listener);

    void requestPayment(Payload payload, BootpayEventListener listener);

//    void requestPayment(androidx.fragment.app.FragmentManager fragmentManager, Payload payload, BootpayEventListener listener);

//    void setEventListeners(BootpayEventListener listener);
}
