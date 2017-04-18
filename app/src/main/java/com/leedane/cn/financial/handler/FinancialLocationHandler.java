package com.leedane.cn.financial.handler;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.service.CalculateFinancialService;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记账位置相关的处理类
 * Created by LeeDane on 2016/11/22.
 */
public class FinancialLocationHandler {

    /**
     * 添加记账位置记录
     * @param listener
     * @param params
     */
    public static void add(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("fn/location");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_FINANCIAL_LOCATION, listener, requestBean);
    }

    /**
     * 更新记账位置记录
     * @param listener
     * @param params
     */
    public static void update(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("fn/location");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_PUT);
        TaskLoader.getInstance().startTaskForResult(TaskType.UPDATE_FINANCIAL_LOCATION, listener, requestBean);
    }

    /**
     * 删除记账位置记录
     * @param listener
     * @param flid
     */
    public static void delete(TaskListener listener, int flid){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("flid",flid);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("fn/location");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_DELETE);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_FINANCIAL_LOCATION, listener, requestBean);
    }

    /**
     * 获取记账位置列表的请求
     * @param listener
     * @param params
     */
    public static void paging(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("fn/locations");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_FINANCIAL_LOCATION, listener, requestBean);
    }

}
