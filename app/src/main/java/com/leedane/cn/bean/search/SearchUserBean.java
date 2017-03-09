package com.leedane.cn.bean.search;

import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 搜索用户实体bean
 * Created by leedane on 2016/5/22.
 */
public class SearchUserBean extends IdBean {

    private String account;

    private String personalIntroduction;

    private boolean isFan;

    private boolean isFriend;

    private String birthDay;

    private String mobilePhone;

    private String sex;

    private String email;

    private String qq;

    @SerializedName("user_pic_path")
    private String userPicPath;

    @SerializedName("registerTime")
    private String createTime; //用户创建时间

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserPicPath() {
        return userPicPath;
    }

    public void setUserPicPath(String userPicPath) {
        this.userPicPath = userPicPath;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isFan() {
        return isFan;
    }

    public void setIsFan(boolean isFan) {
        this.isFan = isFan;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public String getPersonalIntroduction() {
        return personalIntroduction;
    }

    public void setPersonalIntroduction(String personalIntroduction) {
        this.personalIntroduction = personalIntroduction;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
}
