package com.leedane.cn.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.leedane.cn.leedaneAPP.R;

/**
 * 通知栏工具类
 * Created by LeeDane on 2016/3/9.
 */
public class NotificationUtil {

    private static final String CLEAR_NOTI_ACTION ="com.sec.android.app.simrecord.CLEAR_NOTI_ACTION";

    private Context mContext;
    private int mNotificationId;
    public NotificationUtil(int notificationId, Context context){
        this.mContext = context;
        this.mNotificationId = notificationId;
    }

    /**
     * 发送提示的通知
     * 点击通知将自动消失
     */
    public void sendTipNotification(String title, String content1, String ticker, int number, int iconId) {
        Intent resultIntent = new Intent();
        resultIntent.setAction(CLEAR_NOTI_ACTION);
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(mContext, 0, resultIntent, 0);
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(content1) //<span style="font-family: Arial;">/设置通知栏显示内容</span>
                .setContentIntent(resultPendingIntent) //设置通知栏点击意图 (这里设置点击图标自动消失)
                .setNumber(number) //设置通知集合的数量
                .setTicker(ticker) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                        //.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(iconId > 0 ? iconId : R.mipmap.message).setAutoCancel(true);//设置通知小ICON

        mNotificationManager.notify(1, mBuilder.build());

    }

    /**
     * 发送反馈的通知
     * 点击通知将跳转
     */
    public void sendActionNotification(String title, String content1, String ticker, int number, int iconId) {
        Intent resultIntent = new Intent();
        resultIntent.setAction(CLEAR_NOTI_ACTION);
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(mContext, 0, resultIntent, 0);
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(content1) //<span style="font-family: Arial;">/设置通知栏显示内容</span>
                .setContentIntent(resultPendingIntent) //设置通知栏点击意图 (这里设置点击图标自动消失)
                .setNumber(number) //设置通知集合的数量
                .setTicker(ticker) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                        //.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(iconId > 0 ? iconId : R.mipmap.message).setAutoCancel(true);//设置通知小ICON

        mNotificationManager.notify(1, mBuilder.build());

    }
}
