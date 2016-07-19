package com.leedane.cn.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.leedane.cn.adapter.ChatFragmentPagerAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatBean;
import com.leedane.cn.customview.RightBorderTextView;
import com.leedane.cn.database.BaseSQLiteDatabase;
import com.leedane.cn.fragment.ChatContactFragment;
import com.leedane.cn.fragment.ChatHomeFragment;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.service.LoadNoReadChatService;
import com.leedane.cn.service.LoadUserInfoDataService;
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
    public static final int START_CHAT_DETAIL_CODE = 16; //聊天更新状态码
    public static final int BASE_USER_CHAT_DETAIL_CODE = 11000;//基本的用户聊天码
    private BaseSQLiteDatabase sqLiteDatabase;

    private ImageView mImageViewLine;
    private ViewPager mViewPager;
    private List<Fragment> mFragments;

    //线的宽度
    private int mLineWidth;

    private int tabWidth;

    private ChatFragmentPagerAdapter mChatFragmentAdapter;

    private LinearLayout mChatBottom;

    //偏移量
    private int mOffset;
    private int current_index;
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

        ChatContactFragment contactFragment = ChatContactFragment.newInstance(new Bundle());

        mFragments.add(chatHomeFragment);
        mFragments.add(contactFragment);
        mFragments.add(null);
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
    /**
     * 初始化控件
     */
    private void initView() {
        mChatBottom = (LinearLayout)findViewById(R.id.chat_bottom);
        mImageViewLine = (ImageView)findViewById(R.id.line_imageview);
        //初始化线图像
        initImageView();
        mViewPager = (ViewPager)findViewById(R.id.chat_viewpager);
        mChatFragmentAdapter = new ChatFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mChatFragmentAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            float positionOffsetOld;

            @Override
            public void onPageSelected(int position) {
                current_index = position;

                //更新Tab文字的颜色
                upDateTabTextColor();

                FragmentManager fragmentManager = getSupportFragmentManager();
                List<Fragment> list = fragmentManager.getFragments();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.show(list.get(position));
                fragmentTransaction.commit();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == 0.00 || positionOffset == 1.00 || positionOffset == 2.00) {
                    return;
                }
                Animation animation = new TranslateAnimation((position + positionOffsetOld) * tabWidth, (position + positionOffset) * tabWidth, 0, 0);
                animation.setFillAfter(true);
                animation.setDuration(20);
                mImageViewLine.startAnimation(animation);
                positionOffsetOld = positionOffset;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, "onPageScrollStateChanged:" + state);
            }
        });
        current_index  = 0;
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
    }

    /**
     * 聊天tab的点击
     * @param view
     */
    public void tabChatClick(View view){
        current_index = 0;
        mViewPager.setCurrentItem(current_index);
        upDateTabTextColor();
    }

    /**
     * 通讯录tab的点击
     * @param view
     */
    public void tabContactClick(View view){
        current_index = 1;
        mViewPager.setCurrentItem(current_index);
        upDateTabTextColor();
    }

    /**
     * 操作tab的点击
     * @param view
     */
    public void tabOperateClick(View view){
        current_index = 2;
        mViewPager.setCurrentItem(current_index);
        upDateTabTextColor();
    }

    /**
     * 更新Tab文字的颜色
     */
    private void upDateTabTextColor(){
        RightBorderTextView textView;
        for (int i = 0; i < 3; i++) {
            if (i == current_index) {
                textView = (RightBorderTextView) mChatBottom.getChildAt(current_index);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                textView = (RightBorderTextView) mChatBottom.getChildAt(i);
                textView.setTextColor(getResources().getColor(R.color.gray));
            }
        }
    }

    /**
     * 初始化创建线的图像
     */
    private void initImageView() {
        //获得当前设备的屏幕宽度
        int screenWidth = BaseApplication.newInstance().getScreenWidthAndHeight()[0];

        //设置线性的图像的宽度为1/3的屏幕宽度
        ViewGroup.LayoutParams params = mImageViewLine.getLayoutParams();
        int w = (int)screenWidth/3;
        Log.i(TAG, "screenWidth:"+w);
        tabWidth = w;
        params.width = w;
        //获取图片宽度
        mLineWidth = BitmapFactory.decodeResource(getResources(), R.drawable.line).getWidth();
        Log.i(TAG, "mLineWidth:"+w);
        Matrix matrix = new Matrix();
        mOffset = (int) ((screenWidth/(float)3 - mLineWidth)/2);
        matrix.postTranslate(mOffset, 0);
        //设置初始位置
        mImageViewLine.setImageMatrix(matrix);
    }

    @Override
    public void onItemClick(int position, ChatBean chatBean) {
        int toUserId  = 0;
        if(chatBean.getCreateUserId() == BaseApplication.getLoginUserId()){
            toUserId = chatBean.getToUserId();
        }else{
            toUserId = chatBean.getCreateUserId();
        }
        startForChatDetailActivity(toUserId, chatBean.getAccount(), 0);

    }

    /**
     *
     * @param toUserId
     * @param account
     * @param model 0:首页， 1：联系人列表，2:未知
     */
    public void startForChatDetailActivity(int toUserId, String account, int model){
        Intent it = new Intent(ChatActivity.this, ChatDetailActivity.class);
        it.putExtra("toUserId", toUserId);
        it.putExtra("toUserAccount", account);
        it.putExtra("model", model);
        startActivityForResult(it, START_CHAT_DETAIL_CODE);
        //context.startActivity(it);
        //CommonHandler.startChatDetailActivity(ChatActivity.this, toUserId, chatBean.getAccount());
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
