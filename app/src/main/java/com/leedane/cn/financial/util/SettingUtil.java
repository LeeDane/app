package com.leedane.cn.financial.util;

import android.content.Context;

import com.leedane.cn.application.BaseApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 记账设置工具类
 * Created by LeeDane on 2016/9/18.
 */
public class SettingUtil {
    public static final String TAG = "FinancialSettingUtil";
    //public static final String FILENAME = "financial_setting";
    private final static String APP_CONFIG = "config";
    private static SettingUtil instance = null;
    private Context mContext = BaseApplication.newInstance();

    public static boolean AUTO_SYNCHRONIZED;
    public static boolean RECEIVE_NOTIFICATION;
    public static int RECENT_LOAD;

    private static Map<String, Object> props = new HashMap<>();

    public static synchronized SettingUtil getInstance() {
        if (instance == null) {
            synchronized (SettingUtil.class){
                instance = new SettingUtil();
            }
        }
        return instance;
    }

    private SettingUtil(){
        String path = BaseApplication.newInstance().getDir(APP_CONFIG, Context.MODE_PRIVATE).getPath() + File.separator +APP_CONFIG;
        props = PropertiesUtil.getProps(path);
        if(props.isEmpty()){
            //初始化
            init();
        }
    }

    /**
     * 初始化默认设置
     */
    private void init(){
        props.clear();
        File dirFile = BaseApplication.newInstance().getDir(APP_CONFIG, Context.MODE_PRIVATE);
        if(!dirFile.exists())
            try {
                dirFile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        //初始化数据
        initProps();
        String path = dirFile.getPath() + File.separator +APP_CONFIG;
        PropertiesUtil.addAllProps(path, props);
    }

    /**
     * 初始化数据
     */
    private void initProps() {

        AUTO_SYNCHRONIZED = true;
        RECEIVE_NOTIFICATION = true;
        RECENT_LOAD = 15;

        props.put("auto_synchronized", AUTO_SYNCHRONIZED);
        props.put("receive_notification", RECEIVE_NOTIFICATION);
        props.put("recent_load", RECENT_LOAD);
    }

    /**
     * 添加prop属性
     * 存在的key则做替换处理
     * @param key
     * @param value
     * @return
     */
    public boolean addProp(String key, Object value) {
        return PropertiesUtil.addProp(APP_CONFIG, key, String.valueOf(value));
    }

    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }

    public Properties get() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            // 读取files目录下的config
            // fis = activity.openFileInput(APP_CONFIG);

            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            fis = new FileInputStream(dirConf.getPath() + File.separator
                    + APP_CONFIG);

            props.load(fis);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return props;
    }

    private void setProps(Properties p) {
        FileOutputStream fos = null;
        try {
            // 把config建在files目录下
            // fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);

            // 把config建在(自定义)app_config的目录下
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File conf = new File(dirConf, APP_CONFIG);
            fos = new FileOutputStream(conf);

            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public void set(Properties ps) {
        Properties props = get();
        props.putAll(ps);
        setProps(props);
    }

    public void set(String key, String value) {
        Properties props = get();
        props.setProperty(key, value);
        setProps(props);
    }

    public void remove(String... key) {
        Properties props = get();
        for (String k : key)
            props.remove(k);
        setProps(props);
    }
}
