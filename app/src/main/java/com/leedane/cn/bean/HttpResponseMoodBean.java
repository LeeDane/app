package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取心情的bean
 * Created by LeeDane on 2015/12/8.
 */
public class HttpResponseMoodBean {
    private boolean isSuccess;
    private List<MoodBean> message;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<MoodBean> getMessage() {
        return message;
    }

    public void setMessage(List<MoodBean> message) {
        this.message = message;
    }
}
