package com.leedane.cn.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.customview.GalleryScrollView;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.GalleryHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.FileUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.QiniuUploadManager;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 图库主Activity
 * Created by LeeDane on 2016/1/14.
 */
public class GalleryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private GalleryScrollView scrollView;

    /**
     * 弹出加载ProgressDiaLog
     */
    private ProgressDialog mProgressDialog;
    /**
     * 下拉刷新的对象
     */
    public SwipeRefreshLayout mySwipeRefreshLayout;

    private ImageView mRightImg;

    //上传单张图片成功
    private static final int UPLOAD_IMAGE_SUCCESS = 121;

    //上传单张图片失败
    private static final int UPLOAD_IMAGE_FAILURE = 122;

    public static final int LOAD_NETWORK_IMG_CODE = 33;

    //从服务器获取上传文件的凭证
    private String token;
    boolean isCancel = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(GalleryActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.GalleryActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }
        setContentView(R.layout.activity_gallery);

        mySwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.id_swipe_scrollview);
        mySwipeRefreshLayout.setOnRefreshListener(this);
        mySwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        scrollView = (GalleryScrollView)findViewById(R.id.gallery_scroll_view);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.gallery);
        backLayoutVisible();

        //显示标题栏的选择文件的图片按钮
        mRightImg = (ImageView)findViewById(R.id.view_right_img);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setImageResource(R.mipmap.add);
        mRightImg.setOnClickListener(this);
    }

    private View addGalleryView = null;
    private Dialog mDialog = null;
    private EditText galleryNetworkLink = null;
    private EditText galleryWidth = null;
    private EditText galleryHeight = null;
    private EditText galleryLenght = null;
    private TextView gallerySubmit = null;
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_img:
                mDialog = new Dialog(GalleryActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
                addGalleryView = LayoutInflater.from(GalleryActivity.this).inflate(R.layout.gallery_add, null);
                galleryNetworkLink = (EditText)addGalleryView.findViewById(R.id.gallery_add_network_link);
                galleryWidth = (EditText)addGalleryView.findViewById(R.id.gallery_add_width);
                galleryHeight = (EditText)addGalleryView.findViewById(R.id.gallery_add_height);
                galleryLenght = (EditText)addGalleryView.findViewById(R.id.gallery_add_length);
                gallerySubmit = (TextView)addGalleryView.findViewById(R.id.gallery_add_submit);
                gallerySubmit.setOnClickListener(this);

                mDialog.setTitle(getResources().getString(R.string.gallery_add_tip));
                mDialog.setCancelable(true);
                mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dismissPopUserInfoDialog();
                    }
                });
                //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(BaseApplication.newInstance().getScreenWidthAndHeight()[1]-100, 800);
                mDialog.setContentView(addGalleryView);
                mDialog.show();
                break;
            case R.id.gallery_add_submit:
                if(galleryNetworkLink ==null || StringUtil.isNull(galleryNetworkLink.getText().toString())){
                    Toast.makeText(GalleryActivity.this, "添加到图库的网络图片链接不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                CommonHandler.getQiniuTokenRequest(this);
                showLoadingDialog("Gallery", "Loading, try my best to adding gallery...");
                break;
        }
    }


    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);

        try{
            JSONObject jsonObject = new JSONObject(StringUtil.changeNotNull(result));
            if(type == TaskType.ADD_GALLERY){
                dismissLoadingDialog();
                if(jsonObject != null && jsonObject.has("isSuccess")){
                    //隐藏掉添加的弹出框
                    if(mDialog != null && mDialog.isShowing()){
                        mDialog.dismiss();
                    }
                    if(jsonObject.getBoolean("isSuccess"))
                        addGalleryShowAlertDialog();
                    else
                        ToastUtil.failure(this, JsonUtil.getErrorMessage(result));
                }else{
                    ToastUtil.failure(GalleryActivity.this, "添加入图库失败", Toast.LENGTH_LONG);
                }
            }else if (type == TaskType.QINIU_TOKEN && !isCancel) {//获取凭证
                if (jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true) {
                    token = jsonObject.getString("message");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, Object> map = FileUtil.saveNetWorkLinkToFile(GalleryActivity.this, galleryNetworkLink.getText().toString());
                            Message message = new Message();
                            message.what = LOAD_NETWORK_IMG_CODE;
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isSuccess", StringUtil.changeObjectToBoolean(map.get("isSuccess")));
                            bundle.putString("message", StringUtil.changeNotNull(map.get("message")));
                            bundle.putString("path", StringUtil.changeNotNull(map.get("path")));
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }).start();
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 添加图库成功后弹出的是否刷新的对话框
     */
    public void addGalleryShowAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("是否重新刷新当前的图库?")
                .setPositiveButton("刷新", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        mySwipeRefreshLayout.setRefreshing(true);
                        scrollView.loadMoreImage("firstloading", GalleryActivity.this);
                    }
                })
                .setNegativeButton("不需要",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissPopUserInfoDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    public void onRefresh() {
        //mySwipeRefreshLayout.setRefreshing(false);
        //showLoadingDialog("Gallery", "please wait, loading...");
        scrollView.loadMoreImage("firstloading", this);
    }

    @Override
    protected void onDestroy() {
        isCancel = true;
        super.onDestroy();;
        scrollView.destroy();
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOAD_IMAGE_SUCCESS: //成功
                    String path = msg.getData().getString("path");
                    isCancel = false;
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("path", path);
                    if(galleryWidth != null && StringUtil.changeObjectToInt(galleryWidth.getText().toString()) > 0 ){
                        params.put("width", StringUtil.changeObjectToInt(galleryWidth.getText().toString()));
                    }

                    if(galleryHeight != null && StringUtil.changeObjectToInt(galleryHeight.getText().toString()) > 0 ){
                        params.put("height", StringUtil.changeObjectToInt(galleryHeight.getText().toString()));
                    }

                    if(galleryLenght != null && StringUtil.changeObjectToLong(galleryLenght.getText().toString()) > 0 ){
                        params.put("lenght", StringUtil.changeObjectToLong(galleryLenght.getText().toString()));
                    }
                    params.put("desc", "app图库上加入");
                    GalleryHandler.add(GalleryActivity.this, params);
                    break;
                case UPLOAD_IMAGE_FAILURE: //失败
                    ToastUtil.failure(GalleryActivity.this, "图片上传失败");
                    break;
                case LOAD_NETWORK_IMG_CODE:
                    if(msg.getData().getBoolean("isSuccess")) {
                        //隐藏掉添加的弹出框
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        uploadImg(new File(StringUtil.changeNotNull(msg.getData().getString("path"))));
                    }else{
                        ToastUtil.failure(GalleryActivity.this, "网络图片临时下载出错，原因是：" + StringUtil.changeNotNull(msg.getData().getString("message")));
                        dismissLoadingDialog();
                    }
                    break;
            }
        }
    };
    /**
     * 上传文件
     * @param file 文件的本地
     */
    public void uploadImg(File file){
        if(StringUtil.isNull(token)){
            ToastUtil.failure(this, "未获取到上传图片的凭证");
            return;
        }
        try {
            //上传到七牛服务器的文件名称
            final String serverfilename = BaseApplication.getLoginUserName() + "_app_upload_" + UUID.randomUUID().toString() + file.getName();
            QiniuUploadManager.getInstance().getUploadManager().put(file, serverfilename, token,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject res) {
                            Message msg = new Message();
                            msg.what = UPLOAD_IMAGE_SUCCESS;
                            Bundle bundle = new Bundle();
                            bundle.putString("path", ConstantsUtil.QINIU_CLOUD_SERVER + serverfilename);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }, null);
        }catch (Exception e){
            Message msg = new Message();
            msg.what = UPLOAD_IMAGE_FAILURE;
            handler.sendMessage(msg);
            e.printStackTrace();
        }
    }
}
