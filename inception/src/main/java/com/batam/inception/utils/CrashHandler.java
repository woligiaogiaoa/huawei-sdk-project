package com.batam.inception.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import com.batam.inception.http.HTTPConstants;
import com.batam.inception.http.SimpleCallback;
import com.elephant.library.kalle.BuildConfig;
import com.elephant.library.kalle.Kalle;
import com.elephant.library.kalle.simple.SimpleResponse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    private static String PATH;
    private static final String FILE_NAME = "crash";

    //log文件的后缀名
    private static final String FILE_NAME_SUFFIX = ".trace";

    private final static String SP_KEY_DEVICE_MARKET_NAME = "DEVICE_MARKET_NAME";

    //系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private Context mContext;

    //构造方法私有，防止外部构造多个实例，即采用单例模式
    private CrashHandler() {
    }

    private static class Singleton {
        private static final CrashHandler sInstance = new CrashHandler();
    }

    public static CrashHandler getInstance() {
        return Singleton.sInstance;
    }

    //这里主要完成初始化工作
    public void init(Context context) {
        //获取系统默认的异常处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //获取Context，方便内部使用
        mContext = context.getApplicationContext();
        PATH = mContext.getFilesDir().getAbsolutePath() + "/cluster_sdk/log/";
        try {
            uploadCrash();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //导出异常信息到SD卡中
        dumpExceptionToSDCard(ex);

        //打印出当前调用栈信息
        ex.printStackTrace();

        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }

    }

    private void dumpExceptionToSDCard(Throwable ex) {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡


        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        //以当前时间创建log文件
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //导出发生异常的时间
            pw.println(time);

            //导出手机信息
            dumpPhoneInfo(pw);

            pw.println();
            //导出异常的调用栈信息
            ex.printStackTrace(pw);

            pw.close();
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        //应用的版本名称和版本号
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //SDK版本名和版本号
        pw.print("SDK Version: ");
        pw.print(BuildConfig.VERSION_NAME);
        pw.print('_');
        pw.println(BuildConfig.VERSION_CODE);

        //android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);

        //cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
    }

    private void uploadCrash() throws IOException {
        File dir = new File(PATH);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (final File file : files) {
                InputStream is = new FileInputStream(file);
                String line;
                StringBuffer sb = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                reader.close();
                is.close();
                Kalle.post(HTTPConstants.CRASH_REPORT)
                        .param("log", sb.toString())
                        .perform(new SimpleCallback<String>(mContext) {
                            @Override
                            public void onResponse(SimpleResponse<String, String> simpleResponse) {
                                file.delete();
                                Log.i(TAG, "crash log uploaded!");
                            }
                        });

            }
        }
    }
}