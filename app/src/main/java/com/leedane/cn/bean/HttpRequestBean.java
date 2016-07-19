package com.leedane.cn.bean;

import java.util.HashMap;
import java.util.Map;

import com.leedane.cn.util.ConstantsUtil;

/**
 * Created by LeeDane on 2015/10/7.
 */
public class HttpRequestBean {
    /**
     * 发送请求的url地址(只有ip和端口,可以为空)
     */
    private String url;

    /**
     * 请求远程服务的方法(除了ip和端口后的部分，不能为空)
     */
    private String serverMethod;

    /**
     * 请求的方式(不填或者默认的都是post请求)
     */
    private String requestMethod = ConstantsUtil.REQUEST_METHOD_POST;

    /**
     * 发送的参数(可以为空)
     */
    private Map<String, Object> params = new HashMap<String, Object>();

    /**
     * 请求超时的时间(int)
     */
    private int requestTimeOut;

    /**
     * 响应超时的时间
     */
    private int responseTimeOut;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public int getRequestTimeOut() {
        return requestTimeOut;
    }

    public void setRequestTimeOut(int requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

    public int getResponseTimeOut() {
        return responseTimeOut;
    }

    public void setResponseTimeOut(int responseTimeOut) {
        this.responseTimeOut = responseTimeOut;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getServerMethod() {
        return serverMethod;
    }

    public void setServerMethod(String serverMethod) {
        this.serverMethod = serverMethod;
    }
}
