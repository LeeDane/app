package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取我的好友的bean
 * Created by LeeDane on 2016/4/19.
 */
public class HttpResponseFriendBean {
    private boolean isSuccess;
    private List<FriendBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<FriendBean> getMessage() {
        return message;
    }

    public void setMessage(List<FriendBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
