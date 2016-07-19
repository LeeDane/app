package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取图库的bean
 * Created by LeeDane on 2016/1/15.
 */
public class HttpResponseGalleryBean {
    private boolean isSuccess;
    private List<GalleryBean> message;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<GalleryBean> getMessage() {
        return message;
    }

    public void setMessage(List<GalleryBean> message) {
        this.message = message;
    }
}
