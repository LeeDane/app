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
public class NotificationPagingHandler {

    private String mType;
    private int mPageSize;
    private TaskListener mListener;
    public NotificationPagingHandler(TaskListener listener, String type, int pageSize){
        this.mType = type;
        this.mPageSize = pageSize;
        this.mListener = listener;
    }

    /**
     * 获取通知消息的请求
     * @param current
     */
    public void getNotificationsRequest(int current){
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setServerMethod("nf/notifications/paging");
        HashMap params = new HashMap();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        params.put("type", mType);
        params.put("page_size", mPageSize);
        params.put("total", 0);// 暂时用不上
        params.put("current", current);
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_NOTIFICATION, mListener, requestBean);
    }
}
