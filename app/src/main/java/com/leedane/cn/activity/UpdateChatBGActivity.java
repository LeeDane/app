package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.MySettingBean;
import com.leedane.cn.database.MySettingDataBase;
import com.leedane.cn.fragment.ChatBgSelectSystemFragment;
import com.leedane.cn.fragment.ChatBgSelectWebFragment;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.MediaUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.ToastUtil;

/**
 * 更新聊天背景图片
 * Created by LeeDane on 2016/6/10.
 */
public class UpdateChatBGActivity extends BaseActivity {

    private Button selectGallery;
    private Button selectWeb;
    private Button clearBg;
    private MySettingDataBase mySettingDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if (!checkedIsLogin()) {
            Intent it = new Intent(UpdateChatBGActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.UpdateChatBGActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_update_chat_bg);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.update_chat_bg);
        backLayoutVisible();
        mySettingDataBase = new MySettingDataBase(UpdateChatBGActivity.this);
        initView();
    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        selectGallery = (Button)findViewById(R.id.select_gallery);
        selectWeb = (Button)findViewById(R.id.select_web);
        clearBg = (Button)findViewById(R.id.clear_chat_bg);
        selectGallery.setOnClickListener(this);
        selectWeb.setOnClickListener(this);
        clearBg.setOnClickListener(this);

        Bundle bundle = new Bundle();
        ChatBgSelectWebFragment chatBgSelectWebFragment = ChatBgSelectWebFragment.newInstance(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.chat_bg_container, chatBgSelectWebFragment).commit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.select_gallery:
                //调用系统图库
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra("crop", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, MoodActivity.GET_SYSTEM_IMAGE_CODE);
                break;
            case R.id.select_web:
                Bundle bundle = new Bundle();
                ChatBgSelectWebFragment chatBgSelectWebFragment = ChatBgSelectWebFragment.newInstance(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.chat_bg_container, chatBgSelectWebFragment).commit();
                break;
            case R.id.clear_chat_bg:
                MySettingBean mySettingBean = new MySettingBean();
                mySettingBean.setValue("");
                mySettingBean.setId(13);
                mySettingDataBase.update(mySettingBean);
                MySettingConfigUtil.setCacheChatBgPath("");
                ToastUtil.success(UpdateChatBGActivity.this, "清除聊天背景成功");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            System.out.println("requestCode"+requestCode);
            if (requestCode == MoodActivity.GET_SYSTEM_IMAGE_CODE) {
                try{
                    Bundle bundle = new Bundle();
                    bundle.putString("imagePath", MediaUtil.getImageAbsolutePath(UpdateChatBGActivity.this, data.getData()));
                    ChatBgSelectSystemFragment chatBgSelectSystemFragment = ChatBgSelectSystemFragment.newInstance(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.chat_bg_container, chatBgSelectSystemFragment).commit();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        mySettingDataBase.destroy();
        super.onDestroy();
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);
    }
}
