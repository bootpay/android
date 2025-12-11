package kr.co.bootpay.android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

import kr.co.bootpay.android.enums.WidgetCloseAction;
import kr.co.bootpay.android.models.BootExtra;
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
        renderWidget();
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
    protected void onDestroy() {
        super.onDestroy();
        BootpayWidget.destroy();
    }

    /**
     * Step 3. Payload 설정 (iOS WIDGET_GUIDE.md 참고)
     */
    void initPayload() {
        payload = new Payload();
        payload.setApplicationId("5b9f51264457636ab9a07cdc")  // 부트페이 관리자에서 확인
                .setOrderName("테스트 상품")                    // 주문명
                .setOrderId(String.valueOf(System.currentTimeMillis())) // 주문 고유 ID
                .setPrice(1000d);                              // 결제 금액

        // Widget 필수 설정
        payload.setWidgetKey("default-widget")                 // 위젯 키
                .setWidgetSandbox(true)                        // 샌드박스 모드 (테스트: true, 운영: false)
                .setWidgetUseTerms(true);                      // 약관동의 UI 사용 여부

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
                .setCloseAction(WidgetCloseAction.NONE) // 직접 처리 (권장)

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
                    Log.d("bootpay", "[Widget] ChangePayment: " + data);
                    payload.mergeWidgetData(data);
                    updatePaymentButtonState();
                })

                // 약관동의 변경
                .setOnChangeAgreeTerm(data -> {
                    Log.d("bootpay", "[Widget] ChangeAgreeTerm: " + data);
                    payload.mergeWidgetData(data);
                    updatePaymentButtonState();
                })

                // 결제 완료
                .setOnDone(data -> {
                    Log.d("bootpay", "[Widget] Done: " + data);
                    // 가맹점 결제 결과 페이지로 이동
                    // data에서 receipt_id, order_id 등을 추출하여 서버에서 결제 정보 조회 후 표시
                })

                // 결제 에러
                .setOnError(data -> {
                    Log.d("bootpay", "[Widget] Error: " + data);
                    // 에러 후 위젯 재로드 (재시도 가능)
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        controller.reloadWidget();
                    }, 500);
                })

                // 결제 취소
                .setOnCancel(data -> {
                    Log.d("bootpay", "[Widget] Cancel: " + data);
                    finish(); // 이전 화면으로
                })

                // 결제 확인 (서버 검증 후 진행 여부 결정)
                .setOnConfirm(data -> {
                    Log.d("bootpay", "[Widget] Confirm: " + data);
                    // 서버에서 결제 정보 검증 후 true/false 반환
                    return true;
                })

                // 가상계좌 발급
                .setOnIssued(data -> {
                    Log.d("bootpay", "[Widget] Issued: " + data);
                })

                // 닫기
                .setOnClose(() -> {
                    Log.d("bootpay", "[Widget] Close");
                    finish(); // 이전 화면으로
                })

                // 위젯 재로드 필요
                .setOnNeedReload(() -> {
                    Log.d("bootpay", "[Widget] NeedReload");
                    widgetStatusReset();
                });
    }

    void bindWidgetView() {
        BootpayWidget.bindViewUpdate(this, getSupportFragmentManager(), webViewContainer);
    }

    /**
     * Step 6. 위젯 시작
     */
    void renderWidget() {
        if (BootpayWidget.getView(this, getSupportFragmentManager()).getUrl() == null) {
            // Controller 기반 렌더링
            BootpayWidget.renderWidget(this, payload, controller);
        }
    }

    void updatePaymentButtonState() {
        boolean isCompleted = payload.getWidgetIsCompleted();
        Log.d("bootpay", "widgetIsCompleted: " + isCompleted);
        runOnUiThread(() -> {
            payButton.setEnabled(isCompleted);
        });
    }

    void widgetStatusReset() {
        BootpayWidget.bindViewUpdate(this, getSupportFragmentManager(), webViewContainer);
        BootpayWidget.resizeWidget(mWidgetHeight);
    }

    /**
     * Step 7. 결제 요청
     */
    public void goPayment(View v) {
        if (!payload.getWidgetIsCompleted()) {
            // 결제수단 선택과 약관동의 미완료
            return;
        }
        // iOS 스타일: controller.requestPayment(payload)
        controller.requestPayment(payload);
    }
}
