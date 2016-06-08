package com.leedane.cn.util;

import android.util.Log;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.MySettingBean;
import com.leedane.cn.database.MySettingDataBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的设置配置
 * Created by LeeDane on 2016/6/7.
 */
public class MySettingConfigUtil {

    public static final String TAG = "MySettingConfigUtil";
    private static MySettingConfigUtil instance = null;

    private static boolean load_image = true;
    private static boolean no_notification = false;
    private static int first_load = 10;
    private static int other_load = 5;
    private static boolean double_click_out = false;
    private static boolean cache_blog = true;
    private static boolean cache_mood = true;
    private static int chat_text_size = 16;
    private static int chat_delete = 0; //0只删除本地，1：删除本地和数据库
    private static boolean chat_send_enter = true;
    private static boolean cache_gallery = true;
    private static boolean cache_file = true;

    public static synchronized MySettingConfigUtil getInstance() {
        if (instance == null) {
            instance = new MySettingConfigUtil();
        }
        return instance;
    }

    private MySettingConfigUtil(){
        MySettingDataBase mySettingDataBase = new MySettingDataBase(BaseApplication.newInstance());
        int total = mySettingDataBase.getTotal(BaseApplication.getLoginUserId());
        List<MySettingBean> mySettingBeans = new ArrayList<>();
        if(total < 1){
            mySettingBeans = MySettingDataBase.initMySetting();
        }else{
            mySettingBeans = mySettingDataBase.query();
        }

        for(MySettingBean mySetting: mySettingBeans){
            if("load_image".equals(mySetting.getKey())){
                if(mySetting.getValue().equals("1"))
                    setLoadImage(true);
                else
                    setLoadImage(false);
            }else if("no_notification".equals(mySetting.getKey())){
                if(mySetting.getValue().equals("1"))
                    setNoNotification(true);
                else
                    setNoNotification(false);
            }else if("first_load".equals(mySetting.getKey())){
                setFirstLoad(StringUtil.changeObjectToInt(mySetting.getValue()));
            }else if("other_load".equals(mySetting.getKey())){
                setOtherLoad(StringUtil.changeObjectToInt(mySetting.getValue()));
            }else if("double_click_out".equals(mySetting.getKey())){
                if(mySetting.getValue().equals("1"))
                    setDoubleClickOut(true);
                else
                    setDoubleClickOut(false);
            }else if("cache_blog".equals(mySetting.getKey())){
                if(mySetting.getValue().equals("1"))
                    setCacheBlog(true);
                else
                    setCacheBlog(false);
            }else if("cache_mood".equals(mySetting.getKey())){
                if(mySetting.getValue().equals("1"))
                    setCacheMood(true);
                else
                    setCacheMood(false);
            }else if("chat_text_size".equals(mySetting.getKey())){
                setChatTextSize(StringUtil.changeObjectToInt(mySetting.getValue()));
            }else if("chat_delete".equals(mySetting.getKey())){
                setChatDelete(StringUtil.changeObjectToInt(mySetting.getValue()));
            } else if ("chat_send_enter".equals(mySetting.getKey())){
                if(mySetting.getValue().equals("1"))
                    setChatSendEnter(true);
                else
                    setChatSendEnter(false);
            }else if("cache_gallery".equals(mySetting.getKey())){
                if(mySetting.getValue().equals("1"))
                    setCacheGallery(true);
                else
                    setCacheGallery(false);
            }else if("cache_file".equals(mySetting.getKey())){
                if(mySetting.getValue().equals("1"))
                    setCacheFile(true);
                else
                    setCacheFile(false);
            }
        }
        Log.i(TAG, "第一次把设置加载成功");
    }


    public static void setLoadImage(boolean load_image) {
        MySettingConfigUtil.load_image = load_image;
    }

    public static boolean getLoadImage(){
        return load_image;
    }

    public static void setNoNotification(boolean no_notification) {
        MySettingConfigUtil.no_notification = no_notification;
    }

    public static boolean getNoNotification() {
        return no_notification;
    }

    public static void setFirstLoad(int first_load) {
        MySettingConfigUtil.first_load = first_load;
    }

    public static int getFirstLoad() {
        return first_load;
    }

    public static void setOtherLoad(int other_load) {
        MySettingConfigUtil.other_load = other_load;
    }

    public static int getOtherLoad() {
        return other_load;
    }

    public static void setDoubleClickOut(boolean double_click_out) {
        MySettingConfigUtil.double_click_out = double_click_out;
    }

    public static boolean getDoubleClickOut() {
        return double_click_out;
    }

    public static void setCacheBlog(boolean cache_blog) {
        MySettingConfigUtil.cache_blog = cache_blog;
    }

    public static boolean getCacheBlog() {
        return cache_blog;
    }

    public static void setCacheMood(boolean cache_mood) {
        MySettingConfigUtil.cache_mood = cache_mood;
    }

    public static boolean getCacheMood() {
        return cache_mood;
    }

    public static void setChatTextSize(int chat_text_size) {
        MySettingConfigUtil.chat_text_size = chat_text_size;
    }

    public static int getChatTextSize() {
        return chat_text_size;
    }

    public static void setChatDelete(int chat_delete) {
        MySettingConfigUtil.chat_delete = chat_delete;
    }

    public static int getChatDelete() {
        return chat_delete;
    }

    public static void setChatSendEnter(boolean chat_send_enter) {
        MySettingConfigUtil.chat_send_enter = chat_send_enter;
    }

    public static boolean getChatSendEnter() {
        return chat_send_enter;
    }

    public static void setCacheGallery(boolean cache_gallery) {
        MySettingConfigUtil.cache_gallery = cache_gallery;
    }

    public static boolean getCacheGallery() {
        return cache_gallery;
    }

    public static void setCacheFile(boolean cache_file) {
        MySettingConfigUtil.cache_file = cache_file;
    }

    public static boolean getCacheFile() {
        return cache_file;
    }
}
