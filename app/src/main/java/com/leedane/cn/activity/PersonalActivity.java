package com.leedane.cn.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.leedane.cn.adapter.PersonalFragmentPagerAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.customview.RightBorderTextView;
import com.leedane.cn.fragment.AttentionFragment;
import com.leedane.cn.fragment.CollectionFragment;
import com.leedane.cn.fragment.CommentOrTransmitFragment;
import com.leedane.cn.fragment.LoginHistoryFragment;
import com.leedane.cn.fragment.PersonalFragment;
import com.leedane.cn.fragment.PersonalMoodFragment;
import com.leedane.cn.fragment.ScoreFragment;
import com.leedane.cn.fragment.ZanFragment;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.FanHandler;
import com.leedane.cn.handler.SignInHandler;
import com.leedane.cn.handler.UserHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.SerializableMap;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 个人中心activity
 * Created by LeeDane on 2015/10/17.
 */
public class PersonalActivity extends BaseActivity {
    public static final String TAG = "PersonalActivity";
    //标记对心情是否有修改(增加、评论、转发)
    public static final int MOOD_UPDATE_REQUEST_CODE = 180;

    /**
     * 页面切换的ViewPager对象
     */
    private ViewPager mViewPager;

    /**
     * 标签的所有Fragment
     */
    private List<Fragment> mFragments = new ArrayList<>();

    /**
     * 当前个人中心的用户
     */
    private int mUserId;

    /**
     * 登录的账号id
     */
    private int mLoginAccountId;

    /**
     * 当前是否是登录用户
     */
    private boolean mIsLoginUser;

    /**
     * 个人的头像
     */
    private CircularImageView mPersonalPic;

    private JSONObject mUserInfo;

    private TextView mPersonalFan;
    private TextView mPersonalSignIn;
    private TextView mPersonalInfo;
    private TextView mPersonalFans;

    /**
     * 发送心情的imageview
     */
    private ImageView mRightImg;
    private Intent currentIntent;
    private boolean isCheckFanOrSignIn = true;
    private int type = 0;

    private int mCurrentTab = 0;
    private List<String> mTitleList = null;
    private MagicIndicator magicIndicator;

    private int screenWidth = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(PersonalActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.PersonalActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        screenWidth = BaseApplication.newInstance().getScreenWidthAndHeight()[0];
        setContentView(R.layout.activity_personal);
        currentIntent = getIntent();
        //初始化数据
        initData();

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        //显示整个顶部的导航栏
        backLayoutVisible();
        //以用户名称作为个人中心的标题
        setTitleViewText(getStringResource(R.string.personal_title));

        //显示标题栏的发送心情的图片按钮
        mRightImg = (ImageView)findViewById(R.id.view_right_img);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setOnClickListener(this);

        type = currentIntent.getIntExtra("type", 0);

        mUserId = currentIntent.getIntExtra("userId", 0);
        mCurrentTab = currentIntent.getIntExtra("currentTab", 0);

        if(type == 0 && mUserId <= 0){
            ToastUtil.success(PersonalActivity.this, "该用户不存在");
            finish();
            return;
        }

        showLoadingDialog("", "Loading. Please wait...");

        try{
            //当前的账号是登录账号
            if(mUserId == mLoginAccountId){
                mIsLoginUser = true;
                initView();
            }else{
                //异步加载mUserId对应的用户信息
                asnyLoadUserInfo();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 异步加载mUserId对应的用户信息
     */
    private void asnyLoadUserInfo() {
        HashMap<String, Object> params = new HashMap<>();
        if(type == 1){
            params.put("searchUserIdOrAccount", currentIntent.getStringExtra("account"));
            params.put("type", 1);
        }else{
            params.put("searchUserIdOrAccount", mUserId);
        }
        UserHandler.asnyLoadUserInfo(this, params);
    }
    

    /**
     * 初始化视图
     */
    private void initView() throws Exception{
        mViewPager = (ViewPager) findViewById(R.id.personal_viewpager);
        mPersonalPic = (CircularImageView)findViewById(R.id.personal_pic);
        final String headPath = mUserInfo.getString("user_pic_path");
        if(StringUtil.isNotNull(headPath)){
            ImageCacheManager.loadImage(headPath, mPersonalPic);
        }
        mPersonalPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsLoginUser) {
                    CommonHandler.startUpdateHeaderActivity(PersonalActivity.this);
                }else {
                    if(StringUtil.isNotNull(headPath)) {
                        CommonHandler.startImageDetailActivity(PersonalActivity.this, headPath);
                    }
                }
            }
        });

