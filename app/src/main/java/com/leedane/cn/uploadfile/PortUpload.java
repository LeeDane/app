package com.leedane.cn.uploadfile;

/**
 * 断点上传的实体类
 * Created by LeeDane on 2016/1/29.
 */
public class PortUpload {
    /**
     * serialNumber 断点编号，从1开始
     */
    private int serialNumber;

    /**
     * 存放在本地的路径
     */
    private String localPath;

    /**
     * 请求的地址
     */
    private String url;

    /**
     * 开始的节点
     */
    private long from;

    /**
     * 节点后获取的长度
     */
    private long length;

    /**
     * 是否已经执行完成
     */
    private boolean isFinish;

    private int requestTimeOut;

    private int responseTimeOut;

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
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
}
