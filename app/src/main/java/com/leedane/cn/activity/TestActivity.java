package com.leedane.cn.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;


import com.leedane.cn.adapter.PersonalFragmentPagerAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.fragment.PersonalMoodFragment;
import com.leedane.cn.fragment.TestFragment;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试
 * Created by leedane on 2016/5/17.
 */
public class TestActivity extends FragmentActivity {
    public static final String TAG = "TestActivity";
    private ViewPager mViewPager;

    List<String> mTitleDataList = null;
    private MagicIndicator magicIndicator;

    List<Fragment> mFragments = new ArrayList<>();

   // private  PoiS;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


       mTitleDataList = new ArrayList<>();
        mTitleDataList.add("leedane");
        mTitleDataList.add("今天");
        mTitleDataList.add("您好");
        mTitleDataList.add("今天1");
        mTitleDataList.add("您好1");
        mTitleDataList.add("今天2");
        mTitleDataList.add("您好2");
        mTitleDataList.add("今天3");
        mTitleDataList.add("您好3");
        mTitleDataList.add("今天4");
        mTitleDataList.add("您好4");

        for(int i = 0; i < mTitleDataList.size(); i++){
            Bundle bundle = new Bundle();
            bundle.putInt("id", 1 + i);
            //mFragments.add(new PersonalMoodFragment(i, PersonalActivity.this, mUserId, mIsLoginUser));
            mFragments.add(TestFragment.newInstance(bundle));
        }


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        setContentView(R.layout.activity_test);
        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitleDataList == null ? 0 : mTitleDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(Color.WHITE);
                colorTransitionPagerTitleView.setSelectedColor(Color.BLACK);
                colorTransitionPagerTitleView.setText(mTitleDataList.get(index));
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
        mViewPager.setAdapter(new PersonalFragmentPagerAdapter(getSupportFragmentManager(), getBaseContext(), mFragments));
        mViewPager.setCurrentItem(1);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }


}
