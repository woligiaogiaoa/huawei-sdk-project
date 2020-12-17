package com.batam.inception.http;

import android.app.Application;
import android.content.Context;

import com.batam.inception.BuildConfig;
import com.batam.inception.Constants;
import com.batam.inception.sp.Pref;
import com.batam.inception.sp.SdkConfig;
import com.elephant.library.kalle.Kalle;
import com.elephant.library.kalle.KalleConfig;
import com.elephant.library.kalle.connect.BroadcastNetwork;
import com.elephant.library.kalle.connect.http.LoggerInterceptor;
import com.elephant.library.kalle.cookie.DBCookieStore;
import com.elephant.library.kalle.simple.cache.DiskCacheStore;



public class HttpManager {


    public static void init(Application context) {
        Application applicationContext=context;

        //create a Pref instance,init a cache filw path variable to PATH_APP_CACHE
        Pref.get(applicationContext).initCachePath(HttpManager.class.getSimpleName()); //todo:test
        Kalle.setConfig(KalleConfig.newBuilder()
                .cookieStore(DBCookieStore.newBuilder(context).build())
                .cacheStore(DiskCacheStore.newBuilder(Pref.get(context).PATH_APP_CACHE).build())
                .network(new BroadcastNetwork(context))
                .addInterceptor(new LoggerInterceptor(Constants.HTTP_LOGGER_CONFIG_NAME, BuildConfig.DEBUG))
                .converter(new JsonConverter(context))
                .setHeader(Constants.INFO, SdkConfig.get().getIH())
                .build());

        //we already have a Pref instance,in the get() method ,the Pref instance init its sharedPrefrence and SdkConfig class have the Pref instance reference
        if (!SdkConfig.get().getAuthorization().isEmpty()) {
            updateAuthorizationHeader();
        }
    }

    public static void updateInfoHeader() {
        Kalle.getConfig().setHeader(Constants.INFO, SdkConfig.get().getIH());
    }

    public static void updateAuthorizationHeader() {
        Kalle.getConfig().setHeader(Constants.KEY_AUTHORIZATION, SdkConfig.get().getAuthorization());
    }

}
