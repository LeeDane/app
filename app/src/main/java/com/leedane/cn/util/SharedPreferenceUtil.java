package com.leedane.cn.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.SettingBean;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 从SharedPreference中获取本地缓存的数据
 * 获取这个的方法不能在非UI线程中，不然报错，必须在UI线程中使用
 * Created by LeeDane on 2015/10/15.
 */
public class SharedPreferenceUtil {
    public static final String TAG = "SharedPreferenceUtil";
    /**
     * 保存用户的信息
     *
     * @param context
     * @param userInfo
     * @throws Exception
     */
    public static void saveUserInfo(Context context, String userInfo) throws Exception {
        SharedPreferences preferences = context.getSharedPreferences(ConstantsUtil.STRING_USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ConstantsUtil.USERINFO, userInfo);
        editor.commit();
    }

    /**
     * 获取用户的信息，以jsonObject对象封装
     *
     * @param context
     * @return
     */
    public static JSONObject getUserInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(ConstantsUtil.STRING_USERINFO, Context.MODE_PRIVATE);
        String userinfo = preferences.getString(ConstantsUtil.USERINFO, null);
        if (StringUtil.isNull(userinfo)) {
            return null;
        }
        try {
            return new JSONObject(userinfo);
        } catch (JSONException e) {
            Log.i(TAG, "获取缓存的用户信息出现异常");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 清除缓存的用户信息
     * @param context
     */
    public static void clearUserInfo(Context context){
        SharedPreferences preferences = context.getSharedPreferences(ConstantsUtil.STRING_USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 保存登录用户的好友的列表
     *
     * @param context
     * @param friendJSONObject
     * @throws Exception
     */
    public static void saveFriends(Context context, String friendJSONObject) throws Exception {
        SharedPreferences preferences = context.getSharedPreferences(ConstantsUtil.STRING_FRIENDS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ConstantsUtil.FRIENDS, friendJSONObject);
        editor.commit();
    }

    /**
     * 获取登录用户的好友的列表，以jsonObject对象封装
     *
     * @param context
     * @return
     */
    public static String getFriends(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(ConstantsUtil.STRING_FRIENDS, Context.MODE_PRIVATE);
        String friends = preferences.getString(ConstantsUtil.FRIENDS, null);
        if (StringUtil.isNull(friends)) {
            return null;
        }
        return friends;
    }

    /**
     * 清除缓存的用户信息
     * @param context
     */
    public static void clearFriends(Context context){
        SharedPreferences preferences = context.getSharedPreferences(ConstantsUtil.STRING_FRIENDS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
    /**
     * 保存用户名称的历史输入信息
     *
     * @param context
     * @param username
     * @throws Exception
     */
    public static void saveUsernameHistory(Context context, String username) throws Exception {
        String historys = getUsernameHistory(context);

        SharedPreferences settings = context.getSharedPreferences(ConstantsUtil.STRING_USERNAME_HISTORY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (historys == null) {
            historys = username;
        } else {

            boolean canAdd = true;
            String[] array = historys.split(";");
            for (String ss : array) {
                if (username.equals(ss)) {
                    canAdd = false;
                    return;
                }
            }

            if (canAdd) {
                historys = historys + ";" + username;
            }
        }
        editor.putString(ConstantsUtil.USERNAME_HISTORY, historys);
        editor.commit();

    }

    /**
     * 获取保存用户名称历史输入信息
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getUsernameHistory(Context context) {
        SharedPreferences users = context.getSharedPreferences(ConstantsUtil.STRING_USERNAME_HISTORY, Context.MODE_PRIVATE);
        return users.getString(ConstantsUtil.USERNAME_HISTORY, null);

    }

    /**
     * 清除用户名称历史输入信息
     *
     * @param context
     */
    public static void clearUsernameHistory(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(ConstantsUtil.STRING_USERNAME_HISTORY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 保存设置的选项实体
     * @param context
     * @param key  每个选项的KEY
     * @param settingBean
     */
    public static void saveSettingBean(Context context, String key, SettingBean settingBean){
        SharedPreferences settings = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ConstantsUtil.SETTING_BEAN_UUID, UUID.randomUUID().toString());
        editor.putString(ConstantsUtil.SETTING_BEAN_TITLE, settingBean.getTitle());
        editor.putString(ConstantsUtil.SETTING_BEAN_CONTENT, settingBean.getContent());
        editor.putString(ConstantsUtil.SETTING_BEAN_HINT, settingBean.getHint());
        editor.commit();
    }

    /**
     * 获取设置的选项实体
     * @param context
     * @param key  每个选项的KEY
     */
    public static SettingBean getSettingBean(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SettingBean settingBean = new SettingBean();
        String uuid = settings.getString(ConstantsUtil.SETTING_BEAN_UUID, null);
        if(StringUtil.isNull(uuid))
            uuid = UUID.randomUUID().toString();
        settingBean.setUuid(uuid);
        settingBean.setTitle(settings.getString(ConstantsUtil.SETTING_BEAN_TITLE, null));
        String content = settings.getString(ConstantsUtil.SETTING_BEAN_CONTENT, null);

        //为空并且是服务器地址
        if(StringUtil.isNull(content) && ConstantsUtil.STRING_SETTING_BEAN_SERVER.equalsIgnoreCase(key)){
            content = ConstantsUtil.DEFAULT_SERVER_URL;
        }

        //为空手机标记赋值
        if(StringUtil.isNull(content) && ConstantsUtil.STRING_SETTING_BEAN_PHONE.equalsIgnoreCase(key)){
            content = "手机客户端";
        }
        settingBean.setContent(content);
        settingBean.setHint(settings.getString(ConstantsUtil.SETTING_BEAN_HINT, null));
        return settingBean;
    }

    /**
     * 保存心情草稿信息
     * @param context
     * @param content
     * @param uris
     * @throws Exception
     */
    public static void saveMoodDraft(Context context, String content, String uris) {
        SharedPreferences preferences = context.getSharedPreferences(ConstantsUtil.MOOD_DRAFT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ConstantsUtil.MOOD_DRAFT_CONTENT, content);
        editor.putString(ConstantsUtil.MOOD_DRAFT_IMGS, uris);
        editor.commit();
    }

    /**
     * 获取心情草稿信息，以Map对象封装
     * @param context
     * @return
     */
    public static Map<String, String> getMoodDraft(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(ConstantsUtil.MOOD_DRAFT, Context.MODE_PRIVATE);
        String content = preferences.getString(ConstantsUtil.MOOD_DRAFT_CONTENT, null);
        String uris = preferences.getString(ConstantsUtil.MOOD_DRAFT_IMGS, null);
        if (StringUtil.isNotNull(content) || StringUtil.isNotNull(uris)) {
            Map<String, String> map = new HashMap<>();
            map.put("content", content);
            map.put("uris", uris);
            return map;
        }
       return null;
    }

    /**
     * 清除缓存的心情草稿信息
     * @param context
     */
    public static void clearMoodDraft(Context context){
        /*SharedPreferences preferences = context.getSharedPreferences(ConstantsUtil.MOOD_DRAFT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();*/
        saveMoodDraft(context, "" , "");
    }
}
