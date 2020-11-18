package com.batam.inception;

import android.app.Application;

import com.batam.inception.http.HttpManager;
import com.batam.inception.utils.CrashHandler;
import com.batam.inception.utils.HeaderUtils;

public class Inception {

    private Application application;

    private Inception() {
    }

    private static class Singleton {
        private static final Inception sInstance = new Inception();
    }

    public static Inception getInstance() {
        return Singleton.sInstance;
    }

    public void setApplication(Application application) {
        this.application = application;
        HttpManager.init(application);
        HeaderUtils.initDeviceInfoHeader(application);
        HeaderUtils.updateDeviceInfoHeader(application, HttpManager::updateInfoHeader);
        CrashHandler.getInstance().init(application);
    }

    public Application getApplication() {
        return application;
    }

}