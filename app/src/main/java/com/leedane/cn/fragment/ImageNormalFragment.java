package com.leedane.cn.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.leedane.cn.activity.ImageDetailActivity;
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
 * 普通图像详情的frament类
 * Created by LeeDane on 2018/7/28.
 */
public class ImageNormalFragment extends ImageBaseFragment {
    private SubsamplingScaleImageView mScaleImageView; //图像对象

    public ImageNormalFragment(){
        mNetworkImageLoader = new NetworkImageLoader();
        int[] widthAndHeight = BaseApplication.newInstance().getScreenWidthAndHeight();
        this.acreenWidth = widthAndHeight[0];
        this.screenHeight = widthAndHeight[1]*3/4;
    }

    /*public ImageNormalFragment(int current, Context context, String currentImageUrl, int width, int height){
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

    @Override
    protected int fragmentId() {
        return R.layout.fragment_image_normal;
    }

    public static final ImageNormalFragment newInstance(Bundle bundle){
        ImageNormalFragment fragment = new ImageNormalFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            this.mCurrent = bundle.getInt("current");
            this.mImageDetailBean = (ImageDetailBean)bundle.getSerializable("imageDetailBean");
            this.mNetworkImageLoader = new NetworkImageLoader();
            int[] widthAndHeight = BaseApplication.newInstance().getScreenWidthAndHeight();
            this.acreenWidth = widthAndHeight[0];
            this.screenHeight = widthAndHeight[1]*3/4;
        }

        if(mContext == null)
            mContext = getActivity();

        String currentImageUrl = getImageUrl();
        if(currentImageUrl.startsWith("http://") || currentImageUrl.startsWith("https://")){
            //ImageCacheManager.loadImage(currentImageUrl, (ImageView) getView().findViewById(R.id.image_detail_imageview), width, height);
            mScaleImageView = (SubsamplingScaleImageView)getView().findViewById(R.id.image_normal_imageview);
            ImageCacheManager.loadImage(currentImageUrl, mScaleImageView, getImageWidth(), getImageHeight());
            mScaleImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showListItemMenuDialog();
                    return true;
                }
            });
            mScaleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getActivity() != null)
                        ((ImageDetailActivity)getActivity()).itemClick();
                }
            });
        }else{
            Map<String, Object> map = new HashMap<>();

            //请求的文件的名称(这里是图片)
            map.put("filename", currentImageUrl);
            //获取所有的请求基本参数
            map.putAll(BaseApplication.newInstance().getBaseRequestParams());
            String imageUrl = SharedPreferenceUtil.getSettingBean(mContext, ConstantsUtil.STRING_SETTING_BEAN_SERVER).getContent() + "leedane/download/getLocalBase64Image.action";
            String imageTag = "imagedetail" + currentImageUrl;
            //执行网络加载post请求图片
            mNetworkImageLoader.loadNetBitmap(imageTag, imageUrl, new NetworkImageLoader.ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageBitmap, String tag) {
                    ImageView imageView = (ImageView) getView().findViewById(R.id.image_normal_imageview);
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
