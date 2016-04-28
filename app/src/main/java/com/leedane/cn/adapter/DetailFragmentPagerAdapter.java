package com.leedane.cn.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.leedane.cn.frament.DetailArticleFragment;
import com.leedane.cn.frament.CommentOrTransmitFragment;
import com.leedane.cn.util.SerializableMap;

import java.util.HashMap;
import java.util.List;

/**
 * 查看详情类的适配器
 * Created by LeeDane on 2015/11/14.
 */
public class DetailFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "DetailFragmentPagerAdapter";

    private List<Fragment> mMainFragment;

    private Context mContext;

    private int mBlogId;

    public DetailFragmentPagerAdapter(FragmentManager fragmentManager, Context context, int blog_id, List<Fragment> mainFragment) {
        super(fragmentManager);
        this.mContext = context;
        this.mBlogId = blog_id;
        this.mMainFragment = mainFragment;
    }

    @Override
    public Fragment getItem(int position) {
        if(this.mMainFragment.get(position) == null){
            if(position == 0){
                Bundle bundle = new Bundle();
                bundle.putInt("blogId", mBlogId);
                bundle.putInt("index", 1);
                this.mMainFragment.set(position, DetailArticleFragment.newInstance(bundle));
            }else{
                HashMap<String, Object> commentParams = new HashMap<>();
                commentParams.put("table_name", "t_blog");
                commentParams.put("table_id", mBlogId);
                SerializableMap serializableMap = new SerializableMap();
                serializableMap.setMap(commentParams);
                Bundle bundle = new Bundle();
                bundle.putSerializable("serializableMap", serializableMap);
                bundle.putBoolean("isComment", true);
                bundle.putBoolean("itemSingleClick", true);
                bundle.putBoolean("isLoginUser", false);
                this.mMainFragment.set(position, CommentOrTransmitFragment.newInstance(bundle));
            }

            Log.i(TAG, "当前frament为空，重新创建" + position);
        }
        return this.mMainFragment.get(position);
    }

    @Override
    public int getCount() {
        return this.mMainFragment.size();
    }
}
