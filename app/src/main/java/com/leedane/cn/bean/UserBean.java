package com.leedane.cn.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 用户实体类
 * Created by LeeDane on 2017/1/19.
 */
public class UserBean{
	
	private int id;

	 private int status;
	//用户状态    0:被禁止使用 1：正常，8、注册未激活,6：未完善信息 ， 4：被禁言 ，2:注销
	
	/**
	 * 非必须，uuid,唯一(如在图像的时候关联filepath表)
	 */
	private String uuid;
	
	/**
	 * 用户类型，0：用户,1:商家,2:管理员，3：商家客服
	 */
	private int type;
	
	/**
	 * 帐号,默认登录的标准以及显示的用户名称(不能为空)
	 */
	private String account;

	/**
	 * 用户的头像图片
	 */
	@SerializedName("user_pic_path")
	private String userPicPath;
	
	/**
	 * 中文名
	 */
	@SerializedName("china_name")
	private String chinaName;
	
	/**
	 * 密码,需要MD5加密后保存
	 */
	private String password; 
	
	/**
	 * 真实姓名
	 */
	@SerializedName("real_name")
	private String realName;
	
	/**
	 * 性别
	 */
	private String sex;
	
	/**
	 * 年龄
	 */
	private int age;
	
	/**
	 * 民族
	 */
	private String nation;
	
	/**
	 * 婚姻状态
	 */
	private String marry;  
	
	/**
	 * 籍贯
	 */
	@SerializedName("native_place")
	private String nativePlace;
	
	/**
	 * 最高学历
	 */
	@SerializedName("education_background")
	private String educationBackground;
	
	/**
	 * 毕业学校
	 */
	private String school;
	
	/**
	 * 公司
	 */
	private String company;
	
	/**
	 * 公司地址
	 */
	@SerializedName("company_address")
	private String companyAddress;
	
	/**
	 * 联系地址(默认地址)
	 */
	private String address;
	
	/**
	 * 手机号码，格式：137XXXXXXXXXXX
	 */
	@SerializedName("mobile_phone")
	private String mobilePhone;
	
	/**
	 * 家庭座机电话，格式如020-1234567
	 */
	@SerializedName("home_phone")
	private String homePhone; 
	
	/**
	 * 公司电话号码
	 */
	@SerializedName("company_phone")
	private String companyPhone; 
	
	/**
	 * 生日，格式2014-06-25 00:00:00
	 */
	@SerializedName("birth_day")
	private String birthDay;
	
	/**
	 * 保存照片的相对路径(开发时选择绝对路径)
	 */
	@SerializedName("pic_path")
	private String picPath;
	
	/**
	 * 照片的base64位(开发时选择绝对路径)
	 */
	@SerializedName("pic_base64")
	private String picBase64;
	
	/**
	 * 邮箱，附加登录的条件，找回密码，验证等凭证
	 */
	private String email;
	
	/**
	 * QQ号码
	 */
	private String qq;
	
	/**
	 * 身份证号码
	 */
	@SerializedName("id_card")
	private String idCard;
	
	/**
	 * 个人介绍
	 */
	@SerializedName("personal_introduction")
	private String personalIntroduction; 
	
	/**
	 * 保存注册的注册码
	 */
	@SerializedName("register_code")
	private String registerCode;
	
	/**
	 * 积分
	 */
	private int score;
	
	/**
	 * 注册时间，格式2014-06-25 10:00:00
	 */
	@SerializedName("register_time")
	private String registerTime;
	
	/**
	 * 免登陆验证码
	 */
	@SerializedName("no_login_code")
	private String noLoginCode = "";
	
	/**
	 * 是否被solr索引(冗余字段)
	 */
	@SerializedName("is_solr_index")
	private boolean isSolrIndex;
	
	/**
	 * 是否是管理员(冗余字段)
	 */
	@SerializedName("is_admin")
	private boolean isAdmin;
	
	/**
	 * 所绑定的微信用户的名称
	 */
	@SerializedName("wechat_user_name")
	private String wechatUserName;
	
	/**
	 * 用户拥有的角色(一对多的关系)
	 */
	//private Set<RolesBean> roles = new LinkedHashSet<RolesBean>();
	
	/**
	 * 扩展字段1
	 */
	private String str1; 
	
	/**
	 * 扩展字段2
	 */
	private String str2;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getChinaName() {
		return chinaName;
	}

	public void setChinaName(String chinaName) {
		this.chinaName = chinaName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getMarry() {
		return marry;
	}

	public void setMarry(String marry) {
		this.marry = marry;
	}

	public String getNativePlace() {
		return nativePlace;
	}

	public void setNativePlace(String nativePlace) {
		this.nativePlace = nativePlace;
	}

	public String getEducationBackground() {
		return educationBackground;
	}

	public void setEducationBackground(String educationBackground) {
		this.educationBackground = educationBackground;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getCompanyPhone() {
		return companyPhone;
	}

	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getPicBase64() {
		return picBase64;
	}

	public void setPicBase64(String picBase64) {
		this.picBase64 = picBase64;
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

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getPersonalIntroduction() {
		return personalIntroduction;
	}

	public void setPersonalIntroduction(String personalIntroduction) {
		this.personalIntroduction = personalIntroduction;
	}

	public String getRegisterCode() {
		return registerCode;
	}

	public void setRegisterCode(String registerCode) {
		this.registerCode = registerCode;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getNoLoginCode() {
		return noLoginCode;
	}

	public void setNoLoginCode(String noLoginCode) {
		this.noLoginCode = noLoginCode;
	}

	public boolean isSolrIndex() {
		return isSolrIndex;
	}

	public void setIsSolrIndex(boolean isSolrIndex) {
		this.isSolrIndex = isSolrIndex;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getWechatUserName() {
		return wechatUserName;
	}

	public void setWechatUserName(String wechatUserName) {
		this.wechatUserName = wechatUserName;
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

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}
}