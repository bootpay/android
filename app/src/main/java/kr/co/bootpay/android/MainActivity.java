package kr.co.bootpay.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void DefaultPayment(View v) {
        Intent intent = new Intent(getApplicationContext(), DefaultPaymentActivity.class);
        startActivity(intent);
    }

    public void TotalPayment(View v) {
        Intent intent = new Intent(getApplicationContext(), TotalPaymentActivity.class);
        startActivity(intent);
    }

    public void SubscriptionPayment(View v) {
        Intent intent = new Intent(getApplicationContext(), SubscriptionPaymentActivity.class);
        startActivity(intent);
    }

    public void SubscriptionBootpayPayment(View v) {
        Intent intent = new Intent(getApplicationContext(), SubscriptionBootpayPaymentActivity.class);
        startActivity(intent);
    }

    public void Authentication(View v) {
        Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
        startActivity(intent);
    }

    public void PasswordPayment(View v) {
        Intent intent = new Intent(getApplicationContext(), PasswordPaymentActivity.class);
        startActivity(intent);
    }

    public void WebAppPayment(View v) {
        Intent intent = new Intent(getApplicationContext(), WebAppActivity.class);
        startActivity(intent);
    }
}