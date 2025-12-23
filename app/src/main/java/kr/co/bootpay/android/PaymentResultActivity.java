package kr.co.bootpay.android;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 결제 결과 화면 (iOS PaymentResultController 참조)
 * 세련된 UI와 애니메이션 적용
 */
public class PaymentResultActivity extends AppCompatActivity {

    private static final String EXTRA_PAYMENT_DATA = "payment_data";
    private static final String EXTRA_PAYMENT_MAP = "payment_map";

    // UI 컴포넌트
    private CardView cardView;
    private View iconCircle;
    private TextView statusEmoji;
    private TextView titleText;
    private TextView messageText;
    private LinearLayout detailContainer;
    private Button confirmButton;

    // 데이터
    private String paymentDataJson;
    private Map<String, Object> paymentDataMap;

    // 컬러
    private int successColor = Color.parseColor("#22C55E");
    private int errorColor = Color.parseColor("#EF4444");
    private int warningColor = Color.parseColor("#F97316");
    private int primaryColor = Color.parseColor("#667EEA");

    /**
     * JSON 문자열로 실행
     */
    public static void launch(Context context, String paymentData) {
        Intent intent = new Intent(context, PaymentResultActivity.class);
        intent.putExtra(EXTRA_PAYMENT_DATA, paymentData);
        context.startActivity(intent);
    }

    /**
     * Map으로 실행 (Commerce용)
     */
    public static void launch(Context context, Map<String, Object> paymentData) {
        Intent intent = new Intent(context, PaymentResultActivity.class);
        intent.putExtra(EXTRA_PAYMENT_MAP, new HashMap<>(paymentData));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();

        // ActionBar 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 데이터 가져오기
        paymentDataJson = getIntent().getStringExtra(EXTRA_PAYMENT_DATA);
        Serializable mapData = getIntent().getSerializableExtra(EXTRA_PAYMENT_MAP);
        if (mapData instanceof HashMap) {
            paymentDataMap = (HashMap<String, Object>) mapData;
        }

        // 결과 표시
        displayResult();

        // 애니메이션 시작
        startEntranceAnimation();

        // 확인 버튼 클릭
        confirmButton.setOnClickListener(v -> finish());
    }

    private void setupUI() {
        // 메인 레이아웃 (FrameLayout으로 버튼 고정)
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setBackgroundColor(Color.parseColor("#F8FAFC"));

        // ScrollView
        ScrollView scrollView = new ScrollView(this);
        FrameLayout.LayoutParams scrollParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        scrollParams.bottomMargin = dp(90); // 버튼 공간 확보
        scrollView.setLayoutParams(scrollParams);
        scrollView.setClipToPadding(false);

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        contentLayout.setPadding(dp(20), dp(40), dp(20), dp(20));
        contentLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        // Card View
        cardView = new CardView(this);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setRadius(dp(20));
        cardView.setCardElevation(dp(12));
        cardView.setUseCompatPadding(true);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardView.setLayoutParams(cardParams);

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(dp(24), dp(40), dp(24), dp(30));
        cardContent.setGravity(Gravity.CENTER_HORIZONTAL);

        // Icon Circle Background
        iconCircle = new View(this);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(dp(100), dp(100));
        iconCircle.setLayoutParams(circleParams);

        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(Color.parseColor("#E8F5E9")); // 기본 성공 배경
        iconCircle.setBackground(circleBg);

        // Status Emoji (아이콘 원 안에)
        FrameLayout iconContainer = new FrameLayout(this);
        iconContainer.setLayoutParams(new LinearLayout.LayoutParams(dp(100), dp(100)));

        statusEmoji = new TextView(this);
        statusEmoji.setTextSize(48);
        statusEmoji.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams emojiParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        statusEmoji.setLayoutParams(emojiParams);

        iconContainer.addView(iconCircle);
        iconContainer.addView(statusEmoji);
        cardContent.addView(iconContainer);

        // Title
        titleText = new TextView(this);
        titleText.setTextSize(26);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.topMargin = dp(24);
        titleText.setLayoutParams(titleParams);
        cardContent.addView(titleText);

        // Message
        messageText = new TextView(this);
        messageText.setTextSize(15);
        messageText.setTextColor(Color.parseColor("#64748B"));
        messageText.setGravity(Gravity.CENTER);
        messageText.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams msgParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        msgParams.topMargin = dp(8);
        messageText.setLayoutParams(msgParams);
        cardContent.addView(messageText);

        // Divider
        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#E2E8F0"));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        dividerParams.topMargin = dp(24);
        divider.setLayoutParams(dividerParams);
        cardContent.addView(divider);

        // Detail Container
        detailContainer = new LinearLayout(this);
        detailContainer.setOrientation(LinearLayout.VERTICAL);
        detailContainer.setPadding(0, dp(20), 0, 0);
        LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        detailContainer.setLayoutParams(detailParams);
        cardContent.addView(detailContainer);

        cardView.addView(cardContent);
        contentLayout.addView(cardView);

        scrollView.addView(contentLayout);
        rootLayout.addView(scrollView);

        // Confirm Button (하단 고정)
        confirmButton = new Button(this);
        confirmButton.setText("확인");
        confirmButton.setTextSize(17);
        confirmButton.setTypeface(null, Typeface.BOLD);
        confirmButton.setTextColor(Color.WHITE);
        confirmButton.setAllCaps(false);
        confirmButton.setStateListAnimator(null); // 기본 그림자 제거

        GradientDrawable buttonBg = new GradientDrawable();
        buttonBg.setColor(primaryColor);
        buttonBg.setCornerRadius(dp(14));
        confirmButton.setBackground(buttonBg);
        confirmButton.setElevation(dp(4));

        FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(56));
        btnParams.gravity = Gravity.BOTTOM;
        btnParams.leftMargin = dp(20);
        btnParams.rightMargin = dp(20);
        btnParams.bottomMargin = dp(20);
        confirmButton.setLayoutParams(btnParams);

