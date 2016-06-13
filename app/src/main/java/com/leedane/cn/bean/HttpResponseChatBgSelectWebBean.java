package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取聊天背景图片的bean
 * Created by LeeDane on 2016/6/10.
 */
public class HttpResponseChatBgSelectWebBean {
    private boolean isSuccess;
    private List<ChatBgSelectWebBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<ChatBgSelectWebBean> getMessage() {
        return message;
    }

    public void setMessage(List<ChatBgSelectWebBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
