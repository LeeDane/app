package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取我的好友列表的bean
 * Created by LeeDane on 2016/4/30.
 */
public class HttpResponseMyFriendsBean {
    private boolean isSuccess;
    private List<MyFriendsBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<MyFriendsBean> getMessage() {
        return message;
    }

    public void setMessage(List<MyFriendsBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
