package com.leedane.cn.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.leedane.cn.activity.MoodActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.database.BaseSQLiteDatabase;
import com.leedane.cn.database.BaseSQLiteOpenHelper;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.uploadfile.PortUpload;
import com.leedane.cn.uploadfile.UploadItem;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.FileUtil;
import com.leedane.cn.util.NotificationUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;

import org.json.JSONException;
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
 * 发送心情的service
 * Created by LeeDane on 2016/3/3.
 */
public class SendMoodService extends Service implements TaskListener {

    private boolean isCancel;//是否取消
    private List<UploadItem> mItems = new ArrayList<>();
    private String content;
    private String uris;
    private int itemIndex;
    boolean isItemFinish = false;//判断单个文件下载任务是否完成
    private String tempFilePath = null;
    private BaseSQLiteDatabase sqLiteDatabase;
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
        //service被杀死后重启将没有intent，这里不做进一步处理即可
        if(intent == null){
            return super.onStartCommand(intent, flags, startId);
        }
        isCancel = false;
        mItems.clear();
        sqLiteDatabase = new BaseSQLiteDatabase(SendMoodService.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                Looper.prepare();
                uris =  intent.getStringExtra("uris");
                int oldMoodId = intent.getIntExtra("oldMoodId", 0);
                content = intent.getStringExtra("content");
                JSONObject mUserInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());
                if(!StringUtil.isNull(uris)) {
                    String[] urisArr = uris.split(",");
                    JSONObject userInfo =  SharedPreferenceUtil.getUserInfo(BaseApplication.newInstance());
                    String uuid = null;
                    try {
                        uuid = userInfo.getString("account") + UUID.randomUUID().toString();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    if (urisArr.length > 0) {
                        Bitmap bitmap = null;
                        String path = null;
                        File file;
                        for (int i = 0; i <urisArr.length; i++) {
                            path = urisArr[i];
                            file = new File(path);//源文件
                            if(!file.isFile()){
                                Toast.makeText(getApplicationContext(), "请选择文件", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(!file.exists()){
                                Toast.makeText(getApplicationContext(), "文件不存在:" +path, Toast.LENGTH_LONG).show();
                                return;
                            }

                            tempFilePath = getTempDir(getApplicationContext()) + File.separator+ file.getName();
                            Toast.makeText(getApplicationContext(), "临时文件路径：" +tempFilePath, Toast.LENGTH_LONG).show();
                            file = new File(tempFilePath);
                            if (file.exists()) {
                                file.delete();
                            }
                            bitmap = BitmapUtil.getSmallBitmap(getApplicationContext(), urisArr[i]);
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
                                if(bitmap != null && !bitmap.isRecycled()){
                                    bitmap.recycle();
                                    System.gc();
                                    bitmap = null;
                                }
                            }
                            mItems.add(FileUtil.buildUploadItem(getApplicationContext(), tempFilePath, new Date(), i, uuid, "t_mood", 60000,60000));
                        }
                    }
                }
                //有文件需要上传的时候
                if(mItems.size() > 0){
                    //启动第一个上传
                    itemIndex = 0;
                    uploadOneFile();
                }else {
                    //没有文件上传，直接发表
                    //简单的发送
                    HttpRequestBean requestBean = new HttpRequestBean();
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("content", content);
                    params.putAll(BaseApplication.newInstance().getBaseRequestParams());
                    requestBean.setParams(params);
                    requestBean.setServerMethod("leedane/mood_sendWord.action");
                    requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
                    requestBean.setRequestTimeOut(60000);
                    requestBean.setResponseTimeOut(60000);
                    TaskLoader.getInstance().startTaskForResult(TaskType.SEND_MOOD_NORMAL, SendMoodService.this, requestBean);
                }


            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        if(StringUtil.isNotNull(tempFilePath)){
            File file = new File(tempFilePath);
            if(file.exists()){
                file.delete();
            }
        }
        Toast.makeText(getApplicationContext(), "stopService()", Toast.LENGTH_LONG).show();
        return super.stopService(name);
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
    /**
     * 找到下一个符合上传条件的文件进行上传
     */
    private void uploadOneFile(){
        UploadItem item = mItems.get(itemIndex);
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
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(result instanceof Error){
            Toast.makeText(getBaseContext(), ((Error) result).getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

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

                    if(isItemFinish){
                        isItemFinish = false;
                        uploadOneFile();
                    }
                    //返回失败
                }else{
                    saveError("文件上传过程中");
                }

            }else if(type == TaskType.MERGE_PORT_FILE && !isCancel){//合并文件
                //合并完成
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    mItems.get(itemIndex).setStatus(EnumUtil.FileStatus.删除断点文件.value);

                    //简单的发送
                    HttpRequestBean requestBean = new HttpRequestBean();
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("content", content);
                    params.put("uuid", mItems.get(0).getUuid());
                    params.putAll(BaseApplication.newInstance().getBaseRequestParams());
                    requestBean.setParams(params);
                    requestBean.setServerMethod("leedane/mood_sendWord.action");
                    requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
                    TaskLoader.getInstance().startTaskForResult(TaskType.SEND_MOOD_NORMAL, SendMoodService.this, requestBean);
                    //删除断点文件
                    deletePortFile();
                }else{
                    saveError("合并文件过程中");
                }
            }else if(type == TaskType.DELETE_PORT_FILE && !isCancel){//删除断点文件
                //合并完成
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    mItems.get(itemIndex).setStatus(EnumUtil.FileStatus.完成.value);
                    isItemFinish = true;
                    uploadOneFile();
                }else{
                    saveError("删除断点文件过程中");
                }
            }else if(type == TaskType.SEND_MOOD_NORMAL && !isCancel){//发表心情
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    new NotificationUtil(1, SendMoodService.this).sendTipNotification("信息提示", "您的发表的心情发送成功", "测试", 1, 0);
                }else{
                    saveError("发表文字过程中");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 操作失败后的操作
     * @param steps
     */
    public void saveError(String steps){
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", 1);
            params.put("content", content);
            params.put("uris",uris);
            params.put("create_time", DateUtil.DateToString(new Date()));
            params.put("create_user_id", String.valueOf(BaseApplication.getLoginUserId()));
            sqLiteDatabase.insert(BaseSQLiteOpenHelper.TABLE_MOOD_DRAFT, params);
            //SharedPreferenceUtil.saveMoodDraft(getApplicationContext(), content, uris);
            new NotificationUtil(1, SendMoodService.this).sendActionNotification("心情发表失败", "失败步骤:"+steps+"，心情已经保存为草稿", "测试", 1, 0, MoodActivity.class);
        }catch (Exception e){
            e.printStackTrace();
            new NotificationUtil(1, SendMoodService.this).sendActionNotification("心情发表失败", "失败步骤:"+steps, "测试", 1, 0, MoodActivity.class);
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }

    /**
     * 执行合并操作
     */
    private void merge(){
        //启动合并操作
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setServerMethod("leedane/filepath_mergePortFile.action");
        requestBean.setResponseTimeOut(60000);
        requestBean.setRequestTimeOut(60000);
        Map<String, Object> params = new HashMap<>();
        params.put("fileName", mItems.get(itemIndex).getFileName());
        params.put("uuid", mItems.get(itemIndex).getUuid());
        params.put("tableName", "t_mood");
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
        requestBean.setServerMethod("leedane/filepath_deletePortFile.action");
        requestBean.setResponseTimeOut(60000);
        requestBean.setRequestTimeOut(60000);
        Map<String, Object> params = new HashMap<>();
        params.put("uuid", mItems.get(itemIndex).getUuid());
        params.put("tableName", ConstantsUtil.UPLOAD_FILE_TABLE_NAME);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestTimeOut(60000);
        requestBean.setResponseTimeOut(60000);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_PORT_FILE, this, requestBean);
    }
}
