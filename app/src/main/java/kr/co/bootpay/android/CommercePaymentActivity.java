package kr.co.bootpay.android;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.bootpay.android.commerce.BootpayCommerce;
import kr.co.bootpay.android.commerce.CommerceExtra;
import kr.co.bootpay.android.commerce.CommercePayload;
import kr.co.bootpay.android.commerce.CommerceProduct;
import kr.co.bootpay.android.commerce.CommerceUser;

/**
 * Commerce 결제 예제 Activity
 * iOS CommerceExampleController와 동일한 UI/로직
 */
public class CommercePaymentActivity extends AppCompatActivity {
    private static final String TAG = "CommercePayment";

    // 환경 설정
    private static final Map<String, Map<String, Object>> ENV_CONFIG = new HashMap<>();
    private static final Map<String, Map<String, Object>> PLAN_INFO = new HashMap<>();

    static {
        // Development 환경
        Map<String, Object> devConfig = new HashMap<>();
        devConfig.put("client_key", "hxS-Up--5RvT6oU6QJE0JA");
        Map<String, Map<String, String>> devPlans = new HashMap<>();
        devPlans.put("starter", new HashMap<String, String>() {{
            put("monthly_product_id", "69268625d8df8fa1837cf661");
            put("yearly_product_id", "692686c4d8df8fa1837cf666");
        }});
        devPlans.put("pro", new HashMap<String, String>() {{
            put("monthly_product_id", "692686e5d8df8fa1837cf66b");
            put("yearly_product_id", "69268721d8df8fa1837cf670");
        }});
        devPlans.put("enterprise", new HashMap<String, String>() {{
            put("monthly_product_id", "69268783d8df8fa1837cf675");
            put("yearly_product_id", "692687a2d8df8fa1837cf67a");
        }});
        devConfig.put("plans", devPlans);
        ENV_CONFIG.put("development", devConfig);

        // Production 환경
        Map<String, Object> prodConfig = new HashMap<>();
        prodConfig.put("client_key", "sEN72kYZBiyMNytA8nUGxQ");
        Map<String, Map<String, String>> prodPlans = new HashMap<>();
        prodPlans.put("starter", new HashMap<String, String>() {{
            put("monthly_product_id", "6927d893ff30795ff003d374");
            put("yearly_product_id", "6927d8c310561eabadddcfae");
        }});
        prodPlans.put("pro", new HashMap<String, String>() {{
            put("monthly_product_id", "6927d8f9ff30795ff003d379");
            put("yearly_product_id", "6927d9167f65277ba9ddcf71");
        }});
        prodPlans.put("enterprise", new HashMap<String, String>() {{
            put("monthly_product_id", "6927d8f9ff30795ff003d379");
            put("yearly_product_id", "6927d9167f65277ba9ddcf71");
        }});
        prodConfig.put("plans", prodPlans);
        ENV_CONFIG.put("production", prodConfig);

        // 플랜 정보
        Map<String, Object> starterInfo = new HashMap<>();
        starterInfo.put("name", "Starter");
        starterInfo.put("monthly_price", 9900);
        starterInfo.put("yearly_price", 7900);
        starterInfo.put("features", new String[]{"5GB 클라우드 스토리지", "최대 3개 프로젝트", "기본 분석 대시보드"});
        PLAN_INFO.put("starter", starterInfo);

        Map<String, Object> proInfo = new HashMap<>();
        proInfo.put("name", "Professional");
        proInfo.put("monthly_price", 29900);
        proInfo.put("yearly_price", 23900);
        proInfo.put("features", new String[]{"100GB 클라우드 스토리지", "무제한 프로젝트", "고급 분석 및 리포트"});
        PLAN_INFO.put("pro", proInfo);

        Map<String, Object> enterpriseInfo = new HashMap<>();
        enterpriseInfo.put("name", "Enterprise");
        enterpriseInfo.put("monthly_price", 99000);
        enterpriseInfo.put("yearly_price", 79000);
        enterpriseInfo.put("features", new String[]{"무제한 클라우드 스토리지", "무제한 프로젝트", "전용 계정 매니저"});
        PLAN_INFO.put("enterprise", enterpriseInfo);
    }

