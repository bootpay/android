package kr.co.bootpay.android.widget;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.lang.ref.WeakReference;

import kr.co.bootpay.android.BootpayWidget;
import kr.co.bootpay.android.enums.WidgetCloseAction;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.models.widget.WidgetData;

/**
 * Widget 상태 관리 및 콜백 컨트롤러 (iOS 패리티)
 * 람다/메서드 레퍼런스 기반의 콜백 패턴 지원
 */
public class BootpayWidgetController {

    // Close action configuration
    private WidgetCloseAction closeAction = WidgetCloseAction.NONE;

    // Widget state
    private boolean isFullscreen = false;
    private double currentHeight = 300.0;

    // Activity/FragmentManager references for requestPayment
    @Nullable private WeakReference<Activity> activityRef;
    @Nullable private WeakReference<FragmentManager> fragmentManagerRef;

    // Callback interfaces
    @FunctionalInterface
    public interface OnReadyCallback {
        void onReady();
    }

    @FunctionalInterface
    public interface OnResizeCallback {
        void onResize(double height);
    }

    @FunctionalInterface
    public interface OnChangePaymentCallback {
        void onChangePayment(WidgetData data);
    }

    @FunctionalInterface
    public interface OnChangeAgreeTermCallback {
        void onChangeAgreeTerm(WidgetData data);
    }

    @FunctionalInterface
    public interface OnErrorCallback {
        void onError(String data);
    }

    @FunctionalInterface
    public interface OnCancelCallback {
        void onCancel(String data);
    }

    @FunctionalInterface
    public interface OnDoneCallback {
        void onDone(String data);
    }

    @FunctionalInterface
    public interface OnConfirmCallback {
        boolean onConfirm(String data);
    }

    @FunctionalInterface
    public interface OnIssuedCallback {
        void onIssued(String data);
    }

    @FunctionalInterface
    public interface OnCloseCallback {
        void onClose();
    }

    @FunctionalInterface
    public interface OnNeedReloadCallback {
        void onNeedReload();
    }

    // Callbacks
    @Nullable private OnReadyCallback onReady;
    @Nullable private OnResizeCallback onResize;
    @Nullable private OnChangePaymentCallback onChangePayment;
    @Nullable private OnChangeAgreeTermCallback onChangeAgreeTerm;
    @Nullable private OnErrorCallback onError;
    @Nullable private OnCancelCallback onCancel;
    @Nullable private OnDoneCallback onDone;
    @Nullable private OnConfirmCallback onConfirm;
    @Nullable private OnIssuedCallback onIssued;
    @Nullable private OnCloseCallback onClose;
    @Nullable private OnNeedReloadCallback onNeedReload;

    public BootpayWidgetController() {}

    // Setters for callbacks (fluent API)
    public BootpayWidgetController setOnReady(@Nullable OnReadyCallback callback) {
        this.onReady = callback;
        return this;
    }

    public BootpayWidgetController setOnResize(@Nullable OnResizeCallback callback) {
        this.onResize = callback;
        return this;
    }

    public BootpayWidgetController setOnChangePayment(@Nullable OnChangePaymentCallback callback) {
        this.onChangePayment = callback;
        return this;
    }

    public BootpayWidgetController setOnChangeAgreeTerm(@Nullable OnChangeAgreeTermCallback callback) {
        this.onChangeAgreeTerm = callback;
        return this;
    }

    public BootpayWidgetController setOnError(@Nullable OnErrorCallback callback) {
        this.onError = callback;
        return this;
    }

    public BootpayWidgetController setOnCancel(@Nullable OnCancelCallback callback) {
        this.onCancel = callback;
        return this;
    }

    public BootpayWidgetController setOnDone(@Nullable OnDoneCallback callback) {
        this.onDone = callback;
        return this;
    }

    public BootpayWidgetController setOnConfirm(@Nullable OnConfirmCallback callback) {
        this.onConfirm = callback;
        return this;
    }

    public BootpayWidgetController setOnIssued(@Nullable OnIssuedCallback callback) {
        this.onIssued = callback;
        return this;
    }

    public BootpayWidgetController setOnClose(@Nullable OnCloseCallback callback) {
        this.onClose = callback;
        return this;
    }

