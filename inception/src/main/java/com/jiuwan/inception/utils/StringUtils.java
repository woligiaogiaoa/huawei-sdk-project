package com.jiuwan.inception.utils;

import android.text.TextUtils;

import com.jiuwan.inception.Constants;
import com.jiuwan.inception.Inception;
import com.jiuwan.inception.bean.GameConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

public class StringUtils {

    public static String encryptPaySign(TreeMap<String, String> params) {
        String param = "";
        if (params.size() > 0) {
            NavigableMap<String, String> descendingMap = params.descendingMap();
            StringBuilder paramBuilder = new StringBuilder();
            Set<String> paramsKeys = descendingMap.keySet();
            for (String paramKey : paramsKeys) {
                String value = descendingMap.get(paramKey);
                if (!paramKey.equals("extend_data") && !TextUtils.isEmpty(value)) {
                    paramBuilder.append(paramKey);
                    paramBuilder.append("=");
                    paramBuilder.append(value);
                    paramBuilder.append("&");
                }
            }
            GameConfig gameConfig = ConfigUtils.jsonToObject(Inception.getInstance().getApplication(), Constants.CONFIG_FILE_NAME, GameConfig.class);
            if (gameConfig == null) {
                gameConfig = GameConfig.BLANK_OBJ;
            }
            paramBuilder.append("game_id=" + gameConfig.getGame_id());
            try {
                param = URLEncoder.encode(paramBuilder.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return e.getLocalizedMessage();
            }
        }
        return toMD5(param);
    }

    public static String toMD5(String text) {
        try {
            //获取摘要器 MessageDigest
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            //通过摘要器对字符串的二进制字节数组进行hash计算
            byte[] digest = messageDigest.digest(text.getBytes());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                //循环每个字符 将计算结果转化为正整数;
                int digestInt = digest[i] & 0xff;
                //将10进制转化为较短的16进制
                String hexString = Integer.toHexString(digestInt);
                //转化结果如果是个位数会省略0,因此判断并补0
                if (hexString.length() < 2) {
                    sb.append(0);
                }
                //将循环结果添加到缓冲区
                sb.append(hexString);
            }
            //返回整个结果
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public static String toUnicode(String originStr, String charset) {
        try {
            return URLEncoder.encode(originStr, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public static String toUTF_8(String originStr) {
        try {
            return URLEncoder.encode(originStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

}
