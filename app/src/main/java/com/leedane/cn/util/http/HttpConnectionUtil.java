package com.leedane.cn.util.http;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import com.leedane.cn.bean.DownloadBean;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.FileUtil;
import com.leedane.cn.util.StringUtil;

/**
 * Created by LeeDnae on 2015/10/7.
 */
public class HttpConnectionUtil {

    public static final String TAG = "HttpConnectionUtil";
    private static Context mContext;

    private static Gson gson = new Gson();

    private HttpConnectionUtil(){

    }

    /**
     * 设置应用上下文，在BaseApplication中进行设置
     */
    public static void setApplicationContext(Context context){
        mContext = context;
    }

    /**
     * 发送get请求
     * @param serverPath
     * @return
     * @throws IOException
     */
    public static String sendGetRequest(String serverPath) throws IOException {
        return sendGetRequest(serverPath, 0, 0);
    }

    /**
     * 发送get请求
     * @param serverPath
     * @param requestTimeOut
     * @param responseTimeOut
     * @return
     * @throws IOException
     */
    public static String sendGetRequest(String serverPath, int requestTimeOut, int responseTimeOut) throws IOException {
        InputStream is = getGetRequestInputStream(serverPath, requestTimeOut, responseTimeOut);
        if(is != null){
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            //存储单行文本的数据
            String line;
            //保存请求返回的所有文本数据
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine())!= null){
                buffer.append(line);
            }

