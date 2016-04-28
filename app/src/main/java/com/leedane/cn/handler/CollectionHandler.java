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
 * 收藏相关的处理类
 * Created by LeeDane on 2016/4/6.
 */
public class CollectionHandler {

    /**
     * 添加收藏
     * @param listener
     * @param params
     */
    public static void sendCollection(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/collection_add.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_COLLECTION, listener, requestBean);
    }

    /**
     * 获取赞的请求
     * @param listener
     * @param params
     */
    public static void getCollectionsRequest(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod("POST");
        requestBean.setServerMethod("leedane/collection_paging.action");

        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_COLLECTION, listener, requestBean);
    }

    /**
     * 删除收藏
     * @param listener
     * @param collectionId
     * @param createUserId
     */
    public static void deleteCollection(TaskListener listener, int collectionId, int createUserId){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("cid",collectionId);
        params.put("create_user_id", createUserId);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/collection_delete.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_COLLECTION, listener, requestBean);
    }
}
