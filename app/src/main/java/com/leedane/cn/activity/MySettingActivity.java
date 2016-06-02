package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.SharedPreferenceUtil;

import org.json.JSONObject;

/**
 * 我的设置的activity
 * Created by leedane on 2016/6/2.
 */
public class MySettingActivity extends BaseActivity{

    public static final String TAG = "MySettingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(MySettingActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.MySettingActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }

        setContentView(R.layout.activity_my_setting);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        JSONObject jsonObject = SharedPreferenceUtil.getUserInfoData(getApplicationContext());
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.nav_setting);
        backLayoutVisible();

        String userPicPath = BaseApplication.getLoginUserPicPath();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_img: //点生成二维码
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
