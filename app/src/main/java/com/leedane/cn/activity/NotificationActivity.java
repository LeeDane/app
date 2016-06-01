package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.fragment.NotificationFragment;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.EnumUtil;

/**
 * 我的消息activity
 * Created by LeeDane on 2016/3/26.
 */
public class NotificationActivity extends BaseActivity {
    public static final String TAG = "NotificationActivity";
    private ViewPager viewPager;
    private TabLayout tl;
    private String[] mTitles = null;
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
        setContentView(R.layout.activity_fan);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.message);
        backLayoutVisible();
        initView();
    }

    public void initView(){

        viewPager = (ViewPager) findViewById(R.id.vp_viewpager);
        tl = (TabLayout) findViewById(R.id.tl_tabs);

        mTitles = EnumUtil.getNotificationTypeList();
        viewPager.setCurrentItem(2);
        viewPager.setOffscreenPageLimit(mTitles.length);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Bundle bundle = new Bundle();
                bundle.putString("type", mTitles[position]);
                return NotificationFragment.newInstance(bundle);
            }

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });
        tl.setupWithViewPager(viewPager); //ViewPager 和 TabLayout 关联
        tl.setTabMode(TabLayout.MODE_SCROLLABLE); //用于多个TAB, Tablayout可以滚动
        //更改TAB默认的文本布局,自定义TAB布局
        /*for (int i = 0; i < tl.getTabCount(); i++) {
            TabLayout.Tab tabAt = tl.getTabAt(i);
            tabAt.setCustomView(viewPagerAdapter.getTabView(i));
        }
        viewPagerAdapter.notifyDataSetChanged();*/
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
