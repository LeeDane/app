package com.leedane.cn.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.leedane.cn.bean.MySettingBean;
import com.leedane.cn.database.BlogDataBase;
import com.leedane.cn.database.FileDataBase;
import com.leedane.cn.database.GalleryDataBase;
import com.leedane.cn.database.MoodDataBase;
import com.leedane.cn.database.MySettingDataBase;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.ToastUtil;

/**
 * 我的设置的activity
 * Created by leedane on 2016/6/2.
 */
public class MySettingActivity extends BaseActivity implements Switch.OnCheckedChangeListener{

    public static final String TAG = "MySettingActivity";

    private static final String[] deleteArray = new String[]{"只删除本地记录", "同时删除服务器记录"};

    private MySettingDataBase mySettingDataBase;

    private Switch mLoadImage;  //加载图片
    private Switch mNoNotification; //勿扰模式
    private TextView mFirstLoad; //首次加载的条数
    private TextView mOtherLoad; //其他次加载的条数
    private Switch mDoubleClickOut; //双击退出
    private Switch mCacheBlog;  //缓存博客
    private TextView mClearBlog;  //清除缓存的博客
    private TextView mClearBlogShow; //目前缓存博客的数量
    private Switch mCacheGallery;  //缓存图库
    private TextView mClearGallery;  //清除缓存的图库
    private TextView mClearGalleryShow; //目前缓存图库的数量
    private Switch mCacheFile;  //缓存文件
    private TextView mClearFile;  //清除缓存的文件
    private TextView mClearFileShow; //目前缓存文件的数量
    private Switch mCacheMood; //缓存心情
    private TextView mClearMood; //清除缓存的心情
    private TextView mClearMoodShow; //目前缓存心情的数量
    private TextView mChatTextSize; //聊天字体大小
    private TextView mChatBg; //设置聊天背景
    private Spinner mChatDelete;  //聊天删除设置
    private Switch mChatSendEnter; //聊天回车发送

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(MySettingActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.MySettingActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }

        setContentView(R.layout.activity_my_setting);

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

