package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取登录历史记录列表的bean
 * Created by LeeDane on 2016/5/5.
 */
public class HttpResponseLoginHistoryBean {
    private boolean isSuccess;
    private List<LoginHistoryBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<LoginHistoryBean> getMessage() {
        return message;
    }

    public void setMessage(List<LoginHistoryBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
