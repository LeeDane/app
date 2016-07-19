package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取粉丝的bean
 * Created by LeeDane on 2016/4/11.
 */
public class HttpResponseFanBean {
    private boolean isSuccess;
    private List<FanBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<FanBean> getMessage() {
        return message;
    }

    public void setMessage(List<FanBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
