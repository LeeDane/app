package com.leedane.cn.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.handler.FriendHandler;
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
 * 异步获取用户的好友列表service，
 * Created by LeeDane on 2016/4/30.
 */
public class LoadUserFriendService extends Service implements TaskListener {

    public static final int LOAD_USER_FRIENDS_CODE = 12;

    private int toUserId;
    private boolean isShowErrorNotification;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            return super.onStartCommand(intent, flags, startId);
        }
        int userId = intent.getIntExtra("toUserId", 0);
        //默认展示获取失败的错误通知
        isShowErrorNotification = intent.getBooleanExtra("isShowErrorNotification", true);
        if(userId == toUserId){
            return super.onStartCommand(intent, flags, startId);
        }else{
            taskCanceled(TaskType.LOAD_USER_FRIENS);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FriendHandler.sendGetAllFriends(LoadUserFriendService.this);
            }
        }, 500);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(type == TaskType.LOAD_USER_FRIENS && StringUtil.isNotNull(StringUtil.changeNotNull(result))){
            try{
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                //获取到数据
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    if(jsonObject.has("message"))
                        SharedPreferenceUtil.saveFriends(getApplicationContext(), jsonObject.toString());
                }else{
                    ToastUtil.failure(LoadUserFriendService.this, jsonObject);
                    saveError(jsonObject.getString("message"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 操作失败后的操作
     * @param steps
     */
    private void saveError(String steps){
        if(isShowErrorNotification){
            try {
                //SharedPreferenceUtil.saveMoodDraft(getApplicationContext(), content, uris);
                new NotificationUtil(LOAD_USER_FRIENDS_CODE, LoadUserFriendService.this).sendTipNotification("信息提示", steps, "测试", 1, 0);
            }catch (Exception e){
                e.printStackTrace();
                new NotificationUtil(LOAD_USER_FRIENDS_CODE, LoadUserFriendService.this).sendTipNotification("信息提示", steps, "测试", 1, 0);
            }
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }
}
