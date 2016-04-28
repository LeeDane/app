package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取朋友圈的bean
 * Created by LeeDane on 2016/4/15.
 */
public class HttpResponseCircleOfFriendBean {
    private boolean isSuccess;
    private List<TimeLineBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<TimeLineBean> getMessage() {
        return message;
    }

    public void setMessage(List<TimeLineBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
