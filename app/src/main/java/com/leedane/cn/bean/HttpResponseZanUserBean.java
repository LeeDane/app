package com.leedane.cn.bean;

import java.util.List;

/**
 * http响应获取赞用户的bean
 * Created by LeeDane on 2016/5/21.
 */
public class HttpResponseZanUserBean {
    private boolean isSuccess;
    private List<ZanUserBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<ZanUserBean> getMessage() {
        return message;
    }

    public void setMessage(List<ZanUserBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
