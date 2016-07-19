package com.leedane.cn.bean;
import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 聊天详情实体类
 * Created by LeeDane on 2016/5/5.
 */
public class ChatDetailBean extends IdBean {

	/**
	 * 聊天消息的内容
	 */
	private String content;

	private int type;//内容的类型

	/**
	 * 接收消息的用户ID
	 */
	@SerializedName("create_user_id")
	private int createUserId;
	/**
	 * 接收消息的用户ID
	 */
	@SerializedName("to_user_id")
	private int toUserId;
	/**
	 * 用户的头像图片
	 *//*
	@SerializedName("user_pic_path")
	private String userPicPath;

	*//**
	 * 创建人账号
	 *//*
	private String account;*/
	/**
	 * 创建时间
	 */
	@SerializedName("create_time")
	private String createTime;

	/**
	 * 是否被读取
	 */
	@SerializedName("is_read")
	private boolean read;

	/**
	 * 这个是特殊编码，记录的是和登录用户聊天的人的ID
	 */
	private int code;

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public int getToUserId() {
		return toUserId;
	}

	public void setToUserId(int toUserId) {
		this.toUserId = toUserId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/*public String getUserPicPath() {
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
	}*/

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
