package com.leedane.cn.bean;
import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 登录历史记录实体类
 * Created by LeeDane on 2016/5/5.
 */
public class LoginHistoryBean extends IdBean {

	private int status;
	/**
	 * 方式类型：register,login等
	 */
	private String method;

	/**
	 * 操作的ip地址
	 */
	private String ip;

	/**
	 * 操作的浏览器名称
	 */
	private String browser;
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

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
