package kr.co.bootpay.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 결제 결과 화면 (iOS PaymentResultController 참조)
 */
public class PaymentResultActivity extends AppCompatActivity {

    private static final String EXTRA_PAYMENT_DATA = "payment_data";

    private ImageView statusIcon;
    private TextView titleText;
    private TextView messageText;
    private LinearLayout detailContainer;
    private Button confirmButton;

    private String paymentData;

    public static void launch(Context context, String paymentData) {
        Intent intent = new Intent(context, PaymentResultActivity.class);
        intent.putExtra(EXTRA_PAYMENT_DATA, paymentData);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        // ActionBar 설정
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("결제 결과");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // View 바인딩
        statusIcon = findViewById(R.id.statusIcon);
        titleText = findViewById(R.id.titleText);
        messageText = findViewById(R.id.messageText);
        detailContainer = findViewById(R.id.detailContainer);
        confirmButton = findViewById(R.id.confirmButton);

        // 데이터 가져오기
        paymentData = getIntent().getStringExtra(EXTRA_PAYMENT_DATA);

        // 결과 표시
        displayResult();

        // 확인 버튼 클릭
        confirmButton.setOnClickListener(v -> {
            // 메인 화면으로 돌아가기
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 시에도 finish
        finish();
    }

    private void displayResult() {
        if (paymentData == null || paymentData.isEmpty()) {
            showError();
            return;
        }

        try {
            JSONObject json = new JSONObject(paymentData);
            JSONObject data = json.optJSONObject("data");

            if (data == null) {
                showError();
                return;
            }

            int status = data.optInt("status", 0);

            if (status == 1) {
                // 결제 성공
                statusIcon.setImageResource(android.R.drawable.ic_dialog_info);
                statusIcon.setColorFilter(Color.parseColor("#4CAF50")); // Green
                titleText.setText("결제 완료");
                titleText.setTextColor(Color.parseColor("#4CAF50"));
                messageText.setText("결제가 성공적으로 완료되었습니다.");
            } else {
                // 결제 실패
                statusIcon.setImageResource(android.R.drawable.ic_dialog_alert);
                statusIcon.setColorFilter(Color.parseColor("#F44336")); // Red
                titleText.setText("결제 실패");
                titleText.setTextColor(Color.parseColor("#F44336"));
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

    private void showError() {
        statusIcon.setImageResource(android.R.drawable.ic_dialog_alert);
        statusIcon.setColorFilter(Color.parseColor("#FF9800")); // Orange
        titleText.setText("결과 확인 불가");
        titleText.setTextColor(Color.parseColor("#FF9800"));
        messageText.setText("결제 결과를 확인할 수 없습니다.");
    }

    private void addDetailRow(String title, String value) {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        rowLayout.setPadding(0, 8, 0, 8);

        // Title
        TextView titleLabel = new TextView(this);
        titleLabel.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));
        titleLabel.setText(title);
        titleLabel.setTextSize(14);
        titleLabel.setTextColor(Color.parseColor("#666666"));

        // Value
        TextView valueLabel = new TextView(this);
        valueLabel.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f
        ));
        valueLabel.setText(value);
        valueLabel.setTextSize(14);
        valueLabel.setTextColor(Color.parseColor("#333333"));
        valueLabel.setGravity(Gravity.END);

        rowLayout.addView(titleLabel);
        rowLayout.addView(valueLabel);

        detailContainer.addView(rowLayout);
    }

    private String formatPrice(int price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
        return formatter.format(price) + "원";
    }

    private String formatDate(String dateString) {
        try {
            // ISO 8601 형식 파싱
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
}
