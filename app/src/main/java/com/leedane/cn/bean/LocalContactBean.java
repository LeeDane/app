package com.leedane.cn.bean;

import com.leedane.cn.bean.base.IdBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 手机联系人列表bean
 * Created by LeeDane on 2016/4/22.
 */
public class LocalContactBean implements Serializable {
    private String id;
    private String name;
    private List<String> phoneNumbers;
    private List<String> emails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
