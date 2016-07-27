package com.leedane.cn.financial.handler;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;

import java.util.HashMap;
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
}
