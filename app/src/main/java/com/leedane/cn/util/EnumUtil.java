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
		未登录(1001),
		服务器异常(500),
		链接不存在(400),
		文件不存在(2001),
		操作文件失败(2002),
		某些参数为空(2003),
		缺少参数(2004),
		JSON解析失败(2005),
		禁止访问(2006),
		系统维护时间(2007),
		文件上传失败(2008),
		没有操作实例(2009),
		没有操作权限(2010),
		没有下载码(2011),
		下载码失效(2012);
		
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
