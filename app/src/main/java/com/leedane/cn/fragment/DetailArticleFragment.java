package com.leedane.cn.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DensityUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import lib.homhomlib.design.SlidingLayout;

/**
 * 全文的内容的fragment类
 * Created by LeeDane on 2015/11/14.
 */
public class DetailArticleFragment extends Fragment{

    private Context mContext;
    private ListView mListViewComment;
    private int mIndex;

    private View mRootView;
    private boolean isFirstLoading = true; //是否是第一次加载
    private String mBlogUrl;
    private WebSettings mSettings;
    private int mBlogId;

    private SlidingLayout mSlidingLayout;
    /**
     * 内置浏览器webview对象
     */
    private WebView mWebView;
    /**
     * 加载进度的进度条
     */
    private ProgressBar mProgressBar;

    public DetailArticleFragment(){

    }
    public static final DetailArticleFragment newInstance(Bundle bundle){
        DetailArticleFragment fragment = new DetailArticleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_detail_article, container,
                    false);
        Bundle bundle = getArguments();
        if(bundle != null){
            mBlogId = bundle.getInt("blogId");
            mIndex = bundle.getInt("index");
        }
        if(mContext == null)
            mContext = getActivity();

        if(isFirstLoading){
            mWebView = (WebView)mRootView.findViewById(R.id.detail_webview);
            mSlidingLayout = (SlidingLayout)mRootView.findViewById(R.id.webview_background_view);
            mProgressBar = (ProgressBar)mRootView.findViewById(R.id.detail_progressbar);

            String serverBasePath = SharedPreferenceUtil.getSettingBean(mContext, ConstantsUtil.STRING_SETTING_BEAN_SERVER).getContent();
            if(mSlidingLayout.getBackgroundView() != null){
                ((TextView)mSlidingLayout.getBackgroundView().findViewById(R.id.webview_backgroup_textview)).setText("网页由 "+serverBasePath +"提供\n\r   LeeDane官方提供技术支持");
            }

            float device_width_dp = BaseApplication.newInstance().getScreenWidthAndHeight()[0];
            mBlogUrl = serverBasePath + "/content?blog_id=" + mBlogId+"&device_width="+ (DensityUtil.px2dip(mContext, device_width_dp) -20);
            Log.i("blogDetail", "博客的地址：" + mBlogUrl);
            mWebView.loadUrl(mBlogUrl);
            //启用支持javascript
            mSettings = mWebView.getSettings();
            mSettings.setJavaScriptEnabled(true);
            mSettings.setSupportZoom(false);
            mWebView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                        handler.sendEmptyMessage(1);
                        return true;
                    }
                    return false;
                }
            });

//            LayoutAlgorithm是一个枚举，用来控制html的布局，总共有三种类型：
//            NORMAL：正常显示，没有渲染变化。
//            SINGLE_COLUMN：把所有内容放到WebView组件等宽的一列中。
//            NARROW_COLUMNS：可能的话，使所有列的宽度不超过屏幕宽度。
            mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

            mSettings.setUseWideViewPort(true);
            //mSettings.setLoadWithOverviewMode(true);


            mWebView.addJavascriptInterface(this, "webview");//对应js中的test.xxx
            //使用缓存模式缓存加载过的数据
//            缓存模式(5种)
//            LOAD_CACHE_ONLY:  不使用网络，只读取本地缓存数据
//            LOAD_DEFAULT:  根据cache-control决定是否从网络上取数据。
//            LOAD_CACHE_NORMAL: API level 17中已经废弃, 从API level 11开始作用同LOAD_DEFAULT模式
//            LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
//            LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

            //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }
            });

            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress == 100) {
                        mProgressBar.setVisibility(View.GONE);
                        //Toast.makeText(DetailActivity.this, "网页加载完成", Toast.LENGTH_SHORT).show();
                    } else {
                        // 加载中
                        mProgressBar.setProgress(newProgress);
                    }
                }
            });
        }else{
            mWebView.loadUrl(mBlogUrl);
        }
        return mRootView;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1: {
                    webViewGoBack();
                }
                break;
            }
        }
    };

    private void webViewGoBack() {
        mWebView.goBack();
    }

    /**
     * 图片的点击事件
     * @param imgs
     * @param index
     * @param maxWidth
     * @param maxHeight
     */
    @JavascriptInterface
    public void clickImg(String imgs, int index, int maxWidth, int maxHeight) {//对应js中xxx.hello("")
        Log.e("webview", "hello");
        String[] arrayImg = imgs.split(";");

        if (arrayImg.length > 0 && index >= 0 && arrayImg.length > index) {
            List<ImageDetailBean> list = new ArrayList<>();
            ImageDetailBean imageDetailBean = null;
            for (int i = 0; i < arrayImg.length; i++) {
                imageDetailBean = new ImageDetailBean();
                imageDetailBean.setPath(arrayImg[i]);
                /*if(maxWidth > 0 && maxHeight > 0){
                    imageDetailBean.setWidth(maxWidth);
                    imageDetailBean.setHeight(maxHeight);
                }*/
                list.add(imageDetailBean);
            }
            CommonHandler.startImageDetailActivity(mContext, list, index);
        }


    }

    @Override
    public void onDestroy() {
        if(mWebView != null)
            mWebView.destroy();
        if(handler != null)
            handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
