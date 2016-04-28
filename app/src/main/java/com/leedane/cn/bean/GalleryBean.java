package com.leedane.cn.bean;

import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 图库的实体类
 * Created by LeeDane on 2016/1/15.
 */
public class GalleryBean extends IdBean {

    /**
     * 图片的路径
     */
    private String path;

    /**
     * 图片的描述
     */
    @SerializedName("gallery_desc")
    private String desc;

    /**
     * 创建时间
     */
    @SerializedName("create_time")
    private String createTime;

    /**
     * 创建用户的ID
     */
    @SerializedName("create_user_id")
    private int userId;

    /**
     * 创建用户的账号
     */
    @SerializedName("account")
    private String account;

    private int width;

    private int height;

    private long length;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
