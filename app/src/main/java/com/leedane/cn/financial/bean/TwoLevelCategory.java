package com.leedane.cn.financial.bean;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.StringUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 二级类bean实体类
 * Created by LeeDane on 2016/7/21.
 */
public class TwoLevelCategory implements Serializable{
	
	public static int DEFAULT_SUB_CATEGORY_ICON = 0;//默认显示的图标

	/**
	 * 大类对应的ID
	 */
	private int id;

	/**
	 * 一级分类的ID
	 */
	private int oneLevelId;

	/**
	 * 状态， 0禁用， 1：正常
	 */
	private int status;

	/**
	 * 展示的大类名称
	 */
	private String value;

	/**
	 * 显示的图标
	 */
	private int icon;

	/**
	 * 二级分类的预算
	 * 默认是0.00，没有预算限制。
	 * 需要设置预算时候，先判断是否设置一级分类，有设置一级分类，则该二级预算的最大限制值就是一级分类减去其他二级分类的总预算。
	 * 要是没有设置一级分类，则判断总预算是否设置，要是总预算也没有设置，则该二级预算没有最大设置。要是有总预算，则最大值限制就是
	 * 总预算减去其他一级预算的和。
	 */
	private float budget;

	/**
	 * 是否是默认的分类
	 */
	private boolean isDefault;

	/**
	 * 排序的位置
	 */
	private int order;

	/**
	 * 创建人ID
	 */
	private int createUserId;

	/**
	 * 创建时间
	 */
	private String createTime;
	
	public TwoLevelCategory(){}
	
	public TwoLevelCategory(int oneLevelId, String value, boolean isDefault, int order){
		this.oneLevelId = oneLevelId;
		this.value = value;
		this.isDefault = isDefault;
		this.order = order;
		this.createUserId = BaseApplication.getLoginUserId();
		this.createTime = DateUtil.DateToString(new Date());
		this.status = ConstantsUtil.STATUS_NORMAL;
		this.icon = DEFAULT_SUB_CATEGORY_ICON;
	}
	
	@Override
	public String toString() {
		return "TwoLevelCategory [value=" + value + ", id=" + id + "]";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public float getBudget() {
		return budget;
	}

	public void setBudget(float budget) {
		this.budget = budget;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getOneLevelId() {
		return oneLevelId;
	}

	public void setOneLevelId(int oneLevelId) {
		this.oneLevelId = oneLevelId;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
