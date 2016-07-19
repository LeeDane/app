package com.leedane.cn.bean;
import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 点赞实体类
 * Created by LeeDane on 2016/4/5.
 */
public class ZanBean extends IdBean {

	/**
	 * 赞附加内容
	 */
	private String content;
	/**
	 * 来自什么方式
	 */
	private String froms;

	/**
	 * 评论对象的类型(对象表名)必须
	 */
	@SerializedName("table_name")
	private String tableName;

	/**
	 * 评论对象的ID，必须
	 */
	@SerializedName("table_id")
	private int tableId;

	/**
	 * 创建人ID
	 */
	@SerializedName("create_user_id")
	private int createUserId;

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
	 * 源文件的内容
	 */
	private String source;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
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

	public String getFroms() {
		return froms;
	}

	public void setFroms(String froms) {
		this.froms = froms;
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
}
