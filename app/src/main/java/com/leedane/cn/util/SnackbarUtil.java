package com.leedane.cn.util;

import android.support.design.widget.Snackbar;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Snackbar的工具类
 * Created by LeeDane on 2016/4/29.
 */
public class SnackbarUtil {

    public static final String SUCCESS_TIP = "操作成功";
    public static final String FAILURE_TIP = "操作失败";
    /**
     * 提示成功((默认1秒消失))
     * @param view
     */
    public static void success(View view){
        show(view, SUCCESS_TIP, Snackbar.LENGTH_LONG);
    }
    /**
     * 提示成功
     * @param view
     * @param times
     */
    public static void success(View view, int times){
        show(view, SUCCESS_TIP, times);
    }

    /**
     * 提示成功((默认1秒消失))
     * @param view
     * @param content
     */
    public static void success(View view, String content){
        show(view, content, Snackbar.LENGTH_SHORT);
    }

    /**
     * 提示成功
     * @param view
     * @param content
     * @param times
     */
    public static void success(View view, String content, int times){
        show(view, content, times);
    }
    /**
     * 提示成功
     * @param view
     * @param jsonObject
     * @param times
     */
    public static void success(View view, JSONObject jsonObject, int times){
        try{
            show(view, jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") && jsonObject.has("message") ? jsonObject.getString("message") : SUCCESS_TIP, times);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 提示成功(默认1秒消失)
     * @param view
     * @param jsonObject
     */
    public static void success(View view, JSONObject jsonObject){
        try{
            show(view, jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") && jsonObject.has("message")? jsonObject.getString("message"):SUCCESS_TIP, Snackbar.LENGTH_SHORT);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 提示失败((默认3秒消失))
     * @param view
     */
    public static void failure(View view){
        show(view, FAILURE_TIP, Snackbar.LENGTH_LONG);
    }

    /**
     * 提示失败
     * @param view
     * @param times
     */
    public static void failure(View view, int times){
        show(view, FAILURE_TIP, times);
    }

    /**
     * 提示失败((默认3秒消失))
     * @param view
     * @param Content
     */
    public static void failure(View view, String Content){
        show(view, Content, Snackbar.LENGTH_LONG);
    }

    /**
     * 提示失败
     * @param view
     * @param content
     * @param times
     */
    public static void failure(View view, String content, int times){
        show(view, content, times);
    }
    /**
     * 提示失败
     * @param view
     * @param jsonObject
     * @param times
     */
    public static void failure(View view, JSONObject jsonObject, int times){
        try{
            show(view, jsonObject.has("isSuccess") && !jsonObject.getBoolean("isSuccess") && jsonObject.has("message") ? jsonObject.getString("message") : FAILURE_TIP, times);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 提示失败(默认3秒消失)
     * @param view
     * @param jsonObject
     */
    public static void failure(View view, JSONObject jsonObject){
        try{
            show(view, jsonObject.has("isSuccess") && !jsonObject.getBoolean("isSuccess") && jsonObject.has("message") ? jsonObject.getString("message") : FAILURE_TIP, Snackbar.LENGTH_LONG);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 系统默认的Snackbar
     * @param view
     * @param content
     * @param times
     */
    private static void show(View view, String content, int times){
        Snackbar.make(view, content, times).setAction("Action", null).show();
    }
}