        mPersonalInfo = (TextView)findViewById(R.id.personal_info);
        mPersonalFans = (TextView)findViewById(R.id.personal_fans);
        mPersonalSignIn = (TextView)findViewById(R.id.personal_sign_in);
        mPersonalFan = (TextView)findViewById(R.id.personal_add_fan);

        mPersonalFans.setOnClickListener(this);

        //以用户名称作为个人中心的标题
        if(StringUtil.isNull(mUserInfo.getString("account"))){
            setTitleViewText(getStringResource(R.string.personal_title));
        }else{
            setTitleViewText(mUserInfo.getString("account"));
        }

        isCheckFanOrSignIn = true;
        int size = 0;
        mTitleList = new ArrayList<>();
        if(mIsLoginUser){
            size = 8;
            mTitleList.add(getStringResource(R.string.personal_mood));
            mTitleList.add(getStringResource(R.string.personal_comment));
            mTitleList.add(getStringResource(R.string.personal_transmit));
            mTitleList.add(getStringResource(R.string.personal_praise));
            mTitleList.add(getStringResource(R.string.personal_attention));
            mTitleList.add(getStringResource(R.string.personal_collection));
            mTitleList.add(getStringResource(R.string.personal_login_history));
            mTitleList.add(getStringResource(R.string.personal_score));

            //当前的个人中心是登录用户，异步执行判断是否当天已经登录
            mPersonalSignIn.setVisibility(View.VISIBLE);
            mPersonalFan.setVisibility(View.GONE);
            SignInHandler.isSignIn(PersonalActivity.this);
        }else {
            //ToastUtil.success(PersonalActivity.this, "非登录用户");
            //把关注，收藏,私信隐藏掉
            size = 4;
            mTitleList.add(getStringResource(R.string.personal_mood));
            mTitleList.add(getStringResource(R.string.personal_comment));
            mTitleList.add(getStringResource(R.string.personal_transmit));
            mTitleList.add(getStringResource(R.string.personal_praise));
            //当前不是登录用户，即是查看别人的个人中心，异步判断和登录用户是否是好友
            mPersonalSignIn.setVisibility(View.GONE);
            mPersonalFan.setVisibility(View.VISIBLE);
            FanHandler.isFan(PersonalActivity.this, mUserId);
        }

