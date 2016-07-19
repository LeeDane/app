package com.leedane.cn.bean;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.base.IdBean;
import com.leedane.cn.util.DateUtil;

import java.util.Date;

/**
 * 我的设置选项饿实体bean
 * Created by LeeDane on 2016/6/6.
 */
public class MySettingBean extends IdBean {

    private String key;
    private String value;

    private int createUserId;
    private String createTime;

    public MySettingBean(){}

    public MySettingBean(int id, String key, String value){
        setId(id);
        setKey(key);
        setValue(value);
        setCreateUserId(BaseApplication.getLoginUserId());
        setCreateTime(DateUtil.DateToString(new Date()));
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
