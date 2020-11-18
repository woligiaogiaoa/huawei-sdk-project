package com.batam.inception.sp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.batam.inception.utils.FileUtils;
import com.elephant.library.fastjson.JSON;

import java.io.File;

public class Pref {

    private static Pref sInstance;

    public String PATH_APP_CACHE;

    public SharedPreferences mPreferences;

    private Application context;

    public static Pref get(Application context) {
        if (sInstance == null) {
            synchronized (Pref.class) {
                if (sInstance == null) sInstance = new Pref(context);
            }
        }
        return sInstance;
    }

    private Pref(Application context) {
        this.context = context;
    }

    public void initPreference(String configName) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            mPreferences = context.getSharedPreferences(configName, Context.MODE_PRIVATE);
//        } else {
//            try {
////                mPreferences = EncryptedSharedPreferences.create(configName,
////                        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
////                        context,
////                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
////                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
//            } catch (GeneralSecurityException | IOException e) {
//                e.printStackTrace();
//            }
//        }
        mPreferences = context.getSharedPreferences(configName, Context.MODE_PRIVATE);

    }

    public void initCachePath(String cachePath) {
        this.PATH_APP_CACHE = FileUtils.getAppRootPath(context).getAbsolutePath() +
                File.separator + cachePath + File.separator + "Cache";
    }

    public void setStringApply(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    public boolean setStringCommit(String key, String value) {
        return mPreferences.edit().putString(key, value).commit();
    }

    public void setBooleanApply(String key, boolean value) {
        mPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean setBooleanCommit(String key, boolean value) {
        return mPreferences.edit().putBoolean(key, value).commit();
    }

    public void setFloatApply(String key, float value) {
        mPreferences.edit().putFloat(key, value).apply();
    }

    public boolean setFloatCommit(String key, float value) {
        return mPreferences.edit().putFloat(key, value).commit();
    }

    public void setIntApply(String key, int value) {
        mPreferences.edit().putInt(key, value).apply();
    }

    public boolean setIntCommit(String key, int value) {
        return mPreferences.edit().putInt(key, value).commit();
    }

    public void setLongApply(String key, long value) {
        mPreferences.edit().putLong(key, value).apply();
    }

    public boolean setLongCommit(String key, long value) {
        return mPreferences.edit().putLong(key, value).commit();
    }

    public <P extends Parcelable> void setObjectApply(String key, P param) {
        if (param != null) {
            mPreferences.edit().putString(key, JSON.toJSONString(param)).apply();
        } else {
            mPreferences.edit().putString(key, "").apply();
        }
    }

    public <P extends Parcelable> boolean setObjectCommit(String key, P param) {
        if (param != null) {
            return mPreferences.edit().putString(key, JSON.toJSONString(param)).commit();
        } else {
            return mPreferences.edit().putString(key, "").commit();
        }
    }

    public String getString(String key, String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return mPreferences.getFloat(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return mPreferences.getInt(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return mPreferences.getLong(key, defaultValue);
    }

    @Nullable
    public <P extends Parcelable> P getObject(String key, Class<P> pClass) {
        String jsonObject = getString(key, "");
        if (!TextUtils.isEmpty(jsonObject)) {
            try {
                return JSON.parseObject(jsonObject, pClass);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