    public void test(View v){
        ToastUtil.success(MySettingActivity.this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.nav_setting);
        backLayoutVisible();

        mLoadImage = (Switch)findViewById(R.id.my_setting_load_image);
        mNoNotification = (Switch)findViewById(R.id.my_setting_no_notification);
        mFirstLoad = (TextView)findViewById(R.id.my_setting_first_load);
        mOtherLoad = (TextView)findViewById(R.id.my_setting_other_load);
        mDoubleClickOut = (Switch)findViewById(R.id.my_setting_double_click_out);
        mCacheBlog = (Switch)findViewById(R.id.my_setting_cache_blog);
        mClearBlog = (TextView)findViewById(R.id.my_setting_cache_clear_blog);
        mClearBlogShow = (TextView)findViewById(R.id.my_setting_cache_clear_blog_show);
        mCacheGallery = (Switch)findViewById(R.id.my_setting_cache_gallery);
        mClearGallery = (TextView)findViewById(R.id.my_setting_cache_clear_gallery);
        mClearGalleryShow = (TextView)findViewById(R.id.my_setting_cache_clear_gallery_show);
        mCacheFile = (Switch)findViewById(R.id.my_setting_cache_file);
        mClearFile = (TextView)findViewById(R.id.my_setting_cache_clear_file);
        mClearFileShow = (TextView)findViewById(R.id.my_setting_cache_clear_file_show);
        mCacheMood = (Switch)findViewById(R.id.my_setting_cache_mood);
        mClearMood = (TextView)findViewById(R.id.my_setting_cache_clear_mood);
        mClearMoodShow = (TextView)findViewById(R.id.my_setting_cache_clear_mood_show);
        mChatTextSize = (TextView)findViewById(R.id.my_setting_chat_text_size);
        mChatBg = (TextView)findViewById(R.id.my_setting_chat_bg);
        mChatDelete = (Spinner)findViewById(R.id.my_setting_chat_delete);
        mChatSendEnter = (Switch)findViewById(R.id.my_setting_chat_send_enter);

        mLoadImage.setOnCheckedChangeListener(MySettingActivity.this);
        mNoNotification.setOnCheckedChangeListener(MySettingActivity.this);
        mDoubleClickOut.setOnCheckedChangeListener(MySettingActivity.this);
        mCacheBlog.setOnCheckedChangeListener(MySettingActivity.this);
        mCacheMood.setOnCheckedChangeListener(MySettingActivity.this);
        mChatSendEnter.setOnCheckedChangeListener(MySettingActivity.this);
        mCacheGallery.setOnCheckedChangeListener(MySettingActivity.this);
        mCacheFile.setOnCheckedChangeListener(MySettingActivity.this);

        mClearBlog.setOnClickListener(MySettingActivity.this);
        mClearBlogShow.setOnClickListener(MySettingActivity.this);
        mClearGallery.setOnClickListener(MySettingActivity.this);
        mClearGalleryShow.setOnClickListener(MySettingActivity.this);
        mClearFile.setOnClickListener(MySettingActivity.this);
        mClearFileShow.setOnClickListener(MySettingActivity.this);
        mClearMood.setOnClickListener(MySettingActivity.this);
        mClearMoodShow.setOnClickListener(MySettingActivity.this);
        mFirstLoad.setOnClickListener(MySettingActivity.this);
        mOtherLoad.setOnClickListener(MySettingActivity.this);

        mySettingDataBase = new MySettingDataBase(MySettingActivity.this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {

        ArrayAdapter<String> arraySexAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, deleteArray);
        mChatDelete.setAdapter(arraySexAdapter);
        mChatDelete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mLoadImage.setChecked(MySettingConfigUtil.getLoadImage());

        mNoNotification.setChecked(MySettingConfigUtil.getNoNotification());

        mFirstLoad.setText(MySettingConfigUtil.getFirstLoad() + "条");

        mOtherLoad.setText(MySettingConfigUtil.getOtherLoad() + "条");

        mDoubleClickOut.setChecked(MySettingConfigUtil.getDoubleClickOut());


        mCacheBlog.setChecked(MySettingConfigUtil.getCacheBlog());
        mClearBlog.setEnabled(MySettingConfigUtil.getCacheBlog());
        mClearBlogShow.setEnabled(MySettingConfigUtil.getCacheBlog());
        if(MySettingConfigUtil.getCacheBlog()){
            BlogDataBase blogDataBase = new BlogDataBase(MySettingActivity.this);
            mClearBlogShow.setText(blogDataBase.getTotal() + "条");
            blogDataBase.destroy();
        }

        mCacheMood.setChecked(MySettingConfigUtil.getCacheMood());
        mClearMood.setEnabled(MySettingConfigUtil.getCacheMood());
        mClearMoodShow.setEnabled(MySettingConfigUtil.getCacheMood());
        if(MySettingConfigUtil.getCacheMood()){
            MoodDataBase moodDataBase = new MoodDataBase(MySettingActivity.this);
            mClearMoodShow.setText(moodDataBase.getTotal() + "条");
            moodDataBase.destroy();
        }

        mChatTextSize.setText(MySettingConfigUtil.getChatTextSize() + "");

        mChatDelete.setSelection(MySettingConfigUtil.getChatDelete(), true);

        mChatSendEnter.setChecked(MySettingConfigUtil.getChatSendEnter());

        mCacheGallery.setChecked(MySettingConfigUtil.getCacheGallery());
        mClearGallery.setEnabled(MySettingConfigUtil.getCacheGallery());
        mClearGalleryShow.setEnabled(MySettingConfigUtil.getCacheGallery());
        if(MySettingConfigUtil.getCacheGallery()){
            GalleryDataBase galleryDataBase = new GalleryDataBase(MySettingActivity.this);
            mClearGalleryShow.setText(galleryDataBase.getTotal() + "条");
            galleryDataBase.destroy();
        }

        mCacheFile.setChecked(MySettingConfigUtil.getCacheFile());
        mClearFile.setEnabled(MySettingConfigUtil.getCacheFile());
        mClearFileShow.setEnabled(MySettingConfigUtil.getCacheFile());
        if(MySettingConfigUtil.getCacheFile()){
            FileDataBase fileDataBase = new FileDataBase(MySettingActivity.this);
            mClearFileShow.setText(fileDataBase.getTotal() + "条");
            fileDataBase.destroy();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.my_setting_cache_clear_blog:
                BlogDataBase blogDataBase = new BlogDataBase(MySettingActivity.this);
                blogDataBase.deleteAll();
                blogDataBase.destroy();
                mClearBlogShow.setText(0+"条");
                break;
            case R.id.my_setting_cache_clear_blog_show:
                BlogDataBase blogDataBase1 = new BlogDataBase(MySettingActivity.this);
                blogDataBase1.deleteAll();
                blogDataBase1.destroy();
                mClearBlogShow.setText(0+"条");
                break;
            case R.id.my_setting_cache_clear_gallery:
                GalleryDataBase galleryDataBase = new GalleryDataBase(MySettingActivity.this);
                galleryDataBase.deleteAll();
                galleryDataBase.destroy();
                mClearGalleryShow.setText(0+"条");
                break;
            case R.id.my_setting_cache_clear_gallery_show:
                GalleryDataBase galleryDataBase1 = new GalleryDataBase(MySettingActivity.this);
                galleryDataBase1.deleteAll();
                galleryDataBase1.destroy();
                mClearGalleryShow.setText(0+"条");
                break;
            case R.id.my_setting_cache_clear_file:
                FileDataBase fileDataBase = new FileDataBase(MySettingActivity.this);
                fileDataBase.deleteAll();
                fileDataBase.destroy();
                mClearFileShow.setText(0+"条");
                break;
            case R.id.my_setting_cache_clear_file_show:
                FileDataBase fileDataBase1 = new FileDataBase(MySettingActivity.this);
                fileDataBase1.deleteAll();
                fileDataBase1.destroy();
                mClearFileShow.setText(0+"条");
                break;
            case R.id.my_setting_cache_clear_mood:
                MoodDataBase moodDataBase = new MoodDataBase(MySettingActivity.this);
                moodDataBase.deleteAll();
                moodDataBase.destroy();
                mClearMoodShow.setText(0+"条");
                break;
            case R.id.my_setting_cache_clear_mood_show:
                MoodDataBase moodDataBase1 = new MoodDataBase(MySettingActivity.this);
                moodDataBase1.deleteAll();
                moodDataBase1.destroy();
                mClearMoodShow.setText(0+"条");
                break;
            case R.id.my_setting_first_load:
                NumberPicker mPicker = new NumberPicker(MySettingActivity.this);
                mPicker.setMinValue(6);
                mPicker.setMaxValue(15);
                mPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        MySettingBean mySettingBean = new MySettingBean();
                        mySettingBean.setValue(String.valueOf(newVal));
                        mySettingBean.setId(3);
                        mySettingDataBase.update(mySettingBean);
                        mFirstLoad.setText(newVal + "条");
                        MySettingConfigUtil.setFirstLoad(newVal);
                    }
                });


