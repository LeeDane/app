package com.leedane.cn.handler;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.MD5Util;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关的处理类
 * Created by LeeDane on 2016/4/19.
 */
public class UserHandler {

    /**
     * 更新用户头像
     * @param listener
     * @param params
     */
    public static void updateHander(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/user_paging.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.UPDATE_HANDER, listener, requestBean);
    }

    /**
     * 获取当前用户的最新头像的路径
     * @param listener
     */
    public static void getHeadPath(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/user_getHeadPath.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_HEAD_PATH, listener, requestBean);
    }

    /**
     * 更新用户的基本信息
     * @param listener
     * @param params
     */
    public static void updateUserBase(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/user_updateUserBase.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.UPDATE_USER_BASE, listener, requestBean);
    }

    /**
     * 更新登录密码
     * @param listener
     * @param password
     * @param newPassword
     */
    public static void updateLoginPsw(TaskListener listener, String password, String newPassword){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("password", MD5Util.compute(password));
        params.put("new_password", MD5Util.compute(newPassword));
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/user_updatePassword.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.UPDATE_LOGIN_PSW, listener, requestBean);
    }
}
