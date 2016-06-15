package com.leedane.cn.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.task.NetworkImageLoader;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 多图像详情的frament类
 * Created by LeeDane on 2015/11/16.
 */
public class ImageDetailFragment extends Fragment{

    public static final String TAG = "ImageDetailFragment";
    private Context mContext;
    private int mCurrent;
    private ImageDetailBean mImageDetailBean;

    private NetworkImageLoader mNetworkImageLoader;
    private int acreenWidth;
    private int screenHeight;

    public ImageDetailFragment(){
        mNetworkImageLoader = new NetworkImageLoader();
        int[] widthAndHeight = BaseApplication.newInstance().getScreenWidthAndHeight();
        this.acreenWidth = widthAndHeight[0];
        this.screenHeight = widthAndHeight[1]*3/4;
    }

    /*public ImageDetailFragment(int current, Context context, String currentImageUrl, int width, int height){
        this.mNetworkImageLoader = new NetworkImageLoader();
        int[] widthAndHeight = BaseApplication.newInstance().getScreenWidthAndHeight();
        if(width > 0){
            this.acreenWidth = width;
        }else{
            this.acreenWidth = widthAndHeight[0];
        }
        if(height >0){
            this.screenHeight = height;
        }else{
            this.screenHeight = widthAndHeight[1]*3/4;
        }
    }*/

    /**
     * 构建Frament对象
     * @param current 当前frament是第几个
     * @param context
     */
    public ImageDetailFragment(int current, Context context, ImageDetailBean imageDetailBean){
        this.mContext = context;
        this.mCurrent = current;
        this.mImageDetailBean = imageDetailBean;
        this.mNetworkImageLoader = new NetworkImageLoader();
        int[] widthAndHeight = BaseApplication.newInstance().getScreenWidthAndHeight();
        this.acreenWidth = widthAndHeight[0];
        this.screenHeight = widthAndHeight[1]*3/4;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_detail, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String currentImageUrl = mImageDetailBean.getPath();
        int width = mImageDetailBean.getWidth() == 0 ||  mImageDetailBean.getWidth() > acreenWidth ? acreenWidth: mImageDetailBean.getWidth();
        int height = mImageDetailBean.getHeight() == 0 ||  mImageDetailBean.getHeight() > screenHeight ? screenHeight: mImageDetailBean.getHeight();
        if(currentImageUrl.startsWith("http://") || currentImageUrl.startsWith("https://")){
           //ImageCacheManager.loadImage(currentImageUrl, (ImageView) getView().findViewById(R.id.image_detail_imageview), width, height);
            SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)getView().findViewById(R.id.image_detail_imageview);
            ImageCacheManager.loadImage(currentImageUrl, imageView, width, height);
        }else{
            Map<String, Object> map = new HashMap<>();

            //请求的文件的名称(这里是图片)
            map.put("filename", currentImageUrl);
            //获取所有的请求基本参数
            map.putAll(BaseApplication.newInstance().getBaseRequestParams());
            String imageUrl = SharedPreferenceUtil.getSettingBean(mContext, ConstantsUtil.STRING_SETTING_BEAN_SERVER).getContent() + "leedane/download_getLocalBase64Image.action";
            String imageTag = "imagedetail" + currentImageUrl;
            //执行网络加载post请求图片
            mNetworkImageLoader.loadNetBitmap(imageTag, imageUrl, new NetworkImageLoader.ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageBitmap, String tag) {
                    ImageView imageView = (ImageView) getView().findViewById(R.id.image_detail_imageview);
                    if (imageBitmap == null && imageView != null) {
                        imageView.setImageResource(R.drawable.error_cat);
                        return;
                    }

                    if (imageView != null) {
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
            }, ConstantsUtil.REQUEST_METHOD_POST, map);
        }
    }
}
