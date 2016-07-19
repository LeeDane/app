package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取心情图片列表的bean
 * Created by LeeDane on 2016/3/17.
 */
public class HttpResponseMoodImagesBean {
    private boolean isSuccess;
    private List<MoodImagesBean> message;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<MoodImagesBean> getMessage() {
        return message;
    }

    public void setMessage(List<MoodImagesBean> message) {
        this.message = message;
    }
}
