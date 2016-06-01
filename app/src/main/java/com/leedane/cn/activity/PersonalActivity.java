package com.leedane.cn.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SerializableMap;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.util.http.HttpConnectionUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 个人中心activity
 * Created by LeeDane on 2015/10/17.
 */
public class PersonalActivity extends BaseActivity {
    public static final String TAG = "PersonalActivity";
    public static final int MOOD_COMMENT_REQUEST_CODE = 102;

    /**
     * 水平滑动的view
     */
    private HorizontalScrollView mScrollview;

    /**
     * 水平滑动的单选按钮组view
     */
    private RadioGroup mRadioGroup;

    /**
     * 页面切换的ViewPager对象
     */
    private ViewPager mViewPager;

    /**
     *  标签的总数
     */
    private int mTotalTabs = 5;

    /**
     * 当上一个的Tab索引
     */
    private int mPreTab = 0;
    /**
     * 当前的Tab索引
     */
    private int mCurrentTab = 0;

    /**
     * 标签的所有Fragment
     */
    private List<Fragment> mFragments = new ArrayList<>();
    /**
     * tab的宽度
     */
    private int mTabWidth;

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

        setContentView(R.layout.activity_personal);
        currentIntent = getIntent();
        //初始化数据
        initData();

        //显示标题栏的发送心情的图片按钮
        mRightImg = (ImageView)findViewById(R.id.view_right_img);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setOnClickListener(this);

        mUserId = currentIntent.getIntExtra("userId", 0);
        mCurrentTab = currentIntent.getIntExtra("currentTab", 0);

