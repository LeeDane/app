package com.leedane.cn.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.handler.FileHandler;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.uploadfile.PortUpload;
import com.leedane.cn.uploadfile.UploadItem;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.FileUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上传文件activity
 * Created by LeeDane on 2016/1/29.
 */
public class UploadFileActivity extends BaseActivity {
    private static final String TAG = "UploadFileActivity";
    public static final int GET_SYSTEM_FILE_CODE = 88;
    private List<UploadItem> mItems;
    //private List<PortUpload> portUploads;
    //private UploadItem mItem;
    private boolean isStop;//控制是否暂停
    boolean isItemFinish = false;//判断单个文件下载任务是否完成
    private int itemIndex;

    /**
     * 装载下载列表的容器
     */
    private LinearLayout mLinearLayout;

    /**
     * 全部开始或者全部结束
     */
    private Button mAllStart,mAllStop;
    private Intent currentIntent;
    private JSONObject mUserInfo;
    /**
     * 登录的账号
     */
    private String mLoginAccountName;

    /**
     * 登录的账号id
     */
    private int mLoginAccountId;
    /**
     * 触发选择文件的的imageview
     */
    private ImageView mRightImg;

    //标记是否上传的是APP版本控制
    private boolean isAppVersion;
    private LinearLayout mHidden_linearLayout;
    private EditText mETVersionNumber;//版本编号
    private EditText mETVersionDesc;//版本描述
    private String tableName = ConstantsUtil.UPLOAD_FILE_TABLE_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentIntent = getIntent();
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(UploadFileActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.UploadFileActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        initData();
        setContentView(R.layout.activity_upload_file);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.system_upload);
        backLayoutVisible();
        initView();

