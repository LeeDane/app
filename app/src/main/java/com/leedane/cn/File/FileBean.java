package com.leedane.cn.File;

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
    private String fileName;

    /**
     *创建时间
     */
    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
