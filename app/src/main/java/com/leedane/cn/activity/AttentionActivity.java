package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.http.HttpConnectionUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * 我的关注activity
 * Created by LeeDane on 2016/1/18.
 */
public class AttentionActivity extends BaseActivity {
    public static final String TAG = "AttentionActivity";
    /**
     * 当前个人中心的用户
     */
    private int mUserId;

    /**
     * 登录的账号id
     */
    private int mLoginAccountId;

    /**
     * 当前是否是登录用户
     */
    private boolean mIsLoginUser;

    private JSONObject mUserInfo;

    /**
     * 发表的按钮
     */
    private Button mBtnRefresh;

    private Intent currentIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        currentIntent = getIntent();
        //检查是否登录
        checkedIsLogin();

        //显示标题栏的发送心情的图片按钮
        mBtnRefresh = (Button)findViewById(R.id.view_right_button);
        mBtnRefresh.setVisibility(View.VISIBLE);
        mBtnRefresh.setOnClickListener(this);

        showLoadingDialog("Attention", "Try to loading. Please wait...", true);

        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 初始化视图
     */
    private void initView() throws Exception{
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));

        //以用户名称作为个人中心的标题
        if(StringUtil.isNull(mUserInfo.getString("account"))){
            setTitleViewText(getResources().getString(R.string.attention));
        }else{
            setTitleViewText(mUserInfo.getString("account"));
        }

        HttpRequestBean httpRequestBean = new  HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();

        //显示整个顶部的导航栏
        backLayoutVisible();
    }

    /**
     * 检查是否登录
     */
    private void checkedIsLogin() {
        mUserInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());
        //判断是否有缓存用户信息
        if(mUserInfo == null || !mUserInfo.has("account") ){
            Intent it = new Intent(AttentionActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.AttentionActivity");
            it.setData(currentIntent.getData());
            startActivity(it);
            AttentionActivity.this.finish();
            return;
        }

        try {
            mLoginAccountId = mUserInfo.getInt("id");
        }catch (Exception e){
            Log.i(TAG, "获取缓存的用户名称为空");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);
        try{
            JSONObject jsonObject = new JSONObject(String.valueOf(result));
            //判断是否是好友或者是签到
            if(type == TaskType.IS_SIGN_IN || type == TaskType.IS_FRIEND){
                Log.i(TAG, "签到返回的数据"+String.valueOf(result));

                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){

                    return;
                }
                return;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_button://更新
                break;
        }
    }
}
