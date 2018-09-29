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
 * 图库相关处理类
 * Created by LeeDane on 2016/7/15.
 */
public class GalleryHandler {

    /**
     * 添加图库
     * @param listener
     * @param params
     */
    public static void add(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setParams(params);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setServerMethod("gl/photo");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_GALLERY, listener, requestBean);
    }

    /**
     * 获取图库列表
     * @param listener
     * @param params
     */
    public static void getPagingRequest(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setParams(params);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setServerMethod("gl/photos");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.DO_GALLERY, listener, requestBean);
    }

    /**
     * 删除
     * @param listener
     * @param gid
     */
    public static void delete(TaskListener listener, int gid){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        requestBean.setParams(params);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setServerMethod("gl/photo/"+ gid);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_DELETE);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_GALLERY, listener, requestBean);
    }
}
