package com.jiuwan.inception.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.RequiresPermission;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;


import com.jiuwan.inception.Constants;
import com.jiuwan.inception.BuildConfig;
import com.jiuwan.inception.bean.GameConfig;
import com.elephant.library.fastjson.JSON;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;

@SuppressLint("MissingPermission")
public class DeviceUtils {


    private static final String SEND_DEVICE_INFO_URL = "https://api.9wangame.com/Android/info";
    private static final String CHANNEL_NAME = "1";
    public static final String NETWORK_TYPE = "type";
    public static final String NETWORK_NAME = "name";
    public static final String NETWORK_CODE = "code";

    // 综上述，AndroidId 和 Serial Number 的通用性都较好，并且不受权限限制
    // 如果刷机和恢复出厂设置会导致设备标识符重置这一点可以接受的话，那么将他们组合使用时，唯一性就可以应付绝大多数设备了。
    public static String getUniqueId(Context context) {
        String androidID = getAndroidID(context);
        String id = androidID + Build.SERIAL;
        return toMD5(id);
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

    public static String getAndroidID(Context context) {
        try {
            String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            //在次做个验证，也不是什么时候都能获取到的啊
            if (androidID == null) {
                androidID = "";
            }
            return androidID;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    @SuppressLint({"NewApi", "HardwareIds"})
    private static List<String> getDeviceIdList(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<String> list = new ArrayList<>();
        String deviceId = null;
        try {
            deviceId = tm.getDeviceId();
            if (deviceId != null && !deviceId.isEmpty()) {
                list.add(deviceId);
            }
        } catch (Exception ignored) {
        } catch (NoSuchMethodError ignored) {
        }
        for (int i = 0; i < 3; i++) {
            try {
                String imei = tm.getImei(i);
                if (imei != null && !imei.isEmpty() && !list.contains(imei)) {
                    list.add(imei);
                }
            } catch (Exception ignored) {
            } catch (NoSuchMethodError ignored) {
            }
            try {
                String deviceId1 = tm.getDeviceId(i);
                if (deviceId != null && !deviceId1.isEmpty() && !list.contains(deviceId1)) {
                    list.add(deviceId1);
                }
            } catch (Exception ignored) {
            } catch (NoSuchMethodError ignored) {
            }
        }
        return list;
    }

    public static String getImei1(Context context) {
        List<String> list = getDeviceIdList(context);
        if (!list.isEmpty()) {
            for (String s : list) {
                if (s.length() == 15) {//取第一个位数为15的号码，即imei1
                    return s;
                }
            }
        }
        return "";
    }

    public static String getImei2(Context context) {
        List<String> list = getDeviceIdList(context);
        if (!list.isEmpty()) {
            int size = list.size();
            if (size >= 2) {
                for (int i = 0; i < list.size(); i++) {
                    if (i != 0 && list.get(i).length() == 15) {//获取第二个位数为15的号码，即imei2
                        return list.get(i);
                    }
                }
            }
        }
        return "";
    }

    public static String getMeid(Context context) {
        List<String> list = getDeviceIdList(context);
        if (!list.isEmpty()) {
            for (String s : list) {
                if (s.length() == 14) {//获取位数为14的号码
                    return s;
                }
            }
        }
        return "";
    }

    public static HashMap getNetwork(Context context) {
        HashMap<String, String> network = new HashMap<>();
        network.put(NETWORK_TYPE, "");
        network.put(NETWORK_NAME, "");
        String netType = "nono_connect";
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            return network;
        }
        //否则 NetworkInfo对象不为空 则获取该networkInfo的类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            //WIFI
            netType = "wifi";
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            network.put(NETWORK_NAME,
                    ssid.replaceAll("\"", ""));
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            //4G
            if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
                    && !telephonyManager.isNetworkRoaming()) {
                netType = "4G";
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0 && !telephonyManager.isNetworkRoaming()) {
                netType = "3G";
                //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS || nSubType == TelephonyManager.NETWORK_TYPE_EDGE || nSubType == TelephonyManager.NETWORK_TYPE_CDMA && !telephonyManager.isNetworkRoaming()) {
                netType = "2G";
            } else {
                netType = "2G";
            }
            network.put(NETWORK_NAME, telephonyManager.getSimOperatorName());
        }
        network.put(NETWORK_CODE, telephonyManager.getSimOperator());
        network.put(NETWORK_TYPE, netType);
        return network;
    }

    public static String getInNetIp(Context context) {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            return "";
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);

