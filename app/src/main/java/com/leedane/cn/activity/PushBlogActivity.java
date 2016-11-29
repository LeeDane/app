package com.leedane.cn.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.handler.BlogHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.richtext.RichTextEditText;
import com.leedane.cn.richtext.ToolFragment;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DensityUtil;
import com.leedane.cn.util.FileUtil;
import com.leedane.cn.util.MediaUtil;
import com.leedane.cn.util.QiniuUploadManager;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 发布博客的activity类
 * Created by Leedane on 2016/10/10.
 */
public class PushBlogActivity extends BaseActivity {

    public static final String TAG = "PushBlogActivity";
    private EditText mTitle;
    private TextView mImageProgressTip;
    public RichTextEditText richTextContent;
    public int preSelectToolId;  //当前选择的工具栏的标签的ID
    private boolean addToList = true; //判断是否添加到List中，对于撤销状态，不添加进去

    //保存全部的操作列表
    public List<OperateObject> operateList = new ArrayList<>();

    /**
     * 发送的imageview
     */
    private ImageView mRightSend;

    /**
     * 预览的Button
     */
    private Button mRightPreView;

    //标记当前是否插入图片中
    public boolean isInsertImage;
    private String mLocalImagePath;
    //标记图片插入的开始位置
    private int imgInertStart;
    //标记图片插入的前页面展示的文字信息
    //private String imgBeforeInert;

    public String token;

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
        mImageProgressTip = (TextView)findViewById(R.id.rich_text_tip);
        richTextContent = (RichTextEditText)findViewById(R.id.rich_text_content);
        //文字变化的监听
        richTextContent.addTextChangedListener(textWatcher);

        ToolFragment toolFragment = ToolFragment.newInstance(null);
        getSupportFragmentManager().beginTransaction().replace(R.id.rich_text_tool, toolFragment).commit();

        //显示标题栏的发送图片按钮
        mRightSend = (ImageView)findViewById(R.id.view_right_img);
        mRightSend.setImageResource(R.drawable.send);
        mRightSend.setVisibility(View.VISIBLE);
        mRightSend.setOnClickListener(this);

