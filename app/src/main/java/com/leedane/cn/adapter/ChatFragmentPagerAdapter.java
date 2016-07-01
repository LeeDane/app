package com.leedane.cn.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.leedane.cn.fragment.ChatHomeFragment;
import com.leedane.cn.fragment.CommentOrTransmitFragment;
import com.leedane.cn.util.SerializableMap;

import java.util.HashMap;
import java.util.List;

/**
 * 聊天多个fragment切换的适配器
 * Created by LeeDane on 2016/5/5.
 */
public class ChatFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "ChatFragmentPagerAdapter";

    private List<Fragment> mMainFragment;

    public ChatFragmentPagerAdapter(FragmentManager fragmentManager, List<Fragment> mainFragment) {
        super(fragmentManager);
        this.mMainFragment = mainFragment;
    }

    @Override
    public Fragment getItem(int position) {
        if(this.mMainFragment.get(position) == null){
            if(position == 0){
                Bundle bundle = new Bundle();
                this.mMainFragment.set(position, ChatHomeFragment.newInstance(bundle));
            }else{
                HashMap<String, Object> commentParams = new HashMap<>();
                commentParams.put("table_name", "t_blog");
                SerializableMap serializableMap = new SerializableMap();
                serializableMap.setMap(commentParams);
                Bundle bundle = new Bundle();
                bundle.putSerializable("serializableMap", serializableMap);
                bundle.putBoolean("isComment", true);
                bundle.putBoolean("itemSingleClick", true);
                bundle.putBoolean("isLoginUser", false);
                this.mMainFragment.set(position, CommentOrTransmitFragment.newInstance(bundle));
            }
            Log.i(TAG, "frament空，重新创建" + position);
        }
        return this.mMainFragment.get(position);
    }

    @Override
    public int getCount() {
        return this.mMainFragment.size();
    }
}
