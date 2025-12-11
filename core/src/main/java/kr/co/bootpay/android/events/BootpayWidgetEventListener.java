package kr.co.bootpay.android.events;


import kr.co.bootpay.android.models.widget.WidgetData;

/**
 * Widget 이벤트 리스너 (iOS 패리티)
 * 기본 메서드는 하위 호환성을 위해 빈 구현 제공
 */
public interface BootpayWidgetEventListener {

    // Required callbacks (기존)
    void onWidgetResize(double height);
    void onWidgetReady();
    void onWidgetChangePayment(WidgetData data);
    void onWidgetChangeAgreeTerm(WidgetData data);
    void needReloadWidget();

    // Optional callbacks (iOS 패리티 - default 구현 제공)

    /**
     * Widget에서 에러 발생 시 호출
     * @param data 에러 데이터 (JSON)
     */
    default void onWidgetError(String data) {}

    /**
     * Widget에서 결제 취소 시 호출
     * @param data 취소 데이터 (JSON)
     */
    default void onWidgetCancel(String data) {}

    /**
     * Widget에서 결제 완료 시 호출
     * @param data 완료 데이터 (JSON)
     */
    default void onWidgetDone(String data) {}

    /**
     * Widget에서 결제 확인 요청 시 호출
     * @param data 확인 데이터 (JSON)
     * @return true면 결제 진행, false면 중단
     */
    default boolean onWidgetConfirm(String data) {
        return true;
    }

    /**
     * 가상계좌 발급 완료 시 호출
     * @param data 발급 데이터 (JSON)
     */
    default void onWidgetIssued(String data) {}

    /**
     * Widget 닫힐 때 호출
     */
    default void onWidgetClose() {}
}
