package com.leedane.cn.util;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.Service;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/11.
 */
public class AppUtil {
    public static final String TAG = "AppUtil";

    /**
     * 获取当前android的sdk的版本
     * @return
     */
    public static int getAndroidSDKVersion(){
        int version = 0;
        try {
            version = Integer.valueOf(Build.VERSION.SDK_INT);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 接收外部分享的图片
     * @param activity
     * @return
     */
    public static List<String> getListPicPaths(Activity activity){
        List<String> picPaths = new ArrayList<>();
        Intent intent = activity.getIntent();//如果是从外部进入APP，则实现以下方法
        if(Intent.ACTION_SEND.equals(intent.getAction())){
            if(intent.getType().startsWith("image/")){
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if(imageUri != null){
                    //处理单张照片
                    Log.i(TAG, "外部分享到本app的地址："+imageUri.getPath());
                    if(!imageUri.getPath().contains("external/images/media"))
                        picPaths.add(imageUri.getPath());
                    else {
                        String img_path = MediaUtil.getImageAbsolutePath(activity, imageUri);
                        picPaths.add(img_path);
                    }
                }
            }
        }else if(Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())){
            if(intent.getType().startsWith("image/")){
                List<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                if(imageUris != null){
                    //处理多张图片
                    for(int i =0; i < imageUris.size(); i++){
                        Log.i(TAG, "外部分享到本app的地址："+imageUris.get(i).getPath());
                        if(!imageUris.get(i).getPath().contains("external/images/media"))
                            picPaths.add(imageUris.get(i).getPath());
                        else {
                            String img_path = MediaUtil.getImageAbsolutePath(activity, imageUris.get(i));
                            picPaths.add(img_path);
                        }
                    }
                }
            }
        }

        return picPaths;
    }

    /**
     * 从uri中获取文件的真实路径
     * @param activity
     * @param uri
     * @return
     */
    /*public static String getPathByUri(Activity activity, Uri uri){
        String imagePath = uri.getPath();
        if(imagePath.contains("external/images/media")){
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            imagePath = actualimagecursor.getString(actual_image_column_index);
        }

        return imagePath;
    }*/

    /**
     * 手机设置震动
     * @param context
     * @param milliSeconds 振动时长，单位是毫秒
     */
    public static void vibrate(final Context context, long milliSeconds){
        Vibrator vib = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliSeconds);
    }

    /**
     * 手机设置震动
     * @param context
     * @param pattern 自定义振动模式，数组中的数字含义依次是【静止时长，振动时长，静止时长，振动时长。。。】时长的单位是毫秒
     * @param isRepeat 是否反复振动，如果是true，反复振动，如果是false,只振动一次
     */
    public static void vibrate(final Context context, long[] pattern, boolean isRepeat){
        Vibrator vib = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat? 1: -1);
    }

}
