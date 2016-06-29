package com.leedane.cn.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.MoodGridViewAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.bean.LocationBean;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.service.SendMoodService;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.MediaUtil;
import com.leedane.cn.util.NotificationUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 处理心情的activity类
 * Created by Leedane on 2015/12/3.
 */
public class MoodActivity extends BaseActivity {

    public static final String TAG = "MoodActivity";

    private LinearLayout mOperate;
    /**
     * 位置信息
     */
    private ImageView mMoodLocation;

    private TextView mMoodLocationShow;

    /**
     * 是否可以评论
     */
    private CheckBox mCanComment;

    /**
     * 是否可以转发
     */
    private CheckBox mCanTransmit;

    /**
     * 选择At好友
     */
    private ImageView mMoodFriend;
    /**
     * 发表的按钮
     */
    private Button mBtnPublish;

    /**
     * 添加图片
     */
    private ImageView mMoodAdd;

    /**
     * 展示原文的信息
     */
    private TextView mOldMood;

    private LinearLayout mMoodTransmitCommentContent;

    /**
     * 转发或者评论的原文对象
     */
    private MoodBean mOldMoodBean;

    /**
     * 展示已经添加的图片
     */
    private GridView mGridview;

    /**
     * 展示心情的内容
     */
    private EditText mMoodContent;

    /**
     * 操作类型(0：发表，1:转发，2:评论)
     */
    private int mOperateType;

    /**
     * 转发等操作的心情对象
     */
    private JSONObject mMoodObj;

    private JSONObject mUserInfo;

    //private BaseSQLiteDatabase sqLiteDatabase;

    /**
     * 用户选择的图片地址列表
     */
    private List<String> mUris = new ArrayList<>();
    private List<Uri> mUriList = new ArrayList<>();

    private MoodGridViewAdapter mMoodGridViewAdapter;
    Intent it_mood;

    private LocationBean locationBean; //位置信息
    /**
     * 调用系统图库的请求编码
     */
    public static final int GET_SYSTEM_IMAGE_CODE = 2;

