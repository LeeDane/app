package com.leedane.cn.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.leedane.cn.activity.ImageDetailActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.fragment.SearchListFragment;
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.task.NetworkImageLoader;
import com.leedane.cn.util.Base64Util;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.FileUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * gif图像详情的frament类
 * Created by LeeDane on 2017/8/28.
 */
public class ImageGifFragment extends ImageBaseFragment {

    //保存gif图像对象
    private GifImageView mGIfImageView;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            GifDrawable drawable = null;
            switch (msg.what) {
                case FlagUtil.GALLERY_LOAD_NETWORK_IMAGE: //加载图库的图片成功
                    try{
                        String path = bundle.getString("path");
                        if(StringUtil.isNull(path)){
                            //ToastUtil.failure(mContext, "网络图片处理失败！");
                            drawable = new GifDrawable(getResources(), R.drawable.load_gif);
                            break;
                        }
                        drawable = new GifDrawable(FileUtil.getEncryptFile(path));
                    }catch (IOException e){
                        ToastUtil.failure(mContext, "IOException: 网络图片处理失败！");
                    }
                    mGIfImageView.setImageDrawable(drawable);
                    break;
            }
        }
    };

    public ImageGifFragment() {
        mNetworkImageLoader = new NetworkImageLoader();
        int[] widthAndHeight = BaseApplication.newInstance().getScreenWidthAndHeight();
        this.acreenWidth = widthAndHeight[0];
        this.screenHeight = widthAndHeight[1] * 3 / 4;
    }

    @Override
    protected int fragmentId() {
        return R.layout.fragment_image_gif;
    }

    public static final ImageGifFragment newInstance(Bundle bundle){
        ImageGifFragment fragment = new ImageGifFragment();
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
            //ImageCacheManager.loadImage(currentImageUrl, (ImageView) getView().findViewById(R.id.image_normal_imageview), width, height);
            mGIfImageView = (GifImageView)getView().findViewById(R.id.image_gif_imageview);

            // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
            new Thread(networkTask).start();

            //ImageCacheManager.loadGifImage(currentImageUrl, mGIfImageView, getImageWidth(), getImageHeight());
            Message msg = new Message();
            msg.what = FlagUtil.GALLERY_LOAD_NETWORK_IMAGE;
            mHandler.sendMessage(msg);
            mGIfImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showListItemMenuDialog();
                    return true;
                }
            });
            mGIfImageView.setOnClickListener(new View.OnClickListener() {
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
                    ImageView imageView = (ImageView) getView().findViewById(R.id.image_gif_imageview);
                    if (imageBitmap == null && imageView != null) {
                        imageView.setImageResource(R.drawable.load_gif);
                        return;
                    }

                    if (imageView != null) {
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
            }, ConstantsUtil.REQUEST_METHOD_POST, map);
        }
    }

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            Map<String, Object> resultMap = FileUtil.saveNetWorkLinkToFile(mImageDetailBean.getPath(), true);

            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("path", StringUtil.changeNotNull(resultMap.get("path")));
            msg.setData(data);
            msg.what = FlagUtil.GALLERY_LOAD_NETWORK_IMAGE;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    public void onDestroy() {
        if(mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
