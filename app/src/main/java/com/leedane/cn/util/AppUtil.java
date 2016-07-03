package com.leedane.cn.util;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor actualimagecursor = activity.managedQuery(imageUri, proj, null, null, null);
                        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        actualimagecursor.moveToFirst();
                        String img_path = actualimagecursor.getString(actual_image_column_index);
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
                            String[] proj = {MediaStore.Images.Media.DATA};
                            Cursor actualimagecursor = activity.managedQuery(imageUris.get(i), proj, null, null, null);
                            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            actualimagecursor.moveToFirst();
                            String img_path = actualimagecursor.getString(actual_image_column_index);
                            picPaths.add(img_path);
                        }
                    }
                }
            }
        }

        return picPaths;
    }
}
