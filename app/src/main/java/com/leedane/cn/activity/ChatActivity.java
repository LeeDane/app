package com.leedane.cn.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.leedane.cn.adapter.ChatFragmentPagerAdapter;
import com.leedane.cn.bean.ChatBean;
import com.leedane.cn.database.BaseSQLiteDatabase;
import com.leedane.cn.fragment.ChatHomeFragment;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天首页activity
 * Created by LeeDane on 2016/5/4.
 */
public class ChatActivity extends BaseActivity implements ChatHomeFragment.OnItemClickListener{
    private static final String TAG = "ChatActivity";
    private BaseSQLiteDatabase sqLiteDatabase;

    private ViewPager mViewPager;
    private List<Fragment> mFragments;

    //线的宽度
    private int mLineWidth;

    //偏移量
    private int mOffset;
    private int current_index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //检查是否登录
        checkedIsLogin("com.leedane.cn.activity.ChatActivity");

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.chat);
        backLayoutVisible();
        sqLiteDatabase = new BaseSQLiteDatabase(ChatActivity.this);
        initData();
        initView();
    }
    /**
     * 初始化数据
     */
    private void initData() {
        mFragments = new ArrayList<>();
        ChatHomeFragment chatHomeFragment = ChatHomeFragment.newInstance(new Bundle());
        chatHomeFragment.setOnItemClickListener(ChatActivity.this);
        mFragments.add(chatHomeFragment);
        mFragments.add(null);
        mFragments.add(null);
    }
    /**
     * 初始化控件
     */
    private void initView() {
        mViewPager = (ViewPager)findViewById(R.id.chat_viewpager);
        mViewPager.setAdapter(new ChatFragmentPagerAdapter(getSupportFragmentManager(), mFragments));
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int one = mOffset * 2 + mLineWidth;// 页卡1 -> 页卡2 偏移量
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {

                } else if (position == 1) {

                } else {

                }
                Animation animation = new TranslateAnimation(one * current_index, one * position, 0, 0);
                animation.setFillAfter(true);
                animation.setDuration(300);
                current_index = position;
                FragmentManager fragmentManager = getSupportFragmentManager();
                List<Fragment> list = fragmentManager.getFragments();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.show(list.get(position));
                fragmentTransaction.commit();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG, "onPageScrolled-->position: " + position + ", positionOffset:" + positionOffset + ", positionOffsetPixels:" + positionOffsetPixels);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, "onPageScrollStateChanged:" + state);
				/*if(state == 0){
					current_index = getSupportFragmentManager().getFragments().get
				}*/
            }
        });
        current_index  = 0;
        //ChatHomeFragment chatHomeFragment = ChatHomeFragment.newInstance(null);
        //getSupportFragmentManager().beginTransaction().replace(R.id.chat_container, chatHomeFragment).commit();
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);

    }

    @Override
    protected void onDestroy() {
        sqLiteDatabase.closeDataBase();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()){
        }
    }

    @Override
    public void onItemClick(int position, ChatBean chatBean) {
        ToastUtil.success(ChatActivity.this, chatBean.getAccount());
        CommonHandler.startChatDetailActivity(ChatActivity.this, chatBean.getId(), chatBean.getAccount());
    }
}
