package com.leedane.cn.util;

import android.os.Build;

/**
 * Created by Administrator on 2015/10/11.
 */
public class AppUtil {

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
}
