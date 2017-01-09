package com.leedane.cn.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.handler.UserHandler;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.uploadfile.PortUpload;
import com.leedane.cn.uploadfile.UploadItem;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.FileUtil;
import com.leedane.cn.util.MediaUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import butterknife.ButterKnife;

/**
 * 更新用户头像
 * Created by LeeDane on 2016/4/29.
 */
public class UpdateUserHeaderActivity extends BaseActivity {

    private Button selectGallery;
    private ImageView showImage;
    private TextView confirmUploadHeader;
    private List<UploadItem> mItems = new ArrayList<>();
    private String imagePath;
    private boolean isCancel = false;
    private int itemIndex = 0;
    private int mLoginUserId;
    private String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if (!checkedIsLogin()) {
            Intent it = new Intent(UpdateUserHeaderActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.UpdateUserHeaderActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        mLoginUserId = BaseApplication.getLoginUserId();

        setContentView(R.layout.activity_update_header);
        //ButterKnife.bind(this);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.update_header);
        backLayoutVisible();

        initView();
    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        selectGallery = (Button)findViewById(R.id.select_gallery);
        showImage = (ImageView)findViewById(R.id.show_image);
        confirmUploadHeader = (TextView)findViewById(R.id.confirm_upload_header);
        selectGallery.setOnClickListener(this);
        confirmUploadHeader.setOnClickListener(this);
        if(StringUtil.isNotNull(BaseApplication.getLoginUserPicPath())){
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    ImageCacheManager.loadImage(BaseApplication.getLoginUserPicPath(), showImage, 300, 300);
                }
            }, 500);
        }
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
            case R.id.confirm_upload_header: //上传头像
                upload();
                break;
        }
    }

    private void upload(){
        if(StringUtil.isNull(imagePath)){
            ToastUtil.failure(UpdateUserHeaderActivity.this, "请先选择图片");
            return;
        }

        Uri mUri = Uri.parse(imagePath);
        String p = mUri.getPath();
        File file = new File(imagePath);//源文件
        if(!file.isFile()){
            ToastUtil.failure(getApplicationContext(), "请选择文件", Toast.LENGTH_LONG);
            return;
        }
        if(!file.exists()){
            ToastUtil.failure(getApplicationContext(), "文件不存在:" + imagePath, Toast.LENGTH_LONG);
            return;
        }

        fileName = file.getName();
        String tempFilePath = getTempDir(getApplicationContext()) + File.separator+ fileName;
        ToastUtil.success(getApplicationContext(), "临时文件路径：" + tempFilePath);
        file = new File(tempFilePath);
        if (file.exists()) {
            file.delete();
        }
        Bitmap bitmap = BitmapUtil.getSmallBitmap(getApplicationContext(), imagePath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            String suffix = StringUtil.getSuffixs(tempFilePath);
            if(suffix.equalsIgnoreCase("png")){
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            }else{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            BitmapUtil.recycled(bitmap);
        }
        mItems.add(FileUtil.buildUploadItem(getApplicationContext(), tempFilePath, new Date(), 0, String.valueOf(mLoginUserId), ConstantsUtil.UPLOAD_UPDATE_HEADER_TABLE_NAME, 60000, 60000));
        showLoadingDialog("Upload", "is updating header...");
        uploadOneFile();
    }

    /**
     * 找到下一个符合上传条件的文件进行上传
     */
    private void uploadOneFile(){
        UploadItem item = mItems.get(0);
        if(item != null && item.getPortUploads() != null){
            for(int i=0; i< item.getPortUploads().size(); i++){
                if(item.getPortUploads().get(i).isFinish())//完成的不做处理
                    continue;
                if(isCancel)//取消状态就不发送
                    break;
                TaskLoader.getInstance().startTaskForResult(TaskType.UPLOAD_FILE, this, item.getPortUploads().get(i));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取临时文件的文件夹
     * @param context
     * @return
     */
    private File getTempDir(Context context){
        File sdDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdDir = Environment.getExternalStorageDirectory();
        }
        else{
            sdDir = context.getCacheDir();
        }
        File cacheDir = new File(sdDir, context.getResources().getString(R.string.app_dirsname) + File.separator+ getApplicationContext().getResources().getString(R.string.temp_filepath));
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            System.out.println("requestCode" + requestCode);
            if (requestCode == MoodActivity.GET_SYSTEM_IMAGE_CODE) {
                Bitmap bitmap = null;
                try{
                    //imagePath = AppUtil.getPathByUri(UpdateUserHeaderActivity.this, data.getData());
                    imagePath = MediaUtil.getImageAbsolutePath(UpdateUserHeaderActivity.this, data.getData());
                    if(StringUtil.isNotNull(imagePath)){
                        bitmap = BitmapUtil.getSmallBitmap(getApplicationContext(), imagePath, 300, 300);
                        showImage.setImageBitmap(bitmap);
                    }else
                        ToastUtil.failure(UpdateUserHeaderActivity.this, "获取不到图片路径");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);
        try{
            JSONObject jsonObject = new JSONObject(String.valueOf(result));
            if(type == TaskType.UPLOAD_FILE && !isCancel){
                String url = "";
                if(jsonObject.has("url"))
                    url = jsonObject.getString("url");

                UploadItem item = mItems.get(itemIndex);
                //返回成功
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    for(PortUpload portUpload: item.getPortUploads()){
                        if(!portUpload.isFinish() && portUpload.getUrl().equalsIgnoreCase(url) && !StringUtil.isNull(url) && !isCancel){
                            portUpload.setIsFinish(true);
                            item.setUploadSize(item.getUploadSize() + portUpload.getLength());
                            //已经完成
                            if(item.getSize() == item.getUploadSize()){
                                mItems.get(itemIndex).setStatus(EnumUtil.FileStatus.合并文件.value);
                                merge();
                            }
                            break;
                        }
                    }
                    //返回失败
                }else{
                    ToastUtil.failure(UpdateUserHeaderActivity.this, "文件上传过程中", Toast.LENGTH_SHORT);
                    dismissLoadingDialog();
                }

            }else if(type == TaskType.MERGE_PORT_FILE && !isCancel){//合并文件
                //合并完成
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    mItems.get(itemIndex).setStatus(EnumUtil.FileStatus.删除断点文件.value);
                    //删除断点文件
                    deletePortFile();
                }else{
                    ToastUtil.failure(UpdateUserHeaderActivity.this, "合并文件过程中", Toast.LENGTH_SHORT);
                    dismissLoadingDialog();
                }
            }else if(type == TaskType.DELETE_PORT_FILE && !isCancel){//删除断点文件
                //合并完成
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    mItems.get(itemIndex).setStatus(EnumUtil.FileStatus.完成.value);
                    //获取用户最新头像的路径
                    UserHandler.getHeadPath(UpdateUserHeaderActivity.this);
                }else{
                    dismissLoadingDialog();
                    ToastUtil.failure(UpdateUserHeaderActivity.this, "删除断点文件过程中", Toast.LENGTH_SHORT);
                }
            }else if(type == TaskType.LOAD_HEAD_PATH){

                //获取用户新的头像
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    mItems.get(itemIndex).setStatus(EnumUtil.FileStatus.完成.value);
                    JSONObject json = SharedPreferenceUtil.getUserInfo(getApplicationContext());
                    if(json != null && jsonObject.has("message") && StringUtil.isNotNull(jsonObject.getString("message"))){
                        //替换文件的路径
                        json.put("user_pic_path", jsonObject.getString("message"));
                        SharedPreferenceUtil.saveUserInfo(getApplicationContext(), json.toString());
                    }
                    ToastUtil.failure(UpdateUserHeaderActivity.this, "更新头像完成", Toast.LENGTH_SHORT);
                    if(StringUtil.isNotNull(fileName)){
                        //删除临时文件
                        String tempFilePath = getTempDir(getApplicationContext()) + File.separator+ fileName;
                        File f = new File(tempFilePath);
                        if(f.exists()){
                            f.delete();
                        }
                    }
                }else{
                    ToastUtil.failure(UpdateUserHeaderActivity.this, "删除断点文件过程中", Toast.LENGTH_SHORT);
                }
                dismissLoadingDialog();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 执行合并操作
     */
    private void merge(){
        //启动合并操作
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setServerMethod("leedane/filepath/mergePortFile.action");
        requestBean.setResponseTimeOut(60000);
        requestBean.setRequestTimeOut(60000);
        Map<String, Object> params = new HashMap<>();
        params.put("fileName", mItems.get(itemIndex).getFileName());
        params.put("uuid",mItems.get(itemIndex).getUuid());
        params.put("tableName", ConstantsUtil.UPLOAD_UPDATE_HEADER_TABLE_NAME);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod("POST");
        requestBean.setRequestTimeOut(60000);
        requestBean.setResponseTimeOut(60000);
        TaskLoader.getInstance().startTaskForResult(TaskType.MERGE_PORT_FILE, this, requestBean);
    }

    /**
     * 删除断点文件
     */
    private void deletePortFile(){
        //启动合并操作
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setServerMethod("leedane/filepath/deletePortFile.action");
        requestBean.setResponseTimeOut(60000);
        requestBean.setRequestTimeOut(60000);
        Map<String, Object> params = new HashMap<>();
        params.put("uuid", mItems.get(itemIndex).getUuid());
        params.put("tableName", ConstantsUtil.UPLOAD_UPDATE_HEADER_TABLE_NAME);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestTimeOut(60000);
        requestBean.setResponseTimeOut(60000);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_PORT_FILE, this, requestBean);
    }
}
