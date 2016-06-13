package com.leedane.cn.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
     * 获取指定路径下的图片文件
     * @param pathString
     * @return
     */
    public static Bitmap getDiskBitmap(String pathString){
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if(file.exists())
            {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将文件生成位图
     * @param path
     * @return
     * @throws IOException
     */
    public static BitmapDrawable getImageDrawable(String path)
            throws IOException
    {
        //打开文件
        File file = new File(path);
        if(!file.exists())
        {
            return null;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] bt = new byte[1024];

        //得到文件的输入流
        InputStream in = new FileInputStream(file);

        //将文件读出到输出流中
        int readLength = in.read(bt);
        while (readLength != -1) {
            outStream.write(bt, 0, readLength);
            readLength = in.read(bt);
        }

        //转换成byte 后 再格式化成位图
        byte[] data = outStream.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// 生成位图
        BitmapDrawable bd = new BitmapDrawable(bitmap);

        return bd;
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
