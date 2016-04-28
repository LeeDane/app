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
 * 签到相关的处理类
 * Created by LeeDane on 2016/4/19.
 */
public class SignInHandler {

    /**
     * 执行签到操作
     * @param listener
     */
    public static void addSignIn(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/signIn_signIn.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.DO_SIGN_IN, listener, requestBean);
    }

    /**
     * 判断服务器当天是否有签到
     * @param listener
     */
    public static void isSignIn(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/signIn_currentDateIsSignIn.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.IS_SIGN_IN, listener, requestBean);
    }
}
