package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.leedane.cn.app.R;
import com.leedane.cn.fragment.SendToolbarFragment;
import com.leedane.cn.richtext.RichTextEditText;
import com.leedane.cn.richtext.ToolFragment;
import com.leedane.cn.task.TaskType;

import org.json.JSONObject;

/**
 * 发布博客的activity类
 * Created by Leedane on 2016/10/10.
 */
public class PushBlogActivity extends BaseActivity {

    public static final String TAG = "PushBlogActivity";
    private EditText mTitle;
    public RichTextEditText richTextContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(PushBlogActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.PushBlogActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }
        setTitleViewText(R.string.push_blog);
        setContentView(R.layout.activity_push_blog);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        backLayoutVisible();

        mTitle = (EditText)findViewById(R.id.rich_text_title);
        richTextContent = (RichTextEditText)findViewById(R.id.rich_text_content);

        ToolFragment toolFragment = ToolFragment.newInstance(null);
        getSupportFragmentManager().beginTransaction().replace(R.id.rich_text_tool, toolFragment).commit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.mood_location:

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event);
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
            dismissLoadingDialog();
            //发表心情
            if(type == TaskType.SEND_MOOD_NORMAL || type == TaskType.ADD_COMMENT || type == TaskType.ADD_TRANSMIT){

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

