package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应的基本的bean
 * Created by LeeDane on 2015/11/17.
 */
public class HttpResponseCommonBean {
    private boolean isSuccess;
    private String message;
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
