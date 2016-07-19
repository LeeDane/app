package com.leedane.cn.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 图像缓存
 * Created by LeeDane on 2015/10/27.
 */
public class ImageCache {

    //实例
    private static ImageCache mImageCache;

    private LruCache<String, Bitmap> mLruCache;
    private ImageCache(){
        //初始化对象
        init();
    }

    /**
     * 初始化对象
     */
    private void init() {
        //计算可使用的最大内存?
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    /**
     * 获取实例
     * @return
     */
    public static ImageCache getInstance(){
        if(mImageCache == null){
            synchronized (ImageCache.class){
                if(mImageCache == null){
                    mImageCache = new ImageCache();
                }
            }
        }
        return mImageCache;
    }

    public void putBitmap(String key, Bitmap bitmap){
        if(mLruCache.get(key) == null && bitmap != null)
            mLruCache.put(key, bitmap);
    }

    public Bitmap getBitmap(String key){
        return mLruCache.get(key);
    }

    public void putUserPicBitmap(int userId, Bitmap bitmap){
        String key = "user_pic_" + userId;
        if(mLruCache.get(key) == null && bitmap != null)
            mLruCache.put(key, bitmap);
    }

    public Bitmap getUserPicBitmap(int userId){
        String key = "user_pic_" + userId;
        return mLruCache.get(key);
    }



}
