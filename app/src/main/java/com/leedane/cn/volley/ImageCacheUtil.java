package com.leedane.cn.volley;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.util.MD5Util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图像缓存帮助类
 * Created by LeeDane on 2015/12/24.
 */
public class ImageCacheUtil implements ImageLoader.ImageCache {

    private final static String TAG = "ImageCacheUtil";

    //缓存类
    private static LruCache<String, Bitmap> mLruCache;
    private static DiskLruCache mDiskLruCache;

    //定义磁盘的缓存大小
    private static final int DISK_MAX_SIZE = 80 * 1024 * 1024;

    public ImageCacheUtil(){
        //获取应用可占内存的1/4作为缓存
        int maxSize = (int)(Runtime.getRuntime().maxMemory()/4);

        //实例化LruCache对象
        mLruCache = new LruCache<String, Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };

        try {
            //获取DiskLruCache对象
            mDiskLruCache = DiskLruCache.open(getDiskCacheDir(BaseApplication.newInstance(), "volley"), getAppVersion(BaseApplication.newInstance()), 1, DISK_MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断当前sd卡是否存在，然后选择缓存路径
     * @param context
     * @param uniqueName
     * @return
     */
    private static File getDiskCacheDir(Context context, String uniqueName){
        String cachePath; //缓存的路径
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()){
            cachePath = context.getExternalCacheDir().getPath();
        }else{
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取应用的版本号
     * @param context
     * @return
     */
    private int getAppVersion(Context context){
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionCode;
    }

    /**
     * 从缓存(内存缓存，磁盘缓存)中获取Bitmap
     * @param url
     * @return
     */
    @Override
    public Bitmap getBitmap(String url) {
        if(mLruCache.get(url) != null){
            Log.i(TAG, "从LruCache内存缓存中获取Bitmap");
            return mLruCache.get(url);
        }else{
            //由于使用磁盘缓存，为了安全，需要对图像的路径进行md5加密
            String key = MD5Util.md5(url);
            Log.i(TAG, "---->" +url +"经md5加密后的字符串："+key);
            try {
                if(mDiskLruCache.get(key) != null){
                    //从DiskLruCache取缓存
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if(snapshot != null){
                        Log.i(TAG, "从DiskLruCache缓存获取");
                        Bitmap bitmap = null;
                        try{
                            bitmap  = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                        }catch(Exception e){
                            Log.i(TAG, "从缓存获取bitmap失败:->"+url);
                        }
                        //获取到bitmap后把它存进LruCache缓存
                        mLruCache.put(url, bitmap);
                        return bitmap;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "没有找到缓存的Bitmap");
        return null;
    }

    /**
     * 存入缓存（内存缓存，磁盘缓存）
     * @param url
     * @param bitmap
     */
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        //存入LruCache缓存
        mLruCache.put(url, bitmap);

        //判断是否存在DiskLru缓存，若没有就存入
        String key = MD5Util.md5(url);
        try {
            if(mDiskLruCache.get(key) == null){
                Log.i(TAG, "没有DiskLruCache还没有缓存："+url);
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                if(editor != null){
                    OutputStream outputStream = editor.newOutputStream(0);
                    if(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)){
                        editor.commit();
                    }else{
                        editor.abort();
                    }
                }
                mDiskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
