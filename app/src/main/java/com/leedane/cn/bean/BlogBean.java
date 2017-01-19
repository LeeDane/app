package com.leedane.cn.bean;
import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.StatusBean;

/**
 * Blog的实体bean
 * @author LeeDane
 * @version 1.0
 *
 */

public class BlogBean extends StatusBean{

    private static final long serialVersionUID = 1L;

    //博客的状态,-1：草稿，1：正常，0:禁用，2、删除

    /**
     * 博客的标题
     */
    private String title;

    /**
     * 博客内容
     */
    private String content;

    /**
     * 博客摘要(建议一般不要超过50个字)
     */
    private String digest;

    /**
     * 标签(多个用逗号隔开)
     */
    private String tag;

    /**
     * 来自(指的是来自发表的方式，如：Android客户端，iPhone客户端等)
     */
    private String froms;

    /**
     * 是否有图片
     */
    @SerializedName("has_img")
    private boolean hasImg;

    /**
     * 图片的地址
     */
    @SerializedName("img_url")
    private String imgUrl;

    /**
     * 原文的链接
     */
    @SerializedName("origin_link")
    private String originLink;

    /**
     * 来源（指的是文章的来源，其他网站的话写的是其他网站的信息，原创的话直接写原创）
     */
    private String source;

    /**
     * 该条博客是否被索引了
     */
    @SerializedName("is_index")
    private boolean isIndex;


    /**
     * 是否被阅读
     */
    @SerializedName("is_read")
    private boolean isRead;

    /**
     * 阅读次数
     */
    @SerializedName("read_number")
    private int readNumber;

    /**
     * 统计赞的数量
     */
    @SerializedName("zan_number")
    private int zanNumber;

    /**
     * 统计评论的数量
     */
    @SerializedName("comment_number")
    private int commentNumber;

    /**
     * 统计转发的数量
     */
    @SerializedName("transmit_number")
    private int transmitNumber ;

    /**
     * 统计分享的数量
     */
    @SerializedName("share_number")
    private int shareNumber;

    /**
     * 是否立即发布
     */
    @SerializedName("is_publish_now")
    private boolean isPublishNow;

    /**
     * 扩展字段1
     */
    private String str1;

    /**
     * 扩展字段2
     */
    private String str2;

    /**
     * 创建人ID
     */
    @SerializedName("create_user_id")
    private int createUserId;

    /**
     * 用户的头像图片
     */
    @SerializedName("user_pic_path")
    private String userPicPath;


    /**
     * 用户的账号
     */
    private String account;

    /**
     * 创建时间
     */
    @SerializedName("create_time")
    private String createTime;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getDigest() {
        return digest;
    }
    public void setDigest(String digest) {
        this.digest = digest;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getStr1() {
        return str1;
    }
    public void setStr1(String str1) {
        this.str1 = str1;
    }
    public String getStr2() {
        return str2;
    }
    public void setStr2(String str2) {
        this.str2 = str2;
    }
    public int getReadNumber() {
        return readNumber;
    }
    public void setReadNumber(int readNumber) {
        this.readNumber = readNumber;
    }
    public int getZanNumber() {
        return zanNumber;
    }
    public void setZanNumber(int zanNumber) {
        this.zanNumber = zanNumber;
    }
    public int getCommentNumber() {
        return commentNumber;
    }
    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }
    public int getTransmitNumber() {
        return transmitNumber;
    }
    public void setTransmitNumber(int transmitNumber) {
        this.transmitNumber = transmitNumber;
    }

    public int getShareNumber() {
        return shareNumber;
    }
    public void setShareNumber(int shareNumber) {
        this.shareNumber = shareNumber;
    }

    public boolean isRead() {
        return isRead;
    }
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isHasImg() {
        return hasImg;
    }
    public void setHasImg(boolean hasImg) {
        this.hasImg = hasImg;
    }

    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getOriginLink() {
        return originLink;
    }
    public void setOriginLink(String originLink) {
        this.originLink = originLink;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getFroms() {
        return froms;
    }
    public void setFroms(String froms) {
        this.froms = froms;
    }
    public boolean isPublishNow() {
        return isPublishNow;
    }
    public void setPublishNow(boolean isPublishNow) {
        this.isPublishNow = isPublishNow;
    }

    public boolean isIndex() {
        return isIndex;
    }
    public void setIndex(boolean isIndex) {
        this.isIndex = isIndex;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserPicPath() {
        return userPicPath;
    }

    public void setUserPicPath(String userPicPath) {
        this.userPicPath = userPicPath;
    }
}