        return ip;
    }

    private static String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
    }

    private static String[] platforms = {
            "http://pv.sohu.com/cityjson",
            "http://pv.sohu.com/cityjson?ie=utf-8",
            "http://ip.chinaz.com/getip.aspx"
    };

    /**
     * Return the MAC address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
    public static String getMacAddress(Context context) {
        return getMacAddress(context, (String[]) null);
    }

    /**
     * Return the MAC address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
    public static String getMacAddress(Context context, final String... excepts) {
        String macAddress = getMacAddressByNetworkInterface();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByInetAddress();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByWifiInfo(context);
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByFile();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        return "";

    }

    private static boolean isAddressNotInExcepts(final String address, final String... excepts) {
        if (excepts == null || excepts.length == 0) {
            return !"02:00:00:00:00:00".equals(address);
        }
        for (String filter : excepts) {
            if (address.equals(filter)) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private static String getMacAddressByWifiInfo(Context context) {
        try {
            final WifiManager wifi = (WifiManager) context
                    .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                final WifiInfo info = wifi.getConnectionInfo();
                if (info != null) return info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacAddressByNetworkInterface() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (ni == null || !ni.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = ni.getHardwareAddress();
                if (macBytes != null && macBytes.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : macBytes) {
                        sb.append(String.format("%02x:", b));
                    }
                    return sb.substring(0, sb.length() - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacAddressByInetAddress() {
        try {
            InetAddress inetAddress = getInetAddress();
            if (inetAddress != null) {
                NetworkInterface ni = NetworkInterface.getByInetAddress(inetAddress);
                if (ni != null) {
                    byte[] macBytes = ni.getHardwareAddress();
                    if (macBytes != null && macBytes.length > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : macBytes) {
                            sb.append(String.format("%02x:", b));
                        }
                        return sb.substring(0, sb.length() - 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static InetAddress getInetAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        if (hostAddress.indexOf(':') < 0) return inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMacAddressByFile() {
        String str = "";
        String macSerial = "02:00:00:00:00:00";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            Log.e("----->" + "NetInfoManager", "getMacAddress:" + ex.toString());
        }
        return macSerial;
    }

    public static String getSdkVersion() {
        return BuildConfig.BUILD_TYPE;
    }

    public static String getGameVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSdkPackageName() {
        return BuildConfig.LIBRARY_PACKAGE_NAME;
    }

    public static String getGamePackageName(Context context) {
        return context.getPackageName();
    }

    private static String getParams(HashMap<String, String> params) {
        String result = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result = "&" + entry.getKey() + "=" + entry.getValue();
        }
        return result.substring(1);
    }

    public static String getDeviceInfoJson(
            Context context,
            DeviceInfo.AndroidQ androidQ
    ) {
        DeviceInfo deviceInfo = new DeviceInfo();
        GameConfig gameConfig = ConfigUtils.jsonToObject(context, Constants.CONFIG_FILE_NAME, GameConfig.class);
        if (gameConfig == null) {
            gameConfig = GameConfig.BLANK_OBJ;
        }
        deviceInfo.setChannel_id(gameConfig.getChannel_id());
        if (TextUtils.isEmpty(gameConfig.getGame_id())) {
            deviceInfo.setGame_id(SPUtils.getString(context, Constants.KEY_SP_GAME_ID, ""));
        } else {
            deviceInfo.setGame_id(gameConfig.getGame_id());
        }
        DeviceInfo.DeviceBean deviceBean = new DeviceInfo.DeviceBean();
        deviceBean.setOs("Android");
        DeviceInfo.DeviceBean.AndroidBean androidBean = new DeviceInfo.DeviceBean.AndroidBean();
        String imei1 = DeviceUtils.getImei1(context);
        String imei2 = DeviceUtils.getImei2(context);
        androidBean.setImei(Arrays.asList(imei1, imei2));
        androidBean.setAndroid_id(DeviceUtils.getAndroidID(context));
        androidBean.setSim_serial(Collections.singletonList(""));
        androidBean.setImsi("");
        androidBean.setVersion(Build.VERSION.RELEASE);
        androidBean.setBrand(Build.BRAND);
        androidBean.setModel(Build.MODEL);
        androidBean.setId(Build.ID);
        androidBean.setProduct(Build.PRODUCT);
        androidBean.setSerial(Build.SERIAL);
        androidBean.setSdk_package_name(DeviceUtils.getSdkPackageName());
        androidBean.setSdk_version(DeviceUtils.getSdkVersion());
        androidBean.setGame_package_name(DeviceUtils.getGamePackageName(context));
        androidBean.setGame_version(DeviceUtils.getGameVersion(context));
        androidBean.setAndroid_q(androidQ);
        deviceBean.setAndroid(androidBean);
        DeviceInfo.DeviceBean.NetworkBean networkBean = new DeviceInfo.DeviceBean.NetworkBean();
        HashMap networkMap = DeviceUtils.getNetwork(context);
        String nCode = (networkMap.get(NETWORK_CODE) == null ? "" : (String) networkMap.get(NETWORK_CODE));
        String nType = (networkMap.get(NETWORK_TYPE) == null ? "" : (String) networkMap.get(NETWORK_TYPE));
        String nName = (networkMap.get(NETWORK_NAME) == null ? "" : (String) networkMap.get(NETWORK_NAME));
        try {
            networkBean.setCode(Integer.valueOf(nCode));
        } catch (Exception e) {
            networkBean.setCode(-1);
        }
        networkBean.setName(nName);
        networkBean.setType(nType);
        networkBean.setIntranet_ip(DeviceUtils.getInNetIp(context));
        networkBean.setMac(DeviceUtils.getMacAddress(context));
        deviceBean.setNetwork(networkBean);
        deviceInfo.setDevice(deviceBean);
        return JSON.toJSONString(deviceInfo);
    }


    public static class DeviceInfo {

        /**
         * channel_id : 08e965eba20944dda47346c43c20107b
         * game_id : aafd3d2fa5d74e6ebd7dec56158017cc
         * package_id : 7d47ed037589411b9d4a8551bb496165
         * plan_id : e3c4578ecd3d480ba862f45d8f9bd38a
         * site_id : 32765d4a18744626b14fecacc4b03ea1
         * device : {"os":"Android","android":{"imei":["imei1","imei2"],"android_id":"bcbc00f09479aa5b","sim_serial":["sim1","sim2"],"imsi":"460017932859596","version":"8.0.1","brand":"Huawei","model":"HUAWEI G750-T01","id":"HUAWEITAG-TLOO","product":"G750-T01","serial":"YGKBBBB5C1711949","sdk_package_name":"com.xxx.xxx.www","sdk_version":"19.2.4","game_package_name":"com.xxx.xxx.www","game_version":"1.0.0"},"network":{"code":46001,"name":"中国联通(wifi名称)","type":"wifi","intranet_ip":"192.168.1.145","mac":"a8:a6:68:a3:d9:ef"}}
         */

        private String channel_id;
        private String game_id;
        private String user_id;
        private String domain;
        private String user_pact;
        private String h5_apk_url;
        private String customer_service;
        private DeviceBean device;

        public String getChannel_id() {
            return channel_id;
        }

        public void setChannel_id(String channel_id) {
            this.channel_id = channel_id;
        }

        public String getGame_id() {
            return game_id;
        }

        public void setGame_id(String game_id) {
            this.game_id = game_id;
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

        public String getUser_pact() {
            return user_pact;
        }

        public void setUser_pact(String user_pact) {
            this.user_pact = user_pact;
        }

        public String getH5_apk_url() {
            return h5_apk_url;
        }

        public void setH5_apk_url(String h5_apk_url) {
            this.h5_apk_url = h5_apk_url;
        }

        public String getCustomer_service() {
            return customer_service;
        }

        public void setCustomer_service(String customer_service) {
            this.customer_service = customer_service;
        }

        public DeviceBean getDevice() {
            return device;
        }

        public void setDevice(DeviceBean device) {
            this.device = device;
        }

        public static class AndroidQ implements Parcelable {
            private String oaid;
            private String vaid;
            private String aaid;

            public String getOaid() {
                return oaid;
            }

            public void setOaid(String oaid) {
                this.oaid = oaid;
            }

            public String getVaid() {
                return vaid;
            }

            public void setVaid(String vaid) {
                this.vaid = vaid;
            }

            public String getAaid() {
                return aaid;
            }

            public void setAaid(String aaid) {
                this.aaid = aaid;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.oaid);
                dest.writeString(this.vaid);
                dest.writeString(this.aaid);
            }

            public AndroidQ() {
            }

            protected AndroidQ(Parcel in) {
                this.oaid = in.readString();
                this.vaid = in.readString();
                this.aaid = in.readString();
            }

            public static final Parcelable.Creator<AndroidQ> CREATOR = new Parcelable.Creator<AndroidQ>() {
                @Override
                public AndroidQ createFromParcel(Parcel source) {
                    return new AndroidQ(source);
                }

                @Override
                public AndroidQ[] newArray(int size) {
                    return new AndroidQ[size];
                }
            };
        }

        public static class DeviceBean {
            /**
             * os : Android
             * android : {"imei":["imei1","imei2"],"android_id":"bcbc00f09479aa5b","sim_serial":["sim1","sim2"],"imsi":"460017932859596","version":"8.0.1","brand":"Huawei","model":"HUAWEI G750-T01","id":"HUAWEITAG-TLOO","product":"G750-T01","serial":"YGKBBBB5C1711949","sdk_package_name":"com.xxx.xxx.www","sdk_version":"19.2.4","game_package_name":"com.xxx.xxx.www","game_version":"1.0.0"}
             * network : {"code":46001,"name":"中国联通(wifi名称)","type":"wifi","intranet_ip":"192.168.1.145","mac":"a8:a6:68:a3:d9:ef"}
             */

            private String os;
            private AndroidBean android;
            private NetworkBean network;

            public String getOs() {
                return os;
            }

            public void setOs(String os) {
                this.os = os;
            }

            public AndroidBean getAndroid() {
                return android;
            }

            public void setAndroid(AndroidBean android) {
                this.android = android;
            }

            public NetworkBean getNetwork() {
                return network;
            }

            public void setNetwork(NetworkBean network) {
                this.network = network;
            }

            public static class AndroidBean {

                /**
                 * imei : ["imei1","imei2"]
                 * android_id : bcbc00f09479aa5b
                 * sim_serial : ["sim1","sim2"]
                 * imsi : 460017932859596
                 * version : 8.0.1
                 * brand : Huawei
                 * model : HUAWEI G750-T01
                 * id : HUAWEITAG-TLOO
                 * product : G750-T01
                 * serial : YGKBBBB5C1711949
                 * sdk_package_name : com.xxx.xxx.www
                 * sdk_version : 19.2.4
                 * game_package_name : com.xxx.xxx.www
                 * game_version : 1.0.0
                 */

                private String android_id;
                private String imsi;
                private String version;
                private String brand;
                private String model;
                private String id;
                private String product;
                private String serial;
                private String sdk_package_name;
                private String sdk_version;
                private String game_package_name;
                private String game_version;
                private List<String> imei;
                private List<String> sim_serial;
                private AndroidQ android_q;

                public String getAndroid_id() {
                    return android_id;
                }

                public void setAndroid_id(String android_id) {
                    this.android_id = android_id;
                }

                public String getImsi() {
                    return imsi;
                }

                public void setImsi(String imsi) {
                    this.imsi = imsi;
                }

                public String getVersion() {
                    return version;
                }

                public void setVersion(String version) {
                    this.version = version;
                }

                public String getBrand() {
                    return brand;
                }

                public void setBrand(String brand) {
                    this.brand = brand;
                }

                public String getModel() {
                    return model;
                }

                public void setModel(String model) {
                    this.model = model;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getProduct() {
                    return product;
                }

                public void setProduct(String product) {
                    this.product = product;
                }

                public String getSerial() {
                    return serial;
                }

                public void setSerial(String serial) {
                    this.serial = serial;
                }

                public String getSdk_package_name() {
                    return sdk_package_name;
                }

                public void setSdk_package_name(String sdk_package_name) {
                    this.sdk_package_name = sdk_package_name;
                }

                public String getSdk_version() {
                    return sdk_version;
                }

                public void setSdk_version(String sdk_version) {
                    this.sdk_version = sdk_version;
                }

                public String getGame_package_name() {
                    return game_package_name;
                }

                public void setGame_package_name(String game_package_name) {
                    this.game_package_name = game_package_name;
                }

                public String getGame_version() {
                    return game_version;
                }

                public void setGame_version(String game_version) {
                    this.game_version = game_version;
                }

                public List<String> getImei() {
                    return imei;
                }

                public void setImei(List<String> imei) {
                    this.imei = imei;
                }

                public List<String> getSim_serial() {
                    return sim_serial;
                }

                public AndroidQ getAndroid_q() {
                    return android_q;
                }

                public void setAndroid_q(AndroidQ android_q) {
                    this.android_q = android_q;
                }

                public void setSim_serial(List<String> sim_serial) {
                    this.sim_serial = sim_serial;
                }
            }

            public static class NetworkBean {
                /**
                 * code : 46001
                 * name : 中国联通(wifi名称)
                 * type : wifi
                 * intranet_ip : 192.168.1.145
                 * mac : a8:a6:68:a3:d9:ef
                 */

                private int code;
                private String name;
                private String type;
                private String intranet_ip;
                private String mac;

                public int getCode() {
                    return code;
                }

                public void setCode(int code) {
                    this.code = code;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getIntranet_ip() {
                    return intranet_ip;
                }

                public void setIntranet_ip(String intranet_ip) {
                    this.intranet_ip = intranet_ip;
                }

                public String getMac() {
                    return mac;
                }

                public void setMac(String mac) {
                    this.mac = mac;
                }
            }
        }

    }

}