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
 * 关注相关的处理类
 * Created by LeeDane on 2016/4/6.
 */
public class AttentionHandler {

    /**
     * 添加关注
     * @param listener
     * @param params
     */
    public static void sendAttention(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("at/attention");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_ATTENTION, listener, requestBean);
    }

    /**
     * 获取赞的请求
     * @param listener
     * @param params
     */
    public static void getAttentionsRequest(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        requestBean.setServerMethod("at/attentions");

        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_ATTENTION, listener, requestBean);
    }

    /**
     * 删除关注
     * @param listener
     * @param attentionId
     * @param createUserId
     */
    public static void deleteAttention(TaskListener listener, int attentionId, int createUserId){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("aid",attentionId);
        params.put("create_user_id", createUserId);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("at/attention");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_DELETE);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_ATTENTION, listener, requestBean);
    }
}
