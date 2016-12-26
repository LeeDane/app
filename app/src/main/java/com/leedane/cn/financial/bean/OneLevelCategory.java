package com.leedane.cn.financial.bean;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.io.Serializable;
import java.util.List;

/**
 * 一级类bean实体类
 * Created by LeeDane on 2016/7/21.
 */
public class OneLevelCategory implements Serializable, IPickerViewData {
	public static int DEFAULT_PARENT_CATEGORY_ICON = 0;

	/**
	 * 大类对应的ID
	 */
	private int id;


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

	private int model; //1表示收入, 2表示支出
	
	/**
	 * 一级分类的预算
	 * 默认是0.00，没有预算限制。
	 * 需要设置预算时候，需要判断其所有二级分类的预算总和，这时设置的值必须大于/等于
	 * 该总和，并且判断是否有设置总预算，没有设置总预算，最高可以设置大于等于二级预算总和即可。
	 * 要是设置了总预算，则将总预算的值减去其他一级分类预算得到该一级预算的最大范围，最小范围还是其所有二级预算的总和。
	 */
	private float budget;

	/**
	 * 排序的位置
	 */
	private int order;

	/**
	 * 是否是默认的分类
	 */
	private boolean isDefault;

	/**
	 * 创建人ID
	 */
	private int createUserId;

	/**
	 * 创建时间
	 */
	private String createTime;
	
	/**
	 * 所有一级预算的列表
	 */
	private List<TwoLevelCategory> twoLevelCategories;
	
	public OneLevelCategory(){}
	
	@Override
	public String toString() {
		return "OneLevelCategory [value=" + value + ", id=" + id + "]";
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

	public List<TwoLevelCategory> getTwoLevelCategories() {
		return twoLevelCategories;
	}

	public void setTwoLevelCategories(List<TwoLevelCategory> twoLevelCategories) {
		this.twoLevelCategories = twoLevelCategories;
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

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	@Override
	public String getPickerViewText() {
		return value;
	}
}
