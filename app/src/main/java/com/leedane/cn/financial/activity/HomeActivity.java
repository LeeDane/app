package com.leedane.cn.financial.activity;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.customview.RightBorderTextView;
import com.leedane.cn.financial.adapter.FragmentPagerAdapter;
import com.leedane.cn.financial.fragment.MainFragment;
import com.leedane.cn.financial.fragment.MonthFragment;
import com.leedane.cn.financial.fragment.WeekFragment;
import com.leedane.cn.financial.fragment.YearFragment;
import com.leedane.cn.financial.fragment.YesterDayFragment;
import com.leedane.cn.util.DensityUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 记账首页activity
 * Created by LeeDane on 2016/7/19.
 */
public class HomeActivity extends BaseActivity {

    public static final String TAG = "HomeActivity";

    /**
     * 水平滑动的view
     */
    private HorizontalScrollView mScrollview;

    private int mPreTab = 0;//当上一个的Tab索引
    private int mCurrentTab;  //标记当前tab位置
    private int mTotalTabs = 5;//标签的总数

    /**
     * 水平滑动的单选按钮组view
     */
    private RadioGroup mRadioGroup;

    private ViewPager mViewPager;

    /**
     * tab的宽度
     */
    private int mTabWidth;

    /**
     * 线性的图像
     */
    private ImageView mImageViewLine;
    //线的宽度
    private int mLineWidth;
    private int tabWidth;

    //偏移量
    private int mOffset;