            //关闭相关的流
            if(is != null) is.close();
            if(reader != null) reader.close();
            return buffer.toString();
        }
        return null;
    }

    /**
     * 发送get请求获取输入流对象
     * @param serverPath
     * @param requestTimeOut
     * @param responseTimeOut
     * @return
     * @throws IOException
     */
    public static InputStream getGetRequestInputStream(String serverPath, int requestTimeOut, int responseTimeOut) throws IOException {
        URL url = new URL(serverPath);
        //打开对服务器的连接
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        //设置连接超时时间
        urlConnection.setConnectTimeout(requestTimeOut > 0 ? requestTimeOut : com.leedane.cn.util.ConstantsUtil.DEFAULT_REQUEST_TIME_OUT);
        //设置读取超时时间
        urlConnection.setReadTimeout(responseTimeOut > 0 ? responseTimeOut : com.leedane.cn.util.ConstantsUtil.DEFAULT_RESPONSE_TIME_OUT);

        //设置允许输入，输出
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        //设置请求模式为GET
        urlConnection.setRequestMethod("GET");
        //连接服务器
        urlConnection.connect();
        InputStream is = null;
        //响应成功
        if(urlConnection.getResponseCode() == com.leedane.cn.util.ConstantsUtil.RESPONSE_CODE_SUCCESS){
            is = urlConnection.getInputStream();
        }

        //断开连接
       // if(urlConnection != null) urlConnection.disconnect();
        return is;
    }

    /**
     * 发送POST请求
     * @param httpRequest
     * @return
     * @throws IOException
     */
    public static String sendPostRequest(HttpRequestBean httpRequest) throws IOException {
        if(httpRequest == null || StringUtil.isNull(httpRequest.getServerMethod())){
            return null;
        }
        String serverPath = "";
        String serverMethod = httpRequest.getServerMethod();

        if(serverMethod.startsWith("/")){
            serverMethod = serverMethod.substring(1, serverMethod.length());
        }
        if(com.leedane.cn.util.StringUtil.isNull(httpRequest.getUrl())){
            serverPath = com.leedane.cn.util.ConstantsUtil.DEFAULT_SERVER_URL + serverMethod;
        }else{
            String url = httpRequest.getUrl();
            if(url.endsWith("/")){
                url = url.substring(0, (url.length() -1));
            }
            serverPath = url + serverMethod;
        }
        return sendPostRequest(serverPath, httpRequest.getParams(), httpRequest.getRequestTimeOut(), httpRequest.getResponseTimeOut());
    }

    /**
     * 发送POST请求
     * @param serverPath
     * @param params
     * @param requestTimeOut
     * @param responseTimeOut
     * @return
     * @throws IOException
     */
    public static String sendPostRequest(String serverPath, Map<String, Object> params, int requestTimeOut, int responseTimeOut) throws IOException {
        InputStream is =  getPostRequestInputStream(serverPath, params, requestTimeOut, responseTimeOut);
        if(is != null){
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            //存储单行文本的数据
            String line;
            //保存请求返回的所有文本数据
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine())!= null){
                buffer.append(line);
            }
            //关闭相关的流
            if(is != null) is.close();
            if(reader != null) reader.close();
            return buffer.toString();
        }
        return null;
    }

    /**
     * 发送POST请求获得输入流
     * @param serverPath
     * @param params
     * @param requestTimeOut
     * @param responseTimeOut
     * @return
     * @throws IOException
     */
    public static InputStream getPostRequestInputStream(String serverPath, Map<String, Object> params, int requestTimeOut, int responseTimeOut) throws IOException {
        URL url = new URL(serverPath);
        //打开对服务器的连接
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        //设置连接超时时间
        urlConnection.setConnectTimeout(requestTimeOut > 0 ? requestTimeOut : ConstantsUtil.DEFAULT_REQUEST_TIME_OUT);
        //设置读取超时时间
        urlConnection.setReadTimeout(responseTimeOut > 0 ? responseTimeOut : ConstantsUtil.DEFAULT_RESPONSE_TIME_OUT);

        //设置允许输入，输出
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        //设置头部信息
        urlConnection.setRequestProperty("headerdata", "leedaneheader");
        //一定要设置 Content-Type 要不然服务端接收不到参数
        urlConnection.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");

        //设置请求模式为GET
        urlConnection.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);

        //post不能缓存用户数据，所有动态设置
        urlConnection.setUseCaches(false);

        urlConnection
                .setRequestProperty(
                        "User-Agent",
                        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/7.0; SLCC2; "
                                + ".NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET4.0C; .NET4.0E)");
        /*urlConnection.setRequestProperty("Content-Length",
                String.valueOf(gson.toJson(httpRequest.getParams()).getBytes().length));*/

        //连接服务器
        urlConnection.connect();

        OutputStream os = urlConnection.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);

        //以utf-8处理数据的编码方式
        dos.writeBytes(URLEncoder.encode(gson.toJson(params), "UTF-8"));
        //刷新提交数据
        dos.flush();
        if(os != null) os.close();
        if(dos != null) dos.close();

        InputStream is = null;
        int code = urlConnection.getResponseCode();
        //响应成功
        if(urlConnection.getResponseCode() == ConstantsUtil.RESPONSE_CODE_SUCCESS){
            is = urlConnection.getInputStream();
        }
        //断开连接
        //if(urlConnection != null) urlConnection.disconnect();
        return is;
    }

    /**
     * 发送文件下载get请求
     * @param downloadBean
     * @return
     * @throws IOException
     */
    public static String sendGetRequest(DownloadBean downloadBean) throws IOException {
        URL url = new URL(downloadBean.getUrl());
        Log.i(TAG, "下载请求地址"+downloadBean.getUrl());
        //打开对服务器的连接
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        //设置连接超时时间
        urlConnection.setConnectTimeout(ConstantsUtil.DEFAULT_REQUEST_TIME_OUT);
        //设置读取超时时间
        urlConnection.setReadTimeout(ConstantsUtil.DEFAULT_RESPONSE_TIME_OUT);

        //文件下载位置   规定的格式 “byte=xxxx-”
        String range = "bytes="+downloadBean.getStart();
        Log.i(TAG, "range:"+ range);
        //设置文件开始的下载位置  使用 Range字段设置断点续传
        urlConnection.setRequestProperty("Range", range);

        //设置允许输入，输出
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        //设置请求模式为GET
        urlConnection.setRequestMethod("GET");
        //连接服务器
        urlConnection.connect();

        Log.i(TAG, "返回的请求码:" + urlConnection.getResponseCode());
        //响应成功
        if(urlConnection.getResponseCode() == ConstantsUtil.RESPONSE_CODE_DOWNLOAD_SUCCESS){
            InputStream is = urlConnection.getInputStream();
            try{
                String location = FileUtil.getInstance().getCacheDir(mContext) + "/file/" ;
                Log.i(TAG, "本地文件的路径是:" + location);
                File f = new File(location);
                if(!f.exists()){
                    f.mkdirs();
                }

                File fi = new File(location + downloadBean.getFilename());
                if(!fi.exists()){
                    fi.createNewFile();
                }
                FileOutputStream os = new FileOutputStream(fi);
                int len = 0;
                byte[] byt = new byte[1024];
                while((len = is.read(byt)) > 0){
                    os.write(byt, 0, len);
                }
                os.flush();
                os.close();
                is.close();
                Log.i(TAG, "下载请求执行成功");
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        //断开连接
        if(urlConnection != null) urlConnection.disconnect();
        return null;
    }

    /**
     * 获取网络图片的InputStream流
     * @param imgUrl
     * @return
     */
    public static InputStream getInputStream(String imgUrl){
        if(StringUtil.isNull(imgUrl))
            return null;

        URL url = null;
        try {
            url = new URL(imgUrl);
            URLConnection uc = url.openConnection();
            uc.setConnectTimeout(60000);
            uc.setDoInput(true);
            uc.setDoInput(true);
            uc.setReadTimeout(30000);
            return uc.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
