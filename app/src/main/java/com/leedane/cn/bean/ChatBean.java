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
	 * 未读取数量
	 */
	@SerializedName("no_read_number")
	private int noReadNumber;

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
}