    public BootpayWidgetController setOnNeedReload(@Nullable OnNeedReloadCallback callback) {
        this.onNeedReload = callback;
        return this;
    }

    // Close action
    public WidgetCloseAction getCloseAction() {
        return closeAction;
    }

    public BootpayWidgetController setCloseAction(WidgetCloseAction closeAction) {
        this.closeAction = closeAction;
        return this;
    }

    // State management
    public boolean isFullscreen() {
        return isFullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        isFullscreen = fullscreen;
    }

    public double getCurrentHeight() {
        return currentHeight;
    }

    public void setCurrentHeight(double height) {
        this.currentHeight = height;
    }

    // Event handlers (called by BootpayWebView)
    public void handleReady() {
        if (onReady != null) onReady.onReady();
    }

    public void handleResize(double height) {
        this.currentHeight = height;
        if (onResize != null) onResize.onResize(height);
    }

    public void handleChangePayment(WidgetData data) {
        if (onChangePayment != null) onChangePayment.onChangePayment(data);
    }

    public void handleChangeAgreeTerm(WidgetData data) {
        if (onChangeAgreeTerm != null) onChangeAgreeTerm.onChangeAgreeTerm(data);
    }

    public void handleError(String data) {
        if (onError != null) onError.onError(data);
    }

    public void handleCancel(String data) {
        if (onCancel != null) onCancel.onCancel(data);
    }

    public void handleDone(String data) {
        if (onDone != null) onDone.onDone(data);
    }

    public boolean handleConfirm(String data) {
        if (onConfirm != null) return onConfirm.onConfirm(data);
        return true; // Default: proceed with transaction
    }

    public void handleIssued(String data) {
        if (onIssued != null) onIssued.onIssued(data);
    }

    public void handleClose() {
        if (onClose != null) onClose.onClose();
    }

    public void handleNeedReload() {
        if (onNeedReload != null) onNeedReload.onNeedReload();
    }

    // Check if callback is set
    public boolean hasOnConfirm() {
        return onConfirm != null;
    }

    public boolean hasOnError() {
        return onError != null;
    }

    public boolean hasOnCancel() {
        return onCancel != null;
    }

    public boolean hasOnDone() {
        return onDone != null;
    }

    public boolean hasOnIssued() {
        return onIssued != null;
    }

    public boolean hasOnClose() {
        return onClose != null;
    }

    // Reset all callbacks
    public void reset() {
        onReady = null;
        onResize = null;
        onChangePayment = null;
        onChangeAgreeTerm = null;
        onError = null;
        onCancel = null;
        onDone = null;
        onConfirm = null;
        onIssued = null;
        onClose = null;
        onNeedReload = null;
        isFullscreen = false;
        currentHeight = 300.0;
        closeAction = WidgetCloseAction.NONE;
        activityRef = null;
        fragmentManagerRef = null;
    }

    // Activity/FragmentManager binding (iOS 패리티)
    /**
     * Activity와 FragmentManager 바인딩
     * requestPayment() 메서드 사용을 위해 필요
     */
    public BootpayWidgetController bind(Activity activity, FragmentManager fragmentManager) {
        this.activityRef = new WeakReference<>(activity);
        this.fragmentManagerRef = new WeakReference<>(fragmentManager);
        return this;
    }

    /**
     * 결제 요청 (iOS 패리티)
     * bind()로 Activity/FragmentManager를 먼저 설정해야 함
     * @param payload 결제 정보
     */
    public void requestPayment(Payload payload) {
        Activity activity = activityRef != null ? activityRef.get() : null;
        FragmentManager fragmentManager = fragmentManagerRef != null ? fragmentManagerRef.get() : null;

        if (activity == null || fragmentManager == null) {
            throw new IllegalStateException("Activity and FragmentManager must be bound. Call bind(activity, fragmentManager) first.");
        }

        BootpayWidget.requestPayment(activity, fragmentManager, payload, null);
    }

    /**
     * 위젯 재로드 (iOS 패리티)
     */
    public void reloadWidget() {
        BootpayWidget.reloadWidget();
    }
}
