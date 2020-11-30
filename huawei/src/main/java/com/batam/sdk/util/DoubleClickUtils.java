package com.batam.sdk.util;

public class DoubleClickUtils {
 
    private static long lastClickTime;
 
    public static boolean isDoubleClick() {
        long time = System.currentTimeMillis();
        long lastTime = time - lastClickTime;
        if (0 < lastTime && lastTime < 5000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}