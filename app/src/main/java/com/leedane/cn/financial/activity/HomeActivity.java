package com.leedane.cn.financial.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.activity.ActionBarBaseActivity;
import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.adapter.PersonalFragmentPagerAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.customview.RightBorderTextView;
import com.leedane.cn.financial.adapter.FragmentPagerAdapter;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.fragment.MainFragment;
import com.leedane.cn.financial.fragment.MonthFragment;
import com.leedane.cn.financial.fragment.WeekFragment;
import com.leedane.cn.financial.fragment.YearFragment;
import com.leedane.cn.financial.fragment.YesterDayFragment;
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.DensityUtil;
import com.leedane.cn.util.ToastUtil;

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
 * 记账首页activity
 * Created by LeeDane on 2016/7/19.
 */
public class HomeActivity extends ActionBarBaseActivity {
    public static final String TAG = "HomeActivity";
    private int mCurrentTab;  //标记当前tab位置
    private ViewPager mViewPager;

    private MagicIndicator magicIndicator;

    private List<String> mTitleList = null;

    private int screenWidth = 0;
    /**
     * 标签的所有Fragment
     */
    private List<Fragment> mFragments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(HomeActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.HomeActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        init();
        initView();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_financial_home;
    }

    @Override
    protected String getLabel() {
        return getString(R.string.financial);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.financial_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Message message = new Message();
        switch(item.getItemId()){
            case R.id.financial_header_search://搜索
                Intent it_search = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(it_search);
                return true;
            case R.id.financial_header_location://地址
                Intent it_location = new Intent(HomeActivity.this, LocationActivity.class);
                startActivity(it_location);
                return true;
            case R.id.financial_header_category: //分类
                Intent it_category = new Intent(HomeActivity.this, OneLevelOperationActivity.class);
                startActivity(it_category);
                return true;
            case R.id.financial_header_setting: //设置
                Intent it_setting = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(it_setting);
                return true;
            case R.id.financial_header_cloud: //云端同步
                Intent it_cloud = new Intent(HomeActivity.this, CloudActivity.class);
                startActivityForResult(it_cloud, FlagUtil.SYNCHRONIZED_CLOUD_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 初始化试图控件
     */
    private void initView() {
        mViewPager = (ViewPager)findViewById(R.id.financial_viewpager);
        String tabText;
        for(int i = 0; i < mTitleList.size(); i++){
            tabText = mTitleList.get(i);
            if(tabText.equalsIgnoreCase(getStringResource(R.string.financial_home))){//首页
                Bundle bundle = new Bundle();
                mFragments.add(MainFragment.newInstance(bundle));
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.financial_yesterday))) {//昨日
                Bundle bundle = new Bundle();
                mFragments.add(YesterDayFragment.newInstance(bundle));
            } else if(tabText.equalsIgnoreCase(getStringResource(R.string.financial_week))) {//本周
                Bundle bundle = new Bundle();
                mFragments.add(WeekFragment.newInstance(bundle));
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.financial_month))) {//本月
                Bundle bundle = new Bundle();
                mFragments.add(MonthFragment.newInstance(bundle));
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.financial_year))) {//本年
                Bundle bundle = new Bundle();
                mFragments.add(YearFragment.newInstance(bundle));
            }
        }

        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitleList == null ? 0 : mTitleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(getColor(R.color.colorPrimary));
                colorTransitionPagerTitleView.setSelectedColor(Color.BLACK);
                colorTransitionPagerTitleView.setText(mTitleList.get(index));
                colorTransitionPagerTitleView.setMinWidth(screenWidth / mTitleList.size());
                colorTransitionPagerTitleView.setTextSize(14);
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), getBaseContext(), mFragments));
        mViewPager.setCurrentItem(mCurrentTab);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    /**
     * 列表滚动到顶部
     * @param view
     */
    private void smoothScrollToTop(View view){
        switch (view.getId()){
            case R.id.financial_home:  //首页
               ((MainFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.financial_yesterday: //昨日
               // ((CommentOrTransmitFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.financial_week: //本周
               // ((CommentOrTransmitFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.financial_month: //本月
                //((ZanFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.financial_year: //本年
                //((AttentionFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
        }
    }

    /**
     * 初始化参数
     */
    private void init() {
        mCurrentTab = getIntent().getIntExtra("currentTab", 0);
        mTitleList = new ArrayList<>();
        mTitleList.add(getStringResource(R.string.financial_home));
        mTitleList.add(getStringResource(R.string.financial_yesterday));
        mTitleList.add(getStringResource(R.string.financial_week));
        mTitleList.add(getStringResource(R.string.financial_month));
        mTitleList.add(getStringResource(R.string.financial_year));
        screenWidth = BaseApplication.newInstance().getScreenWidthAndHeight()[0];
    }

    /**
     * Tab的点击事件
     * @param index
     */
    public void tabClick(int index){
        mCurrentTab = index;

        if(index == mCurrentTab){
            return;
        }
        //设置tab的切换
        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(mCurrentTab);
            }
        }), 5);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case FlagUtil.IS_EDIT_OR_SAVE_FINANCIAL_CODE:
                if(data == null)
                    return;

                boolean hasUpdate = data.getBooleanExtra("hasUpdate", false);
                    if(hasUpdate){
                        List<Fragment> fragments = getSupportFragmentManager().getFragments();
                        for(Fragment fragment: fragments){
                            if(fragment instanceof MainFragment){
                                ((MainFragment)fragment).generateData();
                                break;
                            }
                        }
                }
                break;
        }
    }

}
