package com.leedane.cn.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.adapter.MoodGridViewAdapter;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.bean.LocationBean;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.MoodHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.service.SendMoodService;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.MediaUtil;
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
     * 展示显示的网络图片
     */
    private TextView mMoodNetworkShow;

    /**
     * 是否可以评论
     */
    private CheckBox mCanComment;

    /**
     * 是否可以转发
     */
    private CheckBox mCanTransmit;

    /**
     * 发表的按钮
     */
    private Button mBtnPublish;

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
     * 网络图片的链接，多个用";"分隔开
     */
    private List<String> mNetworkLinks = new ArrayList<>();

    /**
     * 展示心情的内容
     */
    private EditText mMoodContent;

    /**
     * 操作类型(0：发表，1:转发，2:评论)
     */
    private int mOperateType;

    //private BaseSQLiteDatabase sqLiteDatabase;

    /**
     * 用户选择的图片地址列表
     */
    private List<String> mLocalUris = new ArrayList<>();

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
                if(mOldMoodBean == null){
                    if(mOperateType == EnumUtil.MoodOperateType.转发.value)
                        ToastUtil.success(MoodActivity.this, "该心情无法转发");
                    else
                        ToastUtil.success(MoodActivity.this, "该心情无法评论");
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
        mMoodNetworkShow = (TextView)findViewById(R.id.mood_network_link_show);

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
            findViewById(R.id.mood_friend).setOnClickListener(this);

            //添加话题
            findViewById(R.id.mood_topic).setOnClickListener(this);

            //添加照片
            findViewById(R.id.mood_add).setOnClickListener(this);

            mMoodGridViewAdapter = new MoodGridViewAdapter(MoodActivity.this, mLocalUris);
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
        if(getIntent().getType() != null){
            if(getIntent().getType().startsWith("image/")){
                List<String> images = AppUtil.getListPicPaths(MoodActivity.this);
                if(images.size() > 0){
                    if(images.size() > 1){
                        ToastUtil.success(MoodActivity.this, "抱歉，目前系统只接受一张图片，已自动为您选择一张图片展示。");
                    }
                    mLocalUris.add(images.get(0));
                    mMoodGridViewAdapter.notifyDataSetChanged();
                }
            }else if(getIntent().getType().startsWith("text/")){
                String v =  getIntent().getStringExtra(Intent.EXTRA_TEXT);
                if(StringUtil.isNotNull(v)){
                    mMoodContent.setText(v);
                }
                Log.i(TAG, "文字信息是：" + v);
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
            case R.id.mood_topic: //添加话题
                showAddTopicDialog();
                break;
            case R.id.mood_add:
                showSelectItemMenuDialog();
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
                if(StringUtil.isNull(content) && (mLocalUris.size() == 0 || mNetworkLinks.size() ==0)){
                    if(mOperateType == EnumUtil.MoodOperateType.发表.value)
                        ToastUtil.failure(getBaseContext(), "内容不能为空或者至少选择一张图片");
                    else
                        ToastUtil.failure(getBaseContext(), "至少输入点什么吧");
                    mMoodContent.setFocusable(true);
                    return;
                }
                if(mLocalUris.size() > 0){
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
                                    for (String uri : mLocalUris) {
                                        buffer.append(uri);
                                        buffer.append(",");
                                    }
                                    if (mLocalUris.size() > 0) {
                                        uris = buffer.toString().substring(0, buffer.toString().length() - 1);
                                    }
                                    it_service.putExtra("uris", uris);
                                    getApplicationContext().startService(it_service);
                                    Intent intent = new Intent(MoodActivity.this, PersonalActivity.class);
                                    intent.putExtra("type", mOperateType);
                                    intent.putExtra("isService", true);
                                    setResult(PersonalActivity.MOOD_UPDATE_REQUEST_CODE, intent);
                                    finish();
                                }
                            });
                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ToastUtil.success(MoodActivity.this, "取消发表...");
                                }
                            });
                    builder.show();
                }else{
                    simplePublish(content);
                }
                break;
        }
    }

    /**
     * 弹出添加话题的对话框
     */
    private void showAddTopicDialog(){
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入话题").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("放弃", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String s = inputServer.getText().toString();
                if(StringUtil.isNotNull(s)){
                    if(mMoodContent.isCursorVisible()){
                        int index = mMoodContent.getSelectionStart();
                        //将字符串转换为StringBuffer
                        StringBuffer sb = new StringBuffer(mMoodContent.getText().toString().trim());
                        //将字符插入光标所在的位置
                        String tag = " #" + s + "# ";
                        sb = sb.insert(index, tag);
                        mMoodContent.setText(sb.toString());
                        mMoodContent.setSelection(index + tag.length());
                    }else{
                        mMoodContent.setText(mMoodContent.getText().toString() + " #" + s + "# ");
                    }
                }
            }
        });
        builder.show();

    }

    /**
     * 发送简单的
     * @param content
     */
    private void simplePublish(String content){
        //简单的发送
        HashMap<String, Object> params = new HashMap<>();
        params.put("content", content);
        params.put("can_comment", mCanComment.isChecked());
        params.put("can_transmit", mCanTransmit.isChecked());
        if(locationBean != null){
            params.put("location", locationBean.getName());
            params.put("longitude", locationBean.getLongitude());
            params.put("latitude", locationBean.getLatitude());
        }
        if(mNetworkLinks.size() > 0){
            params.put("links", mMoodNetworkShow.getText().toString());
            MoodHandler.sendWordAndLink(MoodActivity.this, params);
        }else {
            MoodHandler.sendWord(MoodActivity.this, params);
        }

        showLoadingDialog("发表心情", "正在发表，请稍等...");
    }

    /**
     * 将网络图片链接变成字符串
     * @return
     */
    private String getStringByNetworkLinksArray(){
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < mNetworkLinks.size(); i++){
            buffer.append(mNetworkLinks.get(i));
            if(i != mNetworkLinks.size()-1){
                buffer.append(";");
            }
        }
        return buffer.toString();
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
        if(mLocalUris != null && mLocalUris.size()> 0){
            for(String u: mLocalUris){
                uStr = uStr + u + ",";
            }
            if(uStr.endsWith(","))
                uStr = uStr.substring(0, uStr.length() -1);
        }
        try{
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
                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){
                    ToastUtil.success(MoodActivity.this, "您的心情" + getNameByType()+ "成功");
                    Intent intent = new Intent(MoodActivity.this, PersonalActivity.class);
                    intent.putExtra("type", mOperateType);
                    setResult(PersonalActivity.MOOD_UPDATE_REQUEST_CODE, intent);
                    finish();//关闭当前activity
                }else{
                    ToastUtil.failure(MoodActivity.this, "心情" + getNameByType()+ "失败" + ":" + jsonObject.optString("message"));
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
                mLocalUris.add(MediaUtil.getImageAbsolutePath(MoodActivity.this, data.getData()));
                mMoodGridViewAdapter.notifyDataSetChanged();
                //Toast.makeText(getBaseContext(), "获取的图片路径是：" + MediaUtil.getImageAbsolutePath(MoodActivity.this, data.getData()), Toast.LENGTH_LONG).show();
            }
        }
        //更新选择
        if(requestCode == AtFriendActivity.SELECT_AT_FRIENDS_CODE && data != null){
            String select = data.getStringExtra("select");
            if(StringUtil.isNotNull(select)){
                String oldContent = mMoodContent.getText().toString();
                int start = mMoodContent.getSelectionStart();
                if(StringUtil.isNotNull(oldContent)){
                    boolean set = start == oldContent.length() - 1;
                    if(oldContent.endsWith("@"))
                        oldContent = oldContent.substring(0, oldContent.length() -1);

                    Editable editable = mMoodContent.getText();
                    char o = oldContent.charAt(start-1); //判断上一个是不是空格
                    if((oldContent.length() >= start) && o == ' '){
                        editable.insert(start, select);
                    }else{
                        editable.insert(start, " " + select);
                    }


                    //mMoodContent.setText(oldContent + select);

                    //重置光标位置为最末
                   /* if(set){
                        mMoodContent.setSelection(mMoodContent.getText().length());
                    }*/
                }else{
                    mMoodContent.setText(select);
                    //重置光标位置为最末
                    mMoodContent.setSelection(mMoodContent.getText().length());
                    /*if(mMoodContent.hasSelection()){
                        mMoodContent.setSelection(mMoodContent.getText().length());
                    }*/
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

    private Dialog mDialog;
    /**
     * 显示弹出自定义view
     */
    public void showSelectItemMenuDialog(){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissSelectItemMenuDialog();

        mDialog = new Dialog(MoodActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(MoodActivity.this).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();

        menus.add(getStringResource(R.string.select_gallery));
        menus.add(getStringResource(R.string.img_link));
        SimpleListAdapter adapter = new SimpleListAdapter(MoodActivity.this.getApplicationContext(), menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                //选择图库
                if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.select_gallery))){

                    if(mLocalUris.size() > 2){
                        ToastUtil.failure(MoodActivity.this, "最多允许选择3张图片");
                        return;
                    }

                    //将图片链接清空
                    mNetworkLinks.clear();
                    mMoodNetworkShow.setText("");
                    mMoodNetworkShow.setVisibility(View.GONE);

                    //调用系统图库
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra("crop", true);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, GET_SYSTEM_IMAGE_CODE);
                    //选择链接
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.img_link))){
                    if(mNetworkLinks.size() > 2){
                        ToastUtil.failure(MoodActivity.this, "最多允许选择3张网络图片");
                        return;
                    }
                    final EditText inputServer = new EditText(MoodActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MoodActivity.this);
                    builder.setTitle("请输入网络图片(大小最好不要超过500k)").setIcon(R.drawable.ic_http_red_200_18dp).setView(inputServer)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            String text = inputServer.getText().toString();
                            if (StringUtil.isNotNull(text)) {
                                if(StringUtil.isLink(text)){
                                    mNetworkLinks.add(text);
                                    buildNetworkLinksShow();
                                }else{
                                    ToastUtil.failure(MoodActivity.this, MoodActivity.this.getString(R.string.is_not_a_link));
                                }
                            }else{
                                ToastUtil.failure(MoodActivity.this, "请输入网络图片链接!");
                            }
                        }
                    });
                    builder.show();
                }
                dismissSelectItemMenuDialog();
            }
        });
        mDialog.setTitle("选择");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissSelectItemMenuDialog();
            }
        });
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(800,(menus.size() +1) * 90 +20);
        mDialog.setContentView(view);
        mDialog.show();
    }

    /**
     * 显示网络图片链接
     */
    private void buildNetworkLinksShow(){
        //将本地图片清空
        mLocalUris.clear();
        mMoodGridViewAdapter.notifyDataSetChanged();
        mMoodNetworkShow.setText(getStringByNetworkLinksArray());
        mMoodNetworkShow.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissSelectItemMenuDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
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

