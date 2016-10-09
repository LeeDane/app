package com.leedane.cn.financial.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.MySettingBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 读取设置的配置Properties文件操作
 * Created by LeeDane on 2016/9/18.
 */
public class PropertiesUtil {
    private static final String TAG = "PropertiesUtil";


    public static File getCacheFileDir(Context context, String fileName){
        File sdDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdDir = Environment.getExternalStorageDirectory();
        }
        else{
            sdDir = context.getCacheDir();
        }
        File cacheDir = new File(sdDir, fileName);
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * fileName
     * @param fileName
     * @return
     */
    public static Map<String, Object> getProps(String fileName){
        Properties props = new Properties();
        Map<String, Object> propVal = new HashMap<>();
        try {
            props.load(BaseApplication.newInstance().getAssets().open(fileName));
            Iterator<Map.Entry<Object, Object>> it = props.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Object, Object> entry = it.next();
                propVal.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }catch (FileNotFoundException e) {
            Log.e(TAG, fileName +" Not Found Exception", e);
        }catch (IOException e) {
                Log.e(TAG, fileName +" IO Exception",e);
        }
        return propVal;
    }

    /**
     * 添加prop属性
     * 存在的key则做替换处理
     * @param fileName
     * @param key
     * @param value
     * @return
     */
    public static boolean addProp(String fileName, String key, String value) {
        Properties props = new Properties();
        try {
            props.load(BaseApplication.newInstance().getAssets().open(fileName));
            OutputStream out = BaseApplication.newInstance().openFileOutput("file:///android_asset/" + fileName, Context.MODE_PRIVATE);
            Enumeration<?> e = props.propertyNames();
            if(e.hasMoreElements()){
                while (e.hasMoreElements()) {
                    String s = (String) e.nextElement();
                    if (!s.equals(key)) {
                        props.setProperty(s, props.getProperty(s));
                    }
                }
            }
            props.setProperty(key, value);
            props.store(out, null);
            return true;
        } catch (IOException e) {
            Log.e(TAG, fileName +" IO Exception",e);
        }
        return false;
    }

    /**
     * 添加所有的属性
     * @param fileName
     * @param propVal
     * @return
     */
    public static boolean addAllProps(String fileName, Map<String, Object> propVal) {
        Properties props = new Properties();
        try {
            props.load(BaseApplication.newInstance().getAssets().open(fileName));
            OutputStream out = BaseApplication.newInstance().openFileOutput("file:///android_asset/" +fileName, Context.MODE_PRIVATE);
            for(Map.Entry<String, Object> entry: propVal.entrySet()){
                props.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
            }
            props.store(out, null);
            return true;
        } catch (Exception e) {
            Log.e(TAG, fileName +" Exception",e);
        }
        return false;
    }
}
