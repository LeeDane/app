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
     * 获取当前用户的最新头像的路径
     * @param listener
     */
    public static void getHeadPath(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("us/head/path");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_HEAD_PATH, listener, requestBean);
    }

    /**
     * 个人中心获取用户的基本信息
     * @param listener
     * @param params
     */
    public static void asnyLoadUserInfo(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("us/searchByIdOrAccount");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_USER_INFO, listener, requestBean);
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
        requestBean.setServerMethod("us/user/base");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_PUT);
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
        requestBean.setServerMethod("us/user/pwd");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_PUT);
        TaskLoader.getInstance().startTaskForResult(TaskType.UPDATE_LOGIN_PSW, listener, requestBean);
    }

    /**
     * 手机号码登陆
     * @param listener
     * @param params
     */
    public static void loginByPhone(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("us/phone/login");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.DO_LOGIN_PHONE, listener, requestBean);
    }

    /**
     * 获取登登录码
     * @param listener
     * @param params
     */
    public static void getPhoneLoginCode(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("us/phone/login/code");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.DO_GET_LOGIN_CODE, listener, requestBean);
    }

    /**
     * 获取登登录码
     * @param listener
     * @param params
     */
    public static void registerByPhoneNoValidate(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("us/phone/register/noValidate");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.REGISTER_DO, listener, requestBean);
    }
}
