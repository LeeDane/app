package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.handler.UserHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

/**
 * 更新用户登录密码activity
 * Created by LeeDane on 2016/6/4.
 */
public class UpdateLoginPswActivity extends BaseActivity{
    public static final String TAG = "UpdateLoginPswActivity";

    public static final int UPDATE_LOGIN_PASSWORD_CODE = 51;//更新登录密码

    private Button mRight;

    private EditText mOldPsw;
    private EditText mNewPsw;
    private EditText mConfirmPsw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_user_psw);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.user_info);
        backLayoutVisible();

        initView();
    }

    private void initView() {

        mRight = (Button)findViewById(R.id.view_right_button);
        mRight.setVisibility(View.VISIBLE);
        mRight.setOnClickListener(this);
        mRight.setText(getStringResource(R.string.comlpete));
        mOldPsw = (EditText)findViewById(R.id.update_user_old_psw);
        mNewPsw = (EditText)findViewById(R.id.update_user_new_psw);
        mConfirmPsw = (EditText)findViewById(R.id.update_user_confirm_psw);
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        dismissLoadingDialog();
        if(result instanceof Error){
            ToastUtil.failure(UpdateLoginPswActivity.this, ((Error) result).getMessage(), Toast.LENGTH_SHORT);
            return;
        }

        try {
            JSONObject resultObject = new JSONObject(String.valueOf(result));
            if(TaskType.UPDATE_LOGIN_PSW == type && resultObject != null){
                if(resultObject.has("isSuccess") && resultObject.getBoolean("isSuccess")){
                    ToastUtil.success(UpdateLoginPswActivity.this, resultObject.getString("message"), Toast.LENGTH_SHORT);

                    Intent intent = new Intent();
                    setResult(UPDATE_LOGIN_PASSWORD_CODE, intent);
                    intent.putExtra("success", true);
                    UpdateLoginPswActivity.this.finish();
                }else{
                    ToastUtil.failure(UpdateLoginPswActivity.this, resultObject.getString("message"), Toast.LENGTH_SHORT);
                }
                return;
            }
        } catch (Exception e) {
            ToastUtil.failure(UpdateLoginPswActivity.this, getStringResource(R.string.update_login_error), Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_button:
                String password = mOldPsw.getText().toString();
                if(StringUtil.isNull(password)){
                    ToastUtil.failure(UpdateLoginPswActivity.this, "请输入原密码");
                    mOldPsw.setSelected(true);
                    mOldPsw.setFocusable(true);
                    return;
                }

                String newPassword = mNewPsw.getText().toString();
                if(StringUtil.isNull(newPassword)){
                    ToastUtil.failure(UpdateLoginPswActivity.this, "请输入新密码");
                    mNewPsw.setSelected(true);
                    mNewPsw.setFocusable(true);
                    return;
                }

                /*if(newPassword.length() < 8){
                    ToastUtil.failure(UpdateLoginPswActivity.this, "新密码长度不能小于8位");
                    mNewPsw.setSelected(true);
                    mNewPsw.setFocusable(true);
                    return;
                }*/

                String confirmPassword = mConfirmPsw.getText().toString();
                if(StringUtil.isNull(confirmPassword)){
                    ToastUtil.failure(UpdateLoginPswActivity.this, "请输入确认密码");
                    mConfirmPsw.setSelected(true);
                    mConfirmPsw.setFocusable(true);
                    return;
                }

                if(password.equals(newPassword)){
                    ToastUtil.failure(UpdateLoginPswActivity.this, "新密码跟原密码一致");
                    mNewPsw.setSelected(true);
                    mNewPsw.setFocusable(true);
                    return;
                }

                if(!newPassword.equals(confirmPassword)){
                    ToastUtil.failure(UpdateLoginPswActivity.this, "确认密码跟新密码不一致");
                    mConfirmPsw.setSelected(true);
                    mConfirmPsw.setFocusable(true);
                    return;
                }

                UserHandler.updateLoginPsw(UpdateLoginPswActivity.this, password, newPassword);
                showLoadingDialog("Update", "try best to update...");
                break;
        }
    }
}
