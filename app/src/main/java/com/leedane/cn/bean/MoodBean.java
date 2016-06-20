package com.leedane.cn.bean;
import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

import java.util.Date;
import java.util.List;

/**
 * 心情实体类
 * Created by LeeDane on 2015/12/8.
 */
public class MoodBean extends IdBean {
	
	/**
	 * 心情内容
	 */
	private String content;
	
	/**
	 * 来自(指的是来自发表的方式，如：Android客户端，iPhone客户端等)
	 */
	private String froms;
	
	/**
	 * 是否有图片
	 */
	@SerializedName("has_img")
	private boolean hasImg;

	private String location; //位置的名称
	private double longitude; //经度
	private double latitude; //纬度
	
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
	 * 点赞的用户列表
	 */
	@SerializedName("zan_users")
	private String praiseUserList;

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
	 * 创建人
	 */
	private String account;

	/**
	 * 唯一的uuid
	 */
	private String uuid;
	/**
	 * 创建时间
	 */
	@SerializedName("create_time")
	private String createTime;

	/**
	 * 多张图像的路径，多个用","分隔开
	 */
	private String imgs;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getCommentNumber() {
		return commentNumber;
	}

	public void setCommentNumber(int commentNumber) {
		this.commentNumber = commentNumber;
	}

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

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public String getFroms() {
		return froms;
	}

	public void setFroms(String froms) {
		this.froms = froms;
	}

	public boolean isHasImg() {
		return hasImg;
	}

	public void setHasImg(boolean hasImg) {
		this.hasImg = hasImg;
	}

	public int getReadNumber() {
		return readNumber;
	}

	public void setReadNumber(int readNumber) {
		this.readNumber = readNumber;
	}

	public int getShareNumber() {
		return shareNumber;
	}

	public void setShareNumber(int shareNumber) {
		this.shareNumber = shareNumber;
	}

	public int getTransmitNumber() {
		return transmitNumber;
	}

	public void setTransmitNumber(int transmitNumber) {
		this.transmitNumber = transmitNumber;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getZanNumber() {
		return zanNumber;
	}

	public void setZanNumber(int zanNumber) {
		this.zanNumber = zanNumber;
	}

	public String getImgs() {
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}

	public String getUserPicPath() {
		return userPicPath;
	}

	public void setUserPicPath(String userPicPath) {
		this.userPicPath = userPicPath;
	}

	public String getPraiseUserList() {
		return praiseUserList;
	}

	public void setPraiseUserList(String praiseUserList) {
		this.praiseUserList = praiseUserList;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
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
}
