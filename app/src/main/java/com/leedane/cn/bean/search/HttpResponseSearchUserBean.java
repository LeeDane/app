package com.leedane.cn.bean.search;

import java.util.List;

/**
 * http响应获取搜索用户的bean
 * Created by LeeDane on 2016/5/22.
 */
public class HttpResponseSearchUserBean {
    private boolean isSuccess;
    private List<SearchUserBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<SearchUserBean> getMessage() {
        return message;
    }

    public void setMessage(List<SearchUserBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
