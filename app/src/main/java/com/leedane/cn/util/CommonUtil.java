package com.leedane.cn.util;

import android.content.Context;

import java.util.List;

/**
 * 公共的操作类
 * Created by LeeDane on 2016/6/10.
 */
public class CommonUtil {

    public void buildNumberPicker(Context context, int minValue, int maxValue){

    }

    /**
     * 判断数组是否为空
     * @param array
     * @return
     */
    public static boolean isEmpty(List<?> array){
        return array == null || array.size() == 0;
    }

    /**
     * 判断数组是否为空
     * @param array
     * @return
     */
    public static boolean isNotEmpty(List<?> array){
        return !isEmpty(array);
    }
}