    /**
     * 标签的所有Fragment
     */
    private List<Fragment> mFragments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(HomeActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.HomeActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_financial_home);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(getStringResource(R.string.financial));
        backLayoutVisible();
        init();
        initView();
    }

    /**
     * 初始化试图控件
     */
    private void initView() {
        mScrollview = (HorizontalScrollView)findViewById(R.id.financial_scrollview);
        mRadioGroup = (RadioGroup) findViewById(R.id.financial_tabs);
        mViewPager = (ViewPager)findViewById(R.id.financial_viewpager);
        mRadioGroup.setVisibility(View.VISIBLE);
        mImageViewLine = (ImageView)findViewById(R.id.financial_line);

        //初始化线图像
        initImageView();

        if(mCurrentTab > 0 ){
            //将第一个的背景颜色变成默认的
            TextView preTextView = (TextView) mRadioGroup.getChildAt(0);
            preTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.default_font));
        }


        //将当前tab的背景颜色变成默认的
        TextView currentTextView = (TextView) mRadioGroup.getChildAt(mCurrentTab);
        currentTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));

        //获取当前点击tab的索引
        mCurrentTab = getTabPosition(currentTextView);

        //获得当前tab的坐标
        getCurrentTabCoordinate(currentTextView);

        mTotalTabs = mRadioGroup.getChildCount();

        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                mScrollview.scrollTo(mTabWidth * mCurrentTab, 0);
            }
        }), 5);

        String tabText;
        for(int i = 0; i < mRadioGroup.getChildCount(); i++){
            tabText = ((RightBorderTextView)mRadioGroup.getChildAt(i)).getText().toString();
            if(tabText.equalsIgnoreCase(getStringResource(R.string.financial_home))){//首页
                Bundle bundle = new Bundle();
                mFragments.add(MainFragment.newInstance(bundle));
                continue;
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.financial_yesterday))) {//昨日
                Bundle bundle = new Bundle();
                mFragments.add(YesterDayFragment.newInstance(bundle));
            } else if(tabText.equalsIgnoreCase(getStringResource(R.string.financial_week))) {//本周
                Bundle bundle = new Bundle();
                mFragments.add(WeekFragment.newInstance(bundle));
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.financial_month))) {//本月
                Bundle bundle = new Bundle();
                mFragments.add(MonthFragment.newInstance(bundle));
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.financial_year))) {//本年
                Bundle bundle = new Bundle();
                mFragments.add(YearFragment.newInstance(bundle));
            }
        }

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), getBaseContext(), mFragments));
        mViewPager.setCurrentItem(mCurrentTab);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            float positionOffsetOld;
            @Override
            public void onPageSelected(int position) {

                //将上一个tab的背景颜色变成默认的
                TextView preTextView = (TextView) mRadioGroup.getChildAt(mCurrentTab);
                preTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.default_font));
                //preTextView.setBackground(ContextCompat.getDrawable(PersonalActivity.this, R.drawable.btn_default_no_seletorbg));

                mPreTab = mCurrentTab;
                mCurrentTab = position;
                Log.i(TAG, "上一个位置是：" + mPreTab + ",当前的位置是：" + mCurrentTab);
                TextView currentTextView = (TextView) mRadioGroup.getChildAt(position);
                //获得当前tab的坐标
                getCurrentTabCoordinate(currentTextView);
                currentTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                //currentTextView.setBackground(ContextCompat.getDrawable(PersonalActivity.this, R.drawable.btn_default_seletorbg));

                //设置tab的切换
                //mScrollview.smoothScrollTo(80, 0);
                new Handler().postDelayed((new Runnable() {
                    @Override
                    public void run() {
                        mScrollview.scrollTo(mTabWidth * mCurrentTab, 0);
                        TextView textView = (TextView)mRadioGroup.getChildAt(mCurrentTab);
                        int[] wah = new int[2];
                        textView.getLocationOnScreen(wah);
                        ToastUtil.success(HomeActivity.this, "呵呵-->" + wah[0]);

                        Animation animation = new TranslateAnimation(mPreTab * tabWidth, mCurrentTab* tabWidth, 0, 0);
                        animation.setFillAfter(true);
                        animation.setDuration(20);
                        mImageViewLine.startAnimation(animation);
                        //获取当前tab的位置
                    }
                }), 5);

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
                Log.i(TAG, "onPageScrollStateChanged:" + state);
            }
        });
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
        //int w = (int)screenWidth/3;

        tabWidth = DensityUtil.dip2px(HomeActivity.this, 80);
        params.width = DensityUtil.dip2px(HomeActivity.this, 80);

        //获取图片宽度
        mLineWidth = BitmapFactory.decodeResource(getResources(), R.drawable.line).getWidth();
        //Toast.makeText(DetailActivity.this, "后来线的宽度:"+ mLineWidth, Toast.LENGTH_SHORT).show();
        Matrix matrix = new Matrix();
        //mOffset = (int) ((screenWidth/(float)3 - mLineWidth)/2);
        mOffset = 0;
        matrix.postTranslate(mOffset, 0);
        //设置初始位置
        mImageViewLine.setImageMatrix(matrix);
    }
    /**
     * 列表滚动到顶部
     * @param view
     */
    private void smoothScrollToTop(View view){
        switch (view.getId()){
            case R.id.financial_home:  //首页
               ((MainFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.financial_yesterday: //昨日
               // ((CommentOrTransmitFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.financial_week: //本周
               // ((CommentOrTransmitFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.financial_month: //本月
                //((ZanFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.financial_year: //本年
                //((AttentionFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
        }
    }
    /**
     * Tab的点击事件
     * @param view
     */
    public void tabClick(View view){
        TextView currentTextView = (TextView)view;
        currentTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));

        mPreTab = mCurrentTab;

        //获取当前点击tab的索引
        mCurrentTab = getTabPosition(currentTextView);

        if(mPreTab == mCurrentTab){
            smoothScrollToTop(view);
            return;
        }
        //设置上一个tab的字体背景为灰色
        TextView preTextView = (TextView)mRadioGroup.getChildAt(mPreTab);
        preTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.default_font));

        //获得当前tab的坐标
        getCurrentTabCoordinate(currentTextView);

        //设置tab的切换
        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                mScrollview.scrollTo(mTabWidth * mCurrentTab, 0);
                mViewPager.setCurrentItem(mCurrentTab);
            }
        }), 5);
    }
    /**
     * 获取当前点击tab的索引
     * @param view
     * @return
     */
    private int getTabPosition(TextView view) {
        return mRadioGroup != null ? mRadioGroup.indexOfChild(view) : 0;
    }

    /**
     * 获取当前tab的坐标
     */
    private void getCurrentTabCoordinate(TextView tabView) {
        if(tabView == null){
            ToastUtil.success(HomeActivity.this, "TextView为空，无法计算X,Y坐标");
            return;
        }
        mTabWidth = tabView.getLayoutParams().width;
    }

    /**
     * 初始化参数
     */
    private void init() {
        mCurrentTab = getIntent().getIntExtra("currentTab", 0);
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
