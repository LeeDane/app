package com.leedane.cn.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.SystemUtil;
import com.leedane.cn.util.ToastUtil;

import java.io.Serializable;

/**
 * 基本的activity
 * Created by LeeDane on 2015/10/17.
 */
public abstract class ActionBarBaseActivity extends AppCompatActivity implements TaskListener, View.OnClickListener, Serializable {

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
    protected ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(getLabel());
    }

    /**
     * 获取主视图容器的ID
     * @return
     */
    protected abstract int getContentViewId();

    /**
     * 获取主视图的标题名称
     * @return
     */
    protected abstract String getLabel();

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
                ActionBarBaseActivity.this.finish();
                //Toast.makeText(BaseActivity.this, getResources().getString(R.string.setting_loginout), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void onGoBack(View v){
        //Toast.makeText(BaseActivity.this, "点击返回", Toast.LENGTH_SHORT).show();
        ActionBarBaseActivity.this.finish();
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
        mProgressDialog = ProgressDialog.show(ActionBarBaseActivity.this, title, main, true, cancelable);
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

    /**
     *
     * 返回菜单的关闭操作
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
