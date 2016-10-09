package com.leedane.cn.financial.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 记账设置工具类
 * Created by LeeDane on 2016/9/18.
 */
public class SettingUtil {
    public static final String TAG = "FinancialSettingUtil";
    public static final String FILENAME = "financial_setting";
    private static SettingUtil instance = null;

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
        props = PropertiesUtil.getProps(FILENAME);
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
        File file = new File("file:///android_asset/"+FILENAME);
        if(!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        //初始化数据
        initProps();
        PropertiesUtil.addAllProps(FILENAME, props);
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
        return PropertiesUtil.addProp(FILENAME, key, String.valueOf(value));
    }
}