        //显示标题栏的选择文件的图片按钮
        mRightImg = (ImageView)findViewById(R.id.view_right_img);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setImageResource(R.mipmap.add);
        mRightImg.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mUserInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());

        try {
            mLoginAccountId = mUserInfo.getInt("id");
            mLoginAccountName = mUserInfo.getString("account");
        }catch (Exception e){
            Log.i(TAG, "获取缓存的用户名称为空");
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mItems = new ArrayList<>();
        //portUploads = new ArrayList<>();
        mLinearLayout = (LinearLayout)findViewById(R.id.upload_file_box);
        mAllStart = (Button)findViewById(R.id.upload_all_start);
        mAllStop = (Button)findViewById(R.id.upload_all_stop);
        mAllStart.setOnClickListener(this);
        mAllStop.setOnClickListener(this);
        mHidden_linearLayout = (LinearLayout)findViewById(R.id.hidden_linearLayout);
        try{
            if(mUserInfo.has("is_admin") && mUserInfo.getBoolean("is_admin")){
                mHidden_linearLayout.setVisibility(View.VISIBLE);
                mETVersionNumber = (EditText)findViewById(R.id.app_version_number);
                mETVersionDesc = (EditText)findViewById(R.id.app_version_desc);

                RadioGroup radioGroup = (RadioGroup)findViewById(R.id.upload_file_group);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId){
                            case R.id.upload_file_app_version:
                                isAppVersion = true;
                                mETVersionNumber.setVisibility(View.VISIBLE);
                                mETVersionDesc.setVisibility(View.VISIBLE);
                                tableName = ConstantsUtil.UPLOAD_APP_VERSION_TABLE_NAME;
                                break;
                            case R.id.upload_file_normal:
                                isAppVersion = false;
                                tableName = ConstantsUtil.UPLOAD_FILE_TABLE_NAME;
                                mETVersionNumber.setVisibility(View.GONE);
                                mETVersionDesc.setVisibility(View.GONE);
                                break;
                        }
                    }
                });
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 记录每次返回的大小
     */
    //private long preSize;
    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);
        try{
            if(type == TaskType.UPLOAD_FILE && !isStop){
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                String url = "";
                if(jsonObject.has("url"))
                    url = jsonObject.getString("url");

                UploadItem item = mItems.get(itemIndex);
                //返回成功
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    for(PortUpload portUpload: item.getPortUploads()){
                        if(!portUpload.isFinish() && portUpload.getUrl().equalsIgnoreCase(url) && !StringUtil.isNull(url) && !isStop){
                            Log.i(TAG, portUpload.getUrl()+"上传完成");
                            portUpload.setIsFinish(true);
                            item.setUploadSize(item.getUploadSize() + portUpload.getLength());
                            TextView desc = (TextView)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.upload_desc);
                            ProgressBar progressBar = (ProgressBar)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.upload_progress);
                            TextView per = (TextView)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.download_per);
                            //已经完成
                            if(item.getSize() == item.getUploadSize()){
                                per.setText("99%");
                                progressBar.setProgress(99);
                                mItems.get(itemIndex).setStatus(EnumUtil.FileStatus.合并文件.value);
                                merge();
                            }else{
                                desc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.正在执行.value));
                                int progress = (int)(item.getUploadSize()* 100 /item.getSize() );
                                per.setText(progress +"%");
                                progressBar.setProgress(progress);
                            }
                            break;
                        }
                    }

                    if(isItemFinish){
                        isItemFinish = false;
                        uploadOneFile();
                    }
                    //返回失败
                }else{
                    TextView desc = (TextView)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.upload_desc);
                    desc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.操作失败.value));
                }

            }else if(type == TaskType.MERGE_PORT_FILE && !isStop){//合并文件
                JSONObject jsonObject = new JSONObject(String.valueOf(result));

                //合并完成
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    mItems.get(itemIndex).setStatus(EnumUtil.FileStatus.删除断点文件.value);
                    //删除断点文件
                    deletePortFile();
                }else{
                    TextView desc = (TextView)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.upload_desc);
                    desc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.操作失败.value));
                }
            }else if(type == TaskType.DELETE_PORT_FILE && !isStop){//删除断点文件
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                TextView desc = (TextView)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.upload_desc);
                //合并完成
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    mItems.get(itemIndex).setStatus(EnumUtil.FileStatus.完成.value);
                    isItemFinish = true;
                    ProgressBar progressBar = (ProgressBar)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.upload_progress);
                    TextView per = (TextView)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.download_per);
                    per.setText("100%");
                    progressBar.setProgress(100);
                    Toast.makeText(UploadFileActivity.this, "上传完成啦,即将开始下一个", Toast.LENGTH_SHORT).show();
                    desc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.完成.value));
                    uploadOneFile();
                }else{
                    desc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.操作失败.value));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }


    }

    /**
     * 执行合并操作
     */
    private void merge(){
        TextView desc = (TextView)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.upload_desc);
        desc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.合并文件.value));
        //启动合并操作
        Map<String, Object> params = new HashMap<>();
        params.put("fileName", mItems.get(itemIndex).getFileName());
        params.put("uuid", mItems.get(itemIndex).getUuid());
        params.put("tableName", tableName);
        try{
            params.put("id", mUserInfo.getInt("id"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        if(isAppVersion){
            params.put("file_version", mETVersionNumber.getText().toString());
            params.put("file_desc", mETVersionDesc.getText().toString());
        }
        FileHandler.mergePortFile(this, params);
    }

    /**
     * 删除断点文件
     */
    private void deletePortFile(){
        TextView desc = (TextView)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.upload_desc);
        desc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.删除断点文件.value));
        //启动合并操作
        Map<String, Object> params = new HashMap<>();
        params.put("uuid", mItems.get(itemIndex).getUuid());
        params.put("tableName", tableName);
        FileHandler.deletePortFile(this, params);
    }

    /**
     * 找到下一个符合上传条件的文件进行上传
     */
    private void uploadOneFile(){
        UploadItem item = null;
        //portUploads.clear();
        itemIndex = 0;
        if(mItems.size()>0){
            boolean isOne = true;
            TextView desc;
            for(int i = 0; i < mItems.size() ;i++){
                if(mItems.get(i).getStatus() != EnumUtil.FileStatus.完成.value && mItems.get(i).getStatus() != EnumUtil.FileStatus.文件不存在.value){//未完成并且文件存在
                    desc = (TextView)mLinearLayout.getChildAt(i).findViewById(R.id.upload_desc);
                    if(isOne){
                        itemIndex = i;
                        item = mItems.get(i);
                        desc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.正在执行.value));
                        isOne = false;
                    }else{
                        desc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.等待中.value));
                    }
                }
            }

            if(item != null){

                String localPath = item.getLocalPath();
                File file = new File(localPath);
                if(!file.exists()){
                    Toast.makeText(UploadFileActivity.this, "文件不存在："+localPath, Toast.LENGTH_LONG).show();
                    desc = (TextView)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.upload_desc);
                    desc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.文件不存在.value));
                    mItems.get(itemIndex).setStatus(EnumUtil.FileStatus.文件不存在.value);
                    uploadOneFile();
                    return;
                }
                List<PortUpload>  portUploads = item.getPortUploads();
                for(int i=0; i< portUploads.size(); i++){
                    if(portUploads.get(i).isFinish())//完成的不做处理
                        continue;

                    if(isStop)//暂停状态就不发送
                        break;
                    /*if(i % 8 == 0){//每执行8个休眠300毫秒
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }*/
                    TaskLoader.getInstance().startTaskForResult(TaskType.UPLOAD_FILE, this, portUploads.get(i));
                    //AppUploadAndDownloadUtil.portUpload(file, urlBuffer.toString(), portUploads.get(i).getFrom(), (int)portUploads.get(i).getLength());
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_img:
                if(mItems.size() == 5){
                    Toast.makeText(UploadFileActivity.this, "已经达到一次性上传5个文件的上限，请先上传或者删除", Toast.LENGTH_LONG).show();
                    break;
                }
                //调用系统文件库
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                intent.putExtra("crop", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, GET_SYSTEM_FILE_CODE);
                break;
            case R.id.upload_all_start://全部开始
                isStop = false;
                if(mItems.size() > 0){
                    mAllStart.setEnabled(false);
                    mAllStop.setEnabled(true);
                    if(isAppVersion){
                        String number = mETVersionNumber.getText().toString();
                        if(StringUtil.isNull(number)){
                            Toast.makeText(UploadFileActivity.this, "版本号不能为空", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String versionDesc = mETVersionDesc.getText().toString();
                        if(StringUtil.isNull(versionDesc)){
                            Toast.makeText(UploadFileActivity.this, "版本描述不能为空", Toast.LENGTH_LONG).show();
                            return;
                        }

                    }

                    mHidden_linearLayout.setVisibility(View.GONE);
                    uploadOneFile();
                }
                break;
            case R.id.upload_all_stop: //全部暂停
                    taskCanceled(TaskType.UPLOAD_FILE);
                    isStop=true;
                    if(mItems.size() > 0){
                        TextView desc;
                        for (int i=0; i< mItems.size(); i++){
                            if(mItems.get(i).getStatus() != EnumUtil.FileStatus.完成.value && mItems.get(i).getStatus() != EnumUtil.FileStatus.文件不存在.value){//未完成并且文件存在
                                desc = (TextView)mLinearLayout.getChildAt(itemIndex).findViewById(R.id.upload_desc);
                                desc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.暂停.value));
                            }
                        }
                    }
                    mAllStop.setEnabled(false);
                    mAllStart.setEnabled(true);
                    //uploadOneFile();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GET_SYSTEM_FILE_CODE) {
                Uri uri = data.getData();
                Date createTime = new Date();
                mItems.add(FileUtil.buildUploadItem(UploadFileActivity.this, uri, createTime, tableName));
                File file = new File(uri.getPath());
                View view = LayoutInflater.from(UploadFileActivity.this).inflate(R.layout.item_upload_file, null);
                mLinearLayout.addView(view);
                mLinearLayout.postInvalidateOnAnimation();
                TextView uploadFilename = (TextView)view.findViewById(R.id.upload_filename);
                TextView uploadDesc = (TextView)view.findViewById(R.id.upload_desc);
                TextView uploadCreateTime = (TextView)view.findViewById(R.id.upload_create_time);
                uploadFilename.setText(file.getName());
                uploadCreateTime.setText(RelativeDateFormat.format(createTime));
                uploadDesc.setText(EnumUtil.getFileStatusValue(EnumUtil.FileStatus.准备就绪.value));

                mAllStart.setEnabled(true);
            }
        }

    }
}
