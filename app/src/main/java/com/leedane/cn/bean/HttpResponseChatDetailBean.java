package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取聊天消息的bean
 * Created by LeeDane on 2016/5/5.
 */
public class HttpResponseChatDetailBean {
    private boolean isSuccess;
    private List<ChatDetailBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<ChatDetailBean> getMessage() {
        return message;
    }

    public void setMessage(List<ChatDetailBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