                AlertDialog mAlertDialog = new AlertDialog.Builder(MySettingActivity.this)
                        .setTitle("NumberPicker").setView(mPicker).setPositiveButton("ok", null).create();
                mAlertDialog.show();
                break;
            case R.id.my_setting_other_load:
                NumberPicker mPicker1 = new NumberPicker(MySettingActivity.this);
                mPicker1.setMinValue(4);
                mPicker1.setMaxValue(8);
                mPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        MySettingBean mySettingBean = new MySettingBean();
                        mySettingBean.setValue(String.valueOf(newVal));
                        mySettingBean.setId(4);
                        mySettingDataBase.update(mySettingBean);
                        mOtherLoad.setText(newVal + "条");
                        MySettingConfigUtil.setOtherLoad(newVal);
                    }
                });


                AlertDialog mAlertDialog1 = new AlertDialog.Builder(MySettingActivity.this)
                        .setTitle("NumberPicker").setView(mPicker1).setPositiveButton("ok", null).create();
                mAlertDialog1.show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySettingDataBase.destroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        MySettingBean mySettingBean = new MySettingBean();
        String flag = "0";
        if(isChecked){
            flag = "1";
        }
        mySettingBean.setValue(flag);
        switch (buttonView.getId()){
            case R.id.my_setting_load_image:
                mySettingBean.setId(1);
                MySettingConfigUtil.setLoadImage(isChecked);
                break;
            case R.id.my_setting_no_notification:
                mySettingBean.setId(2);
                MySettingConfigUtil.setNoNotification(isChecked);
                break;
            case R.id.my_setting_double_click_out:
                mySettingBean.setId(5);
                MySettingConfigUtil.setDoubleClickOut(isChecked);
                break;
            case R.id.my_setting_cache_blog:
                mySettingBean.setId(6);
                MySettingConfigUtil.setCacheBlog(isChecked);
                if(isChecked){
                    mClearBlog.setEnabled(true);
                    mClearBlogShow.setEnabled(true);
                }
                else{
                    mClearBlog.setEnabled(false);
                    mClearBlogShow.setEnabled(false);
                    BlogDataBase blogDataBase = new BlogDataBase(MySettingActivity.this);
                    blogDataBase.deleteAll();
                    blogDataBase.destroy();
                    mClearBlogShow.setText(0 + "条");
                }
                break;
            case R.id.my_setting_cache_mood:
                mySettingBean.setId(7);
                MySettingConfigUtil.setCacheMood(isChecked);
                if(isChecked){
                    mClearMood.setEnabled(true);
                    mClearMoodShow.setEnabled(true);
                }
                else{
                    mClearMood.setEnabled(false);
                    mClearMoodShow.setEnabled(false);
                    MoodDataBase moodDataBase = new MoodDataBase(MySettingActivity.this);
                    moodDataBase.deleteAll();
                    moodDataBase.destroy();
                    mClearMoodShow.setText(0+"条");
                }
                break;
            case R.id.my_setting_cache_gallery:
                mySettingBean.setId(11);
                MySettingConfigUtil.setCacheGallery(isChecked);
                if(isChecked){
                    mClearGallery.setEnabled(true);
                    mClearGalleryShow.setEnabled(true);
                }
                else{
                    mClearGallery.setEnabled(false);
                    mClearGalleryShow.setEnabled(false);
                    GalleryDataBase galleryDataBase = new GalleryDataBase(MySettingActivity.this);
                    galleryDataBase.deleteAll();
                    galleryDataBase.destroy();
                    mClearGalleryShow.setText(0+"条");
                }
                break;
            case R.id.my_setting_cache_file:
                mySettingBean.setId(11);
                MySettingConfigUtil.setCacheFile(isChecked);
                if(isChecked){
                    mClearFile.setEnabled(true);
                    mClearFileShow.setEnabled(true);
                }
                else{
                    mClearFile.setEnabled(false);
                    mClearFileShow.setEnabled(false);
                    FileDataBase fileDataBase = new FileDataBase(MySettingActivity.this);
                    fileDataBase.deleteAll();
                    fileDataBase.destroy();
                    mClearFileShow.setText(0+"条");
                }
                break;
            case R.id.my_setting_chat_send_enter:
                mySettingBean.setId(9);
                MySettingConfigUtil.setChatSendEnter(isChecked);
                break;
        }

        mySettingDataBase.update(mySettingBean);
    }

}
