package com.batam.inception.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.batam.inception.sp.SdkConfig;
import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;


public class HeaderUtils {


    public static void updateDeviceInfoHeader(final Context context, final InfoHeaderUpdateCompleteCallback callback) {
        if (Build.VERSION.SDK_INT >= 29) {
            int nres = MdidSdkHelper.InitSdk(context, true, new IIdentifierListener() {
                @Override
                public void OnSupport(boolean isSupport, final IdSupplier idSupplier) {
                    if (isSupport && idSupplier != null) {
                        DeviceUtils.DeviceInfo.AndroidQ androidQ = new DeviceUtils.DeviceInfo.AndroidQ();
                        androidQ.setOaid(idSupplier.getOAID());
                        androidQ.setAaid(idSupplier.getAAID());
                        androidQ.setVaid(idSupplier.getVAID());
                        String msg="oaid: " + idSupplier.getOAID();
                        String msg1="aaid: " + idSupplier.getAAID();
                        String msg2="vaid: " + idSupplier.getVAID();
                        Log.e("hahahaid",msg+msg1+msg2);
                        SdkConfig.get().setAndroidQ(androidQ); //把AndroidQ写进sp
                    }
                    updateDeviceInfoHeader(context);//从sp取出AndroidQ，然后得到http请求头info，写进sp
                    callback.onComplete();
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
            updateDeviceInfoHeader(context);
            callback.onComplete();
        }
    }

    public static void updateDeviceInfoHeader(Context context) {
        SdkConfig.get().setIH(StringUtils.toUTF_8(DeviceUtils.getDeviceInfoJson(context, SdkConfig.get().getAndroidQ())));
    }

    public static void initDeviceInfoHeader(Context context) {
        SdkConfig.get().setIH(StringUtils.toUTF_8(DeviceUtils.getDeviceInfoJson(context, null)));
    }


    public interface InfoHeaderUpdateCompleteCallback {
        void onComplete();
    }

}
