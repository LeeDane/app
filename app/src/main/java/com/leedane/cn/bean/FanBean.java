package com.leedane.cn.bean;
import com.google.gson.annotations.SerializedName;
import com.leedane.cn.bean.base.IdBean;

/**
 * 粉丝实体类
 * Created by LeeDane on 2016/4/11.
 */
public class FanBean extends IdBean {

	/**
	 * 是否关注他
	 */
	@SerializedName("is_fan")
	private boolean fan;

	/**
	 * 是否关注他
	 */
	@SerializedName("is_attention")
	private boolean attention;

	/**
	 * 粉丝的ID
	 */
	@SerializedName("user_id")
	private int toUserId;

	/**
	 * 用户的头像图片
	 */
	@SerializedName("user_pic_path")
	private String userPicPath;

	/**
	 * 创建人账号
	 */
	private String account;
	/**
	 * 创建时间
	 */
	@SerializedName("create_time")
	private String createTime;

	public boolean isFan() {
		return fan;
	}

	public void setFan(boolean fan) {
		this.fan = fan;
	}

	public boolean isAttention() {
		return attention;
	}

	public void setAttention(boolean attention) {
		this.attention = attention;
	}

	public int getToUserId() {
		return toUserId;
	}

	public void setToUserId(int toUserId) {
		this.toUserId = toUserId;
	}

	public String getUserPicPath() {
		return userPicPath;
	}

	public void setUserPicPath(String userPicPath) {
		this.userPicPath = userPicPath;
	}

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

}
