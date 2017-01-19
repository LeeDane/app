package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.leedane.cn.adapter.ChatFragmentPagerAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatBean;
import com.leedane.cn.database.BaseSQLiteDatabase;
import com.leedane.cn.fragment.ChatContactFragment;
import com.leedane.cn.fragment.ChatHomeFragment;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.service.LoadNoReadChatService;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天首页activity
 * Created by LeeDane on 2016/5/4.
 */
public class ChatActivity extends BaseCustomLayoutTabActivity implements ChatHomeFragment.OnItemClickListener{
    private static final String TAG = "ChatActivity";
    public static final int START_CHAT_DETAIL_CODE = 16; //聊天更新状态码
    public static final int BASE_USER_CHAT_DETAIL_CODE = 11000;//基本的用户聊天码
    private BaseSQLiteDatabase sqLiteDatabase;

    @Override
    protected List<LayoutViewObject> initTitleList() {
        List<LayoutViewObject> layoutViewObjects = new ArrayList<>();
        layoutViewObjects.add(new LayoutViewObject(getStringResource(R.string.chat), R.drawable.ic_home_indigo_500_18dp));
        layoutViewObjects.add(new LayoutViewObject(getStringResource(R.string.address_list), R.drawable.ic_comment_indigo_500_18dp));
        layoutViewObjects.add(new LayoutViewObject(getStringResource(R.string.oparate), R.drawable.ic_transmit_indigo_500_18dp));
        return layoutViewObjects;
    }

    @Override
    protected List<Fragment> initFragmentList() {
        List<Fragment> fragments = new ArrayList<>();
        ChatHomeFragment chatHomeFragment = ChatHomeFragment.newInstance(new Bundle());
        chatHomeFragment.setOnItemClickListener(ChatActivity.this);

        ChatContactFragment contactFragment = ChatContactFragment.newInstance(new Bundle());
        fragments.add(chatHomeFragment);
        fragments.add(contactFragment);
        fragments.add(null);
        return fragments;
    }

    @Override
    protected int getMagicIndicatorViewId() {
        return R.id.chat_magic_indicator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(ChatActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.ChatActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }
        setContentView(R.layout.activity_chat);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.chat);
        backLayoutVisible();
        sqLiteDatabase = new BaseSQLiteDatabase(ChatActivity.this);
        initView();
        initMagicIndicator();
    }
    /**
     * 初始化控件
     */
    private void initView() {
        super.mViewPager = (ViewPager)findViewById(R.id.chat_viewpager);
        super.mViewPager.setAdapter(new ChatFragmentPagerAdapter(getSupportFragmentManager(), initFragmentList()));
        super.mViewPager.setCurrentItem(0);
        super. mViewPager.setOffscreenPageLimit(3);
        initMagicIndicator();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent it_service = new Intent();
                it_service.setClass(getApplicationContext(), LoadNoReadChatService.class);
                it_service.setAction("com.leedane.cn.LoadNoReadChatService");
                it_service.putExtra("toUserId", BaseApplication.getLoginUserId());
                getApplicationContext().startService(it_service);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if(sqLiteDatabase != null)
            sqLiteDatabase.closeDataBase();
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position, ChatBean chatBean) {
        int toUserId  = 0;
        if(chatBean.getCreateUserId() == BaseApplication.getLoginUserId()){
            toUserId = chatBean.getToUserId();
        }else{
            toUserId = chatBean.getCreateUserId();
        }
        CommonHandler.startChatDetailActivity(ChatActivity.this, toUserId, chatBean.getAccount(), 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (resultCode == 0) {
            if (requestCode == START_CHAT_DETAIL_CODE && data != null){
                FragmentManager fragmentManager = getSupportFragmentManager();
                List<Fragment> list = fragmentManager.getFragments();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ChatHomeFragment chatHomeFragment = ChatHomeFragment.newInstance(new Bundle());
                chatHomeFragment.setOnItemClickListener(ChatActivity.this);
                //fragmentTransaction.replace(R.id.chat_viewpager, mFragments.get(data.getIntExtra("model", 0)));
                fragmentTransaction.replace(R.id.chat_viewpager, chatHomeFragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        }*/
    }
}
