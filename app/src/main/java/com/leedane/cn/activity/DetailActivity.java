package com.leedane.cn.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.DetailFragmentPagerAdapter;
import com.leedane.cn.frament.DetailArticleFragment;
import com.leedane.cn.frament.CommentOrTransmitFragment;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.SerializableMap;
import com.leedane.cn.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 查看详情activity
 * Created by LeeDane on 2015/10/17.
 */
public class DetailActivity extends BaseActivity {

    public static final String TAG = "DetailActivity";

    private ViewPager mViewPager;
    /**
     * 正文的点击按钮
     */
    private TextView mTextViewArticle;

    /**
     * 评论的点击按钮
     */
    private TextView mTextViewComment;


    /**
     * 转发的点击按钮
     */
    private TextView mTextViewTransimt;

    /**
     * 线性的图像
     */
    private ImageView mImageViewLine;

    /**
     * 三个frament对象
     */
    private List<Fragment> mMainFragment;

    /**
     * 文章的id
     */
    private int mBlogId;


    //线的宽度
    private int mLineWidth;

    //偏移量
    private int mOffset;
    private int current_index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent it = getIntent();
        String title = it.getStringExtra("title");
        mBlogId = it.getIntExtra("tableId", 0);
        Log.i(TAG, "标题：" + title + ",文章ID:" + mBlogId);
        if(StringUtil.isNull(title)){
            title = "博客详情";
        }

        if(mBlogId <= 0){
            Toast.makeText(DetailActivity.this, "文章ID为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(title);
        backLayoutVisible();
        init();

        mViewPager.setAdapter(new DetailFragmentPagerAdapter(getSupportFragmentManager(), getApplicationContext(), mBlogId, mMainFragment));
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int one = mOffset * 2 + mLineWidth;// 页卡1 -> 页卡2 偏移量

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mTextViewArticle.setBackgroundColor(Color.BLUE);
                    mTextViewComment.setBackgroundColor(Color.RED);
                    mTextViewTransimt.setBackgroundColor(Color.RED);
                } else if (position == 1) {
                    mTextViewComment.setBackgroundColor(Color.BLUE);
                    mTextViewArticle.setBackgroundColor(Color.RED);
                    mTextViewTransimt.setBackgroundColor(Color.RED);
                } else {
                    mTextViewTransimt.setBackgroundColor(Color.BLUE);
                    mTextViewArticle.setBackgroundColor(Color.RED);
                    mTextViewComment.setBackgroundColor(Color.RED);
                }
                Animation animation = new TranslateAnimation(one * current_index, one * position, 0, 0);
                animation.setFillAfter(true);
                animation.setDuration(300);
                mImageViewLine.startAnimation(animation);
                current_index = position;
                FragmentManager fragmentManager = getSupportFragmentManager();
                List<Fragment> list = fragmentManager.getFragments();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.show(list.get(position));
                fragmentTransaction.commit();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG, "onPageScrolled-->position: " + position + ", positionOffset:" + positionOffset + ", positionOffsetPixels:" + positionOffsetPixels);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, "onPageScrollStateChanged:" + state);
				/*if(state == 0){
					current_index = getSupportFragmentManager().getFragments().get
				}*/
            }
        });
        current_index = 0;
    }

    /**
     * 初始化参数
     */
    private void init() {
        mViewPager = (ViewPager) findViewById(R.id.detail_viewpager);
        mTextViewArticle = (TextView)findViewById(R.id.detail_article_btn);
        mTextViewComment = (TextView)findViewById(R.id.detail_comment_btn);
        mTextViewTransimt = (TextView)findViewById(R.id.detail_transmit_btn);
        mImageViewLine = (ImageView)findViewById(R.id.detail_line_imageview);

        mTextViewArticle.setOnClickListener(this);
        mTextViewComment.setOnClickListener(this);
        mTextViewTransimt.setOnClickListener(this);

        //初始化线图像
        initImageView();

        //构建三个frament对象
        this.mMainFragment = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putInt("blogId", mBlogId);
        bundle.putInt("index", 0);
        this.mMainFragment.add(DetailArticleFragment.newInstance(bundle));
        HashMap<String, Object> commentParams = new HashMap<>();
        commentParams.put("table_name", "t_blog");
        commentParams.put("table_id", mBlogId);
        SerializableMap serializableMap = new SerializableMap();
        serializableMap.setMap(commentParams);

        bundle = new Bundle();
        bundle.putSerializable("serializableMap", serializableMap);
        bundle.putBoolean("isComment", true);
        bundle.putBoolean("itemSingleClick", false);
        bundle.putBoolean("isLoginUser", false);//设置长按不让删除
        this.mMainFragment.add(CommentOrTransmitFragment.newInstance(bundle));

        bundle = new Bundle();
        bundle.putSerializable("serializableMap", serializableMap);
        bundle.putBoolean("isComment", false);
        bundle.putBoolean("itemSingleClick", false);
        bundle.putBoolean("isLoginUser", false);//设置长按不让删除
        this.mMainFragment.add(CommentOrTransmitFragment.newInstance(bundle));


    }

    /**
     * 初始化创建线的图像
     */
    private void initImageView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
         //获得当前设备的屏幕宽度
        int screenWidth = dm.widthPixels;

        //设置线性的图像的宽度为1/3的屏幕宽度
        ViewGroup.LayoutParams params = mImageViewLine.getLayoutParams();
        int w = (int)screenWidth/3;
        params.width = w;
        //Toast.makeText(DetailActivity.this, "原来线的宽度:"+ w, Toast.LENGTH_SHORT).show();

        //获取图片宽度
        mLineWidth = BitmapFactory.decodeResource(getResources(), R.drawable.line).getWidth();
        //Toast.makeText(DetailActivity.this, "后来线的宽度:"+ mLineWidth, Toast.LENGTH_SHORT).show();
        Matrix matrix = new Matrix();
        mOffset = (int) ((screenWidth/(float)3 - mLineWidth)/2);
        matrix.postTranslate(mOffset, 0);
        //设置初始位置
        mImageViewLine.setImageMatrix(matrix);
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
