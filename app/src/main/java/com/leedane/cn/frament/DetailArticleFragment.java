package com.leedane.cn.frament;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SharedPreferenceUtil;

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
            mProgressBar = (ProgressBar)mRootView.findViewById(R.id.detail_progressbar);
            mBlogUrl = SharedPreferenceUtil.getSettingBean(mContext, ConstantsUtil.STRING_SETTING_BEAN_SERVER).getContent() + "leedane/blog_getContent.action?blog_id=" + mBlogId;
            mWebView.loadUrl(mBlogUrl);
            //启用支持javascript
            mSettings = mWebView.getSettings();
            mSettings.setJavaScriptEnabled(true);

            //使用缓存模式缓存加载过的数据
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
