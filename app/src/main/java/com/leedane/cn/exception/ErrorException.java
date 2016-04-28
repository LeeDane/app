package com.leedane.cn.exception;

/**
 * 错误异常类
 * @author LeeDane
 * 2015年6月23日 下午5:26:39
 * Version 1.0
 */
public class ErrorException extends Exception{
	/**
	 * create time 2015年6月23日 下午5:26:12
	 */
	private static final long serialVersionUID = 1L;

	public ErrorException(String message){
		super(message);
	}
}
