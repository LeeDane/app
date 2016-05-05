package com.leedane.cn.bean;
import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 积分历史记录实体类
 * Created by LeeDane on 2016/5/5.
 */
public class ScoreBean extends IdBean {

	private int status;
	/**
	 * 当前的获得的积分(非总积分)
	 */
	private int score;

	/**
	 * 历史的总积分(冗余字段)
	 */
	@SerializedName("total_score")
	private int totalScore;
	/**
	 * 描述 信息
	 */
	@SerializedName("score_desc")
	private String desc;

	/**
	 * 相关联的表名
	 */
	@SerializedName("table_name")
	private String tableName;
	/**
	 * 相关联的表ID
	 */
	@SerializedName("table_id")
	private int tableId;
	/**
	 * 创建时间
	 */
	@SerializedName("create_time")
	private String createTime;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

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

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
