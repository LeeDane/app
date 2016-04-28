package com.leedane.cn.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图像工具类
 * Created by Administrator on 2015/10/16.
 */
public class ImageUtil {

    public static final String TAG = "ImageUtil";
    private ImageUtil(){

    }
    private static ImageUtil mImageUtil;

    /**
     * 获取实例化对象
     * @return
     */
    public static synchronized ImageUtil getInstance(){
        if(mImageUtil == null){
            mImageUtil = new ImageUtil();
        }
        return mImageUtil;
    }


    /**
     * 将base64位字符串转成BitMap对象
     * @param base64Str
     * @return
     */
    public Bitmap getBitmapByBase64(String base64Str) throws Exception{
        if(StringUtil.isNull(base64Str)) return null;

        if(base64Str.startsWith("data:image/jpeg;base64,")) {
            base64Str = base64Str.substring("data:image/jpeg;base64,".length(), base64Str.length());
            //Log.i(TAG, "对jpeg图片进行切割");
        }else if(base64Str.startsWith("data:image/jpg;base64,")){
            base64Str = base64Str.substring("data:image/jpg;base64,".length(), base64Str.length());
            //Log.i(TAG, "对jpg图片进行切割");
        }
       // Log.i(TAG, "处理后的字符串:" + base64Str);
        byte[] decodedString = Base64.decode(base64Str, Base64.DEFAULT);
        //Log.i(TAG, "图片:" + decodedString.length);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

    }

    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void destoryBimap(Bitmap bitmap){
        if (bitmap !=null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
