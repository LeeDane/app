package com.leedane.cn.util.http;

import android.util.Log;

import com.leedane.cn.uploadfile.PortUpload;
import com.leedane.cn.util.ConstantsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * App上传和下载的工具类
 * Created by LeeDane on 2016/1/29.
 */
public class AppUploadAndDownloadUtil {

    /**
     * 断点上传
     * @param requestUrl(完整的服务器地址)
     * @param portUpload
     * @return
     */
    public static String portUpload(String requestUrl, PortUpload portUpload){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("url", portUpload.getUrl());
            File file = new File(portUpload.getLocalPath());
            if(!file.exists()){
                jsonObject.put("isSuccess", false);
                jsonObject.put("message", "文件不存在");
                return jsonObject.toString();
            }
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            // 发送POST请求必须设置如下两行

            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/html");
            conn.setRequestProperty("Cache-Control","no-cache");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setReadTimeout(portUpload.getRequestTimeOut() > 0 ? portUpload.getRequestTimeOut() : ConstantsUtil.DEFAULT_REQUEST_TIME_OUT);
            conn.setConnectTimeout(portUpload.getResponseTimeOut() >0 ? portUpload.getResponseTimeOut() :ConstantsUtil.DEFAULT_RESPONSE_TIME_OUT);
            conn.connect();
            OutputStream out =conn.getOutputStream();
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            raf.seek(portUpload.getFrom());
            int length = (int)portUpload.getLength();
            byte[] bs = new byte[length];  //指定长度
            raf.read(bs); //得到内容
            out.write(bs);
            out.flush();
            if(out != null)
                out.close();
            if(raf != null)
                raf.close();
            //上传结束后获取返回信息
            InputStream input = conn.getInputStream();
            StringBuffer sb = new StringBuffer();
            int len;
            while ((len = input.read()) != -1) {
                sb.append((char) len);
            }
            if(input != null)
                input.close();
            conn.disconnect();
            JSONObject object = new JSONObject(sb.toString());
            if(object.has("isSuccess")){
                jsonObject.put("isSuccess", object.optBoolean("isSuccess"));
            }
            if(object.has("message")){
                jsonObject.put("message", object.optString("message"));
            }
            if(object.has("responseCode")){
                jsonObject.put("responseCode", object.optInt("responseCode"));
            }
            return jsonObject.toString();
        } catch (Exception e) {
            System.out.println("上传文件出现异常！" + e);
            e.printStackTrace();
        }
        try {
            jsonObject.put("isSuccess", false);
            jsonObject.put("message", "上传文件出现异常");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
