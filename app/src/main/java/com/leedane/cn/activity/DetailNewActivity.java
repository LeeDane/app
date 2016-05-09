package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.StringUtil;
/*import com.thefinestartist.finestwebview.FinestWebView;
import com.thefinestartist.finestwebview.helpers.DipPixelHelper;*/

/**
 * 新的查看详情activity
 * Created by LeeDane on 2015/10/17.
 */
public class DetailNewActivity extends BaseActivity {

    public static final String TAG = "DetailNewActivity";

    /**
     * 文章的id
     */
    private int mBlogId;
    private int current_index;
    private String mBlogUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent it = getIntent();
        String title = it.getStringExtra("title");
        mBlogId = it.getIntExtra("blog_id", 0);
        Log.i(TAG, "标题：" + title + ",文章ID:" + mBlogId);
        if(StringUtil.isNull(title)){
            title = "标题获取有误";
        }
        mBlogId = 1;
        if(mBlogId <= 0){
            Toast.makeText(DetailNewActivity.this, "文章ID为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(title);
        backLayoutVisible();
        init();
        current_index = 0;
    }

    /**
     * 初始化参数
     */
    private void init() {
        mBlogUrl =  getBaseServerUrl()+ "leedane/blog_getContent.action?blog_id=" + mBlogId;

        /*new FinestWebView.Builder(this)
                .theme(R.style.FinestWebViewTheme)
                .titleDefault("Vimeo")
                .toolbarScrollFlags(0)
                .statusBarColorRes(R.color.bluePrimaryDark)
                .toolbarColorRes(R.color.bluePrimary)
                .titleColorRes(R.color.finestWhite)
                .urlColorRes(R.color.bluePrimaryLight)
                .iconDefaultColorRes(R.color.finestWhite)
                .progressBarColorRes(R.color.finestWhite)
                .showSwipeRefreshLayout(true)
                .swipeRefreshColorRes(R.color.bluePrimaryDark)
                .menuSelector(R.drawable.selector_light_theme)
                .dividerHeight(0)
                .gradientDivider(false)
                .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
                .show(mBlogUrl);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.detail_article_btn:
                break;
            case R.id.detail_comment_btn:
                break;
            case R.id.detail_transmit_btn:
                break;
        }
    }
}
