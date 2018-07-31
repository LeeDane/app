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
 * 朋友圈相关的处理类
 * Created by LeeDane on 2016/4/15.
 */
public class CircleOfFriendPagingHandler {
    private int mPageSize;
    private TaskListener mListener;
    public CircleOfFriendPagingHandler(TaskListener listener, int pageSize){
        this.mPageSize = pageSize;
        this.mListener = listener;
    }
    /**
     * 分页获取我的朋友圈数据的请求
     */
    public void getCircles(int current){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap params = new HashMap();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        params.put("page_size", mPageSize);
        params.put("total", 0);// 暂时用不上
        params.put("current", current);
        requestBean.setParams(params);
        requestBean.setServerMethod("cof/circleOfFriends");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_CIRCLEOFFRIEND, mListener, requestBean);
    }
}
