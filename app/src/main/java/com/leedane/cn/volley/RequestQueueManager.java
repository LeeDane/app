package com.leedane.cn.volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.leedane.cn.application.BaseApplication;

/**
 * Volley请求队列管理
 * Created by LeeDane on 2015/12/24.
 */
public class RequestQueueManager {
    /**
     * 获取请求队列类
     */
    public static RequestQueue mRequestQueue = Volley.newRequestQueue(BaseApplication.newInstance());


    /**
     * 添加任务进任务队列
     * @param request
     * @param tag
     */
    public static void addRequest(Request<?> request, Object tag){
        if(tag != null)
            request.setTag(tag);

        mRequestQueue.add(request);
    }

    /**
     * 取消任务
     * @param tag
     */
    public static void cancelRequest(Object tag){
        mRequestQueue.cancelAll(tag);
    }

}
