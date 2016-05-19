package com.leedane.cn.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.adapter.UserInfoMenuAdapter;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.MenuBean;
import com.leedane.cn.broadcast.UserInfoDataReceiver;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.handler.AttentionHandler;
import com.leedane.cn.handler.CollectionHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.EncodingHandler;
import com.leedane.cn.handler.MoodHandler;
import com.leedane.cn.handler.PraiseHandler;
import com.leedane.cn.handler.UserHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.service.LoadUserInfoDataService;
import com.leedane.cn.util.Base64Util;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用户中心的activity
 * Created by LeeDane on 2016/4/14.
 */
public class UserInfoActivity extends BaseActivity implements UserInfoDataReceiver.UpdateUserInfoDataListener {

    public static final String TAG = "UserInfoActivity";
    private CircularImageView mUserPic;
    private TextView mScore;
    private TextView mComment;
    private TextView mFan;
    private TextView mTransmit;

    /**
     * 发送心情的imageview
     */
    private ImageView mRightImg;

    private JSONObject mUserInfo;

    private int mLoginAccountId;

    private String mLoginAccountName;

    private Dialog mQrCodeDialog;

    //是否已经登录
    private boolean isLogin;

    private ListView mListView;
    private UserInfoMenuAdapter mAdapter;

    private UserInfoDataReceiver userInfoDataReceive = new UserInfoDataReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        //检查是否登录
        checkedIsLogin();
        getUserInfoData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userInfoDataReceive.setUpdateUserInfoDataListener(this);
        //注册广播
        IntentFilter counterActionFilter = new IntentFilter("com.leedane.cn.broadcast.UserInfoDataReceiver");
        registerReceiver(userInfoDataReceive, counterActionFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(userInfoDataReceive);
    }

    /**
     * 后台获取该用户的基本信息
     */
    private void getUserInfoData() {
        Intent it_service = new Intent();
        it_service.setClass(getApplicationContext(), LoadUserInfoDataService.class);
        it_service.setAction("com.leedane.cn.LoadUserInfoDataService");
        it_service.putExtra("toUserId", BaseApplication.getLoginUserId());
        getApplicationContext().startService(it_service);
    }

    /**
     * 检查是否登录
     */
    private void checkedIsLogin() {
        mUserInfo = BaseApplication.getLoginUserInfo();
        //判断是否有缓存用户信息
        if(mUserInfo == null || !mUserInfo.has("account") ){
            ToastUtil.failure(UserInfoActivity.this, "未登录");
            Intent it = new Intent(UserInfoActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.UserInfoActivity");
            startActivity(it);
            finish();
            return;
        }
        isLogin = true;
        try {
            mLoginAccountName = mUserInfo.getString("account");
            mLoginAccountId = mUserInfo.getInt("id");
        }catch (Exception e){
            Log.i(TAG, "获取缓存的用户名称为空");
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {

        JSONObject jsonObject = SharedPreferenceUtil.getUserInfoData(getApplicationContext());
        updateUserInfoData(jsonObject);
        //mUserInfoListView = (ListView)findViewById(R.id.user_info_listview);
        //显示标题栏的发送心情的图片按钮
        mRightImg = (ImageView)findViewById(R.id.view_right_img);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setImageResource(R.drawable.qr_code);
        mRightImg.setOnClickListener(this);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.user_info);
        backLayoutVisible();

        mUserPic = (CircularImageView)findViewById(R.id.user_info_pic);
        mScore = (TextView)findViewById(R.id.user_info_score);
        mComment = (TextView)findViewById(R.id.user_info_comment);
        mTransmit = (TextView)findViewById(R.id.user_info_transmit);
        mFan = (TextView)findViewById(R.id.user_info_fans);

        String userPicPath = BaseApplication.getLoginUserPicPath();
        if(StringUtil.isNotNull(userPicPath)){
            ImageCacheManager.loadImage(userPicPath, mUserPic);
        }else{
            ToastUtil.failure(UserInfoActivity.this, "用户还没有上传头像");
        }

        mListView = (ListView)findViewById(R.id.user_info_listview);
        List<MenuBean> menuBeans = new ArrayList<>();
        menuBeans.add(new MenuBean(R.drawable.menu_base_info, getStringResource(R.string.personal_info)));
        menuBeans.add(new MenuBean(R.drawable.menu_personal_center, getStringResource(R.string.personal_title)));
        menuBeans.add(new MenuBean(R.drawable.menu_message, getStringResource(R.string.nav_message)));
        menuBeans.add(new MenuBean(R.drawable.menu_feedback, getStringResource(R.string.feedback)));
        mAdapter = new UserInfoMenuAdapter(UserInfoActivity.this, menuBeans);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = ((TextView)view.findViewById(R.id.recyclerview_title)).getText().toString();
                if(title.equalsIgnoreCase(getStringResource(R.string.personal_title))){
                    CommonHandler.startPersonalActivity(UserInfoActivity.this, BaseApplication.getLoginUserId());
                }else if(title.equalsIgnoreCase(getStringResource(R.string.nav_message))){
                    CommonHandler.startMyMessageActivity(UserInfoActivity.this);
                }else{
                    ToastUtil.success(UserInfoActivity.this, ((TextView)view.findViewById(R.id.recyclerview_title)).getText().toString());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_img: //点生成二维码
                if(isLogin){
                    showQrCodeDialog();
                }else{
                    ToastUtil.failure(UserInfoActivity.this, getStringResource(R.string.please_login));
                }
                break;
        }
    }

    private Bitmap qrCodeBitmap;
    /**
     * 显示弹出二维码的Dialog
     */
    public void showQrCodeDialog(){
        dismissQrCodeDialog();
        recycleQrCodeBitmap();
        mQrCodeDialog = new Dialog(UserInfoActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        ImageView imageView = new ImageView(UserInfoActivity.this);
        String contentString = null;
        try{
            String str = "{'account':'"+mUserInfo.getString("account")+"','id':"+mUserInfo.getInt("id")+"}";
            contentString = new JSONObject(str).toString();
        }catch (JSONException e){
            e.printStackTrace();
        }

        if (StringUtil.isNotNull(contentString)) {
            //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
            contentString =  CommonHandler.encodeQrCodeStr(contentString);
            ToastUtil.failure(UserInfoActivity.this, "二维码创建成功，长按保存到本地", Toast.LENGTH_SHORT);
            try {
                qrCodeBitmap = EncodingHandler.createQRCode(contentString, 720);
                imageView.setImageBitmap(qrCodeBitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String fPath = getQrCodeDir() + File.separator + mLoginAccountName +".jpg";
                File f = new File(fPath);
                if(f.exists()){
                    ToastUtil.failure(UserInfoActivity.this, "二维码图片保存成功，路径是："+fPath);
                    return true;
                }
                if(BitmapUtil.bitmapToLocalPath(qrCodeBitmap,  fPath)){
                    ToastUtil.failure(UserInfoActivity.this, "二维码图片保存成功，路径是："+fPath);
                    dismissQrCodeDialog();
                }else
                    ToastUtil.failure(UserInfoActivity.this, "二维码图片保存失败");
                return true;
            }
        });
        mQrCodeDialog.setTitle(getStringResource(R.string.my_qr_code));
        mQrCodeDialog.setCancelable(true);
        mQrCodeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissQrCodeDialog();
            }
        });
        mQrCodeDialog.setContentView(imageView);
        mQrCodeDialog.show();
    }

