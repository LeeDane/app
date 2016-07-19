package com.leedane.cn.bean;

import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 消息通知的实体bean
 * Created by LeeDane on 2016/4/27.
 */
public class NotificationBean extends IdBean {

    //状态： 0：禁用， 1：正常
    /**
     * 发送通知的用户
     */
    @SerializedName("from_user_id")
    private int fromUserId;

    /**
     * 接收通知的用户
     */
    @SerializedName("to_user_id")
    private int toUserId;

    /**
     * 消息的内容
     */
    private String content;

    /**
     * 消息的额外信息
     */
    private String extra;

    /**
     * 通知的类型
     */
    private String type;

    /**
     * 创建时间
     */
    @SerializedName("create_time")
    private String createTime;

    private String source;
    /**
     * 关联的表名
     */
    @SerializedName("table_name")
    private String tableName;

    /**
     * 关联的表ID
     */
    @SerializedName("table_id")
    private int tableId;

    /***
     * 标记是否推送成功，默认是true
     */
    @SerializedName("is_push_error")
    private boolean isPushError;

    /**
     * 标记该信息是否已经被阅读,默认是false
     */
    @SerializedName("is_read")
    private boolean isRead;

    /**
     * toUserId的名称
     */
    @SerializedName("account")
    private String toUserAccount;

    /**
     * toUserId的照片地址
     */
    @SerializedName("user_pic_path")
    private String toUserPicPath;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public boolean isPushError() {
        return isPushError;
    }

    public void setIsPushError(boolean isPushError) {
        this.isPushError = isPushError;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToUserAccount() {
        return toUserAccount;
    }

    public void setToUserAccount(String toUserAccount) {
        this.toUserAccount = toUserAccount;
    }

    public String getToUserPicPath() {
        return toUserPicPath;
    }

    public void setToUserPicPath(String toUserPicPath) {
        this.toUserPicPath = toUserPicPath;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