        String tabText;
        for(int i = 0; i < mTitleList.size(); i++){
            tabText = mTitleList.get(i);
            if(tabText.equalsIgnoreCase(getStringResource(R.string.personal_mood))){//心情
                Bundle bundle = new Bundle();
                bundle.putInt("toUserId", mUserId);
                bundle.putBoolean("isLoginUser", mIsLoginUser);
                //mFragments.add(new PersonalMoodFragment(i, PersonalActivity.this, mUserId, mIsLoginUser));
                mFragments.add(PersonalMoodFragment.newInstance(bundle));
                continue;
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.personal_comment))) {//评论
                HashMap<String, Object> commentParams = new HashMap<>();
                commentParams.put("toUserId", mUserInfo.getInt("id"));
                SerializableMap serializableMap = new SerializableMap();
                serializableMap.setMap(commentParams);
                Bundle bundle = new Bundle();
                bundle.putSerializable("serializableMap", serializableMap);
                bundle.putBoolean("isComment", true);
                bundle.putBoolean("itemSingleClick", true);
                bundle.putBoolean("isLoginUser", mIsLoginUser);
                mFragments.add(CommentOrTransmitFragment.newInstance(bundle));
            } else if(tabText.equalsIgnoreCase(getStringResource(R.string.personal_transmit))) {//转发
                HashMap<String, Object> transmitParams = new HashMap<>();
                transmitParams.put("toUserId", mUserInfo.getInt("id"));
                SerializableMap serializableMap = new SerializableMap();
                serializableMap.setMap(transmitParams);
                Bundle bundle = new Bundle();
                bundle.putSerializable("serializableMap", serializableMap);
                bundle.putBoolean("isComment", false);
                bundle.putBoolean("itemSingleClick", true);
                bundle.putBoolean("isLoginUser", mIsLoginUser);
                mFragments.add(CommentOrTransmitFragment.newInstance(bundle));
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.personal_praise))) {//赞
                Bundle bundle = new Bundle();
                bundle.putSerializable("toUserId", mUserInfo.getInt("id"));
                bundle.putBoolean("isLoginUser", mIsLoginUser);
                mFragments.add(ZanFragment.newInstance(bundle));
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.personal_attention))) {//关注
                Bundle bundle = new Bundle();
                bundle.putInt("toUserId", mUserInfo.getInt("id"));
                bundle.putBoolean("isLoginUser", mIsLoginUser);
                mFragments.add(AttentionFragment.newInstance(bundle));
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.personal_collection))) {//收藏
                Bundle bundle = new Bundle();
                bundle.putInt("toUserId", mUserInfo.getInt("id"));
                bundle.putBoolean("isLoginUser", mIsLoginUser);
                mFragments.add(CollectionFragment.newInstance(bundle));
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.personal_login_history))) {//登录历史
                mFragments.add(LoginHistoryFragment.newInstance(new Bundle()));
            }else if(tabText.equalsIgnoreCase(getStringResource(R.string.personal_score))) {//积分历史
                mFragments.add(ScoreFragment.newInstance(new Bundle()));
            }else{
                mFragments.add(new PersonalFragment(i, PersonalActivity.this));
            }
        }

        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setIndicatorOnTop(true);
        //commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitleList == null ? 0 : mTitleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(Color.BLACK);
                colorTransitionPagerTitleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                colorTransitionPagerTitleView.setText(mTitleList.get(index));
                colorTransitionPagerTitleView.setMinWidth(mTitleList.size() < 5 ? screenWidth / mTitleList.size(): 200);
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
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(new PersonalFragmentPagerAdapter(getSupportFragmentManager(), getBaseContext(), mFragments));
        mViewPager.setCurrentItem(mCurrentTab);
        ViewPagerHelper.bind(magicIndicator, mViewPager);

        mPersonalSignIn.setOnClickListener(this);
        mPersonalFan.setOnClickListener(this);
        mPersonalInfo.setOnClickListener(this);
        dismissLoadingDialog();
    }

    /**
     * 检查是否登录
     */
    private void initData() {
        mUserInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());
        try {
            mLoginAccountId = mUserInfo.getInt("id");
        }catch (Exception e){
            Log.i(TAG, "获取缓存的用户名称为空");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 列表滚动到顶部
     * @param view
     */
    private void smoothScrollToTop(View view){
        switch (view.getId()){
            case R.id.personal_mood:  //心情
                ((PersonalMoodFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.personal_comment: //评论
                ((CommentOrTransmitFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.personal_transmit: //转发
                ((CommentOrTransmitFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.personal_praise: //赞
                ((ZanFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.personal_attention: //关注
                ((AttentionFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.personal_collection: //收藏
                ((CollectionFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.personal_login_history: //登录历史
                ((LoginHistoryFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
            case R.id.personal_sign_in_history: //积分
                ((ScoreFragment)mFragments.get(mCurrentTab)).smoothScrollToTop();
                break;
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);
        try{
            JSONObject jsonObject = new JSONObject(String.valueOf(result));
            //判断是否是好友或者是签到
            if(type == TaskType.IS_SIGN_IN || type == TaskType.IS_FAN){
                isCheckFanOrSignIn = false;
                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){
                    if(mIsLoginUser){
                        mPersonalSignIn.setText(getStringResource(R.string.personal_is_sign_in));
                        mPersonalSignIn.setVisibility(View.VISIBLE);
                        mPersonalFan.setVisibility(View.GONE);
                        //设置不可点击
                        mPersonalSignIn.setClickable(false);
                    }else{
                        mPersonalFan.setText(getStringResource(R.string.personal_is_fan));
                        mPersonalFan.setVisibility(View.VISIBLE);
                        mPersonalSignIn.setVisibility(View.GONE);
                        //设置不可点击
                        mPersonalFan.setClickable(false);
                    }
                    return;
                }
                mPersonalSignIn.setClickable(true);
                if(mIsLoginUser && jsonObject != null && jsonObject.optBoolean("isSuccess")){
                    mPersonalSignIn.setText(getStringResource(R.string.personal_sign_in));
                    mPersonalSignIn.setVisibility(View.VISIBLE);
                    mPersonalFan.setVisibility(View.GONE);
                    isCheckFanOrSignIn = false;
                    return;
                }

                if(!mIsLoginUser && jsonObject != null && !jsonObject.optBoolean("isSuccess")){
                    mPersonalFan.setText(getStringResource(R.string.personal_add_fan));
                    mPersonalFan.setVisibility(View.VISIBLE);
                    mPersonalSignIn.setVisibility(View.GONE);
                    isCheckFanOrSignIn = false;
                    return;
                }
                return;
            }
            //执行签到操作
            if(type == TaskType.DO_SIGN_IN){
                dismissLoadingDialog();
                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){
                    //ToastUtil.success(PersonalActivity.this, "签到成功");
                    //设置不可点击
                    mPersonalSignIn.setClickable(false);
                    mPersonalSignIn.setText(getStringResource(R.string.personal_is_sign_in));
                }else{
                    ToastUtil.failure(PersonalActivity.this, jsonObject);
                }
                return;
            }
            //执行添加关注操作
            if(type == TaskType.ADD_FAN){
                dismissLoadingDialog();
                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){
                    ToastUtil.success(PersonalActivity.this,jsonObject);
                    //设置不可点击
                    mPersonalFan.setClickable(false);
                    mPersonalFan.setText(getStringResource(R.string.personal_is_fan));
                }else{
                    ToastUtil.failure(PersonalActivity.this, jsonObject);
                }
                return;
            }
            //获取非登录用户的基本信息
            if(type == TaskType.LOAD_USER_INFO){
                dismissLoadingDialog();
                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){
                    mUserInfo = new JSONObject(jsonObject.getString("userinfo"));
                    mUserId = mUserInfo.getInt("id");
                    initView();
                }else{
                    ToastUtil.failure(PersonalActivity.this, jsonObject);
                }
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.personal_add_fan:
                if(!isCheckFanOrSignIn){
                    FanHandler.addAttention(PersonalActivity.this, mUserId);
                    showLoadingDialog("关注他/她", "正在关注他/她。。。");
                }
                break;
            case R.id.personal_sign_in:
                if(!isCheckFanOrSignIn){
                    SignInHandler.addSignIn(PersonalActivity.this);
                    showLoadingDialog("签到", "正在签到。。。");
                }
                break;
            case R.id.personal_info:
                Log.i(TAG, "点击查看基本信息");
                if(mIsLoginUser)
                    CommonHandler.startUserBaseActivity(PersonalActivity.this, BaseApplication.getLoginUserId());
                else
                    showPopUserInfoDialog();
                break;
            case R.id.personal_fans:
                Intent it_fan = new Intent(PersonalActivity.this, FanActivity.class);
                it_fan.putExtra("toUserId", mUserId);
                it_fan.putExtra("isLoginUser", mIsLoginUser);
                startActivity(it_fan);
                break;
            case R.id.view_right_img:
                Intent it_mood = new Intent();
                it_mood.setClass(PersonalActivity.this, MoodActivity.class);
                //it_mood.putExtra("publish", true);
                startActivityForResult(it_mood, MOOD_UPDATE_REQUEST_CODE);
                break;
        }
    }

    private Dialog mDialog;

    /**
     * 显示弹出自定义view
     */
    public void showPopUserInfoDialog(){
        mDialog = new Dialog(PersonalActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(PersonalActivity.this).inflate(R.layout.base_view_user_info, null);

        if(mUserInfo != null){
            try{
                if(mUserInfo.has("sex"))
                    ((TextView)view.findViewById(R.id.base_user_info_sex)).setText(StringUtil.changeNotNull(mUserInfo.getString("sex")));
                if(mUserInfo.has("birth_day")){/*
                    int age = 0;
                    try{
                        String birthDay = StringUtil.changeNotNull(mUserInfo.getString("birth_day"));
                        if(StringUtil.isNotNull(birthDay)){
                            age = DateUtil.getAge(DateUtil.stringToDate(birthDay));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }*/
                    ((TextView)view.findViewById(R.id.base_user_info_birthday)).setText(StringUtil.changeNotNull(mUserInfo.getString("birth_day")));
                }
                if(mUserInfo.has("email"))
                    ((TextView)view.findViewById(R.id.base_user_info_email)).setText(StringUtil.changeNotNull(mUserInfo.getString("email")));
                if(mUserInfo.has("mobile_phone"))
                    ((TextView)view.findViewById(R.id.base_user_info_mobile_phone)).setText(StringUtil.changeNotNull(mUserInfo.getString("mobile_phone")));
                if(mUserInfo.has("qq"))
                    ((TextView)view.findViewById(R.id.base_user_info_qq)).setText(StringUtil.changeNotNull(mUserInfo.getString("qq")));
                if(mUserInfo.has("personal_introduction"))
                    ((TextView)view.findViewById(R.id.base_user_info_personal_introduction)).setText(StringUtil.changeNotNull(mUserInfo.getString("personal_introduction")));
                if(mUserInfo.has("last_request_time")){
                    view.findViewById(R.id.base_user_info_personal_last_request).setVisibility(View.VISIBLE);
                    ((TextView)view.findViewById(R.id.base_user_info_personal_last_request_time)).setText(StringUtil.changeNotNull(mUserInfo.getString("last_request_time")));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //获得当前设备的屏幕宽度
        int screenWidth = dm.widthPixels;

        mDialog.setTitle("基本信息");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissPopUserInfoDialog();
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(screenWidth-100, 800);
        mDialog.setContentView(view, params);
        mDialog.show();
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissPopUserInfoDialog(){
        if(mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (data == null) { 
            data = new Intent();
        } */
        //super.startActivityForResult(data, requestCode);
        switch (requestCode){
            case MOOD_UPDATE_REQUEST_CODE: //是否有添加、评论和转发心情
                if (data != null) {
                    boolean isService = data.getBooleanExtra("isService", false);
                    int type = data.getIntExtra("type", -1);
                    if (isService) {
                        ToastUtil.success(PersonalActivity.this, "后台运行" + type);
                    } else {
                        ToastUtil.success(PersonalActivity.this, "非后台运行" + type);
                        List<Fragment> fragments = getSupportFragmentManager().getFragments();
                        for (Fragment fragment : fragments) {
                            if (fragment instanceof PersonalMoodFragment) {
                                ((PersonalMoodFragment) fragment).showRefresh();
                                ((PersonalMoodFragment) fragment).sendFirstLoading();
                                break;
                            }
                        }
                    }
                }
                break;
            default:
                //ToastUtil.failure(PersonalActivity.this, "other update");
                break;
        }
    }
}
