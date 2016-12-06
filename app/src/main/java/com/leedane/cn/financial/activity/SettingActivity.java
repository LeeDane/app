package com.leedane.cn.financial.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.HttpResponseFinancialBean;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.financial.handler.FinancialHandler;
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.financial.util.SettingUtil;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.ToastUtil;

/**
 * 设置的activity
 * Created by leedane on 2016/8/30.
 */
public class SettingActivity extends BaseActivity implements Switch.OnCheckedChangeListener{

    public static final String TAG = "SettingActivity";

    private Switch mAutoSynchronized;  //自动同步
    private Switch mReceiveNotification; //接收推送
    private LinearLayout mCategory; //分类管理
    private LinearLayout mForceAll; //强制云端拉取
    private LinearLayout mSmartAll; //智能云端拉取
    private LinearLayout mRecentLoad;  //最新展示数量
    private FinancialDataBase financialDataBase;
    /**
     * 初始化数据的handler
     */
    private Handler synchronizedHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FlagUtil.SYNCHRONIZED_FLAG:
                    ToastUtil.success(SettingActivity.this, "数据同步完成");
                    dismissLoadingDialog();
                    //后台计算记账数据
                    FinancialHandler.calculateFinancialData(SettingActivity.this);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.financial.SettingActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_financial_setting);

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
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.financial_setting);
        backLayoutVisible();

        financialDataBase = new FinancialDataBase(SettingActivity.this);

        mAutoSynchronized = (Switch)findViewById(R.id.financial_setting_auto_synchronized);  //自动同步
        mReceiveNotification = (Switch)findViewById(R.id.financial_setting_receive_notification); //接收推送
        mCategory = (LinearLayout)findViewById(R.id.financial_setting_category); //分类管理
        mForceAll = (LinearLayout)findViewById(R.id.financial_setting_force_all); //强制云端拉取
        mSmartAll = (LinearLayout)findViewById(R.id.financial_setting_smart_all); //智能云端拉取
        mRecentLoad = (LinearLayout)findViewById(R.id.financial_setting_recent_load);

        mAutoSynchronized.setOnCheckedChangeListener(this);
        mReceiveNotification.setOnCheckedChangeListener(this);

        mCategory.setOnClickListener(this);
        mForceAll.setOnClickListener(this);
        mSmartAll.setOnClickListener(this);
        mRecentLoad.setOnClickListener(this);

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mAutoSynchronized.setChecked(SettingUtil.AUTO_SYNCHRONIZED);
        mReceiveNotification.setChecked(SettingUtil.RECEIVE_NOTIFICATION);
        ((TextView)mRecentLoad.findViewById(R.id.financial_setting_recent_load_show)).setText(SettingUtil.RECENT_LOAD + "条");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.financial_setting_category://分类管理
                Intent itOneLevel = new Intent(this, OneLevelOperationActivity.class);
                startActivity(itOneLevel);
                break;
            case R.id.financial_setting_force_all: //强制云端拉取
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingActivity.this);
                builder.setCancelable(true);
                builder.setIcon(R.drawable.menu_feedback);
                builder.setTitle("提示");
                builder.setMessage("强制拉取云端数据，同时将清空本地数据，请在确定同步之前先备份好本地数据?");
                builder.setPositiveButton("同步",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                FinancialHandler.forceAll(SettingActivity.this);
                                showLoadingDialog("强制同步中", "请中途不要断开网络...");
                            }
                        });
                builder.setNegativeButton("放弃",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                builder.show();
                break;
            case R.id.financial_setting_smart_all://智能云端拉取
                android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(SettingActivity.this);
                builder1.setCancelable(true);
                builder1.setIcon(R.drawable.menu_feedback);
                builder1.setTitle("提示");
                builder1.setMessage("智能同步云端数据，不会清空本地数据，只会通过比对增量同步数据?");
                builder1.setPositiveButton("同步",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                FinancialHandler.smartAll(SettingActivity.this);
                                showLoadingDialog("智能同步中", "请中途不要断开网络...");
                            }
                        });
                builder1.setNegativeButton("放弃",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                builder1.show();
                break;
            case R.id.financial_setting_recent_load:
                NumberPicker mPicker1 = new NumberPicker(this);
                mPicker1.setMinValue(10);
                mPicker1.setMaxValue(20);
                mPicker1.setValue(MySettingConfigUtil.getOtherLoad());
                mPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        if(SettingUtil.getInstance().addProp("recent_load", newVal))
                            ((TextView)findViewById(R.id.financial_setting_recent_load_show)).setText(SettingUtil.RECENT_LOAD + "条");
                        else
                            ToastUtil.failure(SettingActivity.this, "选择最近展示数量设置失败");
                    }
                });


                AlertDialog mAlertDialog1 = new AlertDialog.Builder(this)
                        .setTitle("选择展示最新记账列表的数量").setView(mPicker1).setPositiveButton("选择", null).create();
                mAlertDialog1.show();
                break;
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(result instanceof Error) {
            dismissLoadingDialog();
            ToastUtil.failure(getBaseContext(), ((Error) result).getMessage(), Toast.LENGTH_SHORT);
            return;
        }
        try{
            if(type == TaskType.FORCE_ALL || type == TaskType.SMART_ALL){

                final HttpResponseFinancialBean responseFinancialBean = BeanConvertUtil.strConvertToFinancialBeanBeans(String.valueOf(result));
                if(responseFinancialBean != null && !CommonUtil.isEmpty(responseFinancialBean.getMessage())){
                    if(type ==  TaskType.FORCE_ALL){
                        financialDataBase.deleteAll();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for(FinancialBean financialBean: responseFinancialBean.getMessage()){
                                financialBean.setSynchronous(true);
                                financialDataBase.insertServer(financialBean);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    dismissLoadingDialog();
                                }
                            }
                            Message msg = new Message();
                            msg.what = FlagUtil.SYNCHRONIZED_FLAG;
                            //55毫秒秒后进行
                            synchronizedHandler.sendMessageDelayed(msg, 55);

                        }
                    }).start();
                }
                //ToastUtil.failure(this, JsonUtil.getTipMessage(result));
            }
        }catch (Exception e){
            e.printStackTrace();
            dismissLoadingDialog();
        }
    }

    @Override
    protected void onDestroy() {
        if(financialDataBase != null)
            financialDataBase.destroy();

        synchronizedHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.financial_setting_auto_synchronized:
                if(SettingUtil.getInstance().addProp("auto_synchronized", isChecked))
                    ToastUtil.failure(SettingActivity.this, "自动同步设置失败");
                break;
            case R.id.financial_setting_receive_notification:
                if(SettingUtil.getInstance().addProp("receive_notification", isChecked))
                    ToastUtil.failure(SettingActivity.this, "接收通知设置失败");
                break;
        }
    }

}
