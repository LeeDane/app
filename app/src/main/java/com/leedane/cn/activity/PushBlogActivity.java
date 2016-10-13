package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.fragment.SendToolbarFragment;
import com.leedane.cn.handler.BlogHandler;
import com.leedane.cn.richtext.RichTextEditText;
import com.leedane.cn.richtext.ToolFragment;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 发布博客的activity类
 * Created by Leedane on 2016/10/10.
 */
public class PushBlogActivity extends BaseActivity {

    public static final String TAG = "PushBlogActivity";
    private EditText mTitle;
    public RichTextEditText richTextContent;

    /**
     * 发送的imageview
     */
    private ImageView mRightSend;

    /**
     * 预览的Button
     */
    private Button mRightPreView;

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

        //显示标题栏的发送图片按钮
        mRightSend = (ImageView)findViewById(R.id.view_right_img);
        mRightSend.setImageResource(R.drawable.send);
        mRightSend.setVisibility(View.VISIBLE);
        mRightSend.setOnClickListener(this);

        //显示标题栏的预览按钮
        mRightPreView = (Button)findViewById(R.id.view_right_button);
        mRightPreView.setVisibility(View.VISIBLE);
        mRightPreView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_img: //发送
                showLoadingDialog("Blog", "try best to pushing...");
                HashMap<String, Object> params = new HashMap<>();
                params.put("has_img", false);
                params.put("title", mTitle.getText().toString());
                params.put("content", richTextContent.getText().toString());
                params.put("status", ConstantsUtil.STATUS_NORMAL);
                BlogHandler.send(this, params);
                break;
            case R.id.view_right_button://预览

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
            if(type == TaskType.ADD_BLOG){
                if (jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")) {
                    ToastUtil.success(this, "成功"+jsonObject, Toast.LENGTH_SHORT);
                }else{
                    ToastUtil.failure(this, "失败"+jsonObject, Toast.LENGTH_SHORT);
                }
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

