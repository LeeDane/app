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
		服务器处理异常(500),
		链接不存在(400),
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
		需要添加的记录已经存在(2014),
		数据库删除数据失败(2015),
		操作对象不存在(2016),
		参数信息不符合规范(2017),
		缺少请求参数(2018),
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
		邮件发送成功(3028),
		参数存在或为空(3029),
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
