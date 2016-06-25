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
 * 邮件相关的处理类
 * Created by LeeDane on 2016/6/25.
 */
public class EmailHandler {
    /**
     * 发送邮件
     * @param listener
     * @param toUserId  接收邮件的用户的Id 必须
     * @param content 邮件的内容，必须
     * @param object 邮件的标题，必须
     */
    public static void sendEmail(TaskListener listener, int toUserId, String content, String object){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("to_user_id", toUserId);
        params.put("content", content);
        params.put("object", object);
        requestBean.setParams(params);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setServerMethod("leedane/tool_sendEmail.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.SEND_EMAIL, listener, requestBean);
    }
}
