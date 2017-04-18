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
 * 心情相关处理类
 * Created by LeeDane on 2016/3/27.
 */
public class MoodHandler {

    /**
     * 发送心情
     * @param listener
     * @param params
     */
    public static void sendWord(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestTimeOut(60000);
        requestBean.setResponseTimeOut(60000);
        requestBean.setServerMethod("md/sendWord");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.SEND_MOOD_NORMAL, listener, requestBean);
    }
    /**
     * 发送心情
     * @param listener
     * @param params
     */
    public static void sendWordAndLink(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("md/wordAndLink");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.SEND_MOOD_NORMAL, listener, requestBean);
    }

    /**
     * 获取心情列表
     * @param listener
     * @param params
     */
    public static void getMoods(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        /*params.put("toUserId", mPreUid);
        params.put("pageSize", 10);
        params.put("method", mPreLoadMethod);*/
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("md/moods");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.PERSONAL_LOADMOODS, listener, requestBean);
    }

    /**
     * 删除心情
     * @param listener
     * @param params
     */
    public static void delete(TaskListener listener, HashMap<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("md/mood");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_DELETE);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_MOOD, listener, requestBean);
    }

    /**
     * 获取心情详情基本信息
     */
    public static void detail(TaskListener listener, int mid){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("mid", mid);
        requestBean.setParams(params);
        requestBean.setServerMethod("md/detail");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.DETAIL_MOOD, listener, requestBean);
    }

    /**
     * 获取心情对应的图片列表信息
     */
    public static void detailImages(TaskListener listener, int mid, String uuid){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("mid", mid);
        params.put("table_name", "t_mood");
        params.put("table_uuid", uuid);
        requestBean.setParams(params);
        requestBean.setServerMethod("md/detail/imgs");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.DETAIL_MOOD_IMAGE, listener, requestBean);
    }
}
