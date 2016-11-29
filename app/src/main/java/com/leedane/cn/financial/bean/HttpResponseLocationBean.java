package com.leedane.cn.financial.bean;

import com.leedane.cn.bean.AttentionBean;

import java.util.List;

/**
 * http响应获取记账位置列表的bean
 * Created by LeeDane on 2016/11/22.
 */
public class HttpResponseLocationBean {
    private boolean isSuccess;
    private List<FinancialLocationBean> message;
    private int code;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<FinancialLocationBean> getMessage() {
        return message;
    }

    public void setMessage(List<FinancialLocationBean> message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
