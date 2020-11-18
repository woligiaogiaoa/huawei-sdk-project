package com.batam.inception.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.batam.inception.bean.GameConfig;
import com.elephant.library.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigUtils {
    public static <T> T jsonToObject(Context context, String fileName, Class<T> cls) {
        T t=null;
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            return null;
        }
        t = JSON.parseObject(stringBuilder.toString(), cls);
        return t;
    }
}