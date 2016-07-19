package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应的文件基本的bean
 * Created by LeeDane on 2016/6/8.
 */
public class HttpResponseFileBean {
    private boolean isSuccess;
    private List<FileBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<FileBean> getMessage() {
        return message;
    }

    public void setMessage(List<FileBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
