package com.jiuwan.inceptionsdksample;

import android.app.Application;

import com.jiuwan.publication.PublicationSDK;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PublicationSDK.getInstance().setApplicationContext(this);
    }
}