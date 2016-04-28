package com.leedane.cn.handler;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;

import java.util.HashMap;

/**
 * 博客相关的处理类
 * Created by LeeDane on 2016/3/29.
 */
public class BlogHandler {

    /**
     * 发送博客的请求
     * @param listener
     * @param params
     */
    public static void getBlogsRequest(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/blog_getPagingBlog.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.HOME_LOADBLOGS, listener, requestBean);
    }

    /**
     * 删除博客的请求
     * @param listener
     * @param params
     */
    public static void deleteBlog(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/blog_deleteBlog.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_BLOG, listener, requestBean);
    }
}
