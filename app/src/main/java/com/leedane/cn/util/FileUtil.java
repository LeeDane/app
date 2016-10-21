package com.leedane.cn.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.uploadfile.PortUpload;
import com.leedane.cn.uploadfile.UploadItem;
import com.leedane.cn.util.http.HttpConnectionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * 获取临时文件的文件夹
     *
     * @param context
     * @return
     */
    public static File getTempDir(Context context) {
        File sdDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sdDir = Environment.getExternalStorageDirectory();
        } else {
            sdDir = context.getCacheDir();
        }
        File cacheDir = new File(sdDir, context.getResources().getString(R.string.app_dirsname) + File.separator + context.getResources().getString(R.string.temp_filepath));
        if (!cacheDir.exists()) {
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
                urlBuffer.append("leedane/appUpload/execute.action?uid=");
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
                    urlBuffer.append("leedane/appUpload/execute.action?uid=");
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

    /**
     * 读取文件(文件夹)大小
     * @param file
     * @return KB
     */
    public static Long getFileSizeFormatKB(File file) {
        return getFileSize(file) / 1024;
    }

    /**
     * 读取文件(文件夹)大小
     * @param file
     * @return MB
     */
    public static Long getFileSizeFormatMB(File file) {
        return getFileSizeFormatKB(file) / 1024;
    }

    /**
     * 读取文件(文件夹)大小
     * @param file
     * @return Byte
     */
    public static Long getFileSize(File file) {
        long size = 0;
        try {
            if (!file.exists()) {
                return size;
            }

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    size += getFileSize(files[i]);
                }
            } else {
                size = file.length();
            }
        } catch (Exception ex) {

        }
        return new Long(size);
    }

    /**
     * 读取文件(文件夹)文件数
     * @param file
     * @return
     */
    public static int getFileNumber(File file) {
        int size = 0;
        try {
            if (!file.exists()) {
                return size;
            }

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    size += getFileNumber(files[i]);
                }
            } else {
                size = 1;
            }
        } catch (Exception ex) {

        }
        return size;
    }

    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    public static boolean isExist(String fileName) {
        try {
            if (fileName == null || fileName.equals("")) {
                return false;
            }
            File file = new File(fileName);
            if (file.exists() && file.isFile()) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 从指定的文件路径中加载字符串文件返回
     * @param filePath 文件的路径
     * @return
     * @throws IOException
     */
    public static String getStringFromPath(String filePath) throws IOException{
        StringBuffer bf = new StringBuffer();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath));
        BufferedReader bufferedReader = new BufferedReader(reader);
        String text = "";
        while( (text = bufferedReader.readLine()) != null){
            bf.append(text);
        }
        reader.close();
        return bf.toString();
    }

    /**
     * 读取断点文件进流中
     * @param filePath
     * @param out
     * @throws IOException
     */
    public static boolean readFile(String filePath, FileOutputStream out) throws IOException {
        boolean result = false;
        try{
            DataInputStream in = new DataInputStream(new FileInputStream(filePath));
            int bytes = 0;
            byte[] buffer = new byte[1024];
            while ((bytes = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytes);
            }
            out.flush();
            in.close();

	        /*//删除文件
	        File file = new File(filePath);
	        if(file.exists())
	        	file.deleteOnExit();*/

            result = true;
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取网络图片的宽和高以及大小
     * @param imgUrl
     * @return 返回数组，依次是：宽，高，大小
     */
    public static Map<String, Object> saveNetWorkLinkToFile(Context context, String imgUrl) {

        Map<String, Object> b = new HashMap<>();
        b.put("isSuccess", false);
        if(StringUtil.isNotNull(imgUrl) && ImageUtil.isSupportType(StringUtil.getFileName(imgUrl))){

            StringBuffer saveFilePath = new StringBuffer();
            File ff = FileUtil.getInstance().getCacheDir(BaseApplication.newInstance());
            ff = new File(ff.getAbsolutePath() + File.separator + "file");
            if(!ff.exists())
                ff.mkdirs();

            saveFilePath.append(ff.getAbsolutePath());
            saveFilePath.append(File.separator);
            saveFilePath.append(StringUtil.getFileName(imgUrl));
            File saveFile = new File(saveFilePath.toString());
            if(!saveFile.exists()){
                try {
                    InputStream inputStream = HttpConnectionUtil.getInputStream(imgUrl);
                    if(inputStream != null){
                        //载入图片到输入流
                        BufferedInputStream bis = new BufferedInputStream(inputStream);
                        //实例化存储字节数组
                        byte[] bytes = new byte[1024];
                        //设置写入路径以及图片名称
                        OutputStream bos = new FileOutputStream(saveFile);
                        int len;
                        while ((len = bis.read(bytes)) > 0) {
                            bos.write(bytes, 0, len);
                        }
                        inputStream.close();
                        bis.close();
                        bos.flush();
                        bos.close();
                        //关闭输出流
                        b.put("message", "文件保存成功，路径是:"+StringUtil.getFileName(imgUrl));
                        b.put("isSuccess", true);
                        b.put("path", saveFilePath.toString());
                    }else{
                        b.put("message", "文件下载异常");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //如果图片未找到
                    b.put("message", "该地址不是正确的图片路径");
                }
            }else{
                b.put("message", "文件已经存在");
                b.put("isSuccess", true);
                b.put("path", saveFilePath.toString());
            }
        }else{
            b.put("message", "文件链接为空");
        }
        return b;

    }
}
