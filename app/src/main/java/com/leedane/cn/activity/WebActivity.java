package com.leedane.cn.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.util.ConstantsUtil;

import org.json.JSONObject;

/**
 * 浏览器activity
 */
public class WebActivity extends BaseActivity{

    private WebView webView;
    private Context context;
    //private Activity activity;
    private String ref;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.webview);
        backLayoutVisible();
        context = this;
        //activity = this;
        init();
    }

    //
    private void init() {
        ref = getIntent().getStringExtra("ref");
        webView = (WebView) findViewById(R.id.webview);

        initWebView();
    }

    private void initWebView() {
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setCacheMode(
                    WebSettings.LOAD_NO_CACHE);
        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                setTitleViewText("Loading...");
                /*activity.setProgress(newProgress * 100);
                if (newProgress == 100) {
                    activity.setTitle(R.string.app_name);
                }*/
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        webView.setWebViewClient(new GameWebViewClient());
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        String token = null;
        int userId = -1;
        //设置头部信息
        try{
            JSONObject userInfo = BaseApplication.getLoginUserInfo();
            if(userInfo != null){
                if(userInfo.has("token"))
                    token = userInfo.getString("token");
                userId = userInfo.getInt("id");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        webView.loadUrl(ConstantsUtil.DEFAULT_SERVER_URL + "/transfer?ref="+ ref+"&token="+ token+"&userId="+ userId);
    }

    class GameWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,
                                                String url_Turntable) {
            view.loadUrl(url_Turntable);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setTitleViewText(view.getTitle());
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
