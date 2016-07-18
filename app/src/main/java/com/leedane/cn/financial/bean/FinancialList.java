package com.leedane.cn.financial.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 记账实体列表
 * Created by LeeDane on 2016/7/13.
 */
public class FinancialList implements Serializable{
    private List<FinancialBean> financialBeans;

    //1表示今日， 2表示昨日， 3表示本周，4表示本月，5表示本年
    private int model;

    public List<FinancialBean> getFinancialBeans() {
        return financialBeans;
    }

    public void setFinancialBeans(List<FinancialBean> financialBeans) {
        this.financialBeans = financialBeans;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }
}
