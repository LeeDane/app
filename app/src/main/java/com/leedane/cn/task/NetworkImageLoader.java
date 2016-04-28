package com.leedane.cn.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.leedane.cn.bean.HttpResponseCommonBean;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.ImageCache;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.http.HttpConnectionUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 网络图片的加载器
 * Created by LeeDane on 2015/10/27.
 */
public class NetworkImageLoader {
    /***
     * 强制从互联网拉去数据，不再从缓存中获取
     */
    //private boolean mForceLoadFromInternet;

     public static final String TAG = "NetworkImageLoader";

    private ImageCache mImageCache = ImageCache.getInstance();
    //线程池，线程数量为cpu的数量
    ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.
            getRuntime().availableProcessors());

    /**
     * 显示网络下载GET请求ImageView
     * @param imageTag 保存图像的标签
     * @param imageUrl
     * @param imageCallback
     * @param method 请求方式，get或者post请求
     * @param params 请求参数，可以为空
     */
    public void loadNetBitmap(final String imageTag, final String imageUrl, final ImageCallback imageCallback, final String method, final Map<String, Object> params) {

        Log.i(TAG, "请求的方式是--->"+ method);
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, imageTag);
            }
        };
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = mImageCache.getBitmap(imageTag);
                if(bitmap != null){
                    Log.i(TAG, "从缓存获取图片成功" + imageTag);
                    Message message = handler.obtainMessage(0, bitmap);
                    handler.sendMessage(message);
                }else{

                    //执行POST请求
                    if(ConstantsUtil.REQUEST_METHOD_POST.equalsIgnoreCase(method)){
                        bitmap = downloadPOSTImage(imageUrl, params);
                    }else{
                        bitmap = downloadGETImage(imageUrl);
                    }

                    if (bitmap != null) {
                        Log.i(TAG, "从网络获取图片成功,TAG:"+imageTag);
                        mImageCache.putBitmap(imageTag, bitmap);
                        Message message = handler.obtainMessage(0, bitmap);
                        handler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    /**
     * 显示下载base64ImageView
     * @param imageTag  图像保存的标签
     * @param imageUrl
     * @param imageCallback
     */
    public void loadBase64Bitmap(final String imageTag, final String imageUrl, final ImageCallback imageCallback) {
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, imageTag);
            }
        };
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = mImageCache.getBitmap(imageTag);
                if(bitmap != null){
                    Log.i(TAG, "从缓存获取本地图片成功,TAG:" + imageTag);
                    Message message = handler.obtainMessage(0, bitmap);
                    handler.sendMessage(message);
                }else{
                    try {
                        bitmap = ImageUtil.getInstance().getBitmapByBase64(imageUrl);
                    } catch (Exception e) {
                        Log.i(TAG, "从base64解析获取图片失败");
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        Log.i(TAG, "从base64解析获取图片成功");
                        mImageCache.putBitmap(imageTag, bitmap);
                        Message message = handler.obtainMessage(0, bitmap);
                        handler.sendMessage(message);
                    }
                }
            }
        }.start();
    }

    /**
     * 执行真正的下载文件的任务(GET)请求
     * @param imageUrl
     * @return
     */
    private Bitmap downloadGETImage(String imageUrl){
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            Log.i(TAG, "请求网络图片的地址是："+imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 执行真正的下载文件的任务(POST)请求
     * @param imageUrl
     * @return
     */
    private Bitmap downloadPOSTImage(String imageUrl, Map<String, Object> params){
        Bitmap bitmap = null;
        try {

            //Log.i(TAG, "请求网络图片的地址是："+imageUrl);
            String resultStr = HttpConnectionUtil.sendPostRequest(imageUrl, params, 3000, 3000);
            HttpResponseCommonBean commonBean = BeanConvertUtil.strConvertToCommonBeans(resultStr);
            //Log.i(TAG, "成功是否：" + commonBean.isSuccess());
            if(commonBean.isSuccess() == true){
                String msg = commonBean.getMessage();
                bitmap = ImageUtil.getInstance().getBitmapByBase64(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public interface ImageCallback {
        public void imageLoaded(Bitmap imageBitmap, String imageTag);
    }
}
