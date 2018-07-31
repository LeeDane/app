package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.broadcast.UserInfoDataReceiver;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 用户中心的activity
 * Created by LeeDane on 2016/4/14.
 */
public class CircleSquareActivity extends BaseActivity {

    public static final String TAG = "UserInfoActivity";
    @Bind(R.id.login_user_name)
    TextView loginUserName;
    @Bind(R.id.scroe)
    TextView scroe;
    @Bind(R.id.circle_number)
    TextView circleNumber;
    @Bind(R.id.all_circles)
    HorizontalScrollView allCircles;
    @Bind(R.id.circle_user_posts)
    RecyclerView circleUserPosts;
    @Bind(R.id.hotest_posts)
    RecyclerView hotestPosts;
    @Bind(R.id.recommends)
    RecyclerView recommends;
    @Bind(R.id.hotests)
    RecyclerView hotests;
    @Bind(R.id.newests)
    RecyclerView newests;

    private UserInfoDataReceiver userInfoDataReceive = new UserInfoDataReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if (!checkedIsLogin()) {
            Intent it = new Intent(CircleSquareActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.SquareCircleActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_circle_square);
        ButterKnife.bind(CircleSquareActivity.this);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.message);
        backLayoutVisible();
        initView();
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap params = new HashMap();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("cc/init");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
        TaskLoader.getInstance().startTaskForResult(TaskType.CIRCLE_SQUARE_初始化, this, requestBean);
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
        JSONObject userInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());
        loginUserName.setText(userInfo.optString("account"));

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);
        if (result instanceof Error) {
            if (type == TaskType.CIRCLE_SQUARE_初始化) {
                return;
            }
        }
        if (type == TaskType.CIRCLE_SQUARE_初始化) {
            JSONObject rootJson = null;
            try {
                rootJson = new JSONObject(String.valueOf(result));
                ToastUtil.success(CircleSquareActivity.this, rootJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
