package com.leedane.cn.handler;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;

import java.util.Map;

/**
 * 积分历史相关的处理类
 * Created by LeeDane on 2016/4/6.
 */
public class ScoreHandler {
    /**
     * 获取积分历史的请求
     * @param listener
     * @param params
     */
    public static void getScoresRequest(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod("POST");
        requestBean.setServerMethod("leedane/score/paging.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_SCORE, listener, requestBean);
    }
}
