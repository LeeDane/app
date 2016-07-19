package com.leedane.cn.download;

import java.util.Date;
import java.util.List;

/**
 * 每个下载项的实体
 * Created by LeeDane on 2016/1/24.
 */
public class DownloadItem {
    /**
     * 下载任务的唯一UUID
     */
    private String uuid;

    /**
     * 下载状态(0:暂停，1：等待下载,2:正在下载，3：下载失败,4:未合并, 5:下载完成)
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
     * 已经下载的大小
     */
    private long downloadSize;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 断点下载的列表
     */
    private List<PortDownload> portDownloads;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<PortDownload> getPortDownloads() {
        return portDownloads;
    }

    public void setPortDownloads(List<PortDownload> portDownloads) {
        this.portDownloads = portDownloads;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }
}
