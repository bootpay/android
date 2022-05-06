package kr.co.bootpay.android;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.bootpay.android.webview.BootpayWebView;

public class WebAppActivity extends AppCompatActivity {
    BootpayWebView bootpayWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webapp);

        bootpayWebView = findViewById(R.id.webview);
        bootpayWebView.loadUrl("https://www.yourdomain.com");
    }
}