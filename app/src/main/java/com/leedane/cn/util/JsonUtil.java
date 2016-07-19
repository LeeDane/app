package com.leedane.cn.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Json解析类
 * Created by LeeDane on 2015/10/7.
 */
public class JsonUtil {

    public static Object parseJSON(InputStream inputStream){
        Object obj = null;
        BufferedReader bufferReader;
        String bf;
        StringBuffer sb = new StringBuffer();
        try {
            bufferReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while((bf = bufferReader.readLine()) != null){
                sb.append(bf);
            }
            String sbStr = sb.toString();
            if(sbStr != null){
                if(sbStr.startsWith("[")){
                    obj = new JSONArray(sbStr);
                }
                else if(sbStr.startsWith("{")){
                    obj = new JSONObject(sbStr);
                }
            }
            System.out.println("解析输入流转成的json:"+sbStr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 从异常在获取信息
     * @param result
     * @return
     */
    public static String getErrorMessage(Object result){
        String error = null;
        try{
            error = EnumUtil.getResponseValue(new JSONObject(StringUtil.changeNotNull(result)).getInt("responseCode"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return StringUtil.isNull(error) ? "网络异常" : error;
    }



}