        //显示标题栏的预览按钮
        mRightPreView = (Button)findViewById(R.id.view_right_button);
        mRightPreView.setText(getString(R.string.preview));
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
                params.put("has_img", true);
                params.put("title", mTitle.getText().toString());
                params.put("content", richTextContent.getText().toString());
                params.put("status", ConstantsUtil.STATUS_NORMAL);
                params.put("has_digest", true);//自动获取摘要
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
            }else if(type == TaskType.QINIU_TOKEN){
                dismissLoadingDialog();
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    token = jsonObject.getString("message");
                    uploadImg();
                }else{
                    ToastUtil.failure(PushBlogActivity.this, jsonObject);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void uploadImg(){
        File data = new File(mLocalImagePath);
        String filename = BaseApplication.getLoginUserName() + "_app_upload_" + UUID.randomUUID().toString() +StringUtil.getFileName(mLocalImagePath);
        QiniuUploadManager.getInstance().getUploadManager().put(data, filename, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置。
                        //Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                        //ToastUtil.success(IncomeOrSpendActivity.this, "qiniu progress--->" + key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, new UploadOptions(null, null, false, new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        int i = (int) (percent * 100);
                        mImageProgressTip.setText("正在上传图片，当前进度是"+i+"%");
                        if (i == 100) {
                            mImageProgressTip.setVisibility(View.GONE);
                            StringBuffer buffer = new StringBuffer(richTextContent.getText().toString());

                            float device_width_dp = DensityUtil.px2dip(PushBlogActivity.this, BaseApplication.newInstance().getScreenWidthAndHeight()[0]) -20;
                            buffer.insert(imgInertStart,
                                    "<p><img width=\""+device_width_dp+"\" src=\"" +ConstantsUtil.QINIU_CLOUD_SERVER + key+"\"></p>");
                            add = true;
                            richTextContent.setText(buffer.toString());
                            richTextContent.setSelection(buffer.length());
                            isInsertImage = false;
                            mLocalImagePath = null;
                            imgInertStart = -1;
                        }
                        // ToastUtil.success(IncomeOrSpendActivity.this, "qiniu progress--->" + percent);
                        Log.i("qiniu progress", "i=" + i + "---->" + key + ": " + percent);
                    }
                }, null));
    }

    /**
     * 获取系统的图库的图片文件
     */
    public void getSystemImage(){
        //调用系统图库
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra("crop", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, MoodActivity.GET_SYSTEM_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            System.out.println("requestCode"+requestCode);
            if (requestCode == MoodActivity.GET_SYSTEM_IMAGE_CODE) {//图库返回

                mLocalImagePath = MediaUtil.getImageAbsolutePath(PushBlogActivity.this, data.getData());
                Bitmap bitmap = null;
                if(StringUtil.isNotNull(mLocalImagePath)){
                    //缩放图片的大小最大为600x800，压缩质量为60
                    bitmap = BitmapUtil.getSmallBitmap(PushBlogActivity.this, mLocalImagePath, 600, 800, 60);
                    CommonHandler.getQiniuTokenRequest(PushBlogActivity.this);

                    mLocalImagePath = FileUtil.getTempDir(getApplicationContext()) + File.separator + StringUtil.getFileName(mLocalImagePath);
                    BitmapUtil.bitmapToLocalPath(bitmap, mLocalImagePath);

                    if(!bitmap.isRecycled()){
                        bitmap.recycle();  //回收图片所占的内存
                        System.gc();  //提醒系统及时回收
                    }
                    isInsertImage = true;
                    imgInertStart = richTextContent.getSelectionStart();
                    add = false; //控制这次操作不触发EditText
                    mImageProgressTip.setVisibility(View.VISIBLE);
                    mImageProgressTip.setText("正在上传图片，当前进度是0%");
                }else
                    ToastUtil.failure(PushBlogActivity.this, "获取不到图片路径");
            }
        }
    }

    public boolean add;
    private TextWatcher textWatcher = new TextWatcher() {

        private CharSequence noChangeData;//未改变前的数据
        private int beforeCurrorPosition;
        @Override
        public void afterTextChanged(Editable s) {
            if(addToList)
                operateList.add(new OperateObject(richTextContent.getText().toString(), beforeCurrorPosition));
            else
                addToList = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            noChangeData = s;
            beforeCurrorPosition = richTextContent.getSelectionStart();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            StringBuffer buffer = new StringBuffer(s);
            String preString = buffer.substring(start, start +count);

            if(isInsertImage && start <= imgInertStart && !add){
               // buffer.replace(start, start + count,  bStart + preString +bEnd);
                add = true;
                richTextContent.setText(noChangeData.toString());//将数据改成未修改前的数据
                ToastUtil.failure(PushBlogActivity.this, "图片上传未完成，无法对图片位置前面的数据进行增删改操作");
                return;
            }


            //非删除操作才会处理
            boolean isDelete = count == 0;
            //说明当前已经选择了一个tool
            if(preSelectToolId > 0 && !add && !isDelete){

                switch (preSelectToolId){
                    case R.id.rich_text_bold:
                        if(!StringUtil.foucsIsInTag(richTextContent.getText().toString(), richTextContent.getSelectionStart(), "<b>","</b>")){
                            String bStart = "<b>";
                            String bEnd = "</b>";
                            buffer.replace(start, start + count,  bStart + preString +bEnd);
                            add = true;
                            richTextContent.setText(buffer.toString());
                            richTextContent.setSelection(buffer.toString().length());
                        }
                        break;
                    case R.id.rich_text_italic:
                        if(!StringUtil.foucsIsInTag(richTextContent.getText().toString(), richTextContent.getSelectionStart(), "<i>","</i>")){
                            String bStart = "<i>";
                            String bEnd = "</i>";
                            buffer.replace(start, start + count, bStart + preString + bEnd);
                            add = true;
                            richTextContent.setText(buffer.toString());
                            richTextContent.setSelection(buffer.toString().length());
                        }
                        break;
                    case R.id.rich_text_underline:
                        if(!StringUtil.foucsIsInTag(richTextContent.getText().toString(), richTextContent.getSelectionStart(), "<u>","</u>")){
                            String bStart = "<u>";
                            String bEnd = "</u>";
                            buffer.replace(start, start + count, bStart + preString + bEnd);
                            add = true;
                            richTextContent.setText(buffer.toString());
                            richTextContent.setSelection(buffer.toString().length());
                        }
                        break;
                    case R.id.rich_text_h1:
                        if(!StringUtil.foucsIsInTag(richTextContent.getText().toString(), richTextContent.getSelectionStart(), "<h1>","</h1>")){
                            String bStart = "<h1>";
                            String bEnd = "</h1>";
                            buffer.replace(start, start + count, bStart + preString + bEnd);
                            add = true;
                            richTextContent.setText(buffer.toString());
                            richTextContent.setSelection(buffer.toString().length());
                        }
                        break;
                    case R.id.rich_text_h2:
                        if(!StringUtil.foucsIsInTag(richTextContent.getText().toString(), richTextContent.getSelectionStart(), "<h2>","</h2>")){
                            String bStart = "<h2>";
                            String bEnd = "</h2>";
                            buffer.replace(start, start + count, bStart + preString + bEnd);
                            add = true;
                            richTextContent.setText(buffer.toString());
                            richTextContent.setSelection(buffer.toString().length());
                        }
                        break;
                    case R.id.rich_text_h3:
                        if(!StringUtil.foucsIsInTag(richTextContent.getText().toString(), richTextContent.getSelectionStart(), "<h3>","</h3>")){
                            String bStart = "<h3>";
                            String bEnd = "</h3>";
                            buffer.replace(start, start + count, bStart + preString + bEnd);
                            add = true;
                            richTextContent.setText(buffer.toString());
                            richTextContent.setSelection(buffer.toString().length());
                        }
                        break;
                    case R.id.rich_text_h4:
                        if(!StringUtil.foucsIsInTag(richTextContent.getText().toString(), richTextContent.getSelectionStart(), "<h4>","</h4>")){
                            String bStart = "<h4>";
                            String bEnd = "</h4>";
                            buffer.replace(start, start + count, bStart + preString + bEnd);
                            add = true;
                            richTextContent.setText(buffer.toString());
                            richTextContent.setSelection(buffer.toString().length());
                        }
                        break;
                    case R.id.rich_text_h5:
                        if(!StringUtil.foucsIsInTag(richTextContent.getText().toString(), richTextContent.getSelectionStart(), "<h5>","</h5>")){
                            String bStart = "<h5>";
                            String bEnd = "</h5>";
                            buffer.replace(start, start + count, bStart + preString + bEnd);
                            add = true;
                            richTextContent.setText(buffer.toString());
                            richTextContent.setSelection(buffer.toString().length());
                        }
                        break;
                    case R.id.rich_text_h6:
                        if(!StringUtil.foucsIsInTag(richTextContent.getText().toString(), richTextContent.getSelectionStart(), "<h6>","</h6>")){
                            String bStart = "<h6>";
                            String bEnd = "</h6>";
                            buffer.replace(start, start + count,  bStart + preString +bEnd);
                            add = true;
                            richTextContent.setText(buffer.toString());
                            richTextContent.setSelection(buffer.toString().length());
                        }
                        break;

                }

            }else{
                add = false;
            }
        }
    };

    public synchronized void removeOperateData(){
        add = true;
        addToList = false;
        if(operateList.size() > 0){
            operateList.remove(operateList.size() - 1);
            if(operateList.size() > 0){
                richTextContent.setText(operateList.get(operateList.size() -1).text);
                richTextContent.setSelection(operateList.get(operateList.size() -1).cursorPosition);
            }else
                richTextContent.setText("");
        }else{
            richTextContent.setText("");
        }
    }

    class OperateObject{
        String text; //当前文本信息
        int cursorPosition;// 光标位置
        OperateObject(String text, int cursorPosition){
            this.text = text;
            this.cursorPosition = cursorPosition;
        }
    }
}

