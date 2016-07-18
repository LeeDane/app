package com.leedane.cn.financial.activity;

import android.content.Intent;
import android.os.Bundle;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.app.R;

/**
 * 设置的界面
 * Created by leedane on 2016/8/7.
 */
public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(SettingActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.SettingActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_financial_home);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(getStringResource(R.string.financial));
        backLayoutVisible();
        init();
        initView();
    }

    /**
     * 初始化参数
     */
    private void init() {

    }

    /**
     * 初始化试图控件
     */
    private void initView() {

    }

}
