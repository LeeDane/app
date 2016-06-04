package com.leedane.cn.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.leedane.cn.adapter.UserInfoMenuAdapter;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.MenuBean;
import com.leedane.cn.broadcast.UserInfoDataReceiver;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.EncodingHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.service.LoadUserInfoDataService;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
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

    private Dialog mQrCodeDialog;
    private ListView mListView;
    private UserInfoMenuAdapter mAdapter;

    private View mHeader;

    private UserInfoDataReceiver userInfoDataReceive = new UserInfoDataReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(UserInfoActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.UserInfoActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }

        setContentView(R.layout.activity_user_info);
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
     * 初始化控件
     */
    private void initView() {

        JSONObject jsonObject = SharedPreferenceUtil.getUserInfoData(getApplicationContext());
        updateUserInfoData(jsonObject);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.user_info);
        backLayoutVisible();

        mListView = (ListView)findViewById(R.id.user_info_listview);
        List<MenuBean> menuBeans = new ArrayList<>();
        menuBeans.add(new MenuBean(R.drawable.menu_base_info, getStringResource(R.string.personal_info)));
        menuBeans.add(new MenuBean(R.drawable.menu_personal_center, getStringResource(R.string.personal_title)));
        menuBeans.add(new MenuBean(R.drawable.qr_code, getStringResource(R.string.qr_code_idcard)));
        menuBeans.add(new MenuBean(R.drawable.menu_security, getStringResource(R.string.update_login_password)));
        menuBeans.add(new MenuBean(R.drawable.menu_message, getStringResource(R.string.nav_message)));
        menuBeans.add(new MenuBean(R.drawable.menu_feedback, getStringResource(R.string.feedback)));
        menuBeans.add(new MenuBean(R.drawable.menu_setting, getStringResource(R.string.nav_setting)));

        mAdapter = new UserInfoMenuAdapter(UserInfoActivity.this, menuBeans);
        mListView.setAdapter(mAdapter);
        mHeader = LayoutInflater.from(UserInfoActivity.this).inflate(R.layout.user_info_header, null);
        mUserPic = (CircularImageView)mHeader.findViewById(R.id.user_info_pic);
        mScore = (TextView)mHeader.findViewById(R.id.user_info_score);
        mComment = (TextView)mHeader.findViewById(R.id.user_info_comment);
        mTransmit = (TextView)mHeader.findViewById(R.id.user_info_transmit);
        mFan = (TextView)mHeader.findViewById(R.id.user_info_fans);
        mListView.addHeaderView(mHeader, null ,false);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = ((TextView)view.findViewById(R.id.recyclerview_title)).getText().toString();
                if(title.equalsIgnoreCase(getStringResource(R.string.personal_title))){
                    CommonHandler.startPersonalActivity(UserInfoActivity.this, BaseApplication.getLoginUserId());
                }else if(title.equalsIgnoreCase(getStringResource(R.string.nav_message))){
                    CommonHandler.startMyMessageActivity(UserInfoActivity.this);
                }else if(title.equalsIgnoreCase(getStringResource(R.string.personal_info))){
                    CommonHandler.startUserBaseActivity(UserInfoActivity.this, BaseApplication.getLoginUserId());
                }else if(title.equalsIgnoreCase(getStringResource(R.string.nav_setting))){
                    CommonHandler.startMySettingActivity(UserInfoActivity.this);
                }else if(title.equalsIgnoreCase(getStringResource(R.string.qr_code_idcard))){ //二维码名片
                    showQrCodeDialog();//展示二维码名片
                }else if(title.equalsIgnoreCase(getStringResource(R.string.update_login_password))){//修改登录密码
                    Intent it = new Intent(UserInfoActivity.this, UpdateLoginPswActivity.class);
                    startActivityForResult(it, UpdateLoginPswActivity.UPDATE_LOGIN_PASSWORD_CODE);
                }else{
                    ToastUtil.success(UserInfoActivity.this, ((TextView)view.findViewById(R.id.recyclerview_title)).getText().toString());
                }
            }
        });

        String userPicPath = BaseApplication.getLoginUserPicPath();
        if(StringUtil.isNotNull(userPicPath)){
            ImageCacheManager.loadImage(userPicPath, mUserPic);
        }else{
            ToastUtil.failure(UserInfoActivity.this, "您还没有上传头像");
        }

        //修改用户头像
        mUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startUpdateHeaderActivity(UserInfoActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
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
            String str = "{'account':'"+BaseApplication.getLoginUserName()+"','id':"+BaseApplication.getLoginUserId()+"}";
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
                String fPath = getQrCodeDir() + File.separator + BaseApplication.getLoginUserName() +".jpg";
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case UpdateLoginPswActivity.UPDATE_LOGIN_PASSWORD_CODE:
                if(data != null && data.getBooleanExtra("success", false)){//密码更新成功，跳转到登录页面
                    Intent it = new Intent(UserInfoActivity.this, LoginActivity.class);
                    //设置跳转的activity
                    it.putExtra("returnClass", "com.leedane.cn.activity.UserInfoActivity");
                    it.setData(getIntent().getData());
                    it.putExtra("forceClear", true);
                    startActivity(it);
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
