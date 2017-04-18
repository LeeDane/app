package com.leedane.cn.handler;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 举报相关处理类
 * Created by LeeDane on 2016/7/15.
 */
public class ReportHandler {
    /**
     * 获取图库列表
     * @param listener
     * @param params
     */
    public static void add(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setParams(params);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setServerMethod("rp/report");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_REPORT, listener, requestBean);
    }
}
