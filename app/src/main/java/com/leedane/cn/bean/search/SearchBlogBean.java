package com.leedane.cn.bean.search;

import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 搜索博客实体bean
 * Created by leedane on 2016/5/22.
 */
public class SearchBlogBean extends IdBean {

    private String account;

    @SerializedName("create_user_id")
    private int createUserId;

    @SerializedName("user_pic_path")
    private String userPicPath;

    private String source; //来源

    private String title;//博客的标题

    private String tag; //标签(多个用逗号隔开)

    private String digest; //博客的简介

    @SerializedName("has_img")
    private boolean hasImg; //是否有图片

    @SerializedName("img_url")
    private String imgUrl;  //图片的地址

    @SerializedName("create_time")
    private String createTime; //用户创建时间

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

    public String getUserPicPath() {
        return userPicPath;
    }

    public void setUserPicPath(String userPicPath) {
        this.userPicPath = userPicPath;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public String getImgUrl() {

        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHasImg() {
        return hasImg;
    }

    public void setHasImg(boolean hasImg) {
        this.hasImg = hasImg;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
