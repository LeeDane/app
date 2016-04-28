package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取通知消息的bean
 * Created by LeeDane on 2016/4/27.
 */
public class HttpResponseNotificationBean {
    private boolean isSuccess;
    private List<NotificationBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<NotificationBean> getMessage() {
        return message;
    }

    public void setMessage(List<NotificationBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
