package com.leedane.cn.activity;

import android.os.Bundle;
import android.view.View;

import com.leedane.cn.database.BaseSQLiteDatabase;
import com.leedane.cn.frament.ChatHomeFragment;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskType;

/**
 * 聊天首页activity
 * Created by LeeDane on 2016/5/4.
 */
public class ChatActivity extends BaseActivity {
    private static final String TAG = "ChatActivity";
    private BaseSQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //检查是否登录
        checkedIsLogin("com.leedane.cn.activity.ChatActivity");

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.chat);
        backLayoutVisible();
        sqLiteDatabase = new BaseSQLiteDatabase(ChatActivity.this);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ChatHomeFragment chatHomeFragment = ChatHomeFragment.newInstance(null);
        getSupportFragmentManager().beginTransaction().replace(R.id.chat_container, chatHomeFragment).commit();
    }

    /**
     * 初始化数据
     */
    private void initData() {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);

    }

    @Override
    protected void onDestroy() {
        sqLiteDatabase.closeDataBase();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()){
        }

    }

}