    @Override
    protected void onDestroy() {
        recycleQrCodeBitmap();
        super.onDestroy();
    }

    /**
     * 回收二维码生成的bitmap
     */
    private void recycleQrCodeBitmap(){
        if(qrCodeBitmap != null && !qrCodeBitmap.isRecycled()){
            qrCodeBitmap.recycle();
            System.gc();
        }
    }
    /**
     * 获取存放本地二维码的文件夹
     * @return
     */
    private File getQrCodeDir(){
        File sdDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdDir = Environment.getExternalStorageDirectory();
        }
        else{
            sdDir = getCacheDir();
        }
        File cacheDir = new File(sdDir, getResources().getString(R.string.app_dirsname) + File.separator+ getResources().getString(R.string.qr_code_filepath));
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 隐藏二维码
     */
    public void dismissQrCodeDialog(){
        if(mQrCodeDialog != null && mQrCodeDialog.isShowing())
            mQrCodeDialog.dismiss();
    }

    @Override
    public void updateUserInfoData(final JSONObject jsonObject) {
        if(jsonObject != null){
            /**
             * 延迟1秒钟后去加载数据
             */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try{
                        if(jsonObject.has("userId")){
                            if(BaseApplication.getLoginUserId() == jsonObject.getInt("userId")){
                                if(jsonObject.has("scores"))
                                    mScore.setText(String.valueOf(jsonObject.getInt("scores")));
                                if(jsonObject.has("fans"))
                                    mFan.setText(String.valueOf(jsonObject.getInt("fans")));
                                if(jsonObject.has("comments"))
                                    mComment.setText(String.valueOf(jsonObject.getInt("comments")));
                                if(jsonObject.has("transmits"))
                                    mTransmit.setText(String.valueOf(jsonObject.getInt("transmits")));
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, 100);
        }
    }

    /**
     * 启动个人中心积分
     * @param view
     */
    public void startPersonalScore(View view){
        Intent it = new Intent(UserInfoActivity.this, PersonalActivity.class);
        it.putExtra("currentTab", 7);
        it.putExtra("userId", BaseApplication.getLoginUserId());
        startActivity(it);
    }

    /**
     * 启动个人中心的评论
     * @param view
     */
    public void startPersonalComment(View view){
        Intent it = new Intent(UserInfoActivity.this, PersonalActivity.class);
        it.putExtra("currentTab", 1);
        it.putExtra("userId", BaseApplication.getLoginUserId());
        startActivity(it);
    }

    /**
     * 启动个人中心的转发
     * @param view
     */
    public void startPersonalTransmit(View view){
        Intent it = new Intent(UserInfoActivity.this, PersonalActivity.class);
        it.putExtra("currentTab", 2);
        it.putExtra("userId", BaseApplication.getLoginUserId());
        startActivity(it);
    }

    /**
     * 启动个人中心的粉丝
     * @param view
     */
    public void startPersonalFan(View view){
        Intent it = new Intent(UserInfoActivity.this, FanActivity.class);
        it.putExtra("toUserId", BaseApplication.getLoginUserId());
        it.putExtra("isLoginUser", true);
        startActivity(it);
    }
}
