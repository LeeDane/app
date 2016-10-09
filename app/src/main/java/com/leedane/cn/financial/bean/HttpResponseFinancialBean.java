package com.leedane.cn.financial.bean;

import java.util.List;

/**
 * http响应获取记账列表的bean
 * Created by LeeDane on 2016/9/19.
 */
public class HttpResponseFinancialBean {
    private boolean isSuccess;
    private List<FinancialBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<FinancialBean> getMessage() {
        return message;
    }

    public void setMessage(List<FinancialBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
