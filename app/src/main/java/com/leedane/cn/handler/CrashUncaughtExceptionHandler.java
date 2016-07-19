package com.leedane.cn.handler;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.leedane.cn.app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 对没有捕获的异常导致程序奔溃的处理
 * Created by LeeDane on 2015/10/15.
 */
public class CrashUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashUncaughtExceptionHandler";
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashUncaughtExceptionHandler crashHandler = new CrashUncaughtExceptionHandler();
    private Context mContext;
    private Map<String, String> infos = new HashMap<String, String>();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private String filePath ;

    private CrashUncaughtExceptionHandler(){}

    public static CrashUncaughtExceptionHandler getInstance() {
        return crashHandler;
    }

    public void init(Context context) {
        mContext = context;
        filePath = getCacheDir(mContext) + mContext.getResources().getString(R.string.crash_file_path) + "/";
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private File getCacheDir(Context context){
        File sdDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdDir = Environment.getExternalStorageDirectory();
        }
        else{
            sdDir = context.getCacheDir();
        }
        File cacheDir = new File(sdDir, context.getResources().getString(R.string.app_dirsname));
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, mContext.getString(R.string.crash_toast_message), Toast.LENGTH_LONG).show();

                Looper.loop();
            }
        }.start();
        collectDeviceInfo(mContext);
        saveCrashInfoFile(e);
        return true;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
        else {

            try {
                Thread.sleep(800);
            }
            catch (InterruptedException e) {
                //Log.e(TAG, "error : " +e.getMessage());
            }
            System.exit(1);
        }
    }

    public void collectDeviceInfo(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                String versionName = packageInfo.versionName == null ? "null" : packageInfo.versionName;
                String versionCode = packageInfo.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {

            //Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                //Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                //Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     *
     * 生成crash文件
     */
    private String saveCrashInfoFile(Throwable ex) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            stringBuffer.append(key + "=" + value + "\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        stringBuffer.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = filePath ;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(path + fileName);
                fileOutputStream.write(stringBuffer.toString().getBytes());
                fileOutputStream.close();
            }
            return fileName;
        }catch (Exception e) {
            Log.e("CrashHandler", mContext.getString(R.string.crash_write_error), e);
        }
        return null;
    }
}
