package com.leedane.cn.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.customview.EyeEditText;
import com.leedane.cn.database.ChatDataBase;
import com.leedane.cn.database.SearchHistoryDataBase;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.UserHandler;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.MD5Util;
import com.leedane.cn.util.RSACoderUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.SystemUtil;
import com.leedane.cn.util.ToastUtil;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

/**
 * 用户登录操作activity
 * Created by LeeDane on 2015/10/15.
 */
public class LoginActivity extends BaseSwipeBackActivity implements TaskListener, OnSendMessageHandler {
    AutoCompleteTextView mTextEditUsername;
    public static final String TAG = "LoginActivity";

    public static final int FLAT_SEND_CODE = 1; //发送验证码
    public static final int FLAT_SEND_CODE_SUCCESS = 2; //发送验证码成功
    public static final int FLAT_SEND_CODE_ERROR = 3; //发送验证码失败
    public static final int COUNT_DOWN = 4; //倒计时
    /**
     * 登录成功后返回的activity
     */
    private String mReturnClass;

    private Intent currentIntent;

    private EventHandler eventHandler;

    private int time = 60; //60秒后才能重新获取验证码
    private Timer timer;

    private String mPublicKey; //公钥

    public void runTimer(){
        timer=new Timer();
        TimerTask task=new TimerTask() {

            @Override
            public void run(){
                time--;
                Message msg = mDealHandler.obtainMessage();
                msg.what = COUNT_DOWN;
                mDealHandler.sendMessage(msg);
            }
        };
        timer.schedule(task, 100, 1000);
    }

