package com.leedane.cn.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leedane.cn.adapter.DetailFragmentPagerAdapter;
import com.leedane.cn.customview.RightBorderTextView;
import com.leedane.cn.fragment.CommentOrTransmitFragment;
import com.leedane.cn.fragment.DetailArticleFragment;
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
    private int tabWidth;
    private LinearLayout mChatBottom;

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
            float positionOffsetOld;
            @Override
            public void onPageSelected(int position) {
                current_index = position;
                //更新Tab文字的颜色
                upDateTabTextColor();

                FragmentManager fragmentManager = getSupportFragmentManager();
                List<Fragment> list = fragmentManager.getFragments();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.show(list.get(position));
                fragmentTransaction.commit();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == 0.00 || positionOffset == 1.00 || positionOffset == 2.00) {
                    return;
                }
                Animation animation = new TranslateAnimation((position + positionOffsetOld) * tabWidth, (position + positionOffset) * tabWidth, 0, 0);
                animation.setFillAfter(true);
                animation.setDuration(20);
                mImageViewLine.startAnimation(animation);
                positionOffsetOld = positionOffset;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        current_index = 0;
    }

    /**
     * 初始化参数
     */
    private void init() {
        mChatBottom = (LinearLayout)findViewById(R.id.detail_bottom);
        mViewPager = (ViewPager) findViewById(R.id.detail_viewpager);
        mImageViewLine = (ImageView)findViewById(R.id.detail_line_imageview);

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

        tabWidth = w;
        params.width = w;

        //获取图片宽度
        mLineWidth = BitmapFactory.decodeResource(getResources(), R.drawable.line).getWidth();
        //Toast.makeText(DetailActivity.this, "后来线的宽度:"+ mLineWidth, Toast.LENGTH_SHORT).show();
        Matrix matrix = new Matrix();
        mOffset = (int) ((screenWidth/(float)3 - mLineWidth)/2);
        matrix.postTranslate(mOffset, 0);
        //设置初始位置
        mImageViewLine.setImageMatrix(matrix);
    }

    /**
     * 聊天tab的点击
     * @param view
     */
    public void tabMainClick(View view){
        current_index = 0;
        mViewPager.setCurrentItem(current_index);
        upDateTabTextColor();
    }

    /**
     * 通讯录tab的点击
     * @param view
     */
    public void tabCommentClick(View view){
        current_index = 1;
        mViewPager.setCurrentItem(current_index);
        upDateTabTextColor();
    }

    /**
     * 操作tab的点击
     * @param view
     */
    public void tabTransmitClick(View view){
        current_index = 2;
        mViewPager.setCurrentItem(current_index);
        upDateTabTextColor();
    }

    /**
     * 更新Tab文字的颜色
     */
    private void upDateTabTextColor(){
        RightBorderTextView textView;
        for (int i = 0; i < 3; i++) {
            if (i == current_index) {
                textView = (RightBorderTextView) mChatBottom.getChildAt(current_index);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                textView = (RightBorderTextView) mChatBottom.getChildAt(i);
                textView.setTextColor(getResources().getColor(R.color.gray));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
