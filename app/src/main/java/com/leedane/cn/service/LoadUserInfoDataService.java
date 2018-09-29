package com.leedane.cn.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.NotificationUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 异步获取用户的数据service
 * 如获取评论数，积分，
 * Created by LeeDane on 2016/4/28.
 */
public class LoadUserInfoDataService  extends Service implements TaskListener {

    public static final int LOAD_USER_INFO_DATA_CODE = 10;

    private int toUserId;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //service被杀死后重启将没有intent，这里不做进一步处理即可
        if(intent == null){
            return super.onStartCommand(intent, flags, startId);
        }
        int userId = intent.getIntExtra("toUserId", 0);
        if(userId == toUserId){
            return super.onStartCommand(intent, flags, startId);
        }else{
            taskCanceled(TaskType.LOAD_USER_INFO_DATA);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HttpRequestBean requestBean = new HttpRequestBean();
                HashMap<String, Object> params = new HashMap<>();
                params.putAll(BaseApplication.newInstance().getBaseRequestParams());
                requestBean.setParams(params);
                requestBean.setServerMethod("us/user/info");
                requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_GET);
                TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_USER_INFO_DATA, LoadUserInfoDataService.this, requestBean);
            }
        }, 500);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(type == TaskType.LOAD_USER_INFO_DATA && StringUtil.isNotNull(StringUtil.changeNotNull(result))){
            try{
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                //获取到数据
                if(jsonObject.optBoolean("isSuccess")){
                    //saveError(jsonObject.getString("message"));
                    SharedPreferenceUtil.saveUserInfoData(getApplicationContext(), jsonObject.getJSONArray("message").getJSONObject(0).toString());
                    //使用静态的方式注册广播，可以使用显示意图进行发送广播
                    Intent broadcast = new Intent("com.leedane.cn.broadcast.UserInfoDataReceiver");
                    broadcast.putExtra("data", jsonObject.getString("message"));
                    sendBroadcast(broadcast,null);
                }else{
                    ToastUtil.failure(LoadUserInfoDataService.this, jsonObject);
                    saveError(jsonObject.getString("message"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 操作失败后的操作
     * @param steps
     */
    private void saveError(String steps){
        try {
            //SharedPreferenceUtil.saveMoodDraft(getApplicationContext(), content, uris);
            new NotificationUtil(LOAD_USER_INFO_DATA_CODE, LoadUserInfoDataService.this).sendTipNotification("信息提示", steps, "测试", 1, 0);
        }catch (Exception e){
            e.printStackTrace();
            new NotificationUtil(LOAD_USER_INFO_DATA_CODE, LoadUserInfoDataService.this).sendTipNotification("信息提示", steps, "测试", 1, 0);
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }
}
