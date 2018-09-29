package com.leedane.cn.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.leedane.cn.activity.MoodActivity;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.MoodHandler;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.FileUtil;
import com.leedane.cn.util.NotificationUtil;
import com.leedane.cn.util.QiniuUploadManager;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

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
import java.util.UUID;

/**
 * 发送心情的service(支持发送多图)
 * Created by LeeDane on 2016/7/29.
 */
public class SendMoodService extends Service implements TaskListener {

    private boolean isCancel;//是否取消

    //上传到七牛服务器的名称，注意：没有包括七牛域名的前缀
    private List<String> mNetworkLinks = new ArrayList<>();
    private String content;
    private String uris;
    boolean isItemFinish = false;//判断单个文件下载任务是否完成
    private Intent currentIntent;

    //发送心情的通知ID
    private static final int SEND_MOOD_NOTIFICATION_ID = 106;

    //上传单张图片成功
    private static final int UPLOAD_IMAGE_SUCCESS = 121;

    //上传单张图片失败
    private static final int UPLOAD_IMAGE_FAILURE = 122;

    //从服务器获取上传文件的凭证
    private String token;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        isCancel = true;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        currentIntent = intent;
        //service被杀死后重启将没有intent，这里不做进一步处理即可
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        mNetworkLinks.clear();
        //获取上传凭证
        CommonHandler.getQiniuTokenRequest(SendMoodService.this);
        return super.onStartCommand(intent, flags, startId);
    }

    private int successNumber; //记录图片上传成功个数
    private int totalImg;
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOAD_IMAGE_SUCCESS: //成功
                    successNumber  = successNumber +1 ;
                    if(successNumber == totalImg){
                        new NotificationUtil(SEND_MOOD_NOTIFICATION_ID, SendMoodService.this).sendTipNotification("信息提示", "图片上传完成", "测试", 1, 0);
                        //简单的发送
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("content", content);
                        String location = currentIntent.getStringExtra("location");
                        if (StringUtil.isNotNull(location)) {
                            double longitude = currentIntent.getDoubleExtra("longitude", 0);
                            double latitude = currentIntent.getDoubleExtra("latitude", 0);
                            params.put("location", location);
                            params.put("longitude", longitude);
                            params.put("latitude", latitude);
                        }
                        params.put("links", getLinksStr());
                        params.put("can_comment", currentIntent.getBooleanExtra("can_comment", true));
                        params.put("can_transmit", currentIntent.getBooleanExtra("can_transmit", true));
                        MoodHandler.sendWordAndLink(SendMoodService.this, params);
                    }else
                        new NotificationUtil(SEND_MOOD_NOTIFICATION_ID, SendMoodService.this).sendTipNotification("图片上传中", "上传成功"+successNumber+"/"+totalImg +"...", "测试", 1, 0);
                    break;

                case UPLOAD_IMAGE_FAILURE: //失败
                    new NotificationUtil(SEND_MOOD_NOTIFICATION_ID, SendMoodService.this).sendTipNotification("信息提示", "您的发表的心情发送失败", "测试", 1, 0);
                    break;
            }
        }
    };

    private String getLinksStr(){
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < mNetworkLinks.size(); i++){
            buffer.append(ConstantsUtil.QINIU_CLOUD_SERVER + mNetworkLinks.get(i));
            if(i != mNetworkLinks.size()-1){
                buffer.append(";");
            }
        }
        return buffer.toString();
    }
    /**
     * 上传文件
     * @param file 文件的本地
     */
    public void uploadImg(File file){
        if(StringUtil.isNull(token)){
            new NotificationUtil(SEND_MOOD_NOTIFICATION_ID, SendMoodService.this).sendActionNotification("心情发表失败", "未获取到上传图片的凭证", "测试", 1, 0, MoodActivity.class);
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
                            handler.sendMessage(msg);
                            mNetworkLinks.add(serverfilename);
                        }
                    }, null);
        }catch (Exception e){
            Message msg = new Message();
            msg.what = UPLOAD_IMAGE_FAILURE;
            handler.sendMessage(msg);
            e.printStackTrace();
        }
    }
    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if (result instanceof Error) {
            ToastUtil.failure(getBaseContext(), ((Error) result).getMessage(), Toast.LENGTH_SHORT);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(result));
            if (type == TaskType.QINIU_TOKEN && !isCancel) {//获取凭证
                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){
                    ToastUtil.success(SendMoodService.this, jsonObject);
                    token = jsonObject.getString("message");
                    isCancel = false;
                    mNetworkLinks.clear();
                    uris = currentIntent.getStringExtra("uris");
                    //int oldMoodId = currentIntent.getIntExtra("oldMoodId", 0);
                    content = currentIntent.getStringExtra("content");
                    if (!StringUtil.isNull(uris)) {
                        String[] urisArr = uris.split(",");
                        totalImg = urisArr.length;
                        if (totalImg > 0) {
                            Bitmap bitmap = null;
                            String path = null;
                            File file;
                            String tempFilePath = null;
                            for (int i = 0; i < totalImg; i++) {
                                path = urisArr[i];
                                file = new File(path);//源文件
                                if (!file.isFile()) {
                                    ToastUtil.failure(getApplicationContext(), "请选择文件");
                                    return;
                                }
                                if (!file.exists()) {
                                    ToastUtil.failure(getApplicationContext(), "文件不存在:" + path);
                                    return;
                                }

                                tempFilePath =  FileUtil.getTempDir(getApplicationContext()) + File.separator + file.getName();
                                //ToastUtil.success(getApplicationContext(), "临时文件路径：" + tempFilePath);
                                file = new File(tempFilePath);
                                if (file.exists()) {
                                    file.delete();
                                }
                                bitmap = BitmapUtil.getSmallBitmap(getApplicationContext(), urisArr[i], 600, 800);
                                try {
                                    FileOutputStream out = new FileOutputStream(file);
                                    String suffix = StringUtil.getSuffixs(tempFilePath);
                                    if (suffix.equalsIgnoreCase("png")) {
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
                                    } else {
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                                    }
                                    out.flush();
                                    out.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (bitmap != null && !bitmap.isRecycled()) {
                                        bitmap.recycle();
                                        System.gc();
                                        bitmap = null;
                                    }
                                }
                                uploadImg(file);
                            }
                        }
                    }else{
                        //没有文件上传，直接发表
                        //简单的发送
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("content", content);
                        String location = currentIntent.getStringExtra("location");
                        if (StringUtil.isNotNull(location)) {
                            double longitude = currentIntent.getDoubleExtra("longitude", 0);
                            double latitude = currentIntent.getDoubleExtra("latitude", 0);
                            params.put("location", location);
                            params.put("longitude", longitude);
                            params.put("latitude", latitude);
                        }

                        params.put("can_comment", currentIntent.getBooleanExtra("can_comment", true));
                        params.put("can_transmit", currentIntent.getBooleanExtra("can_transmit", true));
                        MoodHandler.sendWord(SendMoodService.this, params);
                    }
                }else{
                    new NotificationUtil(SEND_MOOD_NOTIFICATION_ID, SendMoodService.this).sendActionNotification("心情发表失败", "上传图片的凭证获取失败", "测试", 1, 0, MoodActivity.class);
                }
            }else if (type == TaskType.SEND_MOOD_NORMAL && !isCancel) {//发表心情
                if (jsonObject != null && jsonObject.optBoolean("isSuccess")) {
                    new NotificationUtil(1, SendMoodService.this).sendTipNotification("信息提示", "您的发表的心情发送成功", "测试", 1, 0);
                } else {
                    saveError("发表文字过程中");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 操作失败后的操作
     *
     * @param steps
     */
    public void saveError(String steps) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("content", content);
            params.put("uris", uris);
            params.put("create_time", DateUtil.DateToString(new Date()));
            params.put("create_user_id", String.valueOf(BaseApplication.getLoginUserId()));
            //SharedPreferenceUtil.saveMoodDraft(getApplicationContext(), content, uris);
            new NotificationUtil(SEND_MOOD_NOTIFICATION_ID, SendMoodService.this).sendActionNotification("心情发表失败", "失败步骤:" + steps + "，心情已经保存为草稿", "测试", 1, 0, MoodActivity.class);
        } catch (Exception e) {
            e.printStackTrace();
            new NotificationUtil(SEND_MOOD_NOTIFICATION_ID, SendMoodService.this).sendActionNotification("心情发表失败", "失败步骤:" + steps, "测试", 1, 0, MoodActivity.class);
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }
}
