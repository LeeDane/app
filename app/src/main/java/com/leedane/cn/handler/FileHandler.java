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
 * 文件相关的处理类
 * Created by LeeDane on 2016/6/8.
 */
public class FileHandler {

    /**
     * 获取文件的请求
     * @param listener
     * @param params
     */
    public static void getFilesRequest(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("fp/paths");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_FILE, listener, requestBean);
    }

    /**
     * 删除文件
     * @param listener
     */
    public static void deleteFile(TaskListener listener, int id){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("fid",id);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        //requestBean.setServerMethod("fp/portFile");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_DELETE);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_FILE, listener, requestBean);
    }

    /**
     * 合并文件
     * @param listener
     * @param params
     */
    public static void mergePortFile(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setResponseTimeOut(60000);
        requestBean.setRequestTimeOut(60000);
        requestBean.setServerMethod("fp/mergePortFile");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.MERGE_PORT_FILE, listener, requestBean);
    }

    /**
     * 删除断点文件
     * @param listener
     * @param params
     */
    public static void deletePortFile(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setResponseTimeOut(60000);
        requestBean.setRequestTimeOut(60000);
        requestBean.setServerMethod("fp/portFile");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_DELETE);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_PORT_FILE, listener, requestBean);
    }
}
