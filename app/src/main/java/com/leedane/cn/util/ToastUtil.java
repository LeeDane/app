package com.leedane.cn.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leedane.cn.application.BaseApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * toast的工具类
 * Created by LeeDane on 2016/3/29.
 */
public class ToastUtil {

    public static final String SUCCESS_TIP = "操作成功";
    public static final String FAILURE_TIP = "操作失败";
    /**
     * 提示成功((默认1秒消失))
     * @param context
     */
    public static void success(Context context){
        show(context, SUCCESS_TIP, Toast.LENGTH_SHORT);
    }
    /**
     * 提示成功
     * @param context
     * @param times
     */
    public static void success(Context context, int times){
        show(context, SUCCESS_TIP, times);
    }

    /**
     * 提示成功((默认1秒消失))
     * @param context
     * @param content
     */
    public static void success(Context context, String content){
        show(context, content, Toast.LENGTH_SHORT);
    }

    /**
     * 提示成功
     * @param context
     * @param content
     * @param times
     */
    public static void success(Context context, String content, int times){
        show(context, content, times);
    }
    /**
     * 提示成功
     * @param context
     * @param jsonObject
     * @param times
     */
    public static void success(Context context, JSONObject jsonObject, int times){
        try{
            show(context, jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") && jsonObject.has("message") ? jsonObject.getString("message") : SUCCESS_TIP, times);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 提示成功(默认1秒消失)
     * @param context
     * @param jsonObject
     */
    public static void success(Context context, JSONObject jsonObject){
        try{
            show(context, jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") && jsonObject.has("message")? jsonObject.getString("message"):SUCCESS_TIP, Toast.LENGTH_SHORT);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 提示失败((默认3秒消失))
     * @param context
     */
    public static void failure(Context context){
        show(context, FAILURE_TIP, Toast.LENGTH_LONG);
    }

    /**
     * 提示失败
     * @param context
     * @param times
     */
    public static void failure(Context context, int times){
        show(context, FAILURE_TIP, times);
    }

    /**
     * 提示失败((默认3秒消失))
     * @param context
     * @param Content
     */
    public static void failure(Context context, String Content){
        show(context, Content, Toast.LENGTH_LONG);
    }

    /**
     * 提示失败
     * @param context
     * @param content
     * @param times
     */
    public static void failure(Context context, String content, int times){
        show(context, content, times);
    }
    /**
     * 提示失败
     * @param context
     * @param jsonObject
     * @param times
     */
    public static void failure(Context context, JSONObject jsonObject, int times){
        try{
            show(context, jsonObject.has("isSuccess") && !jsonObject.getBoolean("isSuccess") && jsonObject.has("message") ? jsonObject.getString("message") : FAILURE_TIP, times);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 提示失败(默认3秒消失)
     * @param context
     * @param jsonObject
     */
    public static void failure(Context context, JSONObject jsonObject){
        try{
            show(context, jsonObject.has("isSuccess") && !jsonObject.getBoolean("isSuccess") && jsonObject.has("message") ? jsonObject.getString("message") : FAILURE_TIP, Toast.LENGTH_LONG);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 系统默认的Toast
     * @param context
     * @param content
     * @param times
     */
    private static void show(Context context, String content, int times){
        if(context == null)
            context = BaseApplication.newInstance();
        Toast.makeText(context, content, times).show();
    }

    /**
     * 自定义位置的Toast
     * @param context
     * @param content
     * @param times
     * @param gravity
     * @param offX
     * @param offY
     */
    private static void show(Context context, String content, int times, int gravity, int offX, int offY){
        if(context == null)
            context = BaseApplication.newInstance();

        Toast toast = Toast.makeText(context, content, times);
        toast.setGravity(gravity, offX, offY);
        toast.show();
    }

    /**
     * 带图片的Toast
     * @param context
     * @param content
     * @param times
     * @param imageView
     */
    private static void show(Context context, String content, int times, ImageView imageView){
        if(context == null)
            context = BaseApplication.newInstance();

        Toast toast = Toast.makeText(context, content, times);
        LinearLayout linearLayout  = (LinearLayout)toast.getView();
        linearLayout.addView(imageView, 0);
        toast.show();
    }
}
