package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取积分历史记录列表的bean
 * Created by LeeDane on 2016/5/5.
 */
public class HttpResponseScoreBean {
    private boolean isSuccess;
    private List<ScoreBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<ScoreBean> getMessage() {
        return message;
    }

    public void setMessage(List<ScoreBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
