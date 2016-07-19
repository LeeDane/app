package com.leedane.cn.bean.search;

import java.util.List;

/**
 * http响应获取搜索博客的bean
 * Created by LeeDane on 2016/5/22.
 */
public class HttpResponseSearchBlogBean {
    private boolean isSuccess;
    private List<SearchBlogBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<SearchBlogBean> getMessage() {
        return message;
    }

    public void setMessage(List<SearchBlogBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
