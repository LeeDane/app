package com.leedane.cn.task;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.leedane.cn.impl.UserPicCallBack;
import com.leedane.cn.util.ImageCache;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 个人中心心情图像加载器
 * Created by LeeDane on 2015/12/8.
 */
public class MoodUserImageLoader {
    public static final String TAG = "MoodUserImageLoader";
    private Context mContext;
    public MoodUserImageLoader(Context context){
        this.mContext  = context;
    }

    private ImageCache mImageCache = ImageCache.getInstance();
    //线程池，线程数量为cpu的数量
    ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.
            getRuntime().availableProcessors());

    /**
     * 查找用户的头像
     * @param userId
     * @param userPic
     * @param callBack
     */
    public void findUserPic(final int userId, final ImageView userPic, final UserPicCallBack callBack) {

        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                userPic.setImageBitmap((Bitmap)message.obj);
            }
        };
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = mImageCache.getUserPicBitmap(userId);
                if(bitmap != null){
                    Message message = handler.obtainMessage(0, bitmap);
                    handler.sendMessage(message);
                }
            }
        }.start();
    }
}
