package kr.co.bootpay.android;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import kr.co.bootpay.android.webview.BootpayWebView;

public class WebAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webapp);

        BootpayWebView webView = findViewById(R.id.webview);
        //link your domain
        webView.loadUrl("https://dev-js.bootapi.com/test/payment/");
    }
}