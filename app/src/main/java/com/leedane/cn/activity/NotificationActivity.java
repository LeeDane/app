package com.leedane.cn.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.leedane.cn.adapter.FragmentPagerAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.fragment.NotificationFragment;
import com.leedane.cn.util.EnumUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的消息activity
 * Created by LeeDane on 2016/3/26.
 */
public class NotificationActivity extends BaseActivity {
    public static final String TAG = "NotificationActivity";
    /**
     * 标签的所有Fragment
     */
    private List<String> mTitleList = null;
    private List<Fragment> mFragments = new ArrayList<>();

    private ViewPager mViewPager;
    private MagicIndicator mMagicIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(NotificationActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.NotificationActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }
        setContentView(R.layout.activity_notification);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.message);
        backLayoutVisible();
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        mTitleList = EnumUtil.getNotificationTypeList();
    }

    public void initView(){

        mViewPager = (ViewPager)findViewById(R.id.magic_indicator_viewpager);
        for(int i = 0; i < mTitleList.size(); i++){
            Bundle bundle = new Bundle();
            bundle.putString("type", mTitleList.get(i));
            mFragments.add(NotificationFragment.newInstance(bundle));
        }

        mMagicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        mMagicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.35f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitleList == null ? 0 : mTitleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {

                final BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);

                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mTitleList.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.black));
                //simplePagerTitleView.setMarqueeRepeatLimit(1);
                //simplePagerTitleView.setMinWidth(mTitleList.get(index).length() * 20 + 220); //设置tab滚动的宽度
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                badgePagerTitleView.setInnerPagerTitleView(simplePagerTitleView);
                return badgePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 4));
                indicator.setLineWidth(UIUtil.dip2px(context, 20));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(Color.parseColor("#33ABF9"));
                return indicator;
            }


        });
        mMagicIndicator.setNavigator(commonNavigator);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mMagicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                mMagicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mMagicIndicator.onPageScrollStateChanged(state);
            }
        });
        mViewPager.setOffscreenPageLimit(mFragments.size());
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), getBaseContext(), mFragments));
        mViewPager.setCurrentItem(0);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
