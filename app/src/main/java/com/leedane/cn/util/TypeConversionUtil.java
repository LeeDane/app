package com.leedane.cn.util;

import android.content.ContentValues;

import java.util.Map;

/**
 * 数据库类型转换工具(MapParamsToContentValues)
 * Created by LeeDane on 2016/1/24.
 */
public class TypeConversionUtil {
    public TypeConversionUtil() {

    }

    public static ContentValues changeMapParamsToContentValues(Map<String, Object> params){
        ContentValues v = new ContentValues();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof Integer) { // 整型
                v.put(entry.getKey(), Integer.parseInt(String
                        .valueOf(entry.getValue())));
            } else if (entry.getValue() instanceof String) { // 字符串
                v.put(entry.getKey(),
                        String.valueOf(entry.getValue()));
            } else if (entry.getValue() instanceof Boolean) { // boolean类型
                v.put(entry.getKey(), Boolean.parseBoolean(String
                        .valueOf(entry.getValue())));
            } else if (entry.getValue() instanceof Float) { // float类型
                v.put(entry.getKey(), Float.parseFloat((String
                        .valueOf(entry.getValue()))));
            } else if (entry.getValue() instanceof Double) { // double类型
                v.put(entry.getKey(), Double.parseDouble((String
                        .valueOf(entry.getValue()))));
            } else if (entry.getValue() instanceof Long) { // Long类型
                v.put(entry.getKey(), Long.parseLong((String
                        .valueOf(entry.getValue()))));
            } else {
                continue;
            }
        }
        return v;
    }
}
