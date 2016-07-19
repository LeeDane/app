package com.leedane.cn.bean.search;

import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 搜索心情实体bean
 * Created by leedane on 2016/5/22.
 */
public class SearchMoodBean extends IdBean {

    private String account;

    @SerializedName("create_user_id")
    private int createUserId;

    @SerializedName("user_pic_path")
    private String userPicPath;

    private String content; //心情的内容

    /**
     * 是否有图片
     */
    @SerializedName("has_img")
    private boolean hasImg;


    /**
     * 多张图像的路径，多个用","分隔开
     */
    private String imgs;

    private String froms;//心情来源

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public boolean isHasImg() {
        return hasImg;
    }

    public void setHasImg(boolean hasImg) {
        this.hasImg = hasImg;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getFroms() {
        return froms;
    }

    public void setFroms(String froms) {
        this.froms = froms;
    }
}
