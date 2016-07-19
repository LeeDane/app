package com.leedane.cn.handler;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;

import java.util.HashMap;

/**
 * 通知消息相关处理类
 * Created by LeeDane on 2016/4/27.
 */
public class NotificationHandler {

    /**
     * 获取通知消息的请求
     * @param listener
     * @param params
     */
    public static void getNotificationsRequest(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setServerMethod("leedane/notification/paging.action");
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_NOTIFICATION, listener, requestBean);
    }

    /**
     * 删除服务器上的通知
     * @param listener
     * @param nid
     */
    public static void delete(TaskListener listener, int nid){
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setServerMethod("leedane/notification/delete.action");
        HashMap<String, Object> params = new HashMap<>();
        params.put("nid", nid);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_NOTIFICATION, listener, requestBean);
    }
}
