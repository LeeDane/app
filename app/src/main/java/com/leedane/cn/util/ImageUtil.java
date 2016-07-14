package com.leedane.cn.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.volley.ImageCacheManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 将图像添加到Linnear中
     * @param context
     * @param imgs
     * @param linearLayout
     */
    public static void addImages(final Context context, String imgs, LinearLayout linearLayout){
        //异步去获取该心情的图像路径列表
        if(!StringUtil.isNull(imgs)) {
            final String[] showImages = imgs.split(";");
            if(showImages.length > 0){
                linearLayout.setVisibility(View.VISIBLE);
                linearLayout.removeAllViewsInLayout();
                ImageView imageView = null;
                /*android:id="@+id/personal_mood_img_main3"
                android:layout_width="80dp"
                android:layout_height="100dp"
                android:maxWidth="100dp"
                android:maxHeight="120dp"
                android:scaleType="fitXY"
                android:layout_marginTop="@dimen/default_3dp"
                android:src="@drawable/no_pic"/>*/
                final List<ImageDetailBean> list = new ArrayList<>();
                ImageDetailBean imageDetailBean = null;
                for(int i = 0; i< showImages.length; i++){
                    imageDetailBean = new ImageDetailBean();
                    imageDetailBean.setPath(showImages[i]);
                    list.add(imageDetailBean);
                }
                for(int i = 0; i< showImages.length; i++){
                    final int index = i;
                    final String path = showImages[index];
                    if(StringUtil.isNotNull(showImages[i])){
                        imageView = new ImageView(context);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 80) , DensityUtil.dip2px(context, 100));
                        params.rightMargin = 16;
                        params.topMargin = 3;
                        imageView.setLayoutParams(params);
                        imageView.setMaxWidth(100);
                        imageView.setMaxHeight(120);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        ImageCacheManager.loadImage(path, imageView, 80, 100);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommonHandler.startImageDetailActivity(context, list, index);
                            }
                        });
                        linearLayout.addView(imageView);
                    }
                }
                return;
            }
        }
        linearLayout.setVisibility(View.GONE);
    }

    /**
     * 判断是否是支持的类型
     * @param fileName
     * @return
     */
    public static boolean isSupportType(String fileName){
        //获取文件的后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1 , fileName.length());
        for(String supportSuffix : ConstantsUtil.SUPPORTIMAGESUFFIXS){
            //判断是否在支持的类型里面
            if(supportSuffix.equalsIgnoreCase(suffix)) return true;
        }

        System.out.println("该文件不是目前系统支持的类型");
        return false;
    }
}
