package com.leedane.cn.uploadfile;

import java.util.Date;
import java.util.List;

/**
 * 上传项的实体类
 * Created by LeeDane on 2016/1/29.
 */
public class UploadItem {
    /**
     * 上传任务的唯一UUID
     */
    private String uuid;

    /**
     * 上传状态(0:暂停，1：等待上传,2:正在上传，3：上传失败,4:未合并, 5:上传完成)
     */
    private int status;

    /**
     * 描述信息，或者是文件的名称
     */
    private String desc;

    /**
     * 文件的名称
     */
    private String fileName;

    /**
     * 存放在本地的路径
     */
    private String localPath;

    /**
     * 总长度
     */
    private long size;

    /**
     * 已经上传的大小
     */
    private long uploadSize;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 断点上传的列表
     */
    private List<PortUpload> portUploads;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<PortUpload> getPortUploads() {
        return portUploads;
    }

    public void setPortUploads(List<PortUpload> portUploads) {
        this.portUploads = portUploads;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getUploadSize() {
        return uploadSize;
    }

    public void setUploadSize(long uploadSize) {
        this.uploadSize = uploadSize;
    }
}
