package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.fragment.FriendFragment;
import com.leedane.cn.fragment.FriendNotYetFragment;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

/**
 * 二维码登录Activity
 * Created by LeeDane on 2016/11/29.
 */
public class LoginByQrCodeActivity extends BaseActivity {
    public static final String TAG = "LoginByQrCodeActivity";

    private TextView scanLoginCconfim;
    private TextView scanLoginTip;
    private TextView scanLoginCancel; //取消登录

    private String connId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(LoginByQrCodeActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.LoginByQrCodeActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }

        connId = getIntent().getStringExtra("cid");
        if(StringUtil.isNull(connId)){
            ToastUtil.failure(LoginByQrCodeActivity.this, "无法获取连接参数，请重新扫描。");
            finish();
        }

        setContentView(R.layout.activity_login_qr_code);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.scan_login);
        backLayoutVisible();
        initView();
    }

    public void initView(){

        scanLoginCconfim = (TextView)findViewById(R.id.scan_login_confim);
        scanLoginTip = (TextView)findViewById(R.id.scan_login_tip);
        scanLoginCancel = (TextView)findViewById(R.id.scan_login_cancel);

        scanLoginCconfim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.loginByQrCode(LoginByQrCodeActivity.this, connId);
            }
        });

        scanLoginCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.CancelLoginByQrCode(LoginByQrCodeActivity.this, connId);
            }
        });
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        dismissLoadingDialog();
        if(result instanceof Error){
            ToastUtil.failure(LoginByQrCodeActivity.this, ((Error) result).getMessage(), Toast.LENGTH_LONG);
            return;
        }

        try {
            JSONObject resultObject = new JSONObject(String.valueOf(result));
            if(TaskType.SCAN_LOGIN == type && resultObject != null){
                if(resultObject.optBoolean("isSuccess")){
                    scanLoginTip.setText("扫码登录成功");
                    scanLoginCconfim.setBackgroundResource(R.drawable.btn_default_p);
                    scanLoginCconfim.setFocusable(false);
                    scanLoginCconfim.setClickable(false);
                    scanLoginCconfim.setText("已登录");
                    scanLoginCancel.setClickable(false);
                }else{
                    scanLoginTip.setText(resultObject.getString("message"));
                }
                return;
            }else if(TaskType.CANCEL_SCAN_LOGIN == type && resultObject != null){//取消二维码登录
                if(resultObject.optBoolean("isSuccess")){
                    ToastUtil.success(LoginByQrCodeActivity.this, "已取消登录");
                    finish();
                }else{
                    scanLoginTip.setText(resultObject.getString("message"));
                }
                return;
            }
        } catch (Exception e) {
            ToastUtil.failure(LoginByQrCodeActivity.this, "扫码登录处理异常", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
