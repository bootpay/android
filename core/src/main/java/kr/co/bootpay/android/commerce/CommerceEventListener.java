package kr.co.bootpay.android.commerce;

import java.util.Map;

/**
 * Commerce 결제 이벤트 리스너
 */
public interface CommerceEventListener {
    /**
     * 결제 완료 시 호출
     * @param data 결제 결과 데이터
     */
    void onDone(Map<String, Object> data);

    /**
     * 결제 에러 발생 시 호출
     * @param data 에러 데이터
     */
    void onError(Map<String, Object> data);

    /**
     * 결제 취소 시 호출
     * @param data 취소 데이터
     */
    void onCancel(Map<String, Object> data);

    /**
     * 가상계좌 발급 완료 시 호출
     * @param data 가상계좌 발급 데이터
     */
    void onIssued(Map<String, Object> data);

    /**
     * 결제창 닫힘 시 호출
     */
    void onClose();
}
