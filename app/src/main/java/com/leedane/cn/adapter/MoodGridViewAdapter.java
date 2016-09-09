package com.leedane.cn.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.task.LocalImageLoader;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.StringUtil;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.List;

/**
 *显示心情列表的适配器
 * Created by LeeDane on 2015/12/3.
 */
public class MoodGridViewAdapter extends BaseListAdapter<String>{
    /**
     * SD卡图片读取
     */
    private LocalImageLoader mLocalImageLoader;

    public MoodGridViewAdapter(Context context, List<String> localUris){
        super(context, localUris);
        mLocalImageLoader = new LocalImageLoader(mContext);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        MyHolder myHolder;
        if(view == null){
            myHolder = new MyHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_mood_gridview, null);
            myHolder.imageView = (ImageView) view.findViewById(R.id.mood_item_img);
            view.setTag(myHolder);
        }else{
            myHolder = (MyHolder)view.getTag();
        }
        String uri = mDatas.get(position);

        if(StringUtil.isNull(uri)){
            Toast.makeText(mContext, "图片路径为空", Toast.LENGTH_SHORT).show();
        }else{
            //String imageTag = uri.getPath();
            //myHolder.getImageView().setTag(imageTag);
            final ImageView imageView = myHolder.imageView;

            //getBitMap(mUrls.get(position));
            Bitmap bitmap = BitmapUtil.getSmallBitmap(mContext, uri, 480, 800);
            imageView.setImageBitmap(bitmap);


            final BadgeView badge = new BadgeView(mContext, imageView);
            badge.setText("—");
            badge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.take_photo));
                    mDatas.remove(position);
                    notifyDataSetChanged();
                    badge.hide();
                }
            });
            badge.show();
            /*mLocalImageLoader.loadBitmap(imageTag, uri, new NetworkImageLoader.ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageBitmap, String imageTag) {
                    if(imageBitmap != null)
                        imageView.setImageBitmap(imageBitmap);
                    else
                        Toast.makeText(mContext, "图片读取失败", Toast.LENGTH_SHORT).show();
                }
            });*/
        }
        return view;
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     * @param uri
     * @return
     */
    /*private Bitmap getBitMap(String uri){
        Bitmap bitmap = null;
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;//图片宽高都为原来的二分之一，即图片为原来的四分之一
            ContentResolver cr = mContext.getContentResolver();
            bitmap = BitmapFactory.decodeStream(cr
                    .openInputStream(uri), null, options);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return bitmap;
    }*/

    /**
     * 以最省内存的方式读取本地资源的图片
     * @param imagePath
     * @param screenWidth
     * @param screenHight
     * @return
     */
    public Bitmap optionsBitmapSize(String imagePath, int screenWidth, int screenHight) {

        // 设置解析图片的配置信息
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置为true 不再解析图片 只是获取图片的头部信息及宽高
        options.inJustDecodeBounds = true;
        // 返回为null
        BitmapFactory.decodeFile(imagePath, options);
        // 获取图片的宽高
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        // 计算缩放比例
        int scaleWidth = imageWidth / screenWidth;
        int scaleHeight = imageHeight / screenHight;
        // 定义默认缩放比例为1
        int scale = 1;
        // 按照缩放比例大的 去缩放
        if (scaleWidth > scaleHeight & scaleHeight >= 1) {
            scale = scaleWidth;
        } else if (scaleHeight > scaleWidth & scaleWidth >= 1) {
            scale = scaleHeight;
        }
        // 设置为true开始解析图片
        options.inJustDecodeBounds = false;
        // 设置图片的采样率
        options.inSampleSize = scale;
        // 得到按照scale缩放后的图片
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        return bitmap;
    }

    static class MyHolder{
        ImageView imageView;
    }
}
