package com.leedane.cn.bean;

import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.StatusBean;

/**
 * 我的好友实体类
 * Created by LeeDane on 2016/6/19.
 */
public class FriendBean extends StatusBean{

    //状态：0:请求好友，1:正式好友，2：已经删除的好友，3：黑名单好友
    private static final long serialVersionUID = 1L;

    @SerializedName("fid")
    private int fid;  //用户ID
    @SerializedName("user_pic_path")
    private String userPicPath; //朋友头像

    @SerializedName("accout")
    private String account;  //用户名

    private String remark;//备注

    private String introduce;//介绍信息


    /**
     * 创建时间
     */
    @SerializedName("create_time")
    private String createTime;

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getUserPicPath() {
        return userPicPath;
    }

    public void setUserPicPath(String userPicPath) {
        this.userPicPath = userPicPath;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}