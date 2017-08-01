package com.leedane.cn.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
   // private  PoiS;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //启动新谷app，传递参数
       /* String packageName = "com.xingu.policeservice";
        //要调用另一个APP的activity名字
        String activity = "com.xingu.policeservice.StartupActivity";
        ComponentName component = new ComponentName(packageName, activity);
        Intent intent = new Intent();
        intent.setComponent(component);
        intent.setFlags(101);
        intent.putExtra("data", "123");
        startActivity(intent);*/
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