        if(mUserId <= 0){
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
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg != null && msg.what == 1){
                    try{
                        JSONObject resultObject = new JSONObject(String.valueOf(msg.obj));
                        if(resultObject.has("isSuccess")){
                            Log.i(TAG, "服务器返回的信息：" + resultObject.getString("userinfo"));
                            mUserInfo = new JSONObject(resultObject.getString("userinfo"));

                            initView();
                            return;
                        }else {
                            ToastUtil.failure(PersonalActivity.this, resultObject);
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                        dismissLoadingDialog();
                        Log.i(TAG, "获取用户信息失败");
                    }
                }
                //其他情况关闭当前的activity
                //finish();
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                String searchUserInfoUrl = SharedPreferenceUtil.getSettingBean(getBaseContext(), ConstantsUtil.STRING_SETTING_BEAN_SERVER).getContent() + "leedane/user_searchUserByUserId.action";
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("searchUserId", mUserId);
                params.putAll(BaseApplication.newInstance().getBaseRequestParams());
                String responseStr = "{\"isSuccess\": false, \"message\": \"获取服务器信息失败\"}";
                try {
                    responseStr = HttpConnectionUtil.sendPostRequest(searchUserInfoUrl, params, 0, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Message message = new Message();
                message.obj = responseStr;
                message.what=1;
                handler.sendMessage(message);
            }
        }).start();
    }
    

    /**
     * 初始化视图
     */
    private void initView() throws Exception{
        mViewPager = (ViewPager) findViewById(R.id.personal_viewpager);
        mScrollview = (HorizontalScrollView)findViewById(R.id.personal_scrollview);
        mRadioGroup = (RadioGroup) findViewById(R.id.personal_tabs);
        mPersonalPic = (CircularImageView)findViewById(R.id.personal_pic);

        if(mIsLoginUser){
            mPersonalPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startUpdateHeaderActivity(PersonalActivity.this);
                }
            });
        }

        mPersonalInfo = (TextView)findViewById(R.id.personal_info);
        mPersonalFans = (TextView)findViewById(R.id.personal_fans);
        mPersonalSignIn = (TextView)findViewById(R.id.personal_sign_in);
        mPersonalFan = (TextView)findViewById(R.id.personal_add_fan);

        mPersonalFans.setOnClickListener(this);
        //mPersonalPic.setImageBitmap(ImageUtil.getInstance().getBitmapByBase64());
        String headPath = mUserInfo.getString("user_pic_path");
        if(StringUtil.isNotNull(headPath)){
            ImageCacheManager.loadImage(headPath, mPersonalPic);
        }else{
            mPersonalPic.setVisibility(View.GONE);
        }
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));

        //以用户名称作为个人中心的标题
        if(StringUtil.isNull(mUserInfo.getString("account"))){
            setTitleViewText(getStringResource(R.string.personal_title));
        }else{
            setTitleViewText(mUserInfo.getString("account"));
        }

        if(mCurrentTab > 0 ){
            //将第一个的背景颜色变成默认的
            TextView preTextView = (TextView) mRadioGroup.getChildAt(0);
            preTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.default_font));
        }


        //将当前tab的背景颜色变成默认的
        TextView currentTextView = (TextView) mRadioGroup.getChildAt(mCurrentTab);
        currentTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));

        //获取当前点击tab的索引
        mCurrentTab = getTabPosition(currentTextView);

        //获得当前tab的坐标
        getCurrentTabCoordinate(currentTextView);

        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                mScrollview.scrollTo(mTabWidth * mCurrentTab, 0);
            }
        }), 5);

        isCheckFanOrSignIn = true;
        if(mIsLoginUser){
            mTotalTabs = mRadioGroup.getChildCount();
            //当前的个人中心是登录用户，异步执行判断是否当天已经登录
            mPersonalSignIn.setVisibility(View.VISIBLE);
            mPersonalFan.setVisibility(View.GONE);
            SignInHandler.isSignIn(PersonalActivity.this);
        }else{
            //ToastUtil.success(PersonalActivity.this, "非登录用户");
            //把关注，收藏,私信隐藏掉
            mRadioGroup.removeViewsInLayout(4, 4);
            mTotalTabs = mRadioGroup.getChildCount();//获取当前剩下的标签数量
            //当前不是登录用户，即是查看别人的个人中心，异步判断和登录用户是否是好友
            mPersonalSignIn.setVisibility(View.GONE);
            mPersonalFan.setVisibility(View.VISIBLE);
            FanHandler.isFan(PersonalActivity.this, mUserId);
        }

        //显示整个顶部的导航栏
        backLayoutVisible();
        String tabText;
        for(int i = 0; i < mRadioGroup.getChildCount(); i++){
            tabText = ((RightBorderTextView)mRadioGroup.getChildAt(i)).getText().toString();
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

        mViewPager.setAdapter(new PersonalFragmentPagerAdapter(getSupportFragmentManager(), getBaseContext(), mFragments));
        mViewPager.setCurrentItem(mCurrentTab);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                //将上一个tab的背景颜色变成默认的
                TextView preTextView = (TextView) mRadioGroup.getChildAt(mCurrentTab);
                preTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.default_font));
                //preTextView.setBackground(ContextCompat.getDrawable(PersonalActivity.this, R.drawable.btn_default_no_seletorbg));

                mPreTab = mCurrentTab;
                mCurrentTab = position;
                Log.i(TAG, "上一个位置是：" + mPreTab + ",当前的位置是：" + mCurrentTab);
                TextView currentTextView = (TextView) mRadioGroup.getChildAt(position);
                //获得当前tab的坐标
                getCurrentTabCoordinate(currentTextView);
                currentTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                //currentTextView.setBackground(ContextCompat.getDrawable(PersonalActivity.this, R.drawable.btn_default_seletorbg));

                //设置tab的切换
                //mScrollview.smoothScrollTo(80, 0);
                new Handler().postDelayed((new Runnable() {
                    @Override
                    public void run() {
                        mScrollview.scrollTo(mTabWidth * mCurrentTab, 0);
                    }
                }), 5);

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG, "onPageScrolled-->position: " + position + ", positionOffset:" + positionOffset + ", positionOffsetPixels:" + positionOffsetPixels);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, "onPageScrollStateChanged:" + state);
            }
        });

        mPersonalSignIn.setOnClickListener(this);
        mPersonalFan.setOnClickListener(this);
        mPersonalInfo.setOnClickListener(this);
        dismissLoadingDialog();
    }

    /**
     * 获取当前tab的坐标
     */
    private void getCurrentTabCoordinate(TextView tabView) {
        if(tabView == null){
            ToastUtil.success(PersonalActivity.this, "TextView为空，无法计算X,Y坐标");
            return;
        }
        mTabWidth = tabView.getLayoutParams().width;
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
     * Tab的点击事件
     * @param view
     */
    public void tabClick(View view){
        TextView currentTextView = (TextView)view;
        currentTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        mPreTab = mCurrentTab;
        //设置上一个tab的字体背景为灰色
        TextView preTextView = (TextView)mRadioGroup.getChildAt(mPreTab);
        preTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.default_font));

        //获取当前点击tab的索引
        mCurrentTab = getTabPosition(currentTextView);

        //获得当前tab的坐标
        getCurrentTabCoordinate(currentTextView);

        //设置tab的切换
        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                mScrollview.scrollTo(mTabWidth * mCurrentTab, 0);
                mViewPager.setCurrentItem(mCurrentTab);
            }
        }), 5);

        switch (view.getId()){
            case R.id.personal_mood:  //心情

                break;
            case R.id.personal_attention: //关注

                break;
            case R.id.personal_fans: //粉丝

                break;
            case R.id.personal_comment: //评论

                break;
            case R.id.personal_transmit: //转发

                break;
            case R.id.personal_praise: //赞

                break;
        }
    }

    /**
     * 获取当前点击tab的索引
     * @param view
     * @return
     */
    private int getTabPosition(TextView view) {

        return mRadioGroup != null ? mRadioGroup.indexOfChild(view) : 0;
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);
        try{
            JSONObject jsonObject = new JSONObject(String.valueOf(result));
            //判断是否是好友或者是签到
            if(type == TaskType.IS_SIGN_IN || type == TaskType.IS_FAN){
                isCheckFanOrSignIn = false;
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
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
                if(mIsLoginUser && jsonObject != null && jsonObject.has("isSuccess") && !jsonObject.getBoolean("isSuccess")){
                    mPersonalSignIn.setText(getStringResource(R.string.personal_sign_in));
                    mPersonalSignIn.setVisibility(View.VISIBLE);
                    mPersonalFan.setVisibility(View.GONE);
                    isCheckFanOrSignIn = false;
                    return;
                }

                if(!mIsLoginUser && jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == false){
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
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
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
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(PersonalActivity.this,jsonObject);
                    //设置不可点击
                    mPersonalFan.setClickable(false);
                    mPersonalFan.setText(getStringResource(R.string.personal_is_fan));
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
                startActivity(it_mood);
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
                if(mUserInfo.has("age"))
                    ((TextView)view.findViewById(R.id.base_user_info_age)).setText(StringUtil.changeNotNull(mUserInfo.getString("age")));
                if(mUserInfo.has("email"))
                    ((TextView)view.findViewById(R.id.base_user_info_email)).setText(StringUtil.changeNotNull(mUserInfo.getString("email")));
                if(mUserInfo.has("mobile_phone"))
                    ((TextView)view.findViewById(R.id.base_user_info_mobile_phone)).setText(StringUtil.changeNotNull(mUserInfo.getString("mobile_phone")));
                if(mUserInfo.has("qq"))
                    ((TextView)view.findViewById(R.id.base_user_info_qq)).setText(StringUtil.changeNotNull(mUserInfo.getString("qq")));
                if(mUserInfo.has("personal_introduction"))
                    ((TextView)view.findViewById(R.id.base_user_info_personal_introduction)).setText(StringUtil.changeNotNull(mUserInfo.getString("personal_introduction")));

                if(mUserInfo.has("last_request_time") && !mIsLoginUser){
                    ((LinearLayout)view.findViewById(R.id.base_user_info_personal_last_request)).setVisibility(View.VISIBLE);
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
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        switch (requestCode){
            case MOOD_COMMENT_REQUEST_CODE:
                //ToastUtil.success(PersonalActivity.this, "comment update");
                break;
            default:
                //ToastUtil.failure(PersonalActivity.this, "other update");
                break;
        }

    }
}
