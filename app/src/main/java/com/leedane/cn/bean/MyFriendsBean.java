package com.leedane.cn.bean;

import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 我的好友列表bean
 * Created by LeeDane on 2016/4/30.
 */
public class MyFriendsBean extends IdBean{
    /**
     * 用户的头像图片
     */
    @SerializedName("user_pic_path")
    private String userPicPath;

    /**
     * 创建人
     */
    private String account;

    private String sortLetters;


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserPicPath() {
        return userPicPath;
    }

    public void setUserPicPath(String userPicPath) {
        this.userPicPath = userPicPath;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
