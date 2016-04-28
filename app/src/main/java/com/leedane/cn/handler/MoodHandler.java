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
 * 心情相关处理类
 * Created by LeeDane on 2016/3/27.
 */
public class MoodHandler {


    /**
     * 发送心情
     * @param listener
     * @param params
     */
    public static void sendMood(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setServerMethod("leedane/mood_getPagingMood.action");
        /*params.put("toUserId", mPreUid);
        params.put("pageSize", 10);
        params.put("method", mPreLoadMethod);*/
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        //requestBean.setRequestTimeOut(50000);
        //requestBean.setResponseTimeOut(50000);
        TaskLoader.getInstance().startTaskForResult(TaskType.PERSONAL_LOADMOODS, listener, requestBean);
    }

    /**
     * 删除心情
     * @param listener
     * @param params
     */
    public static void delete(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/mood_delete.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_MOOD, listener, requestBean);
    }
}
