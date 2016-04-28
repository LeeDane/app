package com.leedane.cn.bean;

import java.util.List;

import com.leedane.cn.bean.BlogBean;

/**
 * http响应获取博客的bean
 * Created by LeeDane on 2015/10/7.
 */
public class HttpResponseBlogBean {
    private boolean isSuccess;
    private List<BlogBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<com.leedane.cn.bean.BlogBean> getMessage() {
        return message;
    }

    public void setMessage(List<com.leedane.cn.bean.BlogBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
