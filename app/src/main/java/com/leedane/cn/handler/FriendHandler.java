package com.leedane.cn.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.bean.HttpResponseMyFriendsBean;
import com.leedane.cn.bean.MyFriendsBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 我的好友相关处理类
 * Created by LeeDane on 2016/4/21.
 */
public class FriendHandler {

    /**
     * 添加好友
     * @param listener
     * @param toUserId
     */
    public static void addFriend(TaskListener listener, int toUserId){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("to_user_id", toUserId);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/friend_add.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_FRIEND, listener, requestBean);
    }

    /**
     * 解除与某用户的好友关系
     * @param listener
     * @param toUserId
     */
    public static void cancelFriend(TaskListener listener, int toUserId){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("to_user_id", String.valueOf(toUserId));
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/friend_delete.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.CANCEL_FRIEND, listener, requestBean);
    }

    /**
     * 根据好友ID获取好友名称
     * @param friendId
     * @return
     */
    public static String getFriendAccout(int friendId){
        String friends = SharedPreferenceUtil.getFriends(BaseApplication.newInstance().getApplicationContext().getApplicationContext());
        if(StringUtil.isNotNull(friends)){
            Gson gson = new GsonBuilder().create();
            HttpResponseMyFriendsBean mModel = gson.fromJson(friends, HttpResponseMyFriendsBean.class);
            if(mModel != null && mModel.getMessage().size() > 0){
                for(MyFriendsBean friendsBean: mModel.getMessage()){
                    if(friendsBean.getId() == friendId){
                        return friendsBean.getAccount();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取已经跟我成为好友关系的分页列表
     * @param listener
     * @param params
     */
    public static void sendFriendsPaging(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/friend_friendsPaging.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_FRIENDS_PAGING, listener, requestBean);
    }

    /**
     * 获取我发送的好友请求列表
     * @param listener
     * @param params
     */
    public static void sendRequestPaging(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/friend_requestPaging.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_REQUEST_PAGING, listener, requestBean);
    }

    /**
     * 获取等待我同意的好友关系列表
     * @param listener
     * @param params
     */
    public static void sendResponsePaging(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/friend_responsePaging.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_RESPONSE_PAGING, listener, requestBean);
    }

    /**
     * 获取登录用户的全部好友信息
     * @param listener
     */
    public static void sendGetAllFriends(TaskListener listener){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/friend_friends.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_USER_FRIENS, listener, requestBean);
    }
}
