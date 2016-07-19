package com.leedane.cn.bean;

import com.leedane.cn.bean.base.IdBean;

/**
 * 位置信息的实体bena
 * Created by LeeDane on 2016/6/15.
 */
public class LocationBean extends IdBean{

    private String addrStr;  //地址的信息
    private String name; //位置的名称
    private double longitude; //经度
    private double latitude; //纬度

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddrStr() {
        return addrStr;
    }

    public void setAddrStr(String addrStr) {
        this.addrStr = addrStr;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
