package com.leedane.cn.bean;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * 文件列表的实体bean
 * 数据库对应服务器端的t_file_path
 * 服务器端存储的文件夹是file文件夹
 * Created by LeeDane on 2016/1/24.
 */
public class FileBean {
    /**
     * 文件的id
     */
    private int id;

    /**
     * 文件名称
     */
    @SerializedName("path")
    private String path;

    @SerializedName("is_upload_qiniu")
    private boolean isUploadQiniu;

    @SerializedName("table_id")
    private int tableId;

    @SerializedName("table_name")
    private String tableName;

    @SerializedName("table_uuid")
    private String tableUuid;

    /**
     *创建时间
     */
    @SerializedName("create_time")
    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUploadQiniu() {
        return isUploadQiniu;
    }

    public void setIsUploadQiniu(boolean isUploadQiniu) {
        this.isUploadQiniu = isUploadQiniu;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableUuid() {
        return tableUuid;
    }

    public void setTableUuid(String tableUuid) {
        this.tableUuid = tableUuid;
    }
}
