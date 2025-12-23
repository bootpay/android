package kr.co.bootpay.android.commerce;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

/**
 * Bootpay Commerce SDK
 * Commerce 결제 요청을 위한 메인 클래스
 */
public class BootpayCommerce {
    private static BootpayCommerce instance;

    /** 환경 설정 (development, stage, production) */
    private String environmentMode = "production";

    /** 현재 Payload */
    private CommercePayload payload;

    /** 콜백 리스너들 */
    private CommerceEventListener eventListener;

    private BootpayCommerce() {}

    public static synchronized BootpayCommerce getInstance() {
        if (instance == null) {
            instance = new BootpayCommerce();
        }
        return instance;
    }

    /**
     * 환경 모드 설정 (development, stage, production)
     */
    public static BootpayCommerce setEnvironmentMode(String mode) {
        getInstance().environmentMode = mode;
        return getInstance();
    }

    public String getEnvironmentMode() {
        return environmentMode;
    }

    public CommercePayload getPayload() {
        return payload;
    }

    public CommerceEventListener getEventListener() {
        return eventListener;
    }

    /**
     * Commerce 결제 요청 (requestCheckout)
     *
     * @param activity Activity 컨텍스트
     * @param payload  결제 정보
     * @return BootpayCommerce 인스턴스 (체이닝용)
     */
    public static BootpayCommerce requestCheckout(Activity activity, CommercePayload payload) {
        getInstance().payload = payload;

        CommerceActivity.launch(activity, payload);

        return getInstance();
    }

    /**
     * 결제 완료 콜백 설정
     */
    public BootpayCommerce onDone(OnDoneListener listener) {
        if (eventListener == null) {
            eventListener = new CommerceEventListenerAdapter();
        }
        ((CommerceEventListenerAdapter) eventListener).setOnDoneListener(listener);
        return this;
    }

    /**
     * 결제 에러 콜백 설정
     */
    public BootpayCommerce onError(OnErrorListener listener) {
        if (eventListener == null) {
            eventListener = new CommerceEventListenerAdapter();
        }
        ((CommerceEventListenerAdapter) eventListener).setOnErrorListener(listener);
        return this;
    }

    /**
     * 결제 취소 콜백 설정
     */
    public BootpayCommerce onCancel(OnCancelListener listener) {
        if (eventListener == null) {
            eventListener = new CommerceEventListenerAdapter();
        }
        ((CommerceEventListenerAdapter) eventListener).setOnCancelListener(listener);
        return this;
    }

    /**
     * 결제창 닫힘 콜백 설정
     */
    public BootpayCommerce onClose(OnCloseListener listener) {
        if (eventListener == null) {
            eventListener = new CommerceEventListenerAdapter();
        }
        ((CommerceEventListenerAdapter) eventListener).setOnCloseListener(listener);
        return this;
    }

    /**
     * 결제창 닫기
     */
    public static void removePaymentWindow() {
        CommerceActivity currentActivity = CommerceActivity.getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.finish();
        }
        clearInstance();
    }

    /**
     * 인스턴스 초기화
     */
    public static void clearInstance() {
        if (instance != null) {
            instance.payload = null;
            instance.eventListener = null;
        }
    }

    // 콜백 인터페이스들
    public interface OnDoneListener {
        void onDone(Map<String, Object> data);
    }

    public interface OnErrorListener {
        void onError(Map<String, Object> data);
    }

    public interface OnCancelListener {
        void onCancel(Map<String, Object> data);
    }

    public interface OnCloseListener {
        void onClose();
    }

    /**
     * CommerceEventListener 어댑터 (체이닝 콜백용)
     */
    private static class CommerceEventListenerAdapter implements CommerceEventListener {
        private OnDoneListener onDoneListener;
        private OnErrorListener onErrorListener;
        private OnCancelListener onCancelListener;
        private OnCloseListener onCloseListener;

        void setOnDoneListener(OnDoneListener listener) {
            this.onDoneListener = listener;
        }

        void setOnErrorListener(OnErrorListener listener) {
            this.onErrorListener = listener;
        }

        void setOnCancelListener(OnCancelListener listener) {
            this.onCancelListener = listener;
        }

        void setOnCloseListener(OnCloseListener listener) {
            this.onCloseListener = listener;
        }

        @Override
        public void onDone(Map<String, Object> data) {
            if (onDoneListener != null) onDoneListener.onDone(data);
        }

        @Override
        public void onError(Map<String, Object> data) {
            if (onErrorListener != null) onErrorListener.onError(data);
        }

        @Override
        public void onCancel(Map<String, Object> data) {
            if (onCancelListener != null) onCancelListener.onCancel(data);
        }

        @Override
        public void onClose() {
            if (onCloseListener != null) onCloseListener.onClose();
        }
    }
}
