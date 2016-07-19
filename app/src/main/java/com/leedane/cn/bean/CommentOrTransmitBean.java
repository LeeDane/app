package com.leedane.cn.bean;
import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 评论实体类
 * Created by LeeDane on 2016/3/3.
 */
public class CommentOrTransmitBean extends IdBean {

	/**
	 * 回复别人评论时，别人的评论的id，非必须
	 */
	private int pid;

	/**
	 * 所属的父节点的id（针对所有的子节点是必须的）
	 * 与pid的区别的pid指向的是上一级(不一定是根节点)，
	 * 而cid一定是指向根节点的id
	 */
	private int cid;
	/**
	 * 评论内容
	 */
	private String content;

	/**
	 * 来自什么方式
	 */
	private String froms;

	/**
	 * 评论等级，1：很差，2：一般，3：及格，4：良好，5：优秀
	 */
	@SerializedName("comment_level")
	private int commentLevel;

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
	 * 源文件的内容
	 */
	private String source;

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

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCommentLevel() {
		return commentLevel;
	}

	public void setCommentLevel(int commentLevel) {
		this.commentLevel = commentLevel;
	}

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


}
