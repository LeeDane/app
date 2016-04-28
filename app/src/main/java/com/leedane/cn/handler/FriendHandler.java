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
 * 我的好友相关处理类
 * Created by LeeDane on 2016/4/21.
 */
public class FriendHandler {


    /**
     * 获取已经跟我成为好友关系的分页列表
     * @param listener
     * @param params
     */
    public static void sendFriendsPaging(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/friend_friendsPaging.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_FRIENDS_PAGING, listener, requestBean);
    }

    /**
     * 获取我发送的好友请求列表
     * @param listener
     * @param params
     */
    public static void sendRequestPaging(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/friend_requestPaging.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_REQUEST_PAGING, listener, requestBean);
    }

    /**
     * 获取等待我同意的好友关系列表
     * @param listener
     * @param params
     */
    public static void sendResponsePaging(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/friend_responsePaging.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_RESPONSE_PAGING, listener, requestBean);
    }
}
