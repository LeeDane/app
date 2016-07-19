package com.leedane.cn.bean;

import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 点赞的用户实体
 * Created by leedane on 2016/5/21.
 */
public class ZanUserBean extends IdBean {

    @SerializedName("create_user_id")
    private int createUserId;

    private String account;

    @SerializedName("user_pic_path")
    private String userPicPath;

    @SerializedName("create_time")
    private String createTime;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public String getUserPicPath() {
        return userPicPath;
    }

    public void setUserPicPath(String userPicPath) {
        this.userPicPath = userPicPath;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
