package com.leedane.cn.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.leedane.cn.fragment.PersonalFragment;

import java.util.List;

/**
 * 个人中心类的适配器
 * Created by leedane on 2015/11/19.
 */
public class PersonalFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "DetailFragmentPagerAdapter";

    private List<Fragment> mFragments;

    private Context mContext;

    private int mBlogId;

    public PersonalFragmentPagerAdapter(FragmentManager fragmentManager, Context context, List<Fragment> fragments) {
        super(fragmentManager);
        this.mContext = context;
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if(this.mFragments.get(position) == null){
            PersonalFragment frament = new PersonalFragment(position, mContext);
            this.mFragments.set(position, frament);
            Log.i(TAG, "PersonalFragmentPagerAdapter当前frament为空，重新创建" + position);
        }
        return this.mFragments.get(position);
    }

    @Override
    public int getCount() {
        return this.mFragments.size();
    }
}
