package com.leedane.cn.bean.search;

import java.util.List;

/**
 * http响应获取搜索心情的bean
 * Created by LeeDane on 2016/5/22.
 */
public class HttpResponseSearchMoodBean {
    private boolean isSuccess;
    private List<SearchMoodBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<SearchMoodBean> getMessage() {
        return message;
    }

    public void setMessage(List<SearchMoodBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
