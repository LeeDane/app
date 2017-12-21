package com.cn.leedane.netty;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.leedane.cn.activity.TestActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author LeeDane
 *         2017年10月30日 17时00分
 *         version 1.0
 */
public class PushMsgHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        PushMsg pushMsg = message.getMsg();
        System.out.println(pushMsg);
        if(message.getType() == MessageType.MSG_PUSH.getValue()){
            NotificationManager manager = (NotificationManager) BaseApplication.newInstance().getApplicationContext().getSystemService(BaseApplication.newInstance().getApplicationContext().NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(BaseApplication.newInstance().getApplicationContext());

            //创建消息,设置点击效果
            Intent i = new Intent(BaseApplication.newInstance().getApplicationContext(), TestActivity.class);
            NewsDetailList newsDetail = new NewsDetailList();
            newsDetail.author_name = pushMsg.getAuthor_name();
            newsDetail.date = pushMsg.getDate();
            newsDetail.thumbnail_pic_s = pushMsg.getThumbnail_pic_s();
            newsDetail.title = pushMsg.getTitle();
            newsDetail.url = pushMsg.getUrl();
            i.putExtra("data", newsDetail);
            PendingIntent intent = PendingIntent.getActivity(BaseApplication.newInstance().getApplicationContext(), 0,
                    i, PendingIntent.FLAG_ONE_SHOT);

            builder.setContentTitle(pushMsg.getTitle())//设置通知栏标题
                    .setContentText(pushMsg.getAuthor_name()) //设置通知栏显示内容
                    .setTicker("ok社区") //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setContentIntent(intent)
                    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    //.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                    .setSmallIcon(R.mipmap.app_ico);//设置通知小ICON
            manager.notify(1, builder.build());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {

    }
}
