package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.leedane.cn.fragment.FanFragment;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.ToastUtil;

/**
 * 粉丝列表activity
 * Created by LeeDane on 2016/4/13O.
 */
public class FanActivity extends BaseActivity{

    public static final String TAG = "FanActivity";
    //private int fanOrAttention = 0;//控制展示的是获取我的粉丝或者是我的关注用户(0：表示粉丝，1：表示关注的用户)
    private boolean isLoginUser;//是否是登录用户
    private int toUserId;

    private ViewPager viewPager;
    private TabLayout tl;
    private String[] mTitles = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        checkedIsLogin("com.leedane.cn.activity.FanActivity");
        setContentView(R.layout.activity_fan);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.personal_fans);
        backLayoutVisible();
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        Intent it = getIntent();
        toUserId = it.getIntExtra("toUserId", 0);
        isLoginUser = it.getBooleanExtra("isLoginUser", false);
        if(toUserId < 1){
            ToastUtil.failure(FanActivity.this, "用户参数不正确");
            finish();
        }
    }

    public void initView(){

        viewPager = (ViewPager) findViewById(R.id.vp_viewpager);
        tl = (TabLayout) findViewById(R.id.tl_tabs);

        if(isLoginUser){
            mTitles = new String[]{getStringResource(R.string.fan_my_fans), getStringResource(R.string.fan_my_attention)};
        }else{
            mTitles = new String[]{getStringResource(R.string.fan_to_fans), getStringResource(R.string.fan_to_attention)};
        }
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("fanOrAttention", position);
                bundle.putBoolean("isLoginUser", isLoginUser);
                bundle.putInt("toUserId", toUserId);
                return FanFragment.newInstance(bundle);
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
        //tl.setTabMode(TabLayout.MODE_SCROLLABLE); //用于多个TAB, Tablayout可以滚动
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
