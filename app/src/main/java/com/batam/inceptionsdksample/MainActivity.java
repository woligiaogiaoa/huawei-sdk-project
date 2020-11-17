package com.batam.inceptionsdksample;

import android.net.nsd.NsdManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.batam.huawei.SDKManager;


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


    public void login(View view) {
        SDKManager.getInstance().login();
    }
}