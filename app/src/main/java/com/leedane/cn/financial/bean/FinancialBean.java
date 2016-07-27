package com.leedane.cn.financial.bean;



/**
 * 记账实体类
 * @author LeeDane
`* 2016年7月22日 上午9:42:15
 * Version 1.0
 */
//@Table(name="T_FINANCIAL")
public class FinancialBean{
	
	private static final long serialVersionUID = 1L;
	//记账的状态,1：正常，0:禁用，2、删除

	private int localId;

	private int id; // 与服务器保持一致的ID，可以为空

	private int createUserId;

	private String createTime;

	/**
	 * 很重要，添加时间，用于今后的统计时间，必须
	 */
	private String additionTime;

	/**
	 * 状态 1;正常， 2删除
	 */
	private int status;
	
	/**
	 * 模块，1:收入；2：支出
	 */
	private int model;

	/**
	 * 相关金额
	 */
	private float money; 
	
	/**
	 * 一级分类
	 */
	private String oneLevel;
	
	/**
	 * 二级分类
	 */
	private String twoLevel;
	
	/**
	 * 是否有图片
	 */
	private boolean hasImg;
	
	/**
	 * 位置的展示信息
	 */
	private String location;
	
	/**
	 * 经度
	 */
    private double longitude;
    
    /**
     * 纬度
     */
    private double latitude;
    
    /**
     * 备注信息
     */
    private String financialDesc;

	/**
	 * 是否已经同步
	 */
	private boolean synchronous;

	/**
	 * 图像路径
	 */
	private String path;

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public String getOneLevel() {
		return oneLevel;
	}

	public void setOneLevel(String oneLevel) {
		this.oneLevel = oneLevel;
	}

	public String getTwoLevel() {
		return twoLevel;
	}

	public void setTwoLevel(String twoLevel) {
		this.twoLevel = twoLevel;
	}

	public boolean isHasImg() {
		return hasImg;
	}

	public void setHasImg(boolean hasImg) {
		this.hasImg = hasImg;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getFinancialDesc() {
		return financialDesc;
	}

	public void setFinancialDesc(String financialDesc) {
		this.financialDesc = financialDesc;
	}

	public int getLocalId() {
		return localId;
	}

	public void setLocalId(int localId) {
		this.localId = localId;
	}

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

	public boolean isSynchronous() {
		return synchronous;
	}

	public void setSynchronous(boolean synchronous) {
		this.synchronous = synchronous;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAdditionTime() {
		return additionTime;
	}

	public void setAdditionTime(String additionTime) {
		this.additionTime = additionTime;
	}
}
