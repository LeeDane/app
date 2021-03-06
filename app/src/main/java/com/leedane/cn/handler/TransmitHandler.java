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
 * 转发相关的处理类
 * Created by LeeDane on 2016/3/27.
 */
public class TransmitHandler {


    /**
     * 获取评论的请求
     * @param listener
     * @param params
     */
    public static void getTransmitsRequest(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("ts/transmits");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_TRANSMIT, listener, requestBean);
    }

    /**
     * 添加转发
     * @param listener
     * @param params
     */
    public static void sendTransmit(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        if(!params.containsKey("content")){
            params.put("content", "转发了这条信息");
        }
        requestBean.setParams(params);
        requestBean.setServerMethod("ts/transmit");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_TRANSMIT, listener, requestBean);
    }

    /**
     * 删除转发
     * @param listener
     * @param transmitId
     * @param createUserId
     */
    public static void deleteTransmit(TaskListener listener, int transmitId, int createUserId){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("tid",transmitId);
        params.put("create_user_id", createUserId);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("ts/transmit");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_DELETE);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_TRANSMIT, listener, requestBean);
    }

    /**
     * 更新转发状态
     * @param listener
     * @param tableName
     * @param tableId
     * @param canTransmit
     */
    public static void updateTransmitStatus(TaskListener listener, String tableName, int tableId, boolean canTransmit){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("table_name",tableName);
        params.put("table_id", tableId);
        params.put("can_transmit", canTransmit);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("ts/transmit");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_PUT);
        TaskLoader.getInstance().startTaskForResult(TaskType.UPDATE_TRANSMIT_STATUS, listener, requestBean);
    }
}
