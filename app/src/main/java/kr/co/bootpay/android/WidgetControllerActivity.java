package kr.co.bootpay.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

import kr.co.bootpay.android.enums.WidgetCloseAction;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;
import kr.co.bootpay.android.widget.BootpayWidgetController;

/**
 * BootpayWidgetController 사용 예시 (iOS 패리티)
 * 람다/메서드 레퍼런스 기반의 콜백 패턴
 *
 * iOS WIDGET_GUIDE.md 참고하여 구현
 */
public class WidgetControllerActivity extends AppCompatActivity {

    private FrameLayout webViewContainer;
    private Button payButton;
    private TextView productNameText;
    private TextView priceLabelText;

    private Payload payload = new Payload();
    private BootpayWidgetController controller;
    private double mWidgetHeight = 516.0; // 기본 위젯 높이
    private boolean isPaymentCompleted = false; // 결제 완료 여부 (onDone 호출됨)

    // 결제 정보
    private static final String ORDER_NAME = "테스트 상품";
    private static final double PRICE = 1000.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);

        // UI 바인딩
        payButton = findViewById(R.id.payButton);
        webViewContainer = findViewById(R.id.webViewContainer);
        productNameText = findViewById(R.id.productName);
        priceLabelText = findViewById(R.id.priceLabel);

        // UI 초기화
        updateUI();

        initPayload();
        initController();
        bindWidgetView();
        // WebView가 레이아웃에 추가된 후 렌더링 시작
        webViewContainer.post(this::renderWidget);
    }

    /**
     * UI 업데이트
     */
    void updateUI() {
        productNameText.setText(ORDER_NAME);
        priceLabelText.setText(formatPrice(PRICE));
        payButton.setText(formatPrice(PRICE) + " 결제하기");
    }

    /**
     * 가격 포맷팅 (1000 -> "1,000원")
     */
    String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
        return formatter.format((long) price) + "원";
    }

    @Override
    protected void onResume() {
        super.onResume();
        // iOS 방식: 뒤로가기는 BootpayWebView.collapseToOriginal()이 처리
        // 여기서는 특별한 처리 불필요
    }

    @Override
    public void onBackPressed() {
        // 전체화면 상태면 축소 후 위젯 재로드, 아니면 Activity 종료
        if (BootpayWidget.getView(this, getSupportFragmentManager()) != null &&
            BootpayWidget.getView(this, getSupportFragmentManager()).isExpanded()) {
            Log.d("bootpay", "[Widget] onBackPressed - collapsing fullscreen");
            BootpayWidget.collapseAndReload(this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BootpayWidget.destroy();
    }

    /**
     * Step 3. Payload 설정 (iOS WIDGET_GUIDE.md 참고)
     */
    void initPayload() {
        payload = new Payload();
        payload.setApplicationId("5b8f6a4d396fa665fdc2b5e8")  // Android 전용 application_id
                .setOrderName("테스트 상품")                    // 주문명
                .setOrderId(String.valueOf(System.currentTimeMillis())) // 주문 고유 ID
                .setPrice(1000d);                              // 결제 금액

        // Widget 필수 설정
        payload.setWidgetKey("default-widget")                 // 위젯 키
                .setWidgetSandbox(true)                        // 샌드박스 모드 (테스트: true, 운영: false)
                .setWidgetUseTerms(false);                     // 약관동의 UI 사용 여부 (테스트용 false)

        // User 설정 (iOS와 동일)
        BootUser user = new BootUser();
        user.setId("test_user_1234");
        user.setUsername("홍길동");
        user.setEmail("test@bootpay.co.kr");
        user.setPhone("01012341234");
        payload.setUser(user);

        // Extra 설정 (선택)
        BootExtra extra = new BootExtra();
        extra.setAppScheme("bootpayapp");                      // 앱 스킴 (앱투앱 결제 복귀용)
        // extra.setDisplaySuccessResult(true);                // 결제 성공 결과 화면 표시 여부
        // extra.setDisplayErrorResult(true);                  // 결제 에러 결과 화면 표시 여부
        payload.setExtra(extra);
    }

    /**
     * Step 5. WidgetController 설정 (iOS WIDGET_GUIDE.md 참고)
     */
    void initController() {
        controller = new BootpayWidgetController()
                // Activity/FragmentManager 바인딩 (requestPayment 사용을 위해 필요)
                .bind(this, getSupportFragmentManager())

                // 닫기 동작 설정 (3가지 옵션 중 선택)
                // FINISH_ACTIVITY - Activity 종료
                // FRAGMENT_POP    - Fragment pop
                // NONE            - 아무 동작 안함 (onDone/onError/onCancel/onClose에서 직접 처리)
                .setCloseAction(WidgetCloseAction.NONE) // 직접 처리 (iOS와 동일)

                // 위젯 준비 완료
                .setOnReady(() -> {
                    Log.d("bootpay", "[Widget] Ready");
                })

                // 위젯 높이 변경
                .setOnResize(height -> {
                    Log.d("bootpay", "[Widget] Resize: " + height);
                    mWidgetHeight = height;
                    runOnUiThread(() -> {
                        // dp를 px로 변환
                        float density = getResources().getDisplayMetrics().density;
                        int heightPx = (int) (height * density);
                        ViewGroup.LayoutParams params = webViewContainer.getLayoutParams();
                        params.height = heightPx;
                        webViewContainer.setLayoutParams(params);
                    });
                })

                // 결제수단 변경
                .setOnChangePayment(data -> {
                    Log.d("bootpay", "[Widget] ChangePayment: pg=" + data.getPg() + ", method=" + data.getMethod());
                    payload.mergeWidgetData(data);
                    updatePaymentButtonState();
                })

                // 약관동의 변경
                .setOnChangeAgreeTerm(data -> {
                    Log.d("bootpay", "[Widget] ChangeAgreeTerm: termPassed=" + data.getTermPassed() + ", completed=" + data.getCompleted());
                    payload.mergeWidgetData(data);
                    updatePaymentButtonState();
                })

                // 결제 완료 (iOS onDone 참고)
                .setOnDone(data -> {
                    Log.d("bootpay", "[Widget] Done: " + data);
                    isPaymentCompleted = true;

                    // displaySuccessResult = false 일 때: 바로 축소 후 결과 페이지로 이동
                    boolean displaySuccessResult = payload.getExtra() != null
                            && payload.getExtra().isDisplaySuccessResult();

                    if (!displaySuccessResult) {
                        // 축소 후 결과 페이지로 이동
                        BootpayWidget.collapseAndFinish(this);
                        runOnUiThread(() -> {
                            PaymentResultActivity.launch(this, data);
                            finish();
                        });
                    }
                    // displaySuccessResult = true 면 결과 화면 유지 (close에서 처리)
                })

                // 결제 에러 (iOS onError 참고)
                .setOnError(data -> {
                    Log.d("bootpay", "[Widget] Error: " + data);
                    // displayErrorResult = false 일 때: 축소 후 위젯 재로드
                    boolean displayErrorResult = payload.getExtra() != null
                            && payload.getExtra().isDisplayErrorResult();
                    if (!displayErrorResult) {
                        // iOS와 동일: 축소 후 0.5초 딜레이 후 위젯 재로드
                        BootpayWidget.collapseAndReload(this);
                    }
                    // displayErrorResult = true 면 에러 화면 유지 (close에서 처리)
                })

                // 결제 취소 (iOS onCancel 참고)
                .setOnCancel(data -> {
                    Log.d("bootpay", "[Widget] Cancel: " + data);
                    // iOS와 동일: 축소 후 위젯 재로드
                    BootpayWidget.collapseAndReload(this);
                })

                // 결제 확인 (서버 검증 후 진행 여부 결정)
                .setOnConfirm(data -> {
                    Log.d("bootpay", "[Widget] Confirm: " + data);
                    // 서버에서 결제 정보 검증 후 true/false 반환
                    return true; // 결제 진행 (iOS와 동일)
                })

                // 가상계좌 발급 (iOS onIssued 참고)
                .setOnIssued(data -> {
                    Log.d("bootpay", "[Widget] Issued: " + data);
                })

                // 닫기 (iOS onClose 참고)
                .setOnClose(() -> {
                    Log.d("bootpay", "[Widget] Close, isPaymentCompleted=" + isPaymentCompleted);

                    // displaySuccessResult 또는 displayErrorResult 사용 시
                    // close 이벤트에서 처리
                    boolean displaySuccessResult = payload.getExtra() != null
                            && payload.getExtra().isDisplaySuccessResult();
                    boolean displayErrorResult = payload.getExtra() != null
                            && payload.getExtra().isDisplayErrorResult();

                    if (displaySuccessResult && isPaymentCompleted) {
                        // 결과 화면에서 닫기 - 결과 페이지로 이동
                        BootpayWidget.closeDialog(this);
                        // finish(); // closeAction에 따라 처리
                    } else if (displayErrorResult) {
                        // 에러 화면에서 닫기 - 위젯 재로드
                        BootpayWidget.collapseAndReload(this);
                    } else if (!isPaymentCompleted) {
                        // 그 외: 축소만
                        BootpayWidget.closeDialog(this);
                    }
                });
    }

    void bindWidgetView() {
        BootpayWidget.bindViewUpdate(this, getSupportFragmentManager(), webViewContainer);
    }

    /**
     * Step 6. 위젯 시작
     */
    void renderWidget() {
        Log.d("bootpay", "[Widget] Starting widget render with controller");
        BootpayWidget.renderWidget(this, payload, controller);
    }

    void updatePaymentButtonState() {
        boolean isCompleted = payload.getWidgetIsCompleted();
        Log.d("bootpay", "widgetIsCompleted: " + isCompleted);
        runOnUiThread(() -> {
            payButton.setEnabled(isCompleted);
        });
    }

    /**
     * Step 7. 결제 요청 (iOS requestPayment 참고)
     * iOS와 동일하게 expandToFullscreen 후 결제 요청
     */
    public void goPayment(View v) {
        if (!payload.getWidgetIsCompleted()) {
            // 결제수단 선택과 약관동의 미완료 (iOS와 동일한 알림)
            showAlert("알림", "결제수단 선택과 약관동의를 완료해주세요.");
            return;
        }
        Log.d("bootpay", "[Widget] Request Payment");
        isPaymentCompleted = false; // 결제 완료 플래그 초기화
        // iOS 스타일: controller.requestPayment(payload)
        controller.requestPayment(payload);
    }

    /**
     * 알림 다이얼로그 표시 (iOS showAlert 참고)
     */
    void showAlert(String title, String message) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("확인", null)
                    .show();
        });
    }
}