    private String currentEnv = "production";
    private boolean isYearlyBilling = false;
    private String selectedPlan = "pro";

    // 테마 컬러
    private final int primaryColor = Color.parseColor("#667eea");
    private final int secondaryColor = Color.parseColor("#764ba2");

    // UI 컴포넌트
    private Switch billingToggle;
    private CardView starterCard;
    private CardView proCard;
    private CardView enterpriseCard;
    private TextView starterPriceLabel;
    private TextView proPriceLabel;
    private TextView enterprisePriceLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
    }

    private void setupUI() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        scrollView.setBackgroundColor(Color.parseColor("#F8FAFC"));

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        contentLayout.setPadding(dp(16), dp(24), dp(16), dp(32));

        // 헤더
        TextView headerLabel = new TextView(this);
        headerLabel.setText("나에게 맞는 요금제를 선택하세요");
        headerLabel.setTextSize(22);
        headerLabel.setTypeface(null, Typeface.BOLD);
        headerLabel.setTextColor(Color.parseColor("#1E293B"));
        headerLabel.setGravity(Gravity.CENTER);
        contentLayout.addView(headerLabel);

        TextView descLabel = new TextView(this);
        descLabel.setText("모든 요금제에서 14일 무료 체험을 제공합니다");
        descLabel.setTextSize(14);
        descLabel.setTextColor(Color.parseColor("#64748B"));
        descLabel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        descParams.topMargin = dp(8);
        descLabel.setLayoutParams(descParams);
        contentLayout.addView(descLabel);

        // 결제 주기 토글
        LinearLayout billingContainer = createBillingToggle();
        LinearLayout.LayoutParams billingParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        billingParams.gravity = Gravity.CENTER;
        billingParams.topMargin = dp(24);
        billingContainer.setLayoutParams(billingParams);
        contentLayout.addView(billingContainer);

        // 플랜 카드들
        starterCard = createPlanCard("starter", "🚀", false);
        LinearLayout.LayoutParams starterParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        starterParams.topMargin = dp(24);
        starterCard.setLayoutParams(starterParams);
        contentLayout.addView(starterCard);

        proCard = createPlanCard("pro", "⚡", true);
        LinearLayout.LayoutParams proParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        proParams.topMargin = dp(16);
        proCard.setLayoutParams(proParams);
        contentLayout.addView(proCard);

        enterpriseCard = createPlanCard("enterprise", "🏢", false);
        LinearLayout.LayoutParams enterpriseParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        enterpriseParams.topMargin = dp(16);
        enterpriseCard.setLayoutParams(enterpriseParams);
        contentLayout.addView(enterpriseCard);

        scrollView.addView(contentLayout);
        setContentView(scrollView);

        updateCardSelection();
    }

    private LinearLayout createBillingToggle() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);

        TextView monthlyLabel = new TextView(this);
        monthlyLabel.setText("월간");
        monthlyLabel.setTextSize(14);
        monthlyLabel.setTypeface(null, Typeface.BOLD);
        monthlyLabel.setTextColor(Color.parseColor("#212529"));
        container.addView(monthlyLabel);

        billingToggle = new Switch(this);
        LinearLayout.LayoutParams switchParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        switchParams.leftMargin = dp(12);
        switchParams.rightMargin = dp(12);
        billingToggle.setLayoutParams(switchParams);
        billingToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isYearlyBilling = isChecked;
            updatePrices();
        });
        container.addView(billingToggle);

        TextView yearlyLabel = new TextView(this);
        yearlyLabel.setText("연간");
        yearlyLabel.setTextSize(14);
        yearlyLabel.setTypeface(null, Typeface.BOLD);
        yearlyLabel.setTextColor(Color.parseColor("#64748B"));
        container.addView(yearlyLabel);

        // 할인 배지
        TextView discountBadge = new TextView(this);
        discountBadge.setText("20% 할인");
        discountBadge.setTextSize(11);
        discountBadge.setTypeface(null, Typeface.BOLD);
        discountBadge.setTextColor(Color.parseColor("#15803D"));
        discountBadge.setBackgroundColor(Color.parseColor("#DCFCE7"));
        discountBadge.setPadding(dp(8), dp(4), dp(8), dp(4));
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        badgeParams.leftMargin = dp(8);
        discountBadge.setLayoutParams(badgeParams);

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(Color.parseColor("#DCFCE7"));
        badgeBg.setCornerRadius(dp(10));
        discountBadge.setBackground(badgeBg);

        container.addView(discountBadge);

        return container;
    }

    private CardView createPlanCard(String planKey, String icon, boolean isPopular) {
        Map<String, Object> planInfo = PLAN_INFO.get(planKey);
        if (planInfo == null) return new CardView(this);

        String name = (String) planInfo.get("name");
        int monthlyPrice = (int) planInfo.get("monthly_price");
        String[] features = (String[]) planInfo.get("features");

        CardView card = new CardView(this);
        card.setCardBackgroundColor(Color.WHITE);
        card.setRadius(dp(12));
        card.setCardElevation(dp(4));
        card.setUseCompatPadding(true);

        if (isPopular) {
            card.setCardBackgroundColor(Color.WHITE);
        }

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(dp(20), dp(20), dp(20), dp(20));

        // 인기 배지 (Pro만)
        if (isPopular) {
            TextView badge = new TextView(this);
            badge.setText("가장 인기");
            badge.setTextSize(11);
            badge.setTypeface(null, Typeface.BOLD);
            badge.setTextColor(Color.WHITE);
            badge.setPadding(dp(12), dp(4), dp(12), dp(4));
            badge.setGravity(Gravity.CENTER);

            GradientDrawable badgeBg = new GradientDrawable();
            badgeBg.setColor(primaryColor);
            badgeBg.setCornerRadius(dp(10));
            badge.setBackground(badgeBg);

            LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            badgeParams.bottomMargin = dp(12);
            badge.setLayoutParams(badgeParams);
            cardContent.addView(badge);
        }

        // 아이콘
        TextView iconLabel = new TextView(this);
        iconLabel.setText(icon);
        iconLabel.setTextSize(32);
        cardContent.addView(iconLabel);

        // 플랜 이름
        TextView nameLabel = new TextView(this);
        nameLabel.setText(name);
        nameLabel.setTextSize(20);
        nameLabel.setTypeface(null, Typeface.BOLD);
        nameLabel.setTextColor(Color.parseColor("#1E293B"));
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = dp(12);
        nameLabel.setLayoutParams(nameParams);
        cardContent.addView(nameLabel);

        // 가격
        LinearLayout priceRow = new LinearLayout(this);
        priceRow.setOrientation(LinearLayout.HORIZONTAL);
        priceRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams priceRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        priceRowParams.topMargin = dp(8);
        priceRow.setLayoutParams(priceRowParams);

        TextView priceLabel = new TextView(this);
        priceLabel.setText("₩" + formatPrice(monthlyPrice));
        priceLabel.setTextSize(28);
        priceLabel.setTypeface(null, Typeface.BOLD);
        priceLabel.setTextColor(isPopular ? primaryColor : Color.parseColor("#1E293B"));
        priceRow.addView(priceLabel);

        // 가격 라벨 저장
        if ("starter".equals(planKey)) {
            starterPriceLabel = priceLabel;
        } else if ("pro".equals(planKey)) {
            proPriceLabel = priceLabel;
        } else if ("enterprise".equals(planKey)) {
            enterprisePriceLabel = priceLabel;
        }

        TextView periodLabel = new TextView(this);
        periodLabel.setText("/월");
        periodLabel.setTextSize(14);
        periodLabel.setTextColor(Color.parseColor("#64748B"));
        LinearLayout.LayoutParams periodParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        periodParams.leftMargin = dp(4);
        periodLabel.setLayoutParams(periodParams);
        priceRow.addView(periodLabel);

        cardContent.addView(priceRow);

        // 기능 목록
        LinearLayout featuresLayout = new LinearLayout(this);
        featuresLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams featuresParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        featuresParams.topMargin = dp(16);
        featuresLayout.setLayoutParams(featuresParams);

        for (String feature : features) {
            LinearLayout featureRow = createFeatureRow(feature);
            featuresLayout.addView(featureRow);
        }
        cardContent.addView(featuresLayout);

        // 선택 버튼
        Button selectButton = new Button(this);
        selectButton.setText(name + " 시작하기");
        selectButton.setTextSize(16);
        selectButton.setTypeface(null, Typeface.BOLD);
        selectButton.setAllCaps(false);

        GradientDrawable buttonBg = new GradientDrawable();
        buttonBg.setCornerRadius(dp(10));

        if (isPopular) {
            buttonBg.setColor(primaryColor);
            selectButton.setTextColor(Color.WHITE);
        } else {
            buttonBg.setColor(Color.parseColor("#F1F5F9"));
            selectButton.setTextColor(Color.parseColor("#475569"));
        }
        selectButton.setBackground(buttonBg);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(48));
        buttonParams.topMargin = dp(20);
        selectButton.setLayoutParams(buttonParams);

        selectButton.setOnClickListener(v -> onPlanSelected(planKey));
        cardContent.addView(selectButton);

        card.addView(cardContent);
        return card;
    }

    private LinearLayout createFeatureRow(String text) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = dp(8);
        row.setLayoutParams(rowParams);

        TextView checkmark = new TextView(this);
        checkmark.setText("✓");
        checkmark.setTextSize(14);
        checkmark.setTypeface(null, Typeface.BOLD);
        checkmark.setTextColor(Color.parseColor("#22C55E"));
        row.addView(checkmark);

        TextView label = new TextView(this);
        label.setText(text);
        label.setTextSize(14);
        label.setTextColor(Color.parseColor("#475569"));
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        labelParams.leftMargin = dp(8);
        label.setLayoutParams(labelParams);
        row.addView(label);

        return row;
    }

    private void updatePrices() {
        updateCardPrice(starterPriceLabel, "starter");
        updateCardPrice(proPriceLabel, "pro");
        updateCardPrice(enterprisePriceLabel, "enterprise");
    }

    private void updateCardPrice(TextView priceLabel, String planKey) {
        if (priceLabel == null) return;

        Map<String, Object> planInfo = PLAN_INFO.get(planKey);
        if (planInfo == null) return;

        int monthlyPrice = (int) planInfo.get("monthly_price");
        int yearlyPrice = (int) planInfo.get("yearly_price");
        int price = isYearlyBilling ? yearlyPrice : monthlyPrice;

        priceLabel.setText("₩" + formatPrice(price));
    }

    private void updateCardSelection() {
        // 현재는 Pro 카드에 기본 테두리 표시
        if (proCard != null) {
            GradientDrawable border = new GradientDrawable();
            border.setColor(Color.WHITE);
            border.setStroke(dp(2), primaryColor);
            border.setCornerRadius(dp(12));
            proCard.setBackground(border);
        }
    }

    private void onPlanSelected(String planKey) {
        if ("enterprise".equals(planKey)) {
            new AlertDialog.Builder(this)
                    .setTitle("Enterprise 플랜")
                    .setMessage("Enterprise 플랜은 영업팀으로 문의해 주세요.\n이메일: sales@cloudsync.example.com")
                    .setPositiveButton("확인", null)
                    .show();
            return;
        }

        selectedPlan = planKey;
        updateCardSelection();
        startPayment();
    }

    private void startPayment() {
        Map<String, Object> config = ENV_CONFIG.get(currentEnv);
        if (config == null) {
            Log.e(TAG, "Configuration error: env not found");
            return;
        }

        String clientKey = (String) config.get("client_key");
        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> plans = (Map<String, Map<String, String>>) config.get("plans");
        Map<String, String> planConfig = plans.get(selectedPlan);
        Map<String, Object> planInfo = PLAN_INFO.get(selectedPlan);

        if (planConfig == null || planInfo == null) {
            Log.e(TAG, "Configuration error: plan not found");
            return;
        }

        String planName = (String) planInfo.get("name");
        int monthlyPrice = (int) planInfo.get("monthly_price");
        int yearlyPrice = (int) planInfo.get("yearly_price");

        String productId = isYearlyBilling ? planConfig.get("yearly_product_id") : planConfig.get("monthly_product_id");
        String billingType = isYearlyBilling ? "연간" : "월간";
        int price = isYearlyBilling ? yearlyPrice : monthlyPrice;

        Log.d(TAG, "환경: " + currentEnv + ", 플랜: " + selectedPlan + ", 상품ID: " + productId);

        // 사용자 정보 설정
        CommerceUser user = new CommerceUser()
                .setMembershipType("guest")
                .setUserId("demo_user_1234")
                .setName("데모 사용자")
                .setPhone("01040334678")
                .setEmail("demo@example.com");

        // 상품 목록 설정
        List<CommerceProduct> products = new ArrayList<>();
        CommerceProduct product = new CommerceProduct()
                .setProductId(productId)
                .setDuration(-1)  // 무기한 구독
                .setQuantity(1);
        products.add(product);

        // 추가 옵션 설정
        CommerceExtra extra = new CommerceExtra()
                .setSeparatelyConfirmed(false)
                .setCreateOrderImmediately(true);

        // 메타데이터 설정
        String orderId = "order_" + System.currentTimeMillis();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("order_id", orderId);
        metadata.put("plan_key", selectedPlan);
        metadata.put("billing_type", billingType);
        metadata.put("env", currentEnv);
        metadata.put("selected_at", new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(new java.util.Date()));

        // CommercePayload 생성
        CommercePayload payload = new CommercePayload()
                .setClientKey(clientKey)
                .setName("CloudSync Pro " + planName + " 플랜")
                .setMemo(billingType + " 구독 결제")
                .setUser(user)
                .setPrice(price)
                .setRequestId(orderId)
                .setProducts(products)
                .setUseAutoLogin(true)
                .setUseNotification(true)
                .setMetadata(metadata)
                .setExtra(extra);

        // usage_api_url 설정
//        String usageApiUrl = "production".equals(currentEnv)
//                ? "https://api.bootapi.com/v1/billing/usage"
//                : "https://dev-api.bootapi.com/v1/billing/usage";
//        payload.setUsageApiUrl(usageApiUrl);

        // Commerce 결제 요청
        BootpayCommerce.requestCheckout(this, payload)
                .onDone(data -> {
                    Log.d(TAG, "결제 완료: " + data);
                    runOnUiThread(() -> showPaymentResult("결제 완료", data));
                })
                .onIssued(data -> {
                    Log.d(TAG, "가상계좌 발급: " + data);
                    runOnUiThread(() -> showPaymentResult("가상계좌 발급", data));
                })
                .onError(data -> {
                    Log.e(TAG, "결제 에러: " + data);
                    runOnUiThread(() -> showPaymentResult("결제 에러", data));
                })
                .onCancel(data -> {
                    Log.d(TAG, "결제 취소: " + data);
                    runOnUiThread(() -> showPaymentResult("결제 취소", data));
                })
                .onClose(() -> {
                    Log.d(TAG, "결제창 닫힘");
                });
    }

    private void showPaymentResult(String title, Map<String, Object> data) {
        // event를 data에 추가
        Map<String, Object> resultData = new HashMap<>(data);
        String event;
        if ("결제 완료".equals(title)) {
            event = "done";
        } else if ("가상계좌 발급".equals(title)) {
            event = "issued";
        } else if ("결제 취소".equals(title)) {
            event = "cancel";
        } else {
            event = "error";
        }
        resultData.put("event", event);

        // PaymentResultActivity로 이동
        PaymentResultActivity.launch(this, resultData);
    }

    private String formatPrice(int price) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.KOREA);
        return formatter.format(price);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    // 기존 레이아웃 버튼용 메서드 (호환성 유지)
    public void CommercePaymentTest(View v) {
        selectedPlan = "pro";
        startPayment();
    }

    public void SimpleCommercePayment(View v) {
        selectedPlan = "starter";
        startPayment();
    }
}