    /**
     * 调用位置activity的请求编码
     */
    public static final int GET_LOCATION_CODE = 21;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(MoodActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.MoodActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }
        it_mood = getIntent();
        mOperateType = it_mood.getIntExtra("operateType", 0);
        if(mOperateType == EnumUtil.MoodOperateType.转发.value || mOperateType == EnumUtil.MoodOperateType.评论.value){
            try{
                mOldMoodBean = (MoodBean) it_mood.getSerializableExtra("moodObj");
                if(mOldMoodBean != null){
                    if(mOperateType == EnumUtil.MoodOperateType.转发.value)
                        ToastUtil.success(MoodActivity.this, "该心情可以转发" + mOldMoodBean.getContent());
                    else
                        ToastUtil.success(MoodActivity.this, "该心情可以评论" + mOldMoodBean.getContent());
                }else{
                    if(mOperateType == EnumUtil.MoodOperateType.转发.value)
                        ToastUtil.success(MoodActivity.this, "无法转发");
                    else
                        ToastUtil.success(MoodActivity.this, "无法评论");
                    finish();
                    return;
                }
            }catch(Exception e){
                if(mOperateType ==EnumUtil.MoodOperateType.转发.value)
                    ToastUtil.success(MoodActivity.this, "心情json对象转化出错，无法转发");
                else
                    ToastUtil.success(MoodActivity.this, "心情json对象转化出错，无法评论");
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_mood);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mUserInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));

        mOperate = (LinearLayout)findViewById(R.id.mood_operate);

        //内容
        mMoodContent = (EditText)findViewById(R.id.mood_content);
        mMoodContent.setHint("在此写下您想说的");
        mMoodContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = String.valueOf(s);
                //只有一个@字符启动好友选择
                if(value.equals("@")){
                    startATFriendActivity();
                }

                if(value.endsWith(" @")){
                    //startATFriendActivity();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if(mOperateType == 0){
            setTitleViewText("发表" + getStringResource(R.string.personal_mood));
        }else if(mOperateType == 1){
            setTitleViewText("转发" + getStringResource(R.string.personal_mood));
        }else if(mOperateType == 2){
            setTitleViewText("评论" + getStringResource(R.string.personal_mood));
        }
        backLayoutVisible();

        //发表按钮
        mBtnPublish = (Button)findViewById(R.id.view_right_button);
        mBtnPublish.setVisibility(View.VISIBLE);
        mBtnPublish.setOnClickListener(this);

        mMoodLocationShow = (TextView)findViewById(R.id.mood_location_show);

        if(mOperateType == 1){
            mBtnPublish.setText(getStringResource(R.string.mood_transmit));
        }else if(mOperateType == 2){
            mBtnPublish.setText(getStringResource(R.string.mood_comment));
        }
        if(mOperateType == 0){
            mOperate.setVisibility(View.VISIBLE);
            mMoodLocation = (ImageView)findViewById(R.id.mood_location);
            mMoodLocation.setOnClickListener(this);

            //选择好友
            mMoodFriend = (ImageView)findViewById(R.id.mood_friend);
            mMoodFriend.setOnClickListener(this);

            //添加照片
            mMoodAdd = (ImageView)findViewById(R.id.mood_add);
            mMoodAdd.setOnClickListener(this);
            mMoodGridViewAdapter = new MoodGridViewAdapter(MoodActivity.this, mUris);
            //显示添加后的图片
            mGridview = (GridView)findViewById(R.id.mood_gridview);
            mGridview.setAdapter(mMoodGridViewAdapter);

            mCanComment = (CheckBox)findViewById(R.id.mood_can_comment);
            mCanTransmit = (CheckBox)findViewById(R.id.mood_can_transmit);
        }else{
            mOperate.setVisibility(View.GONE);
            mOldMood = (TextView)findViewById(R.id.mood_show_old);
            mMoodTransmitCommentContent = (LinearLayout)findViewById(R.id.mood_transmit_comment_content);
            mMoodTransmitCommentContent.setVisibility(View.VISIBLE);
            mOldMood.setVisibility(View.VISIBLE);
            mOldMood.setText(mOldMoodBean.getContent());
            String imgs = mOldMoodBean.getImgs();
            if(StringUtil.isNotNull(imgs)){
                String[] images = imgs.split(",");
                List<String> links = Arrays.asList(images);
                int width = it_mood.getIntExtra("width", 30) ;//展示的图像的宽度
                int height = it_mood.getIntExtra("height", 30); //展示的图像的高度
                StaticMoodGridViewAdapter adapter = new StaticMoodGridViewAdapter(links, width, height);
                //显示添加后的图片
                mGridview = (GridView)findViewById(R.id.mood_gridview);
                mGridview.setAdapter(adapter);
            }
        }
    }
    /**
     * 触发AT朋友的activity
     */
    public void startATFriendActivity(){
        Intent it = new Intent(MoodActivity.this, AtFriendActivity.class);
        startActivityForResult(it, AtFriendActivity.SELECT_AT_FRIENDS_CODE);
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.mood_location:
                locationBean = null;
                Intent intent_location = new Intent(MoodActivity.this, LocationActivity.class);
                startActivityForResult(intent_location, GET_LOCATION_CODE );
                break;
            case R.id.mood_friend: //选择好友
                startATFriendActivity();
                break;
            case R.id.mood_add:
                if(mUris.size() > 2){
                    Toast.makeText(MoodActivity.this, "最多允许选择3张图片", Toast.LENGTH_LONG).show();
                    return;
                }
                //调用系统图库
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra("crop", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, GET_SYSTEM_IMAGE_CODE);
                break;
            case R.id.view_right_button:
                final String content = mMoodContent.getText().toString();

                //发表评论
                if(mOperateType == EnumUtil.MoodOperateType.评论.value ){
                    if(StringUtil.isNull(content)){
                        ToastUtil.failure(MoodActivity.this, "评论内容不能为空");
                        return;
                    }
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("content", content);
                    params.put("table_name", "t_mood");
                    params.put("table_id", mOldMoodBean.getId());
                    params.put("level", 1);
                    CommentHandler.sendComment(this, params);
                    showLoadingDialog("发表评论", "正在发表评论，请稍等...");
                    return;
                }
                //发表转发
                if(mOperateType == EnumUtil.MoodOperateType.转发.value){
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("content", StringUtil.isNull(content)? "转发了这条心情": content);
                    params.put("table_name", "t_mood");
                    params.put("table_id", mOldMoodBean.getId());
                    TransmitHandler.sendTransmit(this, params);
                    showLoadingDialog("转发", "正在转发，请稍等...");
                    return;
                }
                if(StringUtil.isNull(content) && mUris.size() == 0){
                    if(mOperateType == 0)
                        ToastUtil.failure(getBaseContext(), "内容不能为空或者至少选择一张图片");
                    else
                        ToastUtil.failure(getBaseContext(), "至少输入点什么吧");
                    mMoodContent.setFocusable(true);
                    return;
                }
                if(mUris.size() > 0){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MoodActivity.this);
                    builder.setCancelable(false);
                    builder.setIcon(R.drawable.head);
                    builder.setTitle("提示");
                    builder.setMessage("操作可能很耗时，是否后台进行?");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    Intent it_service = new Intent();
                                    it_service.setClass(getApplicationContext(), SendMoodService.class);
                                    it_service.setAction("com.leedane.cn.sendMoodService");
                                    if (mOldMoodBean != null)
                                        it_service.putExtra("oldMoodId", mOldMoodBean.getId());
                                    it_service.putExtra("content", content);
                                    if(locationBean != null){
                                        it_service.putExtra("location", locationBean.getName());
                                        it_service.putExtra("longitude", locationBean.getLongitude());
                                        it_service.putExtra("latitude", locationBean.getLatitude());
                                    }

                                    it_service.putExtra("can_comment", mCanComment.isChecked());
                                    it_service.putExtra("can_transmit", mCanTransmit.isChecked());
                                    StringBuffer buffer = new StringBuffer();
                                    String uris = "";
                                    if(mUriList.size() > 0){
                                        for (Uri uri : mUriList) {
                                            buffer.append(MediaUtil.getImageAbsolutePath(MoodActivity.this, uri));
                                            buffer.append(",");
                                            //itemList.add(FileUtil.buildUploadItem(MoodActivity.this, uri, mUserInfo, new Date()));
                                        }
                                    }else{
                                        for (String uri : mUris) {
                                            buffer.append(uri);
                                            buffer.append(",");
                                            //itemList.add(FileUtil.buildUploadItem(MoodActivity.this, uri, mUserInfo, new Date()));
                                        }
                                    }
                                    if (mUris.size() > 0) {
                                        uris = buffer.toString().substring(0, buffer.toString().length() - 1);
                                    }
                                    //MediaStore.Images.Media.
                                    it_service.putExtra("uris", uris);
                                /*UploadBean uploadBean = new UploadBean();
                                List<UploadItem> itemList = new ArrayList<>();

                                uploadBean.setItemList(itemList);*/
                                    // it_service.putExtra("uploadBean", uploadBean);
                                    getApplicationContext().startService(it_service);
                                    finish();
                                }
                            });
                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ToastUtil.success(MoodActivity.this, "正在发表...");
                                }
                            });
                    builder.show();
                }else{
                    //简单的发送
                    HttpRequestBean requestBean = new HttpRequestBean();
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("content", content);
                    params.put("can_comment", mCanComment.isChecked());
                    params.put("can_transmit", mCanTransmit.isChecked());
                    if(locationBean != null){
                        params.put("location", locationBean.getName());
                        params.put("longitude", locationBean.getLongitude());
                        params.put("latitude", locationBean.getLatitude());
                    }

                    params.putAll(BaseApplication.newInstance().getBaseRequestParams());
                    requestBean.setParams(params);
                    requestBean.setServerMethod("leedane/mood_sendWord.action");
                    requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
                    TaskLoader.getInstance().startTaskForResult(TaskType.SEND_MOOD_NORMAL, MoodActivity.this, requestBean);
                    showLoadingDialog("发表心情", "正在发表，请稍等...");
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if( mOperateType == 0 && StringUtil.isNotNull(mMoodContent.getText().toString())){
                createLeaveAlertDialog();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 创建离开的警告提示框
     */
    public void createLeaveAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("温馨提示")
                .setMessage("有数据未保存，是否存为草稿？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        saveData();
                        MoodActivity.this.finish();
                    }

                })
                .setNegativeButton("拒绝",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferenceUtil.clearMoodDraft(getApplicationContext());
                        /*sqLiteDatabase = new BaseSQLiteDatabase(MoodActivity.this, 1);
                        sqLiteDatabase.delete(BaseSQLiteOpenHelper.TABLE_MOOD_DRAFT, null, null);*/
                        MoodActivity.this.finish();
                    }
                }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }

    /**
     * 保存未保存的数据
     */
    private void saveData() {
        String uStr = "";
        if(mUris != null && mUris.size()> 0){
            for(String u: mUris){
                uStr = uStr + u + ",";
            }
            if(uStr.endsWith(","))
                uStr = uStr.substring(0, uStr.length() -1);
        }
        try{
            /*Map<String, Object> params = new HashMap<>();
            params.put("id", 1);
            params.put("content", mMoodContent.getText().toString());
            params.put("uris",uStr);
            params.put("create_time", DateUtil.DateToString(new Date()));
            params.put("create_user_id", String.valueOf(BaseApplication.getLoginUserId()));
            sqLiteDatabase.insert(BaseSQLiteOpenHelper.TABLE_MOOD_DRAFT, params);*/
            SharedPreferenceUtil.saveMoodDraft(getApplicationContext(), mMoodContent.getText().toString(), uStr);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if( mOperateType == 0 && StringUtil.isNotNull(mMoodContent.getText().toString())){
            saveData();
        }
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
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(MoodActivity.this, "您的心情" + getNameByType()+ "成功");
                    if(mOperateType == 2){
                        Intent intent = new Intent();
                        setResult(PersonalActivity.MOOD_COMMENT_REQUEST_CODE, intent);
                    }
                    finish();//关闭当前activity
                }else{
                    ToastUtil.failure(MoodActivity.this, "心情" + getNameByType()+ "失败" + ":" + (jsonObject.has("message") ? jsonObject.getString("message") : ""));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getNameByType(){
        String name = "";
        switch (mOperateType){
            case 0: //发表
                name = "发表";
                break;
            case 1://转发
                name = "转发";
                break;
            case 2: //评论
                name = "评论";
                break;
        }
        return name;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            System.out.println("requestCode"+requestCode);
            if (requestCode == GET_SYSTEM_IMAGE_CODE) {//图库返回
                List<String> tempUris = new ArrayList<>();
                for(String uri: mUris){
                    tempUris.add(uri);
                }
                Uri uri = data.getData();
                mUriList.add(uri);
                tempUris.add(uri.getPath());
                mMoodGridViewAdapter.refreshData(tempUris);
                Toast.makeText(getBaseContext(), "获取的图片路径是：" + MediaUtil.getImageAbsolutePath(MoodActivity.this, uri), Toast.LENGTH_LONG).show();
            }
        }
        //更新选择
        if(requestCode == AtFriendActivity.SELECT_AT_FRIENDS_CODE && data != null){
            String select = data.getStringExtra("select");
            if(StringUtil.isNotNull(select)){
                String oldContent = mMoodContent.getText().toString();
                if(StringUtil.isNotNull(oldContent)){
                    if(oldContent.endsWith("@"))
                    oldContent = oldContent.substring(0, oldContent.length() -1);
                    mMoodContent.setText(oldContent + select);
                }else{
                    mMoodContent.setText(select);
                }
            }
        }

        if(requestCode == GET_LOCATION_CODE && data != null){//位置返回
            String location = data.getStringExtra("location");
            if(StringUtil.isNotNull(location)){
                double longitude = data.getDoubleExtra("longitude", 0);
                double latitude = data.getDoubleExtra("latitude", 0);
                locationBean = new LocationBean();
                locationBean.setName(location);
                locationBean.setLongitude(longitude);
                locationBean.setLatitude(latitude);
                mMoodLocationShow.setVisibility(View.VISIBLE);
                mMoodLocationShow.setText(location);
            }
        }
    }

    /**
     * 显示静态心情列表的GridView容器
     */
    class StaticMoodGridViewAdapter extends BaseAdapter {
        /**
         * 图像在本地存储的url地址
         */
        private List<String> links;
        private int mWidth;
        private int mHeight;

        public StaticMoodGridViewAdapter( List<String> links, int width, int height){
            this.links = links;
            this.mWidth = width;
            this.mHeight = height;
        }
        @Override
        public int getCount() {
            return links.size();
        }

        @Override
        public Object getItem(int position) {
            return links.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyHolder myHolder;
            if(convertView == null){
                myHolder = new MyHolder();
                convertView = LayoutInflater.from(MoodActivity.this).inflate(R.layout.item_mood_gridview, null);
                myHolder.setImageView((ImageView)convertView.findViewById(R.id.mood_item_img));
                convertView.setTag(myHolder);
            }else{
                myHolder = (MyHolder)convertView.getTag();
            }
            String i = links.get(position);
            ImageCacheManager.loadImage(links.get(position), myHolder.getImageView(), mWidth, mHeight);
            return convertView;
        }
        class MyHolder{
            private ImageView imageView;

            public ImageView getImageView() {
                return imageView;
            }

            public void setImageView(ImageView imageView) {
                this.imageView = imageView;
            }
        }
    }

}

