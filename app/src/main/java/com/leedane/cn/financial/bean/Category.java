package com.leedane.cn.financial.bean;

import java.util.List;
/**
 * 分类bean实体类
 * Created by LeeDane on 2016/7/21.
 */
public class Category {
	/**
	 * 支出分类的总预算
	 * 默认是0.00，没有预算限制。
	 * 需要设置预算时候，需要判断所有的一级分类和二级分类的预算总和，这时设置的值必须大于/等于
	 * 该总和。
	 */
	private float budget;
	
	/**
	 * 所有的一级分类列表
	 * @return
	 */
	private List<ParentGategory> parentGategories;

	public List<ParentGategory> getParentGategories() {
		return parentGategories;
	}

	public void setParentGategories(List<ParentGategory> parentGategories) {
		this.parentGategories = parentGategories;
	}

	public float getBudget() {
		return budget;
	}

	public void setBudget(float budget) {
		this.budget = budget;
	}
	
	
}
