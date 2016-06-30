package com.leedane.cn.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.leedane.cn.bean.ChatDetailBean;
import com.leedane.cn.bean.HttpResponseChatDetailBean;
import com.leedane.cn.database.ChatDataBase;
import com.leedane.cn.handler.ChatHandler;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.NotificationUtil;
import com.leedane.cn.util.StringUtil;

/**
 * 异步获取未读的聊天列表service，
 * Created by LeeDane on 2016/6/30.
 */
public class LoadNoReadChatService extends Service implements TaskListener {

    public static final int BASE_LOAD_NO_READ_CHAT_CODE = 14;

    private int toUserId;
    private boolean isShowErrorNotification;
    private ChatDataBase dataBase =  new ChatDataBase(getApplicationContext());
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
            taskCanceled(TaskType.LOAD_NO_READ_CHAT);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ChatHandler.loadNoReadChat(LoadNoReadChatService.this);
            }
        }, 500);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(type == TaskType.LOAD_NO_READ_CHAT && StringUtil.isNotNull(StringUtil.changeNotNull(result))){
            try{
                HttpResponseChatDetailBean httpResponseChatDetailBean = BeanConvertUtil.strConvertToChatDetailBeans(StringUtil.changeNotNull(result));
                //获取到数据
               if(httpResponseChatDetailBean.isSuccess() && httpResponseChatDetailBean.getMessage().size() > 0){

                   //将记录保存在本地数据库
                   for(ChatDetailBean chatDetailBean: httpResponseChatDetailBean.getMessage()){
                       dataBase.insert(chatDetailBean);
                   }
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
                new NotificationUtil(BASE_LOAD_NO_READ_CHAT_CODE, LoadNoReadChatService.this).sendTipNotification("信息提示", steps, "测试", 1, 0);
            }catch (Exception e){
                e.printStackTrace();
                new NotificationUtil(BASE_LOAD_NO_READ_CHAT_CODE, LoadNoReadChatService.this).sendTipNotification("信息提示", steps, "测试", 1, 0);
            }
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }

    @Override
    public void onDestroy() {
        dataBase.destroy();
        super.onDestroy();
    }
}
