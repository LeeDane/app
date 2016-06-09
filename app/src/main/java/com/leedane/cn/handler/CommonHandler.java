package com.leedane.cn.handler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leedane.cn.activity.ChatDetailActivity;
import com.leedane.cn.activity.DetailActivity;
import com.leedane.cn.activity.ImageDetailActivity;
import com.leedane.cn.activity.MoodDetailActivity;
import com.leedane.cn.activity.MySettingActivity;
import com.leedane.cn.activity.NotificationActivity;
import com.leedane.cn.activity.PersonalActivity;
import com.leedane.cn.activity.UpdateChatBGActivity;
import com.leedane.cn.activity.UpdateUserHeaderActivity;
import com.leedane.cn.activity.UserBaseActivity;
import com.leedane.cn.activity.UserInfoActivity;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.service.LoadUserFriendService;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.Base64Util;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 相同操作相关的处理类
 * Created by LeeDane on 2016/4/5.
 */
public class CommonHandler {

    /**
     * 触发聊天详情的activity
     * @param context
     * @param toUserId
     * @param toUserAccount
     */
    public static void startChatDetailActivity(Context context, int toUserId, String toUserAccount){
        Intent it = new Intent(context, ChatDetailActivity.class);
        it.putExtra("toUserId", toUserId);
        it.putExtra("toUserAccount", toUserAccount);
        context.startActivity(it);
    }

    /**
     * 触发图片列表详情的activity
     * @param context
     * @param list
     */
    public static void startImageDetailActivity(Context context, List<ImageDetailBean> list){
        Intent it_detail = new Intent(context, ImageDetailActivity.class);
        Type type = new TypeToken<ArrayList<ImageDetailBean>>(){}.getType();
        String json = new Gson().toJson(list,type);
        it_detail.putExtra("ImageDetailBeans", json);
        context.startActivity(it_detail);
    }

    /**
     * 触发图片列表详情的activity
     * @param context
     * @param imgs
     */
    public static void startImageDetailActivity(Context context, String imgs){
        List<ImageDetailBean> list = new ArrayList<>();
        String[] showImages = imgs.split(",");
        ImageDetailBean imageDetailBean;
        for(String img: showImages){
            imageDetailBean = new ImageDetailBean();
            imageDetailBean.setPath(img);
            list.add(imageDetailBean);
        }
        startImageDetailActivity(context, list);
    }

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
     * 触发基本信息activity
     * @param context
     * @param toUserId
     */
    public static void startUserBaseActivity(Context context, int toUserId){
        Intent it = new Intent(context, UserBaseActivity.class);
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
     * 触发我的设置activity
     * @param context
     */
    public static void startMySettingActivity(Context context){
        Intent it = new Intent(context, MySettingActivity.class);
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
     * 触发修改个人头像的activity
     * @param context
     */
    public static void startUpdateHeaderActivity(Context context){
        Intent it = new Intent(context, UpdateUserHeaderActivity.class);
        context.startActivity(it);
    }

    /**
     * 触发修改聊天背景的activity
     * @param context
     */
    public static void startUpdateChatBGActivity(Context context){
        Intent it = new Intent(context, UpdateChatBGActivity.class);
        context.startActivity(it);
    }

    /**
     * 后台获取该用户的好友信息
     * @param context
     * @param isShowErrorNotification 控制是否展示获取失败后的通知
     */
    public static void startUserFreidnsService(Context context, boolean isShowErrorNotification) {
        Intent it_service = new Intent();
        it_service.setClass(context, LoadUserFriendService.class);
        it_service.setAction("com.leedane.cn.LoadUserFriendService");
        it_service.putExtra("toUserId", BaseApplication.getLoginUserId());
        it_service.putExtra("isShowErrorNotification", isShowErrorNotification);
        context.startService(it_service);
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

    /**
     * 获取翻译的请求
     * @param listener
     * @param content
     */
    public static void getFanYiRequest(TaskListener listener, String content){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("content", content);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        requestBean.setServerMethod("leedane/tool_fanyi.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.FANYI, listener, requestBean);
    }
}
