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
 * 搜索相关的处理
 * Created by LeeDane on 2016/5/22.
 */
public class SearchHandler {

    /**
     * 获取搜索用户列表
     * @param listener
     * @param key
     */
    public static void getSearchUserRequest(TaskListener listener, String key){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", key);
        params.put("platformApp", true); //标记是app平台
        params.put("type", ConstantsUtil.SEARCH_TYPE_USER);//type=3表示只搜索用户
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        requestBean.setRequestTimeOut(60000);
        requestBean.setResponseTimeOut(60000);
        requestBean.setServerMethod("leedane/search/get.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_SEARCH_USER, listener, requestBean);
    }

    /**
     * 获取搜索博客列表
     * @param listener
     * @param key
     */
    public static void getSearchBlogRequest(TaskListener listener, String key){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", key);
        params.put("platformApp", true); //标记是app平台
        params.put("type", ConstantsUtil.SEARCH_TYPE_BLOG);//type=1表示只搜索博客
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        requestBean.setRequestTimeOut(60000);
        requestBean.setResponseTimeOut(60000);
        requestBean.setServerMethod("leedane/search/get.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_SEARCH_BLOG, listener, requestBean);
    }

    /**
     * 获取搜索心情列表
     * @param listener
     * @param key
     */
    public static void getSearchMoodRequest(TaskListener listener, String key){
        HttpRequestBean requestBean = new HttpRequestBean();
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", key);
        params.put("platformApp", true); //标记是app平台
        params.put("type", ConstantsUtil.SEARCH_TYPE_MOOD);//type=2表示只搜索心情
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        requestBean.setRequestTimeOut(60000);
        requestBean.setResponseTimeOut(60000);
        requestBean.setServerMethod("leedane/search/get.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_SEARCH_MOOD, listener, requestBean);
    }
}
