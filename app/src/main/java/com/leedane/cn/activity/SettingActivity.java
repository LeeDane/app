package com.leedane.cn.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.SettingAdapter;
import com.leedane.cn.bean.SettingBean;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SharedPreferenceUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置activity
 * Created by LeeDane on 2015/10/17.
 */
public class SettingActivity extends BaseActivity{
    /**
     * 退出系统的对象
     */
    private TextView mLoginOut;

    /**
     * ListView选项
     */
    private ListView mListView;

    /**
     * ListView的适配器
     */
    private SettingAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.system_settings);
        backLayoutVisible();
    }

    private void init() {

        mListView = (ListView)findViewById(R.id.setting_listview);

        View footView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.footer_settings_listview__item, null);
        mListView.addFooterView(footView);

        mLoginOut = (TextView)footView.findViewById(R.id.setting_loginout);

        List<SettingBean> listData = new ArrayList<>();
        //listData.add(SharedPreferenceUtil.getSettingBean(getApplicationContext(), ConstantsUtil.STRING_SETTING_BEAN_PHONE));
        listData.add(SharedPreferenceUtil.getSettingBean(getApplicationContext(), ConstantsUtil.STRING_SETTING_BEAN_SERVER));
        mAdapter = new SettingAdapter(listData, mListView, SettingActivity.this);
        mListView.setAdapter(mAdapter);

        JSONObject userinfoObject = SharedPreferenceUtil.getUserInfo(getApplicationContext());
        //判断是否有缓存用户信息
        if(userinfoObject != null && userinfoObject.has("account")){
            mLoginOut.setVisibility(View.VISIBLE);
            mLoginOut.setOnClickListener(this);
        }else{
            mLoginOut.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
