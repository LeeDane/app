package com.leedane.cn.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.SystemUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 基本的activity
 * Created by LeeDane on 2015/10/17.
 */
public class BaseActivity extends FragmentActivity implements TaskListener, View.OnClickListener, Serializable {

    private LinearLayout mMainLayout;

    /**
     * 检查是否登录
     */
    protected boolean checkedIsLogin() {
        //判断是否有缓存用户信息
        if(BaseApplication.getLoginUserId() < 1){
            return false;
        }
        return true;
    }
    /**
     * 弹出加载ProgressDiaLog
     */
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainLayout = (LinearLayout) ViewGroup.inflate(this, R.layout.activity_base, null);
        setContentView(mMainLayout);
    }
    public void setContentView(int id){
        mMainLayout.addView(ViewGroup.inflate(this, id, null), new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
    }
    protected void setTitleViewText(int id){
        ((TextView)mMainLayout.findViewById(R.id.base_title_textview)).setText(getResources().getString(id));
    }

    protected void setTitleViewText(String title){
        ((TextView)mMainLayout.findViewById(R.id.base_title_textview)).setText(title);
    }

    protected void backLayoutVisible(){
        findViewById(R.id.base_backlayout).setVisibility(View.VISIBLE);
    }

    /**
     * 设置沉浸式状态栏
     * @param view
     */
    protected void setImmerseLayout(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window window = getWindow();

           /* window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    , WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/

            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = SystemUtil.getStatusBarHeight(this.getBaseContext());
            view.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void taskCanceled(TaskType type) {

    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(result instanceof Error){
            ToastUtil.failure(getBaseContext(), ((Error) result).getMessage(), Toast.LENGTH_SHORT);
            dismissLoadingDialog();
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_loginout:
                //清除用户缓存的基本信息
                SharedPreferenceUtil.clearUserInfo(getApplicationContext());
                Intent intent = new Intent();
                setResult(MainActivity.LOGIN_REQUEST_CODE, intent);
                BaseActivity.this.finish();
                //Toast.makeText(BaseActivity.this, getResources().getString(R.string.setting_loginout), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void onGoBack(View v){
        //Toast.makeText(BaseActivity.this, "点击返回", Toast.LENGTH_SHORT).show();
        BaseActivity.this.finish();
    }

    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     */
    protected void showLoadingDialog(String title, String main){
        showLoadingDialog(title, main, false);
    }
    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     * @param cancelable 是否可以取消
     */
    protected void showLoadingDialog(String title, String main, boolean cancelable){
        dismissLoadingDialog();
        mProgressDialog = ProgressDialog.show(BaseActivity.this, title, main, true, cancelable);
    }
    /**
     * 获取服务器端地址
     * @return
     */
    protected String getBaseServerUrl(){
        return SharedPreferenceUtil.getSettingBean(getBaseContext(), ConstantsUtil.STRING_SETTING_BEAN_SERVER).getContent();
    }
    /**
     * 隐藏加载Dialog
     */
    protected void dismissLoadingDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
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

   /* *//**
     * 启动个人中心
     * @param userId
     *//*
    protected void startPersonalActivity(int userId){
        Intent it = new Intent(BaseActivity.this, PersonalActivity.class);
        it.putExtra("userId", userId);
        startActivity(it);
    }*/
}
