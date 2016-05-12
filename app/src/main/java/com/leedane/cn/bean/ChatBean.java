package com.leedane.cn.bean;
import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 聊天首页实体类
 * Created by LeeDane on 2016/5/4.
 */
public class ChatBean extends IdBean {

	/**
	 * 最后一条信息的内容
	 */
	private String content;

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

	/**
	 * 是否读取(0：未读，1：已读)
	 */
	private int read;
	/**
	 * 未读取数量
	 */
	@SerializedName("no_read_number")
	private int noReadNumber;

	/**
	 * 创建人ID
	 */
	@SerializedName("create_user_id")
	private int createUserId;

	/**
	 * 接收人的ID
	 */
	@SerializedName("to_user_id")
	private int toUserId;

	/**
	 * 信息的类型
	 */
	private int type;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getNoReadNumber() {
		return noReadNumber;
	}

	public void setNoReadNumber(int noReadNumber) {
		this.noReadNumber = noReadNumber;
	}

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}
}
