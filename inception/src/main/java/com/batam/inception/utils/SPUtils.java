package com.batam.inception.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * sp工具类
 */
public class SPUtils {

    @SuppressLint("CommitPrefEdits")
    public static void put(Context context, String key, Object value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (int) value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (boolean) value);
        }
        edit.apply();
    }

    public static String getString(Context context, String key, String defaultVal) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(key, defaultVal);
    }

    public static int getInt(Context context, String key, int defaultVal) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getInt(key, defaultVal);
    }

    public static boolean getBoolean(Context context, String defaultVal) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getBoolean(defaultVal, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }
}
