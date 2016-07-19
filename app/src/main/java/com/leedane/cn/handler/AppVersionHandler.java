package com.leedane.cn.handler;

import android.content.pm.PackageInfo;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;

import java.util.Map;

/**
 * App系统更新相关的处理类
 * Created by LeeDane on 2016/3/27.
 */
public class AppVersionHandler {


    /**
     * 获取最新系统的请求
     * @param listener
     * @param params
     */
    public static void getNewestVersion(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        PackageInfo packageInfo = BaseApplication.getPackageInfo();
        if(packageInfo != null){
            params.put("versionName", packageInfo.versionName);
            params.put("versionCode", packageInfo.versionCode);
        }
        requestBean.setRequestMethod("POST");
        requestBean.setServerMethod("leedane/appVersion/getNewest.action");

        TaskLoader.getInstance().startTaskForResult(TaskType.GET_APP_VERSION, listener, requestBean);
    }
}
