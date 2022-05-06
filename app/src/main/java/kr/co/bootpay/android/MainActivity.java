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

    public void goNativeActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), NativeActivity.class);
        startActivity(intent);
    }

    public void goWebAppActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), WebAppActivity.class);
        startActivity(intent);
    }
}