        rootLayout.addView(confirmButton);
        setContentView(rootLayout);
    }

    private void startEntranceAnimation() {
        // 카드 애니메이션
        cardView.setAlpha(0f);
        cardView.setScaleX(0.8f);
        cardView.setScaleY(0.8f);
        cardView.setTranslationY(dp(50));

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(cardView, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(cardView, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(cardView, "scaleY", 0.8f, 1f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(cardView, "translationY", dp(50), 0f);

        AnimatorSet cardAnimSet = new AnimatorSet();
        cardAnimSet.playTogether(fadeIn, scaleX, scaleY, translateY);
        cardAnimSet.setDuration(500);
        cardAnimSet.setInterpolator(new OvershootInterpolator(1.0f));
        cardAnimSet.start();

        // 버튼 애니메이션
        confirmButton.setAlpha(0f);
        confirmButton.setTranslationY(dp(30));
        confirmButton.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(300)
                .start();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void displayResult() {
        // Commerce Map 데이터 처리
        if (paymentDataMap != null && !paymentDataMap.isEmpty()) {
            String event = (String) paymentDataMap.get("event");
            if (event != null) {
                displayCommerceResult(paymentDataMap, event);
                return;
            }
        }

        // JSON 데이터 처리
        if (paymentDataJson == null || paymentDataJson.isEmpty()) {
            showError();
            return;
        }

        try {
            JSONObject json = new JSONObject(paymentDataJson);

            // Commerce 응답인지 확인 (event 필드가 있으면 Commerce)
            if (json.has("event")) {
                displayCommerceResultFromJson(json);
                return;
            }

            // Widget/일반 결제 응답 형식
            JSONObject data = json.optJSONObject("data");
            if (data == null) {
                showError();
                return;
            }

            int status = data.optInt("status", 0);

            if (status == 1) {
                // 결제 성공
                setSuccessState();
                titleText.setText("결제 완료");
                messageText.setText("결제가 성공적으로 완료되었습니다.");
            } else {
                // 결제 실패
                setErrorState();
                titleText.setText("결제 실패");
                messageText.setText("결제 처리 중 문제가 발생했습니다.");
            }

            // 상세 정보 표시
            addDetailRow("주문명", data.optString("order_name", "-"));
            addDetailRow("결제금액", formatPrice(data.optInt("price", 0)));
            addDetailRow("결제수단", data.optString("method", "-"));
            addDetailRow("PG사", data.optString("pg", "-"));
            addDetailRow("주문번호", data.optString("order_id", "-"));
            addDetailRow("영수증 ID", data.optString("receipt_id", "-"));

            String purchasedAt = data.optString("purchased_at", null);
            if (purchasedAt != null && !purchasedAt.isEmpty()) {
                addDetailRow("결제일시", formatDate(purchasedAt));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showError();
        }
    }

    /**
     * Commerce 결과 표시 (Map 기반)
     */
    private void displayCommerceResult(Map<String, Object> data, String event) {
        switch (event) {
            case "done":
                setSuccessState();
                titleText.setText("구독 신청 완료");
                messageText.setText("구독이 성공적으로 시작되었습니다.\n14일 무료 체험이 제공됩니다.");
                break;

            case "cancel":
                setWarningState();
                titleText.setText("결제 취소");
                String cancelMsg = data.get("message") != null ?
                        data.get("message").toString() : "결제가 취소되었습니다.";
                messageText.setText(cancelMsg);
                break;

            case "error":
                setErrorState();
                titleText.setText("결제 실패");
                String errorMsg = data.get("message") != null ?
                        data.get("message").toString() : "결제 처리 중 오류가 발생했습니다.";
                messageText.setText(errorMsg);
                break;

            default:
                showError();
                return;
        }

        // Commerce 상세 정보 표시
        if (data.get("order_number") != null) {
            addDetailRow("주문번호", data.get("order_number").toString());
        }
        if (data.get("request_id") != null) {
            addDetailRow("요청 ID", data.get("request_id").toString());
        }
        if (data.get("receipt_id") != null) {
            addDetailRow("영수증 ID", data.get("receipt_id").toString());
        }

        // metadata 표시
        Object metadataObj = data.get("metadata");
        if (metadataObj instanceof Map) {
            Map<String, Object> metadata = (Map<String, Object>) metadataObj;
            if (metadata.get("plan_key") != null) {
                String planKey = metadata.get("plan_key").toString();
                String planName = getPlanDisplayName(planKey);
                addDetailRow("플랜", planName);
            }
            if (metadata.get("billing_type") != null) {
                addDetailRow("결제 주기", metadata.get("billing_type").toString());
            }
        }
    }

    /**
     * Commerce 결과 표시 (JSON 기반)
     */
    private void displayCommerceResultFromJson(JSONObject json) {
        String event = json.optString("event", "");

        switch (event) {
            case "done":
                setSuccessState();
                titleText.setText("구독 신청 완료");
                messageText.setText("구독이 성공적으로 시작되었습니다.\n14일 무료 체험이 제공됩니다.");
                break;

            case "cancel":
                setWarningState();
                titleText.setText("결제 취소");
                messageText.setText(json.optString("message", "결제가 취소되었습니다."));
                break;

            case "error":
                setErrorState();
                titleText.setText("결제 실패");
                messageText.setText(json.optString("message", "결제 처리 중 오류가 발생했습니다."));
                break;

            default:
                showError();
                return;
        }

        // Commerce 상세 정보 표시
        if (json.has("order_number")) {
            addDetailRow("주문번호", json.optString("order_number"));
        }
        if (json.has("request_id")) {
            addDetailRow("요청 ID", json.optString("request_id"));
        }
        if (json.has("receipt_id")) {
            addDetailRow("영수증 ID", json.optString("receipt_id"));
        }

        // metadata 표시
        JSONObject metadata = json.optJSONObject("metadata");
        if (metadata != null) {
            if (metadata.has("plan_key")) {
                String planKey = metadata.optString("plan_key");
                addDetailRow("플랜", getPlanDisplayName(planKey));
            }
            if (metadata.has("billing_type")) {
                addDetailRow("결제 주기", metadata.optString("billing_type"));
            }
        }
    }

    private void setSuccessState() {
        statusEmoji.setText("✓");
        statusEmoji.setTextColor(successColor);
        titleText.setTextColor(successColor);

        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(Color.parseColor("#DCFCE7"));
        iconCircle.setBackground(circleBg);

        setButtonColor(successColor);
    }

    private void setErrorState() {
        statusEmoji.setText("✕");
        statusEmoji.setTextColor(errorColor);
        titleText.setTextColor(errorColor);

        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(Color.parseColor("#FEE2E2"));
        iconCircle.setBackground(circleBg);

        setButtonColor(errorColor);
    }

    private void setWarningState() {
        statusEmoji.setText("↩");
        statusEmoji.setTextColor(warningColor);
        titleText.setTextColor(warningColor);

        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(Color.parseColor("#FFEDD5"));
        iconCircle.setBackground(circleBg);

        setButtonColor(warningColor);
    }

    private void showError() {
        statusEmoji.setText("!");
        statusEmoji.setTextColor(warningColor);
        titleText.setText("결과 확인 불가");
        titleText.setTextColor(warningColor);
        messageText.setText("결제 결과를 확인할 수 없습니다.");

        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(Color.parseColor("#FFEDD5"));
        iconCircle.setBackground(circleBg);

        setButtonColor(warningColor);
    }

    private void setButtonColor(int color) {
        GradientDrawable buttonBg = new GradientDrawable();
        buttonBg.setColor(color);
        buttonBg.setCornerRadius(dp(14));
        confirmButton.setBackground(buttonBg);
    }

    private void addDetailRow(String title, String value) {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = detailContainer.getChildCount() > 0 ? dp(16) : 0;
        rowLayout.setLayoutParams(rowParams);

        // Title
        TextView titleLabel = new TextView(this);
        titleLabel.setLayoutParams(new LinearLayout.LayoutParams(
                dp(90),
                LinearLayout.LayoutParams.WRAP_CONTENT));
        titleLabel.setText(title);
        titleLabel.setTextSize(14);
        titleLabel.setTextColor(Color.parseColor("#94A3B8"));

        // Value
        TextView valueLabel = new TextView(this);
        valueLabel.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));
        valueLabel.setText(value);
        valueLabel.setTextSize(14);
        valueLabel.setTypeface(null, Typeface.BOLD);
        valueLabel.setTextColor(Color.parseColor("#1E293B"));
        valueLabel.setGravity(Gravity.END);

        rowLayout.addView(titleLabel);
        rowLayout.addView(valueLabel);

        detailContainer.addView(rowLayout);
    }

    private String getPlanDisplayName(String planKey) {
        if (planKey == null) return "-";
        switch (planKey.toLowerCase()) {
            case "starter":
                return "Starter";
            case "pro":
                return "Professional";
            case "enterprise":
                return "Enterprise";
            default:
                return capitalize(planKey);
        }
    }

    private String formatPrice(int price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
        return formatter.format(price) + "원";
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            if (date != null) {
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
