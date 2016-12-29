package com.leedane.cn.util;

import java.lang.reflect.Method;

/**
 * 类工具类
 * Created by LeeDane on 2016/12/28.
 */
public class ClassUtil {

    private static ClassUtil classUtil = null;

    private ClassUtil(){}
    public synchronized static ClassUtil getInstance(){
        if(classUtil == null){
            synchronized (ClassUtil.class){
                if(classUtil == null)
                    classUtil = new ClassUtil();
            }
        }
        return classUtil;
    }

    /**
     * 判断某个类是否有某方法
     * @param clazz
     * @param methodName
     * @param params
     * @return
     */
    public boolean classHasMethod(Class clazz, String methodName, Object ...params){
        boolean has = false;
        Method[] methods = clazz.getDeclaredMethods();
        if(methods.length > 0){
            for(Method method: methods){
                if(method.getName().equals(methodName)){
                    has = true;
                    break;
                }
            }
        }
        return has;
    }
}
