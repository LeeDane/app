package com.leedane.cn.financial.bean;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 记账位置实体类
 * @author LeeDane
`* 2016年11月22日 上午8:42:15
 * Version 1.0
 */
//@Table(name="T_FINANCIAL_LOCATION")
public class FinancialLocationBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int id;

	@SerializedName("create_user_id")
	private int createUserId;

	@SerializedName("create_time")
	private String createTime;

	/**
	 * 位置信息
	 */
	@SerializedName("location")
	private String location;

	/**
	 * 状态 1;正常， 0:禁用
	 */
	private int status;
    
    /**
     * 备注信息
     */
	@SerializedName("location_desc")
    private String locationDesc;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLocationDesc() {
		return locationDesc;
	}

	public void setLocationDesc(String locationDesc) {
		this.locationDesc = locationDesc;
	}
}
