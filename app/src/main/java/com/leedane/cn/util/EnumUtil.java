package com.leedane.cn.util;

/**
 * 枚举工具类
 * Created by LeeDane on 2016/1/29.
 */
public class EnumUtil {
	/**
	 * 文件的状态(0:暂停，1：等待中,2:正在执行，3：操作失败,4:未合并, 5:删除断点文件,6:完成)
	 */
	public enum FileStatus{
		暂停(0),等待中(1),正在执行(2),操作失败(3),合并文件(4),删除断点文件(5),完成(6),准备就绪(7),文件不存在(7);
		FileStatus(int value) {
			this.value = value;
		}

		public final int value;
	}

	/**
	 * 根据值获取FileStatus的key值
	 * @param value
	 * @return
	 */
	public static String getFileStatusValue(int value){
		for(FileStatus fs: FileStatus.values()){
			if(fs.value == value){
				return fs.name();
			}
		}
		return "";
	}

	/**
	 * 字体
	 */
	public enum ZiTi{
		pop字体("popzt.ttf"), 方正粗圆("fzcy.ttf"),华文新宋("fzxs.ttf") ;
		ZiTi(String value) {
			this.value = value;
		}

		public final String value;
	}


	/**
	 * x心情操作的状态(0:发表，1：转发,2:评论)
	 */
	public enum MoodOperateType{
		发表(0),转发(1),评论(2);
		MoodOperateType(int value) {
			this.value = value;
		}
		public final int value;
	}

	/**
	 * 根据值获取MoodOperateType的key值
	 * @param value
	 * @return
	 */
	public static String getMoodOperateTypeValue(int value){
		for(MoodOperateType mot: MoodOperateType.values()){
			if(mot.value == value){
				return mot.name();
			}
		}
		return "";
	}
	/**
	 * 服务器返回码
	 * @author LeeDane
	 * 2016年1月20日 上午10:17:54
	 * Version 1.0
	 */
	public enum ResponseCode{
		请先登录(1001),
		邮件已发送成功(1002),
		不是未注册状态邮箱不能发注册码(1003),
		邮件发送失败(1004),
		暂时不支持手机找回密码功能(1005),
		暂时不支持邮箱找回密码功能(1006),
		未知的找回密码类型(1007),
		注销成功(1008),
		账号已被禁用(1009),
		账号未被激活(1010),
		请先完善账号信息(1011),
		账号已被禁言(1012),
		账号已被注销(1013),
		服务器处理异常(500),
		链接不存在(400),
		请求返回成功码(200),
		文件不存在(2001),
		操作文件失败(2002),
		某些参数为空(2003),
		缺少参数(2004),
		JSON数据解析失败(2005),
		禁止访问(2006),
		系统维护时间(2007),
		文件上传失败(2008),
		没有操作实例(2009),
		没有操作权限(2010),
		没有下载码(2011),
		下载码失效(2012),
		数据库保存失败(2013),
		添加的记录已经存在(2014),
		数据库删除数据失败(2015),
		操作对象不存在(2016),
		参数信息不符合规范(2017),
		缺少请求参数(2018),
		该博客不存在(2019),
		标签添加成功(2020),
		标签长度不能超过5位(2021),
		数据更新失败(2022),
		数据删除失败(2023),
		账号或密码为空(2024),
		您的账号登录失败太多次(2025),
		该文章不需要审核(2026),
		用户已被禁言(2027),
		用户已被禁止使用(2028),
		注册未激活账户(2029),
		未完善信息(2030),
		用户不存在或请求参数不对(3001),
		用户已经注销(3002),
		请先验证邮箱(3003),
		恭喜您登录成功(3004),
		账号或密码不匹配(3005),
		注册失败(3006),
		该邮箱已被占用(3007),
		该用户已被占用(3008),
		该手机号已被注册(3009),
		操作过于频繁(3010),
		手机号为空或者不是11位数(3011),
		操作成功(3012),
		操作失败(3013),
		该通知类型不存在(3014),
		用户名不能为空(3015),
		密码不能为空(3016),
		两次密码不匹配(3017),
		检索关键字不能为空(3018),
		原密码错误(3019),
		要修改的密码跟原密码相同(3020),
		新密码修改成功(3021),
		系统检测到有敏感词(3022),
		请填写每次用户每次下载扣取的积分(3023),
		自己上传的聊天背景资源(3023),
		聊天内容不能为空(3024),
		您还没有绑定电子邮箱(3025),
		对方还没有绑定电子邮箱(3026),
		该用户不存在(3027),
		邮件已经发送(3028),
		参数不存在或为空(3029),
		该资源现在不支持评论(3030),
		该资源现在不支持转发(3031),
		更新评论状态成功(3032),
		更新转发状态成功(3033),
		更新评论状态失败(3034),
		更新转发状态失败(3035),
		删除通知成功(3036),
		删除通知失败(3037),
		私信内容不能为空(3038),
		好友关系不存在(3039),
		好友关系不是待确认状态(3040),
		关注成功(3041),
		解除好友关系成功(3042),
		添加好友失败(3043),
		不能添加自己为好友(3044),
		删除的通知不存在(3045),
		删除的聊天记录不存在(3046),
		删除聊天记录成功(3047),
		删除聊天记录失败(3048),
		心情图片链接处理失败(3049),
		发表心情成功(3050),
		没有要同步的数据(3051),
		数据同步成功(3052),
		系统不支持查找该年份的数据(3053),
		话题不能为空(3054),
		图片大于1M无法上传(3055),
		收藏成功(3056),
		数据库对象数量不符合要求(3057),
		记账位置信息为空(3058),
		添加成功(3059),
		添加失败(3060),
		修改成功(3061),
		修改失败(3062),
		删除成功(3063),
		删除失败(3064),
		免登录码校验失败(3065),
		免登录码为空(3066),
		登录页面已经过期(3067),
		获取不到一级分类列表(3068),
		获取不到二级分类列表(3069),
		举报成功(3070),
		不能举报自己发布的资源(3071),
		请用管理员账号登录(3072),
		数据库修改失败(3073),
		用户不存在(3074),
		密码重置成功(3075),
		用户注销成功(3076),
		不能给自己发信息(3077),
		暂时不支持发送短信(3078),
		未知的发送消息类型(3079),
		通知已经发送(3080),
		私信已经发送(3081),
		头像上传成功(3082),
		上传的文件过大(3083),
		非合法的链接(3084),
		链接操作失败(3085),
		没有更多数据(3086),
		目前暂不支持的操作方法(3087),
		非正常登录状态(3088)
		;

