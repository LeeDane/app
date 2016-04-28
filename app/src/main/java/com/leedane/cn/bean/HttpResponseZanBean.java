package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取赞的bean
 * Created by LeeDane on 2016/4/5.
 */
public class HttpResponseZanBean {
    private boolean isSuccess;
    private List<ZanBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<ZanBean> getMessage() {
        return message;
    }

    public void setMessage(List<ZanBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
