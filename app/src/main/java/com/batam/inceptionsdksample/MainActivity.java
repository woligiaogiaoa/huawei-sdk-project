package com.batam.inceptionsdksample;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import com.batam.sdk.SDKManager;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SDKManager.getInstance().init(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SDKManager.getInstance().onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SDKManager.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SDKManager.getInstance().onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SDKManager.getInstance().onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SDKManager.getInstance().onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SDKManager.getInstance().onActivityResult(requestCode,resultCode,data);
    }

    public void login(View view) {
        SDKManager.getInstance().login();
    }

    public void pay(View view) {
        SDKManager.getInstance().pay("test_product_001","test123");
    }
}