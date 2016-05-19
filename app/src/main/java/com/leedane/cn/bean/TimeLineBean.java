package com.leedane.cn.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 时间线展示的bean
 * Created by LeeDane on 2016/4/15.
 */
public class TimeLineBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("table_id")
	private int tableId;

	@SerializedName("table_name")
	private String tableName;

	@SerializedName("create_time")
	private String createTime; //避免转化json过程中的出现问题，用字符串yyyy-MM-dd HH:mm:ss字符串
	private String source;
	private String content;
	private String froms;
	@SerializedName("create_user_id")
	private int createUserId;
	private String account;
	@SerializedName("user_pic_path")
	private String userPicPath;

	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFroms() {
		return froms;
	}
	public void setFroms(String froms) {
		this.froms = froms;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

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
}