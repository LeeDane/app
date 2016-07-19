package com.leedane.cn.bean.base;

import java.io.Serializable;

public abstract class IdBean implements Serializable{

	private static final long serialVersionUID = 1492976655626017457L;
	
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
