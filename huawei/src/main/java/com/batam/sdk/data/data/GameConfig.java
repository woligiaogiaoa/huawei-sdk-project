package com.batam.sdk.data.data;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GameConfig {
    public static final String JSON_FILE_NAME = "config.json";

    public GameConfig(String channelId, String gameId, String packageId, String planId, String siteId) {
        this.channelId = channelId;
        this.gameId = gameId;
        this.packageId = packageId;
        this.planId = planId;
        this.siteId = siteId;
    }

    @SerializedName("channel_id")
    private String channelId;
    @SerializedName("game_id")
    private String gameId;
    @SerializedName("package_id")
    private String packageId;
    @SerializedName("plan_id")
    private String planId;
    @SerializedName("site_id")
    private String siteId;

    private String user_id = "";
    private String domain = "";
    private String user_pact_url = "";
    private String h5_apk_url = "";
    private String customer_service_url = "";

    public static GameConfig jsonToObject(Context context, String fileName) {
        GameConfig gameConfig = null;
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
            return new GameConfig("", "", "", "", "");
        }
        Gson gson = new Gson();
        gameConfig = gson.fromJson(stringBuilder.toString(),GameConfig.class);
        if (gameConfig == null) {
            gameConfig = new GameConfig("", "", "", "", "");
        }
        return gameConfig;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUser_pact_url() {
        return user_pact_url;
    }

    public void setUser_pact_url(String user_pact_url) {
        this.user_pact_url = user_pact_url;
    }

    public String getH5_apk_url() {
        return h5_apk_url;
    }

    public void setH5_apk_url(String h5_apk_url) {
        this.h5_apk_url = h5_apk_url;
    }

    public String getCustomer_service_url() {
        return customer_service_url;
    }

    public void setCustomer_service_url(String customer_service_url) {
        this.customer_service_url = customer_service_url;
    }
}