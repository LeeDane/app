package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

/**
 * fragment页面列表的适配器
 * Created by LeeDane on 2018/1/29.
 */
public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private static final String TAG = "FPagerAdapter";

    private List<Fragment> mFragments;

    private Context mContext;

    public FragmentPagerAdapter(FragmentManager fragmentManager, Context context, List<Fragment> fragments) {
        super(fragmentManager);
        this.mContext = context;
        this.mFragments = fragments;
    }



    @Override
    public Fragment getItem(int position) {
        return this.mFragments.get(position);
    }

    @Override
    public int getCount() {
        return this.mFragments.size();
    }
}