    //处理逻辑的handler
    private Handler mDealHandler = new Handler(){
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case FLAT_SEND_CODE:
                    SMSSDK.getVerificationCode("86", bundle.getString("phone"));
                    runTimer();
                    break;
                case FLAT_SEND_CODE_SUCCESS:
                    // 处理你自己的逻辑
                    ToastUtil.success(getBaseContext(), "验证码已发送，请注意查收！");
                    break;
                case FLAT_SEND_CODE_ERROR:
                    // 处理你自己的逻辑
                    ToastUtil.success(getBaseContext(), "失败---》"+ bundle.getString("error"));
                    break;
                case COUNT_DOWN:
                    if(time>0){
                        loginPhoneSendMessage.setEnabled(false);
                        loginPhoneSendMessage.setText(time+"秒后重发");
                    }else{
                        timer.cancel();
                        time = 60;
                        loginPhoneSendMessage.setText(getResources().getText(R.string.send_message));
                        loginPhoneSendMessage.setEnabled(true);
                    }
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 通过代码注册你的AppKey和AppSecret
        MobSDK.init(BaseApplication.newInstance(), "1f1494a737c89", "6861f5e3c06685e06ffc07c8d06a7ba7");
        // 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
        SMSSDK.setAskPermisionOnReadContact(true);

        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, final Object data) {

                Bundle bundle = new Bundle();
                Message message = new Message();
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable)data;
                    String msg = throwable.getMessage();
                    message.what = FLAT_SEND_CODE_ERROR;
                    try {
                        JSONObject jsonObject = new JSONObject(msg);
                        bundle.putString("error", jsonObject.getString("detail"));
                    } catch (JSONException e) {
                        bundle.putString("error", msg);
                        e.printStackTrace();
                    }

                } else {
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        bundle.putString("success", String.valueOf(data));
                        message.what = FLAT_SEND_CODE_SUCCESS;
                    }
                }
                message.setData(bundle);
                //55毫秒秒后进行
                mDealHandler.sendMessageDelayed(message, 55);
            }
        };

        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
        //检查是否登录
        checkedIsLogin();
        setContentView(R.layout.activity_login);

        setImmerseLayout(findViewById(R.id.login_linearLayout));

        //先获取用户名输入的历史数据
        String setUsernameHistory = SharedPreferenceUtil.getUsernameHistory(LoginActivity.this);

        mTextEditUsername = (AutoCompleteTextView)findViewById(R.id.editview_username);

        currentIntent = getIntent();
        mReturnClass =  currentIntent.getStringExtra("returnClass");

        if(setUsernameHistory != null){
            String[] mUsernameHistorys = setUsernameHistory.split(";");
            //自动提示
            ArrayAdapter<Object> adapt = new ArrayAdapter<Object>(this,
                    android.R.layout.simple_dropdown_item_1line,
                    mUsernameHistorys);
            mTextEditUsername.setAdapter(adapt);
        }

        CommonHandler.getPublicKeyRequest(this);
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

    public void onDoneClick(View view){
        final String username = mTextEditUsername.getText().toString();
        final String password = ((EyeEditText)findViewById(R.id.editview_password)).getText().toString();

        if(username == null || username.length() == 0 || username.replaceAll(" ", "").length() == 0){
            ToastUtil.failure(LoginActivity.this, getResources().getString(R.string.username_null), Toast.LENGTH_SHORT);
            return;
        }
        if(password == null || password.length() == 0 || password.replaceAll(" ", "").length() == 0){
            ToastUtil.failure(LoginActivity.this, getResources().getString(R.string.password_null), Toast.LENGTH_SHORT);
            return;
        }

        if(StringUtil.isNull(mPublicKey)){
            ToastUtil.failure(LoginActivity.this, "获取不到服务器上的公钥!", Toast.LENGTH_SHORT);
            return;
        }
        try{
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    HttpRequestBean requestBean = new HttpRequestBean();

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("account", username);
                    String rsaPassword = null;
                    try {
                        rsaPassword = RSACoderUtil.encryptWithRSA(mPublicKey, MD5Util.compute(password));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(StringUtil.isNull(rsaPassword)){
                        ToastUtil.failure(LoginActivity.this, "公钥解析失败！请联系管理员！", Toast.LENGTH_SHORT);
                        return;
                    }
                    rsaPassword = rsaPassword.replaceAll("\n", "");
                    rsaPassword = rsaPassword.replaceAll("\r", "");
                    params.put("pwd",  rsaPassword);
                    requestBean.setParams(params);
                    requestBean.setServerMethod("us/login");
                    requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
                    showLoadingDialog("Login", "Try to login now...");
                    TaskLoader.getInstance().startTaskForResult(TaskType.LOGIN_DO, LoginActivity.this, requestBean);

                }
            }, 500);
        }catch (Exception e){
            ToastUtil.failure(LoginActivity.this, "密码加密出现异常", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        dismissLoadingDialog();
        if(result instanceof Error){
            ToastUtil.failure(LoginActivity.this, ((Error) result).getMessage(), Toast.LENGTH_SHORT);
            return;
        }

        try {
            JSONObject resultObject = new JSONObject(String.valueOf(result));
            if(TaskType.LOGIN_DO == type && resultObject != null){
                if(resultObject.optBoolean("isSuccess")){
                    SharedPreferenceUtil.saveUserInfo(getApplicationContext(), resultObject.getString("userinfo"));
                    ToastUtil.success(LoginActivity.this, resultObject.getString("message"), Toast.LENGTH_SHORT);
                    CommonHandler.startUserFreidnsService(getApplicationContext(), true);
                    //把信息返回到上一个activity
                    if(StringUtil.isNull(mReturnClass)){
                        Intent intent = new Intent();
                        setResult(MainActivity.LOGIN_REQUEST_CODE, intent);
                        LoginActivity.this.finish();

                    //直接返回指定的activity
                    }else{
                        Class clazz = Class.forName(mReturnClass);
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, clazz);
                        //把之前的数据传递过去
                        intent.setData(currentIntent.getData());
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }

                }else{
                    ToastUtil.failure(LoginActivity.this, resultObject.getString("message"), Toast.LENGTH_SHORT);
                }
                return;
            }else if(TaskType.DO_GET_LOGIN_CODE == type && resultObject != null){//获取手机验证码完成
                if(resultObject.optBoolean("isSuccess")){
                    ToastUtil.success(LoginActivity.this, "验证码发送成功，1小时内有效", Toast.LENGTH_SHORT);
                    loginPhoneSendMessage.setEnabled(false);
                }else{
                    ToastUtil.failure(LoginActivity.this, "验证码发送失败", Toast.LENGTH_SHORT);
                }
            }else if(type ==TaskType.DO_LOGIN_PHONE){
                if(resultObject.optBoolean("isSuccess")){
                    SharedPreferenceUtil.saveUserInfo(getApplicationContext(), resultObject.getString("userinfo"));
                    ToastUtil.success(LoginActivity.this, resultObject.getString("message"), Toast.LENGTH_SHORT);
                    CommonHandler.startUserFreidnsService(getApplicationContext(), true);
                    //把信息返回到上一个activity
                    if(StringUtil.isNull(mReturnClass)){
                        Intent intent = new Intent();
                        setResult(MainActivity.LOGIN_REQUEST_CODE, intent);
                        LoginActivity.this.finish();

                        //直接返回指定的activity
                    }else{
                        Class clazz = Class.forName(mReturnClass);
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, clazz);
                        //把之前的数据传递过去
                        intent.setData(currentIntent.getData());
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                }else{
                    ToastUtil.failure(LoginActivity.this, resultObject);
                }
            }else if(TaskType.GET_PUBLIC_KEY == type && resultObject != null){//获取公钥
                if(resultObject.optBoolean("isSuccess")){
                    mPublicKey  = resultObject.getString("message");
                    //ToastUtil.success(LoginActivity.this, "获取服务器上的公钥成功："+ mPublicKey, Toast.LENGTH_SHORT);
                }else{
                    ToastUtil.failure(LoginActivity.this, "获取服务器上的公钥失败", Toast.LENGTH_SHORT);
                }
            }
        } catch (Exception e) {
            ToastUtil.failure(LoginActivity.this, getResources().getString(R.string.login_error), Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }

    /**
     * 检查是否登录
     */
    private void checkedIsLogin() {
        //判断是否有缓存用户信息
        if(BaseApplication.getLoginUserId() > 0){

            //判断是否有强制清理缓存的数据
            if(getIntent().getBooleanExtra("forceClear", false)){
                clearCache();
            }else
                showAlertDialog();
        }
    }

    public void showAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("当前您已登录，确认是否先退出登录？")
                .setCancelable(false)
                .setPositiveButton("退出登录", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        clearCache();
                    }

                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }

    private void clearCache(){
        SharedPreferenceUtil.clearUserInfo(LoginActivity.this);
        SharedPreferenceUtil.clearFriends(LoginActivity.this);
        ChatDataBase dataBase = new ChatDataBase(LoginActivity.this);
        dataBase.deleteAll();
        dataBase.destroy();

                       /* MoodDataBase moodDataBase = new MoodDataBase(LoginActivity.this);
                        moodDataBase.deleteAll();
                        moodDataBase.destroy();*/

                        /*GalleryDataBase galleryDataBase = new GalleryDataBase((LoginActivity.this));
                        galleryDataBase.deleteAll();
                        galleryDataBase.destroy();*/

        SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(LoginActivity.this);
        searchHistoryDataBase.deleteAll();
        searchHistoryDataBase.destroy();

        /*MySettingDataBase mySettingDataBase = new MySettingDataBase(LoginActivity.this);
        mySettingDataBase.deleteAll();
        mySettingDataBase.destroy();*/
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
        mProgressDialog = ProgressDialog.show(LoginActivity.this, title, main, true);
    }

    /**
     * 隐藏加载Dialog
     */
    protected void dismissLoadingDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }*/

    /************************手机登录相关开始*******************************************/
    EditText loginPhoneNumber ;
    EditText loginPhoneCode;
    TextView loginPhoneBtn;
    Button loginPhoneSendMessage;
    /**
     * 手机登录的点击事件
     * @param view
     */
    public void loginPhoneClick(View view){
        showPopPhoneLoginDialog();
    }

    /**
     * 注册的点击事件
     * @param view
     */
    public void registerClick(View view){
        Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
        it.putExtra("publicKey", mPublicKey); //把公钥传递过去
        startActivity(it);
        finish();
    }

    /**
     * 执行手机登录点击事件
     * @param view
     */
    public void onLoginPhoneClick(View view){
        if(StringUtil.isNull(mPublicKey)){
            ToastUtil.failure(LoginActivity.this, "获取不到服务器上的公钥!", Toast.LENGTH_SHORT);
            return;
        }

        String phoneNumber = loginPhoneNumber.getText().toString();
        if(StringUtil.isNull(phoneNumber) || phoneNumber.length() != 11){
            ToastUtil.failure(LoginActivity.this, "请输入正确的11位手机号码");
            loginPhoneNumber.setFocusable(true);
            return;
        }

        String validationCode = loginPhoneCode.getText().toString();

        if(StringUtil.isNull(validationCode) || validationCode.length() != 4){
            ToastUtil.failure(LoginActivity.this, "请输入正确的4位验证码");
            loginPhoneCode.setFocusable(true);
            return;
        }

        showLoadingDialog("Logining", "try to login, please wait...");
        String rsaPhone = null;
        try {
            rsaPhone = RSACoderUtil.encryptWithRSA(mPublicKey, phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(StringUtil.isNull(rsaPhone)){
            ToastUtil.failure(LoginActivity.this, "公钥解析失败！请联系管理员！", Toast.LENGTH_SHORT);
            return;
        }
        rsaPhone = rsaPhone.replaceAll("\n", "");
        rsaPhone = rsaPhone.replaceAll("\r", "");
        Map<String, Object> params = new HashMap<>();
        params.put("mobilePhone", rsaPhone);
        params.put("validationCode", validationCode);
        UserHandler.loginByPhone(this, params);
    }

    /**
     * 获取手机登录验证码
     * @param view
     */
    public void getValidationCodeClick(View view){
        String phoneNumber = loginPhoneNumber.getText().toString();
        if(StringUtil.isNull(phoneNumber) || phoneNumber.length() != 11){
            ToastUtil.failure(LoginActivity.this, "请输入正确的11位手机号码");
            loginPhoneNumber.setFocusable(true);
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("phone", phoneNumber);
        Message message = new Message();
        message.setData(bundle);
        message.what = FLAT_SEND_CODE;
        //55毫秒秒后进行
        mDealHandler.sendMessageDelayed(message, 55);
        //Map<String, Object> params = new HashMap<>();
        //params.put("mobilePhone", phoneNumber);
        //loginPhoneSendMessage.setBackgroundResource(R.drawable.btn_disable_setting_loginoutbg);
       // UserHandler.getPhoneLoginCode(this, params);
    }


    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return super.dispatchPopulateAccessibilityEvent(event);
    }

    private Dialog mDialog;
    /**
     * 显示弹出自定义view
     */
    public void showPopPhoneLoginDialog(){
        dismissPopPhoneLoginDialog();

        mDialog = new Dialog(LoginActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.login_phone, null);

        loginPhoneNumber = (EditText)view.findViewById(R.id.login_phone_number);
        loginPhoneCode = (EditText)view.findViewById(R.id.login_phone_validation_code);
        loginPhoneBtn = (TextView)view.findViewById(R.id.login_phone_btn);
        loginPhoneSendMessage = (Button)view.findViewById(R.id.login_phone_send_message);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //获得当前设备的屏幕宽度
        int screenWidth = dm.widthPixels;

        mDialog.setTitle("手机号码登录");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissPopPhoneLoginDialog();
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(screenWidth-160, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.setContentView(view, params);
        mDialog.show();
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissPopPhoneLoginDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    public boolean onSendMessage(String s, String s1) {
        return false;
    }

    /************************手机登录相关结束*******************************************/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
        if(mDealHandler != null)
            mDealHandler.removeCallbacksAndMessages(null);
    }
}
