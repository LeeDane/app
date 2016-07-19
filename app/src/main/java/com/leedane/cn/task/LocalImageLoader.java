package com.leedane.cn.task;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.leedane.cn.util.ImageCache;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地图片的加载器
 * Created by LeeDane on 2015/12/4.
 */
public class LocalImageLoader {
    public static final String TAG = "LocalImageLoader";
    private Context mContext;
    public LocalImageLoader(Context context){
        this.mContext  = context;
    }

    private ImageCache mImageCache = ImageCache.getInstance();
    //线程池，线程数量为cpu的数量
    ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.
            getRuntime().availableProcessors());
    /**
     * 加载BitMap
     * @param imageTag 保存图像的标签
     * @param imageUri
     * @param imageCallback
     */
    public void loadBitmap(final String imageTag, final Uri imageUri, final NetworkImageLoader.ImageCallback imageCallback) {
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
                    Log.i(TAG, "从缓存获取本地图片成功" + imageTag);
                    Message message = handler.obtainMessage(0, bitmap);
                    handler.sendMessage(message);
                }else{
                    ContentResolver cr = mContext.getContentResolver();
                    try {
                        Bitmap bmp = BitmapFactory.decodeStream(cr.openInputStream(imageUri));
                        if (bmp != null) {
                            Log.i(TAG, "从SD卡获取图片成功,TAG:"+imageTag);
                            mImageCache.putBitmap(imageTag, bmp);
                            Message message = handler.obtainMessage(0, bmp);
                            handler.sendMessage(message);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
