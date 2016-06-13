package com.leedane.cn.bean;

import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 从web获取聊天背景图片的bean
 * Created by LeeDane on 2016/6/10.
 */
public class ChatBgSelectWebBean extends IdBean{
    private String path;

    /**
     * 图片的描述
     */
    @SerializedName("chat_bg_desc")
    private String desc;

    private int type; //聊天背景的类型，0：免费,1:收费, 2:全部

    private int score; //当是收费类型的时候需要扣除的积分(收费的时候这个字段必填)

    /**
     * 用户的头像图片
     */
	@SerializedName("user_pic_path")
	private String userPicPath;

	/**
     * 创建人账号
     */
	private String account;
    /**
     * 创建时间
     */
    @SerializedName("create_time")
    private String createTime;

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

    public String getUserPicPath() {
        return userPicPath;
    }

    public void setUserPicPath(String userPicPath) {
        this.userPicPath = userPicPath;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
