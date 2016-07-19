package com.leedane.cn.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.handler.UserHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户基本信息的activity
 * Created by leedane on 2016/6/1.
 */
public class UserBaseActivity extends BaseActivity{

    public static final String TAG = "UserBaseActivity";

    private final String[] sexs = new String[]{"未知", "男", "女"};
    private final String[] schools = new String[]{"本科", "大专", "硕士", "博士", "博士后", "中专", "高中", "初中", "小学", "文盲"};

    private Button mRight;
    private TextView mAccount;
    private Spinner mSex;
    private EditText mPhone;
    private EditText mQq;
    private EditText mEmail;
    private TextView mBirthDay;
    private Button mSelectBirthDay;
    private Spinner mSchool;
    private EditText mIntroduction;
    private String sex;
    private String school;

    private boolean edit; //记录是否编辑状态
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(UserBaseActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.UserBaseActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }

        setContentView(R.layout.activity_user_base);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.user_base);
        backLayoutVisible();

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

        mRight = (Button)findViewById(R.id.view_right_button);
        mRight.setVisibility(View.VISIBLE);
        mRight.setOnClickListener(this);
        mRight.setText(getStringResource(R.string.edit));

        mAccount = (TextView)findViewById(R.id.user_base_account);
        mSex = (Spinner)findViewById(R.id.user_base_sex);
        mPhone = (EditText)findViewById(R.id.user_base_phone);
        mQq = (EditText)findViewById(R.id.user_base_qq);
        mEmail = (EditText)findViewById(R.id.user_base_email);
        mBirthDay = (TextView)findViewById(R.id.user_base_birthday);
        mSelectBirthDay = (Button)findViewById(R.id.user_base_select_birthday);
        mSelectBirthDay.setOnClickListener(this);

        mSchool = (Spinner)findViewById(R.id.user_base_school);
        mIntroduction = (EditText)findViewById(R.id.user_base_introduction);

        ArrayAdapter<String> arraySexAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sexs);
        mSex.setAdapter(arraySexAdapter);
        mSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sex = sexs[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sex = sexs[0];
            }
        });

        ArrayAdapter<String> arraySchoolAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, schools);
        mSchool.setAdapter(arraySchoolAdapter);
        mSchool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                school = schools[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                school = schools[0];
            }
        });

        initData();
        disabledAllView();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        try{
            JSONObject userInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());
            if(userInfo.has("account") && StringUtil.isNotNull(userInfo.getString("account")))
                mAccount.setText(userInfo.getString("account"));
            if(userInfo.has("sex") && StringUtil.isNotNull(userInfo.getString("sex")))
                //这个应该放在setAdapter()方法之后执行才有效
                mSex.setSelection(getArrayPosition(sexs, userInfo.getString("sex")), true);
            if(userInfo.has("mobile_phone") && StringUtil.isNotNull(userInfo.getString("mobile_phone")))
                mPhone.setText(userInfo.getString("mobile_phone"));
            if(userInfo.has("qq") && StringUtil.isNotNull(userInfo.getString("qq")))
                mQq.setText(userInfo.getString("qq"));
            if(userInfo.has("email") && StringUtil.isNotNull(userInfo.getString("email")))
                mEmail.setText(userInfo.getString("email"));
            if(userInfo.has("birth_day") && StringUtil.isNotNull(userInfo.getString("birth_day"))){
                mBirthDay.setText(userInfo.getString("birth_day"));
            }
            if(userInfo.has("education_background") && StringUtil.isNotNull(userInfo.getString("education_background")))
                mSchool.setSelection(getArrayPosition(schools, userInfo.getString("education_background")), true);
            if(userInfo.has("personal_introduction") && StringUtil.isNotNull(userInfo.getString("personal_introduction")))
                mIntroduction.setText(userInfo.getString("personal_introduction"));

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 从数组中获取文本的索引
     * @param arr
     * @param text
     * @return
     */
    private int getArrayPosition(String[] arr, String text){
        int position = 0;
        if(arr != null && arr.length > 0 && StringUtil.isNotNull(text)){
            for(int i = 0; i < arr.length; i++){
                if(text.equalsIgnoreCase(arr[i])){
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_button: //点击
               mRight = (Button)v;
                if(mRight.getText().toString().equalsIgnoreCase(getStringResource(R.string.edit))){//进入编辑状态
                    mRight.setText(getStringResource(R.string.comlpete));
                    enabledAllView();
                    //ToastUtil.success(UserBaseActivity.this, getStringResource(R.string.comlpete));
                }else{//提交编辑
                    //ToastUtil.success(UserBaseActivity.this, getStringResource(R.string.edit));
                    Map<String, Object> params = new HashMap<>();
                    params.put("sex", sex);
                    params.put("mobile_phone", mPhone.getText().toString());
                    params.put("qq", mQq.getText().toString());
                    params.put("email", mEmail.getText().toString());
                    params.put("birth_day", mBirthDay.getText().toString());
                    params.put("education_background", school);
                    params.put("personal_introduction", mIntroduction.getText().toString());
                    showLoadingDialog("Update", "try best to update...");
                    UserHandler.updateUserBase(UserBaseActivity.this, params);
                }
                break;
            case R.id.user_base_select_birthday://选择日期

                DatePickerDialog picker = new DatePickerDialog(UserBaseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mBirthDay.setText(year +"-"+(monthOfYear+1)+"-"+dayOfMonth);
                    }
                }, 1990, 1, 1);
                picker.show();
                break;
        }
    }

    /**
     * 显示所有的视图
     */
    private void disabledAllView(){
        mSex.setEnabled(false);
        mPhone.setEnabled(false);
        mPhone.setBackground(null);
        mQq.setEnabled(false);
        mQq.setBackground(null);
        mEmail.setEnabled(false);
        mEmail.setBackground(null);
        mSelectBirthDay.setEnabled(false);
        mSchool.setEnabled(false);
        mIntroduction.setEnabled(false);
    }

    /**
     * 禁止所有的视图
     */
    private void enabledAllView(){
        mSex.setEnabled(true);
        mPhone.setEnabled(true);
        mPhone.setBackgroundResource(R.drawable.bg_user_base_edittext);
        mQq.setEnabled(true);
        mQq.setBackgroundResource(R.drawable.bg_user_base_edittext);
        mEmail.setEnabled(true);
        mEmail.setBackgroundResource(R.drawable.bg_user_base_edittext);
        mSelectBirthDay.setEnabled(true);
        mSchool.setEnabled(true);
        mIntroduction.setEnabled(true);
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);
        dismissLoadingDialog();
        try {
            JSONObject resultObject = new JSONObject(String.valueOf(result));
            if(TaskType.UPDATE_USER_BASE == type && resultObject != null){
                if(resultObject.has("isSuccess") && resultObject.getBoolean("isSuccess")){
                    //获取新的基本信息
                    SharedPreferenceUtil.saveUserInfo(getApplicationContext(), resultObject.getString("userinfo"));
                    ToastUtil.success(UserBaseActivity.this, getStringResource(R.string.user_base_update_success));
                    mRight.setText(getStringResource(R.string.edit));//将文字设置成编辑状态
                    disabledAllView();//将所有视图设置为不可编辑状态
                }else{
                    ToastUtil.failure(UserBaseActivity.this, resultObject.getString("message"));
                }
                return;
            }
        } catch (Exception e) {
            ToastUtil.failure(UserBaseActivity.this, getStringResource(R.string.user_base_update_error));
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
