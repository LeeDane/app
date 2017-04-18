package com.leedane.cn.handler;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;

import java.util.Map;

/**
 * 朋友圈相关的处理类
 * Created by LeeDane on 2016/4/15.
 */
public class CircleOfFriendHandler {
    /**
     * 获取我的朋友圈数据的请求
     * @param listener
     * @param params
     */
    public static void getCircleOfFriendsRequest(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("cof/circleOfFriends");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_CIRCLEOFFRIEND, listener, requestBean);
    }
}
