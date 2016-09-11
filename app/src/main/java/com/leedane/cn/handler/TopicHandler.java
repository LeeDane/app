package com.leedane.cn.handler;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;

import java.util.HashMap;

/**
 * 话题相关处理类
 * Created by LeeDane on 2016/9/11.
 */
public class TopicHandler {

    /**
     * 获取心情列表
     * @param listener
     * @param params
     */
    public static void paging(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setServerMethod("leedane/mood/topic.action");
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_TOPIC, listener, requestBean);
    }
}
