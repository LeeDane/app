package com.leedane.cn.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.leedane.cn.adapter.HomeAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.database.ChatDataBase;
import com.leedane.cn.database.SearchHistoryDataBase;
import com.leedane.cn.financial.Helper.OnStartDragListener;
import com.leedane.cn.financial.Helper.SimpleItemTouchHelperCallback;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.helper.DoubleClickExitHelper;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
/**
 * 新版首页
 */
public class HomeActivity extends BaseActivity
        implements View.OnClickListener, OnStartDragListener,
        BaseRecyclerViewAdapter.OnItemClickListener, BaseRecyclerViewAdapter.OnItemLongClickListener{
    public static final String TAG = "MainNewActivity";

    private JSONObject mUserInfo;
    private int mLoginAccountId;

    private DoubleClickExitHelper mDoubleClickExit;

    //跳转到登录页面的请求码
    public static final int LOGIN_REQUEST_CODE = 100;

    private RecyclerView mRecyclerView;
    private HomeAdapter homeAdapter;
    private GridLayoutManager mGridLayoutManager;
    private SimpleItemTouchHelperCallback callback;
    private ItemTouchHelper mItemTouchHelper;

    private ImageView mShowMenuList;
    private ImageView mUserImg;
    private TextView mUserName;
    private TextView mUserDesc;
    private TextView mUserLogin;
    private PopupWindow mMenuWindow;

    public static final int TYPE_BLOG_ID = 1;
    public static final int TYPE_MOOD_ID = 2;
    public static final int TYPE_FINANCIAL_ID = 3;
    public static final int TYPE_MESSAGE_ID = 4;
    public static final int TYPE_USER_ID = 5;
    public static final int TYPE_GALLERY_ID = 6;
    public static final int TYPE_FRIENDCIRCLE_ID = 7;
    public static final int TYPE_CHAT_ID = 8;
    public static final int TYPE_FRIENDS_ID = 9;
    public static final int TYPE_SETTING_ID = 10;
    public static final int TYPE_CIRCLE_ID = 11;
    public static final int TYPE_STOCK_ID = 12;
    public static final int TYPE_MATERIAL_ID = 13;

    public final static int MY_PERMISSIONS_REQUEST_ROLE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lee_home);
        setImmerseLayout(findViewById(R.id.home_root));
        //setTitleViewText(R.string.chat);
        findViewById(R.id.baeselayout_navbar).setVisibility(View.GONE);

        checkRole();

        //初始化控件
       // initView();
       // initJPush();
    }

    /**
     * 检查读取手机状态权限
     * 检查写入文件的权限
     * 检查往SD操作的权限
     */
    private void checkRole(){
        //
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE
                            ,Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                            ,Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_ROLE);
        } else
        {
            initView();
            initJPush();
        }
    }
    /**
     * 初始化极光推送
     */
    private void initJPush(){
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        /*BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(MainActivity.this);
        builder.
        JPushInterface.setDefaultPushNotificationBuilder();*/
        JPushInterface.setAlias(getApplicationContext(), "leedane_user_" + mLoginAccountId, new TagAliasCallback() {
            @Override
            public void gotResult(int responseCode, String s, Set<String> set) {
                //ToastUtil.success(getApplicationContext(), mLoginAccountId + ",responseCode=" + responseCode + ",s=" + s + ",set=" + set);
                ToastUtil.success(getApplicationContext(), "消息推送已连接！");
            }
        });
    }

    private List<Map<String, Object>> buildMenuData(){
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_blog);
        mp.put("label", "博客");
        mp.put("mustLogin", false);
        mp.put("desc", "查看博客的列表");
        mp.put("id", TYPE_BLOG_ID);
        mp.put("num", 0);
        list.add(mp);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_mood);
        mp.put("label", "心情");
        mp.put("mustLogin", true);
        mp.put("desc", "查看心情的列表");
        mp.put("id", TYPE_MOOD_ID);
        mp.put("num", 0);
        list.add(mp);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_financial);
        mp.put("label", "记账");
        mp.put("mustLogin", true);
        mp.put("desc", "查看我的记账记录");
        mp.put("id", TYPE_FINANCIAL_ID);
        mp.put("num", 3);
        list.add(mp);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_message);
        mp.put("label", "消息");
        mp.put("mustLogin", true);
        mp.put("desc", "查看我的消息列表");
        mp.put("id", TYPE_MESSAGE_ID);
        mp.put("num", 4);
        list.add(mp);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_userbase);
        mp.put("label", "个人中心");
        mp.put("mustLogin", true);
        mp.put("desc", "查看我的个人中心");
        mp.put("id", TYPE_USER_ID);
        mp.put("num", 0);
        list.add(mp);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_gallery);
        mp.put("label", "图库");
        mp.put("mustLogin", true);
        mp.put("desc", "查看我的图库");
        mp.put("id", TYPE_GALLERY_ID);
        mp.put("num", 0);
        list.add(mp);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_friends);
        mp.put("label", "朋友圈");
        mp.put("mustLogin", true);
        mp.put("desc", "查看我的朋友圈的动态列表");
        mp.put("id", TYPE_FRIENDCIRCLE_ID);
        mp.put("num", 0);
        list.add(mp);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_circle);
        mp.put("label", "圈子");
        mp.put("mustLogin", true);
        mp.put("desc", "查看圈子");
        mp.put("id", TYPE_CIRCLE_ID);
        mp.put("num", 0);
        list.add(mp);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_chat);
        mp.put("label", "聊天");
        mp.put("mustLogin", true);
        mp.put("desc", "聊天功能");
        mp.put("id", TYPE_CHAT_ID);
        mp.put("num", 6);
        list.add(mp);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_friend);
        mp.put("label", "我的好友");
        mp.put("mustLogin", true);
        mp.put("desc", "查看我的好友列表");
        mp.put("id", TYPE_FRIENDS_ID);
        mp.put("num", 0);
        list.add(mp);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_setting);
        mp.put("label", "设置");
        mp.put("mustLogin", false);
        mp.put("desc", "查看设置");
        mp.put("id", TYPE_SETTING_ID);
        mp.put("num", 0);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_stock);
        mp.put("label", "股票");
        mp.put("mustLogin", true);
        mp.put("desc", "股票");
        mp.put("id", TYPE_STOCK_ID);
        mp.put("num", 0);

        mp = new HashMap<>();
        mp.put("img", R.mipmap.home_icon_material);
        mp.put("label", "素材");
        mp.put("mustLogin", true);
        mp.put("desc", "素材管理");
        mp.put("id", TYPE_MATERIAL_ID);
        mp.put("num", 0);

        list.add(mp);
        return list;
    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(getApplicationContext());
        super.onResume();
        updateShowUserinfo();
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(getApplicationContext());
        super.onPause();
    }
    /**
     * 检查是否加载远程服务器上的数据
     */
    private boolean checkedIsLoadServerBlog() {
        return true;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mDoubleClickExit = new DoubleClickExitHelper(this);
        mShowMenuList = (ImageView)findViewById(R.id.show_menu_list);

        mShowMenuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View contentView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.activity_home_menu_list,null);
               // ToastUtil.success(HomeActivity.this);
                //扫一扫
                contentView.findViewById(R.id.sao_yi_sao).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissPopupWindow();
                        CommonHandler.startMipcaActivityCapture(HomeActivity.this);
                    }
                });

                int windowHeight = 600;
                if(checkedIsLogin()) {
                    //附近人
                    contentView.findViewById(R.id.nearby).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismissPopupWindow();
                            CommonHandler.startNearByActivity(HomeActivity.this);
                        }
                    });

                    //退出
                    contentView.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //ToastUtil.failure(HomeActivity.this);
                            showAlertDialog();
                        }
                    });
                }else{
                    contentView.findViewById(R.id.nearby).setVisibility(View.GONE);
                    contentView.findViewById(R.id.logout).setVisibility(View.GONE);
                    windowHeight = 300;
                }

                //分享
                contentView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissPopupWindow();
                        ToastUtil.success(HomeActivity.this, "点击分享");
                    }
                });

                //View popupView = getLayoutInflater().inflate(R.layout.popupwindow, null);
                mMenuWindow = new PopupWindow(contentView, 500, windowHeight);
                // 设置动画
                mMenuWindow.setAnimationStyle(R.style.CustomPopWindowStyle);
                mMenuWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
                mMenuWindow.setFocusable(true);
                mMenuWindow.setOutsideTouchable(true);
                mMenuWindow.update();
                mMenuWindow.showAsDropDown(mShowMenuList, 0, 20);
                /*CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(HomeActivity.this)
                        .setView(contentView)
                        .setFocusable(true)
                        .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                        .setBgDarkAlpha(0.7f) // 控制亮度
                        //.size(500, 500)
                        .setOutsideTouchable(true)
                        .setAnimationStyle(R.style.CustomPopWindowStyle) // 添加自定义显示和消失动画
                        .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                //((WindowManager)HomeActivity.this.getSystemService(Context.WINDOW_SERVICE)).removeView(contentView);
                            }
                        })
                        .create()
                        .showAsDropDown(mShowMenuList, 0, 10);*/
            }
        });

        mUserImg = (ImageView)findViewById(R.id.home_user_img);
        mUserName = (TextView) findViewById(R.id.home_user_name);
        mUserDesc = (TextView)findViewById(R.id.home_user_desc);
        mUserLogin = (TextView)findViewById(R.id.home_user_login);

        List<Map<String, Object>> list = SharedPreferenceUtil.getDesktopData(HomeActivity.this);
        if(list == null)
            list = buildMenuData();
        updateShowUserinfo();

        mRecyclerView = (RecyclerView) findViewById(R.id.home_list_recycleview);
        homeAdapter = new HomeAdapter(HomeActivity.this, list, checkedIsLogin(), this);
        mGridLayoutManager = new GridLayoutManager(HomeActivity.this, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(HomeActivity.this, GridLayoutManager.VERTICAL, R.color.darker_gray));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(HomeActivity.this, GridLayoutManager.HORIZONTAL, R.color.darker_gray));
        mRecyclerView.setAdapter(homeAdapter);

        callback = new SimpleItemTouchHelperCallback(homeAdapter);
        callback.setIsCanDrag(true);
        callback.setIsCanSwipe(true);

        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        homeAdapter.setOnItemClickListener(this);
        homeAdapter.setOnItemLongClickListener(this);
        //检查是否加载远程服务器上的数据
        if(checkedIsLoadServerBlog()){

        }
    }

    /**
     * 弹出确定是否退出
     */
    public void showAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("当前您已登录，确认是否要退出登录？")
                .setCancelable(false)
                .setPositiveButton("退出登录", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dismissPopupWindow();
                        clearCache();
                        initView();
                    }

                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }

    /**
     * 隐藏弹出列表框
     */
    private void dismissPopupWindow(){
        if(mMenuWindow != null && mMenuWindow.isShowing())
            mMenuWindow.dismiss();
    }

    /**
     * 退出清空缓存
     */
    private void clearCache(){
        SharedPreferenceUtil.clearUserInfo(HomeActivity.this);
        SharedPreferenceUtil.clearFriends(HomeActivity.this);
        ChatDataBase dataBase = new ChatDataBase(HomeActivity.this);
        dataBase.deleteAll();
        dataBase.destroy();

                       /* MoodDataBase moodDataBase = new MoodDataBase(LoginActivity.this);
                        moodDataBase.deleteAll();
                        moodDataBase.destroy();*/

                        /*GalleryDataBase galleryDataBase = new GalleryDataBase((LoginActivity.this));
                        galleryDataBase.deleteAll();
                        galleryDataBase.destroy();*/

        SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(HomeActivity.this);
        searchHistoryDataBase.deleteAll();
        searchHistoryDataBase.destroy();

        /*MySettingDataBase mySettingDataBase = new MySettingDataBase(LoginActivity.this);
        mySettingDataBase.deleteAll();
        mySettingDataBase.destroy();*/
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //mAdapter.notifyDataSetChanged();
    }

    /**
     * 更新展示用户的信息
     */
    private void updateShowUserinfo() {

        mUserInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());
        /*if(homeAdapter != null) {
            List<Map<String, Object>> li = homeAdapter.getmDatas();
            ToastUtil.success(HomeActivity.this, "---"+li.get(1).get("label"));
            System.out.print(li.get(0));
            int i = 0;
        }*/
        //控件还没有加载完成
        if(mUserLogin == null )
            return;
        //判断是否有缓存用户信息
        if(mUserInfo != null && mUserInfo.has("account")){
            mUserLogin.setVisibility(View.GONE);
            mUserName.setVisibility(View.VISIBLE);
            mUserDesc.setVisibility(View.VISIBLE);
            if(mUserInfo.has("pic_base64") && !StringUtil.isNull(mUserInfo.optString("pic_base64"))){
                Bitmap bitmap = null;
                try {
                    bitmap = ImageUtil.getInstance().getBitmapByBase64(mUserInfo.optString("pic_base64"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(bitmap != null){
                    mUserImg.setImageBitmap(bitmap);
                }else{
                    mUserImg.setImageResource(R.mipmap.head);
                }
            }

            if(mUserInfo.has("user_pic_path") && !StringUtil.isNull(mUserInfo.optString("user_pic_path"))){
                ImageCacheManager.loadImage(mUserInfo.optString("user_pic_path"), mUserImg );
            }else{
                mUserImg.setImageResource(R.drawable.no_pic);
            }
            mUserImg.setOnClickListener(this);
            mLoginAccountId = mUserInfo.optInt("id");
            mUserName.setText(mUserInfo.optString("account"));
            mUserDesc.setText(mUserInfo.optString("personal_introduction"));
        }else{
            mUserLogin.setVisibility(View.VISIBLE);
            mUserLogin.setOnClickListener(this);
            mUserName.setVisibility(View.GONE);
            mUserDesc.setVisibility(View.GONE);
            mUserImg.setImageResource(R.drawable.no_pic);
        }
    }

    /**
     * 获取字符串资源
     * @param resourseId
     * @return
     */
    protected String getStringResource(int resourseId){
        return getResources().getString(resourseId);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // return true;//返回真表示返回键被屏蔽掉
            if(MySettingConfigUtil.double_click_out){
                return mDoubleClickExit.onKeyDown(keyCode, event);
            }else{
                createLeaveAlertDialog();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 创建离开的警告提示框
     */
    public void createLeaveAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("离开本系统")
                .setMessage("残忍离开？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                })
                .setNegativeButton("点错了",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }

    @Override
    public void onClick(View v) {
        int clickViewId = v.getId();
        switch (clickViewId){
            case R.id.home_user_img:
                /*ToastUtil.success(MainActivity.this, "点击头像");*/
                CommonHandler.startUpdateHeaderActivity(HomeActivity.this);
                break;
            case R.id.home_user_login:
               // ToastUtil.success(HomeActivity.this, "将跳转到登录页面");
                Intent it = new Intent();
                it.setClass(HomeActivity.this, LoginActivity.class);
                startActivityForResult(it, HomeActivity.LOGIN_REQUEST_CODE);
                break;
            case R.id.show_menu_list:

                break;
            default:
                ToastUtil.success(HomeActivity.this, "未知点击事件");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case HomeActivity.LOGIN_REQUEST_CODE:
                initJPush();
                initView();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position, Object data) {
       // ToastUtil.success(HomeActivity.this, "onItemClick");
        Intent intent = null;
        switch ((int)data) {
            case TYPE_BLOG_ID: //
                startActivity(new Intent(HomeActivity.this, BlogActivity.class));
                break;
            case TYPE_MOOD_ID: //
                CommonHandler.startPersonalActivity(HomeActivity.this, BaseApplication.getLoginUserId());
                break;
            case TYPE_FINANCIAL_ID: //
                startActivity(new Intent(HomeActivity.this, com.leedane.cn.financial.activity.HomeActivity.class));
                break;
            case TYPE_MESSAGE_ID: //
                CommonHandler.startMyMessageActivity(HomeActivity.this);
                break;
            case TYPE_USER_ID: //
                CommonHandler.startUserInfoActivity(HomeActivity.this);
                break;
            case TYPE_GALLERY_ID: //
                startActivity(new Intent(HomeActivity.this, GalleryActivity.class));
                break;
            case TYPE_FRIENDCIRCLE_ID: //
                startActivity(new Intent(HomeActivity.this, CircleOfFriendActivity.class));
                break;
            case TYPE_CHAT_ID: //
                startActivity(new Intent(HomeActivity.this, ChatActivity.class));
                break;
            case TYPE_FRIENDS_ID: //
                startActivity(new Intent(HomeActivity.this, FriendActivity.class));
                break;
            case TYPE_SETTING_ID: //
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                break;
            case TYPE_CIRCLE_ID: //
                intent = new Intent(HomeActivity.this, WebActivity.class);
                intent.putExtra("ref", "/cc");
                startActivity(intent);
                break;

            case TYPE_STOCK_ID: //
                intent = new Intent(HomeActivity.this, WebActivity.class);
                intent.putExtra("ref", "/stock");
                startActivity(intent);
                break;

            case TYPE_MATERIAL_ID: //
                intent = new Intent(HomeActivity.this, WebActivity.class);
                intent.putExtra("ref", "/mt");
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemLongClick(int position) {
        //ToastUtil.success(HomeActivity.this, "onItemLongClick");
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
        try {
            SharedPreferenceUtil.saveDesktopData(HomeActivity.this, new JSONArray(homeAdapter.getmDatas()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onStartDragonStartDragonStartDrag");
        //ToastUtil.success(HomeActivity.this, "onStartDrag");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ROLE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView();
                    initJPush();
                } else {
                    ToastUtil.failure(HomeActivity.this, "您已取消授权，将无法继续使用本APP");
                    checkRole();
                }
                return;
            }
        }
    }

    /**
     * 搜索的点击事件
     * @param v
     */
    public void onSearchClick(View v){
        EditText vKey = (EditText) findViewById(R.id.searck_key);
        if(StringUtil.isNull(vKey.getText().toString())){
            findViewById(R.id.searck_key).requestFocus();
            ToastUtil.failure(HomeActivity.this, "请输入搜索关键字！");
            return;
        }

        Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
        intent.putExtra("key", vKey.getText().toString());
        startActivity(intent);
    }
}
