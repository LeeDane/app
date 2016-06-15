package com.leedane.cn.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.uploadfile.PortUpload;
import com.leedane.cn.uploadfile.UploadItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by LeeDane on 2015/10/21.
 */
public class FileUtil {

    private static FileUtil fileUtil;
    //默认的每次上传的数据最大的值
    public static final long MAX_EACH_UPLOAD_SIZE = 1024*200;

    private FileUtil(){}
    public static synchronized FileUtil getInstance(){
        if(fileUtil == null)
            fileUtil = new FileUtil();

        return fileUtil;
    }


    public File getCacheDir(Context context){
        File sdDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdDir = Environment.getExternalStorageDirectory();
        }
        else{
            sdDir = context.getCacheDir();
        }
        File cacheDir = new File(sdDir, context.getResources().getString(R.string.app_dirsname));
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 构建上传文件项实体对象
     * @param context
     * @param uri
     * @param createTime
     * @param tableName
     * @return
     */
    public static UploadItem buildUploadItem(Context context, Uri uri, Date createTime, String tableName){
        return buildUploadItem(context, uri.getPath(), createTime, tableName);
    }

    /**
     * 构建上传文件项实体对象(多图情况下)
     * @param context
     * @param path
     * @param createTime
     * @param order
     * @param uuid
     * * @param tableName
     * @return
     */
    public static UploadItem buildUploadItem(Context context, String path, Date createTime, int order, String uuid, String tableName){
        return buildUploadItem(context, path, createTime,order, uuid, tableName, ConstantsUtil.DEFAULT_REQUEST_TIME_OUT, ConstantsUtil.DEFAULT_RESPONSE_TIME_OUT);
    }

    /**
     * 构建上传文件项实体对象(多图情况下)
     * @param context
     * @param path
     * @param createTime
     * @param order
     * @param uuid
     * @param tableName
     * @param requestTimeOut
     * @param responseTimeout
     * @return
     */
    public static UploadItem buildUploadItem(Context context, String path, Date createTime, int order, String uuid, String tableName, int requestTimeOut, int responseTimeout){
        UploadItem item = new UploadItem();
        try{
            File file = new File(path);
            if(!file.isFile()){
                Toast.makeText(context, "请选择文件", Toast.LENGTH_LONG).show();
                return item;
            }
            if(!file.exists()){
                Toast.makeText(context, "文件不存在:" +path, Toast.LENGTH_LONG).show();
                return item;
            }
            Toast.makeText(context, "获取的文件路径是：" +file.getName(), Toast.LENGTH_LONG).show();
            long fileLength = file.length();
            List<PortUpload> portUploads = new ArrayList<>();
            PortUpload portUpload;
            long portFrom =0;
            long portLength = 0;

            JSONObject userInfo =  SharedPreferenceUtil.getUserInfo(BaseApplication.newInstance());
            StringBuffer urlBuffer;
            //文件小于1M的情况下
            if(fileLength <= MAX_EACH_UPLOAD_SIZE ){
                portUpload = new PortUpload();
                urlBuffer = new StringBuffer();
                //urlBuffer.append(BaseApplication.getBaseServerUrl());
                urlBuffer.append("leedane/appUpload_execute.action?uid=");
                urlBuffer.append(userInfo.getInt("id"));
                urlBuffer.append("&uuid=");
                urlBuffer.append(uuid);
                urlBuffer.append("&tableName="+tableName);
                urlBuffer.append("&order="+order);
                urlBuffer.append("&serialNumber=");
                urlBuffer.append(1);
                portUpload.setSerialNumber(1);
                portUpload.setFrom(0);
                portUpload.setLength(fileLength);
                portUpload.setLocalPath(path);
                portUpload.setUrl(urlBuffer.toString());
                portUpload.setRequestTimeOut(requestTimeOut);
                portUpload.setResponseTimeOut(responseTimeout);
                portUploads.add(portUpload);
            }else{//文件大于每次最大限制的情况下
                //一共分开几个发送
                int count = ((int) (fileLength / MAX_EACH_UPLOAD_SIZE)) +1 ;
                for(int i = 0; i< count; i++){
                    portUpload = new PortUpload();
                    portFrom = MAX_EACH_UPLOAD_SIZE * i;
                    //是最后一个
                    if(count == (i+1)){
                        portLength = fileLength - MAX_EACH_UPLOAD_SIZE * i;
                    }else{
                        portLength = MAX_EACH_UPLOAD_SIZE;
                    }

                    urlBuffer = new StringBuffer();
                    //urlBuffer.append(BaseApplication.getBaseServerUrl());
                    urlBuffer.append("leedane/appUpload_execute.action?uid=");
                    urlBuffer.append(userInfo.getInt("id"));
                    urlBuffer.append("&uuid=");
                    urlBuffer.append(uuid);
                    urlBuffer.append("&tableName="+tableName);
                    urlBuffer.append("&order="+order);
                    urlBuffer.append("&serialNumber=");
                    urlBuffer.append((i+1));
                    portUpload.setSerialNumber((i + 1));
                    portUpload.setFrom(portFrom);
                    portUpload.setLength(portLength);
                    portUpload.setLocalPath(path);
                    portUpload.setUrl(urlBuffer.toString());
                    portUpload.setRequestTimeOut(requestTimeOut);
                    portUpload.setResponseTimeOut(responseTimeout);
                    portUploads.add(portUpload);
                }
            }
            item.setFileName(file.getName());
            item.setLocalPath(path);
            item.setCreateTime(createTime);
            item.setStatus(0);
            item.setSize(fileLength);
            item.setUuid(uuid);
            item.setPortUploads(portUploads);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return item;
    }
    /**
     * 构建上传文件项实体对象
     * @param context
     * @param path
     * @param createTime
     * @param tableName
     * @return
     */
    public static UploadItem buildUploadItem(Context context, String path, Date createTime, String tableName){
        JSONObject userInfo =  SharedPreferenceUtil.getUserInfo(BaseApplication.newInstance());
        String uuid = null;
        try {
            uuid = userInfo.getString("account") + UUID.randomUUID().toString();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return buildUploadItem(context, path, createTime, 0, uuid, tableName);
    }
}
