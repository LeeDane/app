package com.leedane.cn.util;

import com.leedane.cn.exception.UnsupportException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 序列化工具类
 * @author LeeDane
 * 2015年9月2日 下午2:27:51
 * Version 1.0
 */
public class SerializeUtil {

	/**
	 * 将一个对象序列化成一个字节数组(该对象必须实现java.io.Serializable接口)
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public static byte[] serializeObject(Object object) throws IOException{
		//判断对象是否实现了java.io.Serializable接口
		if(!isInterface(object.getClass(),"java.io.Serializable")){
			new UnsupportException(object+"必须是实现java.io.Serializable接口");
			return null;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		oos.flush();
		return baos.toByteArray();
	}
	
	/**
	 * 将一个字节数组反序列化成对象(该对象必须实现java.io.Serializable接口)
	 * @param buf
	 * @param clazz
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserializeObject(byte[] buf, Class<?> clazz) throws IOException, ClassNotFoundException{
		//判断对象是否实现了java.io.Serializable接口
		if(!isInterface(clazz.getClass(), "java.io.Serializable")){
			new UnsupportException(clazz.getName()+"必须是实现java.io.Serializable接口");
			return null;
		}
				
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object o = ois.readObject();
		
		if(null != ois){
			try {
				ois.close();
			} catch (Exception e) {
				ois = null;
			}
		}
		return o;
	}

	public static boolean isInterface(Class clazz, String szInterface){
		Class[] face = clazz.getInterfaces();
		for(int i = 0, j = face.length; i <j; i++ ){
			if(face[i].getName().equals(szInterface)){
				return true;
			}else{
				Class[] face1 = face[i].getInterfaces();
				for(int x = 0; x < face1.length; x++){
					if(face1[x].getName().equals(szInterface)){
						return true;
					}else if(isInterface(face1[x], szInterface)){
						return true;
					}
				}
			}
		}
		if(null != clazz.getSuperclass()){
			return isInterface(clazz.getSuperclass(), szInterface);
		}
		return false;
	}
}
