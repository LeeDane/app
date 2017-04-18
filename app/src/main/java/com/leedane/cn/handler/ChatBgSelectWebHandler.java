package com.leedane.cn.handler;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 聊天背景的选择图片相关的处理类
 * Created by LeeDane on 2016/6/10.
 */
public class ChatBgSelectWebHandler {

    /**
     * 发布聊天背景
     * @param listener
     * @param params
     */
    public static void publishChatBg(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("ctb/bg");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.PUBLISH_CHAT_BG, listener, requestBean);
    }

    /**
     * 获取聊天背景的选择图片的请求
     * @param listener
     * @param params
     */
    public static void getChatBgSelectWebsRequest(TaskListener listener, Map<String, Object> params) {
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("ctb/bgs");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_CHAT_BG_SELECT_WEB, listener, requestBean);
    }

    /**
     * 校验聊天背景是否可以下载
     * @param listener
     * @param cid
     */
    public static void verifyChatBg(TaskListener listener, int cid){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("cid", cid);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("ctb/bg/verify");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.VERIFY_CHAT_BG, listener, requestBean);
    }
}
