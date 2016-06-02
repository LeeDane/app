package com.leedane.cn.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * 完整的日期格式：yyyy-MM-dd HH(hh):mm:ss S E D F w W a k K z
 * 完整的日期解释：yyyy年MM月dd日 HH(hh)时   mm分 ss秒 S毫秒   星期E 今年的第D天  这个月的第F星期   今年的第w个星期   这个月的第W个星期  今天的a k1~24制时间 K0-11小时制时间 z时区
 * @author LeeDane
 * 2015-12-8,下午10:07:10
 * Version 1.0
 */
public class DateUtil {
	
	/**
	 * 默认的日期格式“yyyy-MM-dd HH:mm:ss”,其中的HH大小表示的是24小时制，hh是小写表示的是12小时制
	 */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	//public static final String 

	/**
	 * 将Date格式的日期根据format进行格式化
	 * @param date  原始的日期参数
	 * @param format  格式：如"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String DateToString (Date date,String format){
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	/**
	 * 将日期字符串根据format转成日期,默认的格式是"yyyy-MM-dd HH:mm:ss"
	 * @param stringDate 字符串日期，格式要正确，不然会抛异常
	 * @param 
	 * @return
	 */
	public static Date stringToDate(String stringDate){
		return DateUtil.stringToDate(stringDate, DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * 将日期字符串根据format转成日期
	 * @param stringDate 字符串日期，格式要正确，不然会抛异常
	 * @param format 格式：如"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static Date stringToDate(String stringDate,String format){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date date = null;	   
		try {
			date = simpleDateFormat.parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 将日期转化成字符串
	 * @param origin 原始日期参数
	 * @return
	 */
	public static String DateToString(Date origin){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT);
		return simpleDateFormat.format(origin);
	}
	
	/**
	 * 将日期转化成字符串
	 * @param origin 原始日期参数
	 * @return
	 */
	public static String LongToString(long origin){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT);
		return simpleDateFormat.format(origin);
	}
	
	/**
	 * 将日期对象转换成日期
	 * @param obj 日期对象
	 * @return
	 */
	public static Date objectToDate(Object obj){
		if(obj == null ) return null;
		try{
			return (Date)obj;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * 将日期转化成一定的日期类型, 格式是"yyyy-MM-dd HH:mm:ss"
	 * @param origin 原始日期对象
	 * @param format
	 * @return
	 */
	public static Date DateToDate(Date origin, String format){
		return stringToDate(DateToString(origin), format);
	}
	
	/**
	 * 获得系统昨天的时间
	 * @return
	 */
	public static Date getYestoday(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}
	
	/**
	 * 获得指定日期的前一天的时间
	 * @return
	 */
	public static Date getYestoday(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}
	
	/**
	 * 获得系统的指定天数的时间
	 * @param contains 是否包含当天,true 表示包含,如5-7,包含就是5-1,不包含就是4-30
	 * @param amount 指定的天数，可以为任意整数，负数表示过去的时间，整数表示今天或未来的时间
	 * @return
	 */
	public static Date getDayBeforeOrAfter(int amount, boolean contains){
		Calendar c = Calendar.getInstance();
		if(contains)
			c.add(Calendar.DATE, amount + 1);
		else
			c.add(Calendar.DATE, amount);
		return c.getTime();
	}
	
	/**
	 * 获得指定日期的指定天数的时间
	 * @param date
	 * @param amount 指定的天数，可以为任意整数，负数表示过去的时间，整数表示今天或未来的时间
	 * @param contains 是否包含当天,true 表示包含,如5-7,包含就是5-1,不包含就是4-30
	 * @return
	 */
	public static Date getDayBeforeOrAfter(Date date, int amount, boolean contains){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if(contains)
			c.add(Calendar.DATE, amount + 1);
		else
			c.add(Calendar.DATE, amount);
		return c.getTime();
	}

	/**
	 * 从出生日期获得年龄
	 * @param birthDay
	 * @return
	 */
	public static int getAge(Date birthDay){
		if(birthDay != null){
			Calendar c = Calendar.getInstance();
			c.setTime(birthDay);
			int birthYear = c.get(Calendar.YEAR);

			Calendar c1 = Calendar.getInstance();
			c1.setTime(new Date());
			int thisYear = c.get(Calendar.YEAR);
			return thisYear - birthYear > 0 ? thisYear - birthYear : 0;
		}

		return 0;
	}
	

	/**
	 * 获取系统当前时间
	 * @return
	 */
	public static Long getSystemCurrentTime(){
		return System.currentTimeMillis();
	}
	
	/**
	 * 获取过期时间
	 * 目前支持格式：如"1年","1个月","1天","1小时","1分钟","1秒钟"
	 * @param date
	 * @param overdueFormat
	 * @return
	 * @throws Exception
	 */
	public static Date getOverdueTime(Date date,String overdueFormat) throws Exception{
		if(StringUtil.isNull(overdueFormat)) return null;
		int value = -1;
		if(date == null) {
			date = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if(overdueFormat.contains("年") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "年"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "年"));
			c.add(Calendar.YEAR, value);
		}else if(overdueFormat.contains("个月") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "个月"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "个月"));
			c.add(Calendar.MONTH, value);
		}else if(overdueFormat.contains("天") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "天"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "天"));
			c.add(Calendar.DATE, value);
		}else if(overdueFormat.contains("小时") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "小时"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "小时"));
			//c.add(Calendar.HOUR, value);//12小时制
			c.add(Calendar.HOUR_OF_DAY, value);//24小时制
		}else if(overdueFormat.contains("分钟") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "分钟"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "分钟"));
			c.add(Calendar.MINUTE, value);
		}else if(overdueFormat.contains("秒钟") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "秒钟"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "秒钟"));
			c.add(Calendar.SECOND, value);
		}else{
			throw new Exception("表达式的格式有误，格式如：'1年','1个月','1天','1小时','1分钟','1秒钟'");
		}
		return c.getTime();
	}

	/**
	 * 比较两个时间大小(判断日期是否过期)
	 * 注意：当原始时间或者过期时间都为空的情况下，返回的是false(没过期)
	 * @param origin  原始时间
	 * @param overdueTime  过期时间
	 * @return
	 */
	public static boolean isOverdue(Date origin, Date overdueTime){
		if(origin == null || overdueTime == null) 
			return false;
		return overdueTime.getTime() - origin.getTime() > 0 ? false : true;
	}
	
	/**
	 * 根据指定的格式获取系统当前的时间
	 * @param format 格式：如"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String getSystemCurrentTime(String format){
		return DateToString(new Date(getSystemCurrentTime()),format);
	}

	public static void main(String[] args) {
		System.out.println(DateUtil.DateToString(getYestoday(DateUtil.stringToDate("2015-11-12 00:00:00"))));
	}
}
