package com.leedane.cn.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.adapter.DetailFragmentPagerAdapter;
import com.leedane.cn.app.R;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import java.util.List;

/**
 * MagicIndicator使用自定义列表指示器activity
 * Created by LeeDane on 2017/1/9.
 */
public abstract class BaseCustomLayoutTabActivity extends BaseActivity {
    private List<LayoutViewObject> mTitleList;

    /**
     * frament对象集合
     */
    private List<Fragment> mMainFragment;

    protected ViewPager mViewPager;
    /**
     * 初始化标题的列表
     */
    protected abstract List<LayoutViewObject> initTitleList();

    /**
     * 初始化frament对象集合
     */
    protected abstract List<Fragment> initFragmentList();

    protected abstract int getMagicIndicatorViewId();

    /**
     * 这个方式要在子类手动调用，必须在初始化ViewPager后操作
     */
    protected void initMagicIndicator() {
        mTitleList = initTitleList();
        mMainFragment = initFragmentList();

        MagicIndicator magicIndicator = (MagicIndicator) findViewById(getMagicIndicatorViewId());
        magicIndicator.setBackgroundColor(Color.BLACK);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleList.size();
            }
            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);

                // load custom layout
                View customLayout = LayoutInflater.from(context).inflate(R.layout.simple_pager_title_layout, null);
                final ImageView titleImg = (ImageView) customLayout.findViewById(R.id.title_img);
                final TextView titleText = (TextView) customLayout.findViewById(R.id.title_text);
                titleImg.setImageResource(mTitleList.get(index).imageId);
                titleText.setText(mTitleList.get(index).title);
                commonPagerTitleView.setBackgroundColor(getResources().getColor(R.color.default_bg_color));
                commonPagerTitleView.setContentView(customLayout);
                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

                    @Override
                    public void onSelected(int index, int totalCount) {
                        titleText.setTextColor(getResources().getColor(R.color.blueAccountLink));
                    }

                    @Override
                    public void onDeselected(int index, int totalCount) {
                        titleText.setTextColor(Color.LTGRAY);
                    }

                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                        titleImg.setScaleX(1.5f + (0.8f - 1.5f) * leavePercent);
                        titleImg.setScaleY(1.5f + (0.8f - 1.5f) * leavePercent);
                    }

                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                        titleImg.setScaleX(0.8f + (1.5f - 0.8f) * enterPercent);
                        titleImg.setScaleY(0.8f + (1.5f - 0.8f) * enterPercent);
                    }
                });

                commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });

                return commonPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }


    class LayoutViewObject{
        String title;
        int imageId; //图标资源的ID
        public  LayoutViewObject(String title, int imageId){
            this.title = title;
            this.imageId = imageId < 1 ? R.mipmap.ic_launcher: imageId;
        }
    }
}