		private ResponseCode(int value) {
			this.value = value;
		}

		public final int value;
	}

	/**
	 * 根据值获取ResponseCode的key值
	 * @param code
	 * @return
	 */
	public static String getResponseValue(int code){
		for(ResponseCode rc: ResponseCode.values()){
			if(rc.value == code){
				return rc.name();
			}
		}
		return "";
	}

	/**
	 * 消息通知类型
	 * @author LeeDane
	 * 2016年3月22日 上午10:16:30
	 * Version 1.0
	 */
	public enum NotificationType {
		艾特我("@我"), 评论("评论"),转发("转发"),赞过我("赞过我"),私信("私信"),通知("通知");

		private NotificationType(String value) {
			this.value = value;
		}

		public final String value;

	}

	/**
	 * 搜索类型
	 *  @author LeeDane
	 * 2016年12月6日 上午8:16:30
	 * Version 1.0
	 */
	public enum SearchType {
		用户名("user"), 心情("mood"), 博客("blog"), 记账("financial");

		private SearchType(String value) {
			this.value = value;
		}

		public final String value;

	}

	/**
	 * 获取NotificationType列表
	 * @return
	 */
	public static String[] getNotificationTypeList(){
		String[] array = new String[NotificationType.values().length];
		int i = 0;
 		for(NotificationType nt: NotificationType.values()){
			array[i] = nt.value ;
			i++;
		}
		return array;
	}
	public static void main(String[] args) {
		System.out.println(getResponseValue(1001));
	}
}
