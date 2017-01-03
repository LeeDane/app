package com.leedane.cn.handler;

import android.content.Context;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatBean;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.database.ChatDataBase;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 聊天相关的设置处理
 * Created by LeeDane on 2016/5/4.
 */
public class ChatHandler {

    /**
     * 删除评论
     * @param context
     * @param userId
     */
    public static boolean deleteLocalChat(Context context, int userId){
        try{
            ChatDataBase chatDataBase = new ChatDataBase(context);
            chatDataBase.deleteByUser(userId);
            chatDataBase.destroy();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 加载本地的聊天实体
     * @param context
     * @return
     */
    public static List<ChatBean> getLocalChatBeans(Context context){
        List<ChatBean> chatBeans = new ArrayList<>();
        ChatBean chatBean = new ChatBean();
        chatBean.setAccount("leedane");
        chatBean.setContent("你好啊");
        chatBean.setId(1);
        chatBean.setCreateTime(DateUtil.DateToString(new Date()));
        chatBeans.add(chatBean);

        ChatBean chatBean1 = new ChatBean();
        chatBean1.setAccount("测试号");
        chatBean1.setContent("我是测试号");
        chatBean1.setId(2);
        chatBean1.setCreateUserId(1);
        chatBean1.setToUserId(2);
        chatBean1.setCreateTime(DateUtil.DateToString(new Date()));
        chatBeans.add(chatBean1);
        return chatBeans;
    }

    /**
     * 加载未读的列表
     * @param listener
     */
    public static void loadNoReadChat(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/chat/noReadList.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_NO_READ_CHAT, listener, requestBean);
    }

    /**
     * 获取登录用户的全部与其有过聊天记录的用户的最新一条聊天信息
     * @param listener
     * @return
     */
    public static void getOneChatByAllUser(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/chat/getOneChatByAllUser.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_ONE_CHAT_BY_ALL_USER, listener, requestBean);
    }
}
