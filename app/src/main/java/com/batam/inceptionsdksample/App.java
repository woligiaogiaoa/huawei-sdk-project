package com.batam.inceptionsdksample;

import android.app.Application;

import com.batam.huawei.SDKManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKManager.getInstance().setApplicationContext(this);
    }
}