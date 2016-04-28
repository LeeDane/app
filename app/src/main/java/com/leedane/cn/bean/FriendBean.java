package com.leedane.cn.bean;

import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.StatusBean;

/**
 * 我的好友实体类
 * Created by LeeDane on 2016/4/19.
 */
public class FriendBean extends StatusBean{

    //状态：0:请求好友，1:正式好友，2：已经删除的好友，3：黑名单好友
    private static final long serialVersionUID = 1L;

    @SerializedName("from_user_id")
    private int fromUserId;  //发起请求好友的用户ID
    @SerializedName("to_user_id")
    private int toUserId; //接收好友的用户ID

    @SerializedName("from_user_remark")
    private String fromUserRemark;  //toUserId对FromUserId对应的备注信息
    @SerializedName("to_user_remark")
    private String toUserRemark;  //FromUserId对toUserId对应的备注信息

    @SerializedName("add_introduce")
    private String addIntroduce ;  //FromUserId对toUserId的自我介绍信息

    public int getFromUserId() {
        return fromUserId;
    }
    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public int getToUserId() {
        return toUserId;
    }
    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public String getFromUserRemark() {
        return fromUserRemark;
    }
    public void setFromUserRemark(String fromUserRemark) {
        this.fromUserRemark = fromUserRemark;
    }

    public String getToUserRemark() {
        return toUserRemark;
    }
    public void setToUserRemark(String toUserRemark) {
        this.toUserRemark = toUserRemark;
    }

    public String getAddIntroduce() {
        return addIntroduce;
    }
    public void setAddIntroduce(String addIntroduce) {
        this.addIntroduce = addIntroduce;
    }
}