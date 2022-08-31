package kr.co.bootpay.android.events;


public interface BootpayEventListener {
    void onCancel(String data);
    void onError(String data);
    void onClose();
    void onIssued(String data);
    boolean onConfirm(String data);
    void onDone(String data);
}
