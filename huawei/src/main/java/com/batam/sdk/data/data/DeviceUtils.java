package com.batam.sdk.data.data;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.batam.sdk.BuildConfig;
import com.batam.sdk.http.PrintIntercepter;
import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import okhttp3.OkHttpClient;


@SuppressLint("MissingPermission")
public class DeviceUtils {


    private static final String NETWORK_TYPE = "type";
    private static final String NETWORK_NAME = "name";
    private static final String NETWORK_CODE = "code";



    // 综上述，AndroidId 和 Serial Number 的通用性都较好，并且不受权限限制
    // 如果刷机和恢复出厂设置会导致设备标识符重置这一点可以接受的话，那么将他们组合使用时，唯一性就可以应付绝大多数设备了。
    public static String getUniqueId(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        return toMD5(id);
    }

    private static String toMD5(String text) {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (messageDigest == null) {
            return "";
        }
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


    public static String getSimIccid(Context context) {
        return "";
    }

    public static String getImsi(Context context) {
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
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
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

    public static String getMacAddress(Context context) {
        return getMacAddress(context, (String[]) null);
    }

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
        return BuildConfig.VERSION_NAME;
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
        return BuildConfig.APPLICATION_ID;
    }

    public static String getGamePackageName(Context context) {
        return context.getPackageName();
    }

    public static String getDeviceInfoJson(
            Context context,
            AndroidQ androidQ
    ) {
        DeviceInfo deviceInfo = new DeviceInfo();
        GameConfig gameConfig = GameConfig.jsonToObject(context, GameConfig.JSON_FILE_NAME); //不要用这个 config.json
        deviceInfo.setChannel_id(gameConfig.getChannelId());
        if (TextUtils.isEmpty(gameConfig.getGameId())) {
            deviceInfo.setGame_id("");
        } else {
            deviceInfo.setGame_id(gameConfig.getGameId());
        }
        deviceInfo.setPackage_id(gameConfig.getPackageId());
        deviceInfo.setPlan_id(gameConfig.getPlanId());
        deviceInfo.setSite_id(gameConfig.getSiteId());
        deviceInfo.setUser_id(gameConfig.getUser_id());
        DeviceInfo.DeviceBean deviceBean = new DeviceInfo.DeviceBean();
        deviceBean.setOs("Android");
        DeviceInfo.DeviceBean.AndroidBean androidBean = new DeviceInfo.DeviceBean.AndroidBean();
        String imei1 = DeviceUtils.getImei1(context);
        String imei2 = DeviceUtils.getImei2(context);
        androidBean.setImei(Arrays.asList(imei1, imei2));
        androidBean.setAndroid_id(DeviceUtils.getAndroidID(context));
        androidBean.setSim_serial(Collections.singletonList(getSimIccid(context)));
        androidBean.setImsi(DeviceUtils.getImsi(context));
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
        String nCode = networkMap.get(NETWORK_CODE) == null ? "" : (String) networkMap.get(NETWORK_CODE);
        String nType = networkMap.get(NETWORK_TYPE) == null ? "" : (String) networkMap.get(NETWORK_TYPE);
        String nName = networkMap.get(NETWORK_NAME) == null ? "" : (String) networkMap.get(NETWORK_NAME);
        try {
            if (nCode == null) {
                networkBean.setCode(-1);
            } else {
                networkBean.setCode(Integer.valueOf(nCode));
            }
        } catch (Exception e) {
            networkBean.setCode(-1);
        }
        networkBean.setName(nName);
        networkBean.setType(nType);
        networkBean.setIntranet_ip(DeviceUtils.getInNetIp(context));
        networkBean.setMac(DeviceUtils.getMacAddress(context));
        deviceBean.setNetwork(networkBean);
        deviceInfo.setDevice(deviceBean);
        Gson gson = new Gson();
        return gson.toJson(deviceInfo);
    }

    public static String toUnicode(String originStr, String charset) {
        try {
            return URLEncoder.encode(originStr, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public static String encryptPaySign(Context context, TreeMap<String, String> params) {
        String param = "";
        if (params.size() > 0) {
            NavigableMap<String, String> descendingMap = params.descendingMap();
            StringBuilder paramBuilder = new StringBuilder();
            Set<String> paramsKeys = descendingMap.keySet();
            for (String paramKey : paramsKeys) {
                paramBuilder.append(paramKey);
                paramBuilder.append("=");
                paramBuilder.append(descendingMap.get(paramKey));
                paramBuilder.append("&");
            }
            GameConfig gameConfig = GameConfig.jsonToObject(context, GameConfig.JSON_FILE_NAME);
            if (gameConfig.getGameId().isEmpty()) {
                gameConfig.setGameId(getBuildConfigGameId());
            }
            paramBuilder.append("game_id=").append(gameConfig.getGameId());
            try {
                param = URLEncoder.encode(paramBuilder.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return e.getLocalizedMessage();
            }
        }
        return DeviceUtils.toMD5(param);
    }

    public static final String getBuildConfigGameId() {
        //System.out.println("BuildConfig.TMP_GID = " + BuildConfig.TMP_GID);
        //return BuildConfig.TMP_GID;
        return "";
    }

    public static String readIniValue(Context context,String fileName, String key){
        if(TextUtils.isEmpty(key)) return "";
        Properties properties = new Properties();
        try {
            properties.load(context.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
        return  properties.get(key) !=null ? properties.get(key).toString() : "" ;
        //properties[key]?.toString() ?: ""
    }
    /*------------------------utility functions end----------------------*/


    /*------------------------data------------------------*/

    private static SharedPreferences sp;
    private static Application applicationContext;

    public static void setApp(Application application) {
        applicationContext=application;
        sp= PreferenceManager.getDefaultSharedPreferences(application);
        WriteDeviceInfoToSP();
        /*-----okgo start*/
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.addInterceptor(new PrintIntercepter());
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("HttpManagerOkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        OkGo.getInstance().init(application).setOkHttpClient(builder.build());
        OkGo.getInstance().addCommonHeaders(
                new HttpHeaders(
                        KEY_HTTP_ACCEPT_HEADER,
                        "application/json"
                )
        )
                .addCommonHeaders(
                        new HttpHeaders(
                                KEY_HTTP_DEVICE_HEADER,
                                getDeviceInfo()
                        )
                );

        /*okgo --end ---*/
        updateDeviceInfoHeader(application);

    }

    private static String ANDROIDQ_KEY="ANDROIDQ_KEY";
    private static String DEVICE_INFO_KEY="DEVICE_INFO_KEY";
    private static String TAG="DeviceUtil";

    private static Gson gson=new Gson();

    public static void putAndroidQ(AndroidQ androidQ){
        sp.edit().putString(ANDROIDQ_KEY,gson.toJson(androidQ)).apply();
        Log.e(TAG, "putAndroidQ: "+gson.toJson(androidQ));
    }

    private static AndroidQ getAndroidQ(){
        String a=sp.getString(ANDROIDQ_KEY,null);
        Log.e(TAG, "getAndroidQ:"+a );
        try{
            return gson.fromJson(a,AndroidQ.class);
        }catch (Exception e ){
            return null;
        }
    }

    private static void WriteDeviceInfoToSP(){ //把http 头写入sp
        AndroidQ androidQ=getAndroidQ();
        if(androidQ==null){
            androidQ=new AndroidQ("","","");
            Toast.makeText(applicationContext,"Android q is null",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(applicationContext,"Android q is not null",Toast.LENGTH_SHORT).show();
        }
        String Info=DeviceUtils.toUnicode(DeviceUtils.getDeviceInfoJson(applicationContext, androidQ), "UTF-8");
        Log.e(TAG, "saveDeviceInfo: "+DeviceUtils.getDeviceInfoJson(applicationContext, androidQ) );
        sp.edit().putString(DEVICE_INFO_KEY,Info).apply();
    }

    private static String getDeviceInfo(){
        return sp.getString(DEVICE_INFO_KEY,"");
    }


    public static void updateDeviceInfoHeader(final Context context/*, final InfoHeaderUpdateCompleteCallback callback*/) {
        if (Build.VERSION.SDK_INT >= 29) {
            int nres = MdidSdkHelper.InitSdk(context, true, new IIdentifierListener() {
                @Override
                public void OnSupport(boolean isSupport, final IdSupplier idSupplier) {
                    if (isSupport && idSupplier != null) {
                        AndroidQ androidQ = new AndroidQ();
                        androidQ.setOaid(idSupplier.getOAID());
                        androidQ.setAaid(idSupplier.getAAID());
                        androidQ.setVaid(idSupplier.getVAID());
                        String msg="oaid: " + idSupplier.getOAID();
                        String msg1="aaid: " + idSupplier.getAAID();
                        String msg2="vaid: " + idSupplier.getVAID();
                        Log.e("hahahaid","oaid:"+msg+" aaid:"+msg1+"  vaid:"+msg2);
                        putAndroidQ(androidQ); //把AndroidQ写进sp
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            WriteDeviceInfoToSP();
                            OkGo.getInstance().addCommonHeaders(
                                    new HttpHeaders(
                                            KEY_HTTP_ACCEPT_HEADER,
                                            "application/json"
                                    )
                            )
                                    .addCommonHeaders(
                                            new HttpHeaders(
                                                    KEY_HTTP_DEVICE_HEADER,
                                                    getDeviceInfo()
                                            )
                                    );
                        }
                    });
                }
            });
            if (nres == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT) {
                Log.e("TAG_DEVICE_ID", "不支持的设备");
            } else if (nres == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE) {
                Log.e("TAG_DEVICE_ID", "加载配置文件出错");
            } else if (nres == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT) {
                Log.e("TAG_DEVICE_ID", "不支持的设备厂商");
            } else if (nres == ErrorCode.INIT_ERROR_RESULT_DELAY) {
                Log.e("TAG_DEVICE_ID", "获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程");
            } else if (nres == ErrorCode.INIT_HELPER_CALL_ERROR) {
                Log.e("TAG_DEVICE_ID", "反射调用出错");
            }
        } else {
            WriteDeviceInfoToSP();
        }
    }


    //http header
    public static final String KEY_HTTP_DEVICE_HEADER = "Info";
    public static final String KEY_HTTP_ACCEPT_HEADER = "Accept";
    public static final String KEY_HTTP_AUTH_HEADER = "Authorization";
}


