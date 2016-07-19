package com.leedane.cn.util;

import android.content.Context;

/**
 * 系统工具类
 * Created by LeeDane on 2015/11/7.
 */
public class SystemUtil {

    private static SystemUtil mSystemUtil;
    private  SystemUtil(){}


    public static synchronized SystemUtil getInstance(){
        if(mSystemUtil == null){
            mSystemUtil = new SystemUtil();
        }
        return mSystemUtil;
    }

    /**
     * 获取状态栏的高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context){
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
