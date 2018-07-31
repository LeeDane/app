package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.leedane.cn.app.R;
import com.leedane.cn.fragment.CircleOfFriendFragment;

/**
 * 朋友圈activity
 * Created by LeeDane on 2016/4/15.
 */
public class CircleOfFriendActivity  extends BaseActivity{
    public static final String TAG = "CircleOfFriendActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(CircleOfFriendActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.CircleOfFriendActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }

        setContentView(R.layout.activity_circle_of_friend);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.circle_of_friend);
        backLayoutVisible();
        reload();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 重新加载当前页面
     */
    private void reload(){

        //清空fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragmentManager != null && fragmentManager.getFragments() != null) {
            for (Fragment fragment : fragmentManager.getFragments()) {
                if (fragment != null)
                    transaction.remove(fragment);
            }
        }
        Bundle bundle = new Bundle();
        //必需继承FragmentActivity,嵌套fragment只需要这行代码
        getSupportFragmentManager().beginTransaction().replace(R.id.container, CircleOfFriendFragment.newInstance(bundle)).commitAllowingStateLoss();
    }
}
