package com.batam.inception.http;

import android.app.Application;

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
        Pref.get(context).initCachePath(HttpManager.class.getSimpleName());
        Kalle.setConfig(KalleConfig.newBuilder()
                .cookieStore(DBCookieStore.newBuilder(context).build())
                .cacheStore(DiskCacheStore.newBuilder(Pref.get(context).PATH_APP_CACHE).build())
                .network(new BroadcastNetwork(context))
                .addInterceptor(new LoggerInterceptor(Constants.HTTP_LOGGER_CONFIG_NAME, BuildConfig.DEBUG))
                .converter(new JsonConverter(context))
                .setHeader(Constants.INFO, SdkConfig.get().getIH())
                .build());
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
