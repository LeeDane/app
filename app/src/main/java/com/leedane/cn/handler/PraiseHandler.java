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
 * 点赞相关的处理类
 * Created by LeeDane on 2016/3/27.
 */
public class PraiseHandler {

    /**
     * 添加赞
     * @param listener
     * @param params
     */
    public static void sendZan(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        if(!params.containsKey("content")){
            params.put("content", "赞了这条信息");
        }
        requestBean.setParams(params);
        requestBean.setServerMethod("lk/zan");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_ZAN, listener, requestBean);
    }

    /**
     * 获取赞的请求
     * @param listener
     * @param params
     */
    public static void getZansRequest(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        requestBean.setServerMethod("lk/zans");
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_ZAN, listener, requestBean);
    }

    /**
     * 获取全部点赞用户的请求
     * @param listener
     * @param params
     */
    public static void getZanUsersRequest(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        requestBean.setServerMethod("zn/allZanUsers");
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_ZAN_USER, listener, requestBean);
    }

    /**
     * 删除收藏
     * @param listener
     * @param zanId
     * @param createUserId
     */
    public static void deleteZan(TaskListener listener, int zanId, int createUserId){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("zid",zanId);
        params.put("create_user_id", createUserId);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("zn/delete");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_DELETE);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_ZAN, listener, requestBean);
    }
}
