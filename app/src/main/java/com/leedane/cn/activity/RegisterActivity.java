package com.leedane.cn.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.MD5Util;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.SystemUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 手机注册的activity
 * Created by LeeDane on 2016/4/29.
 */
public class RegisterActivity extends Activity implements TaskListener{

    private EditText mRegisterUsername;
    private EditText mRegisterPassword;
    private EditText mRegisterConfirmPassword;
    private EditText mRegisterMobilePhone;
    private TextView mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        setImmerseLayout(findViewById(R.id.register_linearLayout));

        initView();
        //ButterKnife.bind(this);

    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        mRegisterUsername = (EditText)findViewById(R.id.register_username);
        mRegisterPassword = (EditText)findViewById(R.id.register_password);
        mRegisterConfirmPassword = (EditText)findViewById(R.id.register_confirm_password);
        mRegisterMobilePhone = (EditText)findViewById(R.id.register_mobile_phone);
        mRegisterButton = (TextView)findViewById(R.id.register_btn);
    }

    /**
     * 设置沉浸式状态栏
     * @param view
     */
    private void setImmerseLayout(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = SystemUtil.getStatusBarHeight(this.getBaseContext());
            view.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    /**
     * 注册
     * @param view
     */
    public void onDoneClick(View view){
        final String username = mRegisterUsername.getText().toString();
        final String password = mRegisterPassword.getText().toString();
        final String confirmPassword = mRegisterConfirmPassword.getText().toString();
        final String mobilePhone = mRegisterMobilePhone.getText().toString();

        if(StringUtil.isNull(username)){
            ToastUtil.failure(RegisterActivity.this, getStringResource(R.string.username_null), Toast.LENGTH_SHORT);
            mRegisterUsername.setFocusable(true);
            return;
        }

        if(StringUtil.isNull(mobilePhone)){
            ToastUtil.failure(RegisterActivity.this, getStringResource(R.string.mobile_phone_null), Toast.LENGTH_SHORT);
            mRegisterMobilePhone.setFocusable(true);
            return;
        }

        if(StringUtil.isNull(password)){
            ToastUtil.failure(RegisterActivity.this, getStringResource(R.string.password_null), Toast.LENGTH_SHORT);
            mRegisterPassword.setFocusable(true);
            return;
        }

        if(StringUtil.isNull(confirmPassword)){
            ToastUtil.failure(RegisterActivity.this, getStringResource(R.string.confirm_password_null), Toast.LENGTH_SHORT);
            mRegisterConfirmPassword.setFocusable(true);
            return;
        }

        if(!password.equals(confirmPassword)){
            ToastUtil.failure(RegisterActivity.this, getStringResource(R.string.password_no_match), Toast.LENGTH_SHORT);
            mRegisterConfirmPassword.setFocusable(true);
            return;
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                HttpRequestBean requestBean = new HttpRequestBean();

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("account", username);
                params.put("confirmPassword", MD5Util.compute(confirmPassword));
                params.put("password", MD5Util.compute(password));
                params.put("mobilePhone", mobilePhone);
                requestBean.setParams(params);
                requestBean.setServerMethod("leedane/user_registerByPhoneNoValidate.action");
                showLoadingDialog("Register", "Try to register now...");
                TaskLoader.getInstance().startTaskForResult(TaskType.REGISTER_DO, RegisterActivity.this, requestBean);

            }
        }, 100);

    }

    /**
     * 弹出加载ProgressDiaLog
     */
    private ProgressDialog mProgressDialog;

    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     */
    protected void showLoadingDialog(String title, String main){
        dismissLoadingDialog();
        mProgressDialog = ProgressDialog.show(RegisterActivity.this, title, main, true);
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
     * 登录
     * @param view
     */
    public void loginPhoneClick(View view){
        Intent it = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(it);
        finish();
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        dismissLoadingDialog();
        if(result instanceof Error){
            ToastUtil.failure(RegisterActivity.this, ((Error) result).getMessage(), Toast.LENGTH_SHORT);
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(result));
            if(TaskType.REGISTER_DO == type && jsonObject != null){
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    ToastUtil.success(RegisterActivity.this, jsonObject);
                }else{
                    ToastUtil.failure(RegisterActivity.this, jsonObject, Toast.LENGTH_SHORT);
                }
                return;
            }
        } catch (Exception e) {
            Toast.makeText(RegisterActivity.this, getStringResource(R.string.register_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void taskCanceled(TaskType type) {

    }
}
