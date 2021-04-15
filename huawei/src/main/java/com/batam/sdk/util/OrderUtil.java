package com.batam.sdk.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.batam.sdk.data.data.GameConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

public class OrderUtil {

    public static String fen2yuan(String fen) throws NumberFormatException {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        format.setGroupingUsed(false);
        float fenFloat = Float.parseFloat(fen);
        if (fenFloat <= 0) {
            throw new NumberFormatException("金额不能小于等于0");
        }
        String yuan = format.format(fenFloat / 100);
        return yuan;
    }

    public static String encryptPaySign(Context context, TreeMap<String, String> params) {
        String param = "";
        if (params.size() > 0) {
            NavigableMap<String, String> descendingMap = params.descendingMap();
            StringBuilder paramBuilder = new StringBuilder();
            Set<String> paramsKeys = descendingMap.keySet();
            Iterator var6 = paramsKeys.iterator();

            while(var6.hasNext()) {
                String paramKey = (String)var6.next();
                String value = (String)descendingMap.get(paramKey);
                if (!paramKey.equals("extend_data") && !TextUtils.isEmpty(value)) {
                    paramBuilder.append(paramKey);
                    paramBuilder.append("=");
                    paramBuilder.append(value);
                    paramBuilder.append("&");
                }
            }

            GameConfig gameConfig = GameConfig.jsonToObject(context, GameConfig.JSON_FILE_NAME);
            paramBuilder.append("game_id=" + "0fa1cfba7051472487a954068c133b7c");

            try {
                param = URLEncoder.encode(paramBuilder.toString(), "UTF-8");
            } catch (UnsupportedEncodingException var9) {
                var9.printStackTrace();
                return var9.getLocalizedMessage();
            }
        }

        return toMD5(param);
    }

    public static String toMD5(String text) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest = messageDigest.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < digest.length; ++i) {
                int digestInt = digest[i] & 255;
                String hexString = Integer.toHexString(digestInt);
                if (hexString.length() < 2) {
                    sb.append(0);
                }

                sb.append(hexString);
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
            return var7.getLocalizedMessage();
        }
    }

    public static String toUnicode(String originStr, String charset) {
        try {
            return URLEncoder.encode(originStr, charset);
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return var3.getLocalizedMessage();
        }
    }
}
