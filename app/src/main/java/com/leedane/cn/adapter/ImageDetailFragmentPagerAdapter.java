package com.leedane.cn.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.fragment.ImageDetailFragment;

import java.util.List;

/**
 * 多图查看的适配器
 * Created by LeeDane on 2015/11/14.
 */
public class ImageDetailFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "ImageDetailFragment";

    private List<Fragment> mFraments;

    private Context mContext;

    private List<ImageDetailBean> mImageDetailBeans;

    public ImageDetailFragmentPagerAdapter(FragmentManager fragmentManager, Context context, List<Fragment> mainFragment, List<ImageDetailBean> imageDetailBeans) {
        super(fragmentManager);
        this.mContext = context;
        this.mFraments = mainFragment;
        this.mImageDetailBeans = imageDetailBeans;
        Log.i(TAG, "总数：" + mFraments.size());
    }

    @Override
    public Fragment getItem(int position) {
        if(this.mFraments.get(position) == null){
            //ImageDetailFragment frament = new ImageDetailFragment(position, mContext, mImageDetailBeans.get(position));
            Bundle bundle = new Bundle();
            bundle.putInt("current", position);
            bundle.putSerializable("imageDetailBean", mImageDetailBeans.get(position));
            this.mFraments.set(position, ImageDetailFragment.newInstance(bundle));
            Log.i(TAG, "当前frament为空，重新创建" + position);
        }
        return this.mFraments.get(position);
    }
    @Override
    public int getCount() {
        return this.mFraments.size();
    }
}
