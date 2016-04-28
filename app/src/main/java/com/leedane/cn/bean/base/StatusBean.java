package com.leedane.cn.bean.base;

import com.leedane.cn.bean.base.IdBean;

public class StatusBean extends IdBean{

	private static final long serialVersionUID = 1L;

	/**
	 * 草稿状态
	 */
	public static final int STATUS_DRAFT = -1;

	/**
	 * 禁用状态
	 */
	public static final int STATUS_DISABLE = 0;

	/**
	 * 正常状态
	 */
	public static final int STATUS_NORMAL = 1;

	private int status ; //状态，-1:草稿，0：禁用，1：正常

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}

