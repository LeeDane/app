package com.leedane.cn.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.DetailFragmentPagerAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.fragment.CommentOrTransmitFragment;
import com.leedane.cn.fragment.DetailArticleFragment;
import com.leedane.cn.util.SerializableMap;
import com.leedane.cn.util.StringUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 查看详情activity
 * Created by LeeDane on 2015/10/17.
 */
public class DetailActivity extends BaseCustomLayoutTabActivity {
    public static final String TAG = "DetailActivity";

    /**
     * 文章的id
     */
    private int mBlogId;

    @Override
    protected List<LayoutViewObject> initTitleList() {
        List<LayoutViewObject> layoutViewObjects = new ArrayList<>();
        layoutViewObjects.add(new LayoutViewObject(getStringResource(R.string.home), R.drawable.ic_home_indigo_500_18dp));
        layoutViewObjects.add(new LayoutViewObject(getStringResource(R.string.comment), R.drawable.ic_comment_indigo_500_18dp));
        layoutViewObjects.add(new LayoutViewObject(getStringResource(R.string.transmit), R.drawable.ic_transmit_indigo_500_18dp));
        return layoutViewObjects;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent it = getIntent();
        String title = it.getStringExtra("title");
        mBlogId = it.getIntExtra("tableId", 0);
        Log.i(TAG, "标题：" + title + ",文章ID:" + mBlogId);
        if(StringUtil.isNull(title)){
            title = "博客详情";
        }

        if(mBlogId <= 0){
            Toast.makeText(DetailActivity.this, "文章ID为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(title);
        backLayoutVisible();
        initView();
        super.initMagicIndicator();
    }

    @Override
    protected List<Fragment> initFragmentList() {
        List<Fragment> fragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putInt("blogId", mBlogId);
        bundle.putInt("index", 0);
        fragments.add(DetailArticleFragment.newInstance(bundle));
        HashMap<String, Object> commentParams = new HashMap<>();
        commentParams.put("table_name", "t_blog");
        commentParams.put("table_id", mBlogId);
        SerializableMap serializableMap = new SerializableMap();
        serializableMap.setMap(commentParams);

        bundle = new Bundle();
        bundle.putSerializable("serializableMap", serializableMap);
        bundle.putBoolean("isComment", true);
        bundle.putBoolean("itemSingleClick", false);
        bundle.putBoolean("isLoginUser", false);//设置长按不让删除
        fragments.add(CommentOrTransmitFragment.newInstance(bundle));

        bundle = new Bundle();
        bundle.putSerializable("serializableMap", serializableMap);
        bundle.putBoolean("isComment", false);
        bundle.putBoolean("itemSingleClick", false);
        bundle.putBoolean("isLoginUser", false);//设置长按不让删除
        fragments.add(CommentOrTransmitFragment.newInstance(bundle));
        return fragments;
    }

    @Override
    protected int getMagicIndicatorViewId() {
        return R.id.detail_magic_indicator;
    }

    /**
     * 初始化参数
     */
    private void initView() {
        super.mViewPager = (ViewPager) findViewById(R.id.detail_viewpager);
        super.mViewPager.setAdapter(new DetailFragmentPagerAdapter(getSupportFragmentManager(), getApplicationContext(), mBlogId, initFragmentList()));
        super.mViewPager.setCurrentItem(0);
    }
}
