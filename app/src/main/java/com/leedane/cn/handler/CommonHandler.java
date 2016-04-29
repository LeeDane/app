package com.leedane.cn.handler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.leedane.cn.activity.DetailActivity;
import com.leedane.cn.activity.MoodDetailActivity;
import com.leedane.cn.activity.NotificationActivity;
import com.leedane.cn.activity.PersonalActivity;
import com.leedane.cn.activity.UserInfoActivity;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.Base64Util;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.Map;

/**
 * 相同操作相关的处理类
 * Created by LeeDane on 2016/4/5.
 */
public class CommonHandler {

    /**
     * 触发个人中心的activity
     * @param context
     * @param toUserId
     */
    public static void startPersonalActivity(Context context, int toUserId){
        Intent it = new Intent(context, PersonalActivity.class);
        it.putExtra("userId", toUserId);
        context.startActivity(it);
    }

    /**
     * 触发我的消息activity
     * @param context
     */
    public static void startMyMessageActivity(Context context){
        Intent it = new Intent(context, NotificationActivity.class);
        context.startActivity(it);
    }

    /**
     * 触发个人信息修改的activity
     * @param context
     */
    public static void startUserInfoActivity(Context context){
        Intent it = new Intent(context, UserInfoActivity.class);
        context.startActivity(it);
    }

    /**
     * 触发详情的activity
     * @param context
     * @param tableName
     * @param tableId
     * @param params
     */
    public static void startDetailActivity(Context context, String tableName, int tableId, Map<String, Object> params){
        Class clazz = null;
        if(tableName.equalsIgnoreCase("t_mood")){
            clazz = MoodDetailActivity.class;
        }else if(tableName.equalsIgnoreCase("t_blog")){
            clazz = DetailActivity.class;
        }else{
            ToastUtil.failure(context, "未知的表类型，无法触发详情的点击事件");
            return;
        }
        Intent it = new Intent(context, clazz);
        it.putExtra("tableId", tableId);
        if(params != null && !params.isEmpty()){
            for(Map.Entry<String, Object> entry: params.entrySet()){
                if(entry.getValue() instanceof Boolean){
                    it.putExtra(entry.getKey(), StringUtil.changeObjectToBoolean(entry.getValue()));
                }else if(entry.getValue() instanceof Integer){
                    it.putExtra(entry.getKey(), StringUtil.changeObjectToInt(entry.getValue()));
                }else{
                    it.putExtra(entry.getKey(), StringUtil.changeNotNull(entry.getValue()));
                }
            }
        }
        context.startActivity(it);
    }

    /**
     * 调用系统浏览器打开网络链接
     * @param context
     * @param networkLink
     */
    public static void openLink(Context context, String networkLink){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(networkLink));
        context.startActivity(intent);
    }

    /**
     * 对生成二维码的字符串进行二次编码
     * @param sourceStr
     * @return
     */
    public static String encodeQrCodeStr(String sourceStr){
        if(StringUtil.isNotNull(sourceStr)){
            sourceStr = "leedane:"+ new String(Base64Util.encode(sourceStr.getBytes()));
        }
        return sourceStr;
    }
}
