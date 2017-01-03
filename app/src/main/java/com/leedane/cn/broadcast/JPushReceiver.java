package com.leedane.cn.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.activity.ChatActivity;
import com.leedane.cn.activity.ChatDetailActivity;
import com.leedane.cn.activity.MainActivity;
import com.leedane.cn.activity.NotificationActivity;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatDetailBean;
import com.leedane.cn.database.ChatDataBase;
import com.leedane.cn.handler.FriendHandler;
import com.leedane.cn.util.NotificationUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPushReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[JPushReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[JPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 用户点击打开了通知");
            
        	//打开自定义的Activity
        	Intent i = new Intent(context, NotificationActivity.class);
        	i.putExtras(bundle);
        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        	context.startActivity(i);
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[JPushReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[JPushReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		//ToastUtil.success(context, "极光推送这块代码被注释掉");
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		if(StringUtil.isNotNull(extras)){//自定义消息
			try{
				JSONObject jsonObject = new JSONObject(extras);

				if (ChatDetailActivity.isForeground && ChatDetailActivity.toUserId == jsonObject.getInt("fromUserId")) {
					Intent msgIntent = new Intent(ChatDetailActivity.MESSAGE_RECEIVED_ACTION);
					msgIntent.putExtra(ChatDetailActivity.KEY_MESSAGE, message);
					if (StringUtil.isNotNull(extras)) {
						try {
							JSONObject extraJson = new JSONObject(extras);
							if (null != extraJson && extraJson.length() > 0) {
								msgIntent.putExtra(ChatDetailActivity.KEY_EXTRAS, extras);
							}
						} catch (JSONException e) {

						}

					}
					context.sendBroadcast(msgIntent);
				}else{
					ChatDataBase dataBase = new ChatDataBase(BaseApplication.newInstance());
					Gson gson = new GsonBuilder().create();
					ChatDetailBean chatDetailBean = gson.fromJson(message, ChatDetailBean.class);
					if(chatDetailBean != null){
						//判断是否是已经存在数据库
						if(dataBase.insert(chatDetailBean)){
							String friendAccount = FriendHandler.getFriendAccout(chatDetailBean.getCreateUserId());
							int notificationId = ChatActivity.BASE_USER_CHAT_DETAIL_CODE + chatDetailBean.getCreateUserId();
							if(StringUtil.isNull(friendAccount))
								friendAccount = chatDetailBean.getCreateUserName();

							new NotificationUtil(notificationId, context).sendActionNotification("与"+friendAccount +"聊天中", /*friendAccount +":" +*/chatDetailBean.getContent(), "聊天", dataBase.queryNoRead(chatDetailBean.getCreateUserId()), 0, ChatActivity.class);
						}
					}
				}
			}catch (JSONException e){
				e.printStackTrace();
			}
		}
	}
}
