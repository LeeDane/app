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
 * 摇一摇相关的处理
 * Created by LeeDane on 2016/12/21.
 */
public class ShakeHandler {

    /**
     * 获取摇一摇用户
     * @param listener
     */
    public static void getShakeUserRequest(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        requestBean.setRequestTimeOut(60000);
        requestBean.setResponseTimeOut(60000);
        requestBean.setServerMethod("leedane/shake/user.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_SHAKE_USER, listener, requestBean);
    }

    /**
     * 获取摇一摇博客
     * @param listener
     */
    public static void getShakeBlogRequest(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        requestBean.setRequestTimeOut(60000);
        requestBean.setResponseTimeOut(60000);
        requestBean.setServerMethod("leedane/shake/blog.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_SHAKE_BLOG, listener, requestBean);
    }

    /**
     * 获取摇一摇心情
     * @param listener
     */
    public static void getShakeMoodRequest(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        requestBean.setRequestTimeOut(60000);
        requestBean.setResponseTimeOut(60000);
        requestBean.setServerMethod("leedane/shake/mood.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_SHAKE_MOOD, listener, requestBean);
    }
}
