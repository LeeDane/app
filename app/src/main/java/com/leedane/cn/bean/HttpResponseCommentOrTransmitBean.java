package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取评论的bean
 * Created by LeeDane on 2016/3/3.
 */
public class HttpResponseCommentOrTransmitBean {
    private boolean isSuccess;
    private List<CommentOrTransmitBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<CommentOrTransmitBean> getMessage() {
        return message;
    }

    public void setMessage(List<CommentOrTransmitBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
