package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取收藏列表的bean
 * Created by LeeDane on 2016/4/6.
 */
public class HttpResponseCollectionBean {
    private boolean isSuccess;
    private List<CollectionBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<CollectionBean> getMessage() {
        return message;
    }

    public void setMessage(List<CollectionBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
