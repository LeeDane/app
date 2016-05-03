package com.leedane.cn.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * BitMap相关的工具类
 * Created by LeeDane on 2016/4/12.
 */
public class BitmapUtil {

    /**
     * 计算图片的缩放值
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示(默认展示的宽高是320x400)
     * @param context
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(Context context, String filePath) {
        return getSmallBitmap(context, filePath, 320, 400);
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     * @param context
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getSmallBitmap(Context context, String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //Toast.makeText(context, "宽："+options.outWidth+",高："+options.outHeight, Toast.LENGTH_LONG).show();
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 把bitmap转换成String
     * @param context
     * @param filePath
     * @return
     */
    public static String bitmapToString(Context context, String filePath) {
        Bitmap bm = getSmallBitmap(context, filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * 把bitmap保存到本地路径
     * @param bitmap
     * @param filePath
     * @return
     */
    public static boolean bitmapToLocalPath(Bitmap bitmap, String filePath) {
        boolean result = false;
        FileOutputStream  fos = null;
        try{
            if(bitmap != null){
                File f = new File(filePath);
                f.createNewFile();
                fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                result = true;
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 回收bitmap
     * @param bitmap
     */
    public static void recycled(Bitmap bitmap){
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            System.gc();
            bitmap = null;
        }
    }
}
