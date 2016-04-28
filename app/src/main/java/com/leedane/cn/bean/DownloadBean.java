package com.leedane.cn.bean;

import java.util.HashMap;

/**
 * 下载的参数实体bean
 * Created by LeeDane on 2015/10/21.
 */
public class DownloadBean {

    /**
     * 下载实体的唯一标记id
     */
    private int objectId;

    /**
     * 下载开始位置
     */
    private int start;

    /**
     * 下载结束位置
     */
    private int end;

    /**
     * 是否下载完成
     */
    private boolean isFinish;

    /**
     * 文件的下载全路径
     */
    private String url;

    /**
     * 请求参数
     */
    private HashMap<String, Object> params;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的路径
     */
    private String filepath;

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
