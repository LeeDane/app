package com.leedane.cn.financial.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.leedane.cn.financial.fragment.MainFragment;

import java.util.List;

/**
 * fragment页面列表的适配器
 * Created by LeeDane on 2016/7/21.
 */
public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private static final String TAG = "DetailPagerAdapter";

    private List<Fragment> mFragments;

    private Context mContext;

    public FragmentPagerAdapter(FragmentManager fragmentManager, Context context, List<Fragment> fragments) {
        super(fragmentManager);
        this.mContext = context;
        this.mFragments = fragments;
    }



    @Override
    public Fragment getItem(int position) {
        if(this.mFragments.get(position) == null){
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            MainFragment frament = MainFragment.newInstance(bundle);
            this.mFragments.set(position, frament);
            Log.i(TAG, "FragmentPagerAdapter当前frament为空，重新创建" + position);
        }
        return this.mFragments.get(position);
    }

    @Override
    public int getCount() {
        return this.mFragments.size();
    }
}
