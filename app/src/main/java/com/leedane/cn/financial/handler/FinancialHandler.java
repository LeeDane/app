package com.leedane.cn.financial.handler;

import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记账相关的处理类
 * Created by LeeDane on 2016/7/26.
 */
public class FinancialHandler {

    /**
     * 添加记账记录
     * @param listener
     * @param data
     */
    public static void save(TaskListener listener, Map<String, Object> data){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        params.put("data", data);
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/financial/save.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_FINANCIAL, listener, requestBean);
    }

    /**
     * 获取全部的记账记录请求
     * @param listener
     */
    public static void getAll(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod("POST");
        requestBean.setServerMethod("leedane/financial/getAll.action");

        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_ALL_FINANCIAL, listener, requestBean);
    }

    /**
     * 删除记账记录
     * @param listener
     */
    public static void delete(TaskListener listener, int fid){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("fid",fid);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/financial/delete.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_FINANCIAL, listener, requestBean);
    }

    /**
     * 批量同步记账记录
     * @param listener
     * @param financialBeans
     */
    public static void synchronous(TaskListener listener, List<Map<String, Object>> financialBeans){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("datas", financialBeans);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/financial/synchronous.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.SYNCHRONOUS_FINANCIAL, listener, requestBean);
    }

    /**
     * 获取总数
     * @param financialList
     * @param model model为0表示总数，为1表示获取收入，为2表示获取支出总数
     * @return
     */
    public static float getTotalData(FinancialList financialList, int model){
        float total = 0.0f;
        if(financialList == null || financialList.getFinancialBeans() == null || financialList.getFinancialBeans().size() == 0)
            return total;

        if(model == IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME || model == IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND)
            for(FinancialBean financialBean: financialList.getFinancialBeans()){
                if(financialBean.getModel() == model)
                    total += financialBean.getMoney();
            }
        else
            for(FinancialBean financialBean: financialList.getFinancialBeans())
                    total += financialBean.getMoney();
        return total;
    }

    /**
     * 后台调用去计算记账
     * @param context
     */
    public static void calculateFinancialData(Context context) {
        Intent it_service = new Intent();
        it_service.setClass(context, CalculateFinancialService.class);
        it_service.setAction("com.leedane.cn.CalculateFinancialService");
        context.startService(it_service);
    }
}
