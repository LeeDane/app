package com.leedane.cn.activity;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.MoodGridViewAdapter;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.leedaneAPP.R;
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
import java.util.Map;

/**
 * 处理心情的activity类
 * Created by Leedane on 2015/12/3.
 */
public class MoodActivity extends BaseActivity {

    public static final String TAG = "MoodActivity";
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

    /**
     * 调用系统图库的请求编码
     */
    public static final int GET_SYSTEM_IMAGE_CODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if(mOperateType == 1){
            mBtnPublish.setText(getStringResource(R.string.mood_transmit));
        }else if(mOperateType == 2){
            mBtnPublish.setText(getStringResource(R.string.mood_comment));
        }
        if(mOperateType == 0){
            //添加照片
            mMoodAdd = (ImageView)findViewById(R.id.mood_add);
            mMoodAdd.setVisibility(View.VISIBLE);
            mMoodAdd.setOnClickListener(this);
            mMoodGridViewAdapter = new MoodGridViewAdapter(MoodActivity.this, mUris);
            //显示添加后的图片
            mGridview = (GridView)findViewById(R.id.mood_gridview);
            mGridview.setAdapter(mMoodGridViewAdapter);
        }else{
            mOldMood = (TextView)findViewById(R.id.mood_show_old);
            mMoodTransmitCommentContent = (LinearLayout)findViewById(R.id.mood_transmit_comment_content);
            mMoodTransmitCommentContent.setVisibility(View.VISIBLE);
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
        //mGridview.setColumnWidth(getGridViewColumnWidth());
    }

    /**
     * 选择好友
     * @param view
     */
    public void selectFriend(View view){
        startATFriendActivity();
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
                        Toast.makeText(getBaseContext(), "内容不能为空或者至少选择一张图片", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getBaseContext(), "至少输入点什么吧", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(MoodActivity.this, "正在发表...", Toast.LENGTH_LONG).show();
                                }
                            });
                    builder.show();
                }else{
                    //简单的发送
                    HttpRequestBean requestBean = new HttpRequestBean();
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("content", content);
                    params.putAll(BaseApplication.newInstance().getBaseRequestParams());
                    requestBean.setParams(params);
                    requestBean.setServerMethod("leedane/mood_sendWord.action");
                    requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
                    TaskLoader.getInstance().startTaskForResult(TaskType.SEND_MOOD_NORMAL, MoodActivity.this, requestBean);
                    showLoadingDialog("发表心情", "正在发表，请稍等...");
                }


                //finish();

                /*HttpRequestBean requestBean = new HttpRequestBean();
                HashMap<String, Object> params = new HashMap<>();
                params.put("content", content);
                params.put("froms", Build.MODEL);
                //Toast.makeText(MoodActivity.this, "手机型号："+ Build.MODEL+",手机厂家："+Build.MANUFACTURER, Toast.LENGTH_LONG).show();
                if(mUris.size() > 0){
                    StringBuffer buffer = new StringBuffer();

                    try {
                        for( Uri uri: mUris){
                            ContentResolver cr = getContentResolver();

                                Bitmap bmp = BitmapFactory.decodeStream(cr.openInputStream(uri));
                                if (bmp != null) {
                                    String base64 = ImageUtil.bitmapToBase64(bmp);
                                    if(!StringUtil.isNull(base64)){
                                        buffer.append(base64 + "&&");
                                    }
                                }

                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    String str = buffer.toString();
                    if(!StringUtil.isNull(str) && str.endsWith("&&")){
                        str = str.substring(0, str.length() -2);
                    }
                    params.put("base64", str);
                }
                //每一张图片增加30秒请求超时限制
                requestBean.setRequestTimeOut(ConstantsUtil.DEFAULT_REQUEST_TIME_OUT + 30000 * mUris.size());

                //每一张图片增加30秒响应超时限制
                requestBean.setResponseTimeOut(ConstantsUtil.DEFAULT_RESPONSE_TIME_OUT + 30000*mUris.size());
                params.putAll(getBaseRequestParams());
                requestBean.setParams(params);
                requestBean.setServerMethod("leedane/mood_sendDraft.action");
                showLoadingDialog("发表心情", "正在处理上传草稿。。。");
                TaskLoader.getInstance().startTaskForResult(TaskType.SEND_MOOD_DRAFT, this, requestBean);
*/
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
            //发送草稿
            /*if(type == TaskType.SEND_MOOD_DRAFT){
                dismissLoadingDialog();
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){

                    int mid = jsonObject.getInt("message");
                    HttpRequestBean requestBean = new HttpRequestBean();
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("mid", mid);
                    params.putAll(getBaseRequestParams());
                    requestBean.setParams(params);
                    requestBean.setServerMethod("leedane/mood_send.action");

                    showLoadingDialog("发表心情", "正在更新心情状态。。。");
                    //更新心情状态
                    TaskLoader.getInstance().startTaskForResult(TaskType.SEND_MOOD_NORMAL, this, requestBean);
                }else {
                    Toast.makeText(MoodActivity.this, "服务器连接失败，请稍后重试", Toast.LENGTH_LONG).show();
                }
                return;
            }*/
            dismissLoadingDialog();
            //发表心情
            if(type == TaskType.SEND_MOOD_NORMAL){
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    new NotificationUtil(1, MoodActivity.this).sendTipNotification("信息提示", "您的发表的心情发送成功", "测试", 1, 0);
                    finish();//关闭当前activity
                }else{
                    new NotificationUtil(1, MoodActivity.this).sendActionNotification("信息提示", "您的发表的心情发送失败，点击重试", "测试", 1, 0);
                }
            }else if(type == TaskType.ADD_COMMENT){

                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    new NotificationUtil(1, MoodActivity.this).sendTipNotification("信息提示", "您的评论发表成功", "测试", 1, 0);
                    Intent intent = new Intent();
                    setResult(PersonalActivity.MOOD_COMMENT_REQUEST_CODE, intent);
                    finish();//关闭当前activity
                }else{
                    new NotificationUtil(1, MoodActivity.this).sendActionNotification("信息提示", "您的评论发表失败，点击重试", "测试", 1, 0);
                }
            }else if(type == TaskType.ADD_TRANSMIT){

                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                new NotificationUtil(1, MoodActivity.this).sendTipNotification("信息提示", "转发成功", "测试", 1, 0);
                finish();//关闭当前activity
            }else{
                new NotificationUtil(1, MoodActivity.this).sendActionNotification("信息提示", "转发失败，点击重试", "测试", 1, 0);
            }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            System.out.println("requestCode"+requestCode);
            if (requestCode == GET_SYSTEM_IMAGE_CODE) {
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

