package com.leedane.cn.handler;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 粉丝相关处理类
 * Created by LeeDane on 2016/4/11.
 */
public class FanHandler {


    /**
     * 判断两个用户和登录用户是否是粉丝关注
     * @param listener
     * @param toUserId
     */
    public static void isFan(TaskListener listener, int toUserId){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("toUserId", toUserId);
        requestBean.setParams(params);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setServerMethod("fs/is");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.IS_FAN, listener, requestBean);
    }

    /**
     * 关注某个用户，成为TA的粉丝
     * @param listener
     * @param toUserId
     */
    public static void addAttention(TaskListener listener, int toUserId){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("toUserId", toUserId);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("fs/fan");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_FAN, listener, requestBean);
    }
    /**
     * 获得我的粉丝列表(不能是其他用户，其他用户请调用getToFansRequest())
     * @param listener
     * @param params
     */
    public static void getMyFansRequest(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("fs/myFans");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_MY_FAN, listener, requestBean);
    }

    /**
     * 获得粉丝列表(不是我的，是其他用户，我的粉丝列表请调用getMyFansRequest())
     * @param listener
     * @param params
     */
    public static void getToFansRequest(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("fs/toFans");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_MY_FAN, listener, requestBean);
    }

    /**
     * 获得我的关注用户列表列表(不能是其他用户，其他用户请调用getToAttentionsRequest())
     * @param listener
     * @param params
     */
    public static void getMyAttentionsRequest(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("fs/myAttentions");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_MY_ATTENTION, listener, requestBean);
    }

    /**
     * 获得Ta的关注用户列表列表(不是我的，是其他用户，我的粉丝列表请调用getMyAttentionsRequest())
     * @param listener
     * @param params
     */
    public static void getToAttentionsRequest(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("fs/toAttentions");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_MY_ATTENTION, listener, requestBean);
    }
    /**
     * 取消关注某个用户
     * @param listener
     * @param toUserId
     */
    public static void cancelAttention(TaskListener listener, int toUserId){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("toUserIds", String.valueOf(toUserId));
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("fs/fan");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_DELETE);
        TaskLoader.getInstance().startTaskForResult(TaskType.CANCEL_FAN, listener, requestBean);
    }
}
