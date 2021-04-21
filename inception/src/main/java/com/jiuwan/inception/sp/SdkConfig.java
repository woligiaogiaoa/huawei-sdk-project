package com.jiuwan.inception.sp;

import com.jiuwan.inception.Constants;
import com.jiuwan.inception.Inception;
import com.jiuwan.inception.utils.DeviceUtils;

public class SdkConfig {

    private Pref pref;

    private SdkConfig() {
        pref = Pref.get(Inception.getInstance().getApplication());
        pref.initPreference(Constants.PREFERENCE_CONFIG_NAME);
    }

    private static final class Singleton {
        private static final SdkConfig instance = new SdkConfig();
    }

    public static SdkConfig get() {
        return Singleton.instance;
    }

    /**
     * 设置Http Info Header
     *
     * @param ih Info Header
     */
    public void setIH(String ih) {
        pref.setStringCommit("Info", ih);
    }

    /**
     * 获取HTTP Info Header
     */
    public String getIH() {
        return pref.getString("Info", "");
    }

    /**
     * @return 用户标识
     */
    public String getSlug() {
        return pref.getString("slug", "");
    }

    /**
     * @param slug 用户标识
     */
    public void setSlug(String slug) {
        pref.setStringCommit("slug", slug);
    }

    /**
     * @return 订单编号
     */
    public String getOrderNum() {
        return pref.getString("order_num", "");
    }

    /**
     * @param orderNum 订单编号
     */
    public void setOrderNum(String orderNum) {
        pref.setStringCommit("order_num", orderNum);
    }

    public void setAndroidQ(DeviceUtils.DeviceInfo.AndroidQ androidQ) {
        pref.setObjectCommit("androidQ", androidQ);
    }

    public DeviceUtils.DeviceInfo.AndroidQ getAndroidQ() {
        return pref.getObject("androidQ", DeviceUtils.DeviceInfo.AndroidQ.class);
    }

    public String getAuthorization() {
        return pref.getString(Constants.KEY_AUTHORIZATION, "");
    }
}
