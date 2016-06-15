package com.leedane.cn.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.leedane.cn.app.R;

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
     * 发送反馈的通知(不传递参数)
     * 点击通知将跳转
     */
    public void sendActionNotification(String title, String content, String ticker, int number, int iconId, Class action) {
        sendActionNotification(title, content, ticker, number, iconId, new Intent(mContext, action));
    }

    /**
     * 发送反馈的通知(可以传递参数)
     * 点击通知将跳转
     */
    public void sendActionNotification(String title, String content, String ticker, int number, int iconId, Intent intent) {
        // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                intent, 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API11之后才支持
        Notification notify = new Notification.Builder(mContext).setSmallIcon(R.drawable.head) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                // icon)
                .setTicker(ticker)// 设置在status
                        // bar上显示的提示文字
                .setContentTitle(title)// 设置在下拉status
                        // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                .setContentText(content)// TextView中显示的详细内容
                .setContentIntent(pendingIntent) // 关联PendingIntent
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setNumber(number) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .getNotification(); // 需要注意build()是在API level
        // 16及之后增加的，在API11中可以使用getNotificatin()来代替
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(1, notify);
    }

    public void cancelNotification(){
        // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(mNotificationId);
    }
}
