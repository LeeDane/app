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
 * 聊天详情列表相关的处理类
 * Created by LeeDane on 2016/5/5.
 */
public class ChatDetailHandler {

    /**
     * 获取聊天列表的请求
     * @param listener
     * @param params
     */
    public static void getChatDetailsRequest(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("ct/chats");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_CHAT, listener, requestBean);
    }

    /**
     * 发表聊天信息
     * @param listener
     * @param params
     */
    public static void sendChatDetail(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("ct/chat");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_CHAT, listener, requestBean);
    }

    /**
     * 删除聊天列表
     * @param listener
     * @param chatDetialId
     */
    public static void deleteChatDetail(TaskListener listener, int chatDetialId){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("cid",chatDetialId);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("ct/chat");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_DELETE);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_CHAT, listener, requestBean);
    }

    /**
     * 更新聊天状态为已读
     * @param listener
     * @param chatIds
     */
    public static void updateRead(TaskListener listener, String chatIds){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("cids", chatIds);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("ct/chat");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_PUT);
        TaskLoader.getInstance().startTaskForResult(TaskType.UPDATE_CHAT_READ_STATUS, listener, requestBean);
    }
}
