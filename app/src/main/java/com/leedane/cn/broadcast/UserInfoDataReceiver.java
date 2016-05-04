package com.leedane.cn.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leedane.cn.activity.UserInfoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户数据的接收器
 * Created by LeeDane on 2016/5/3.
 */
public class UserInfoDataReceiver extends BroadcastReceiver {
    private static final String TAG = "UserInfoDataReceiver";

    /**
     * 用户数据有更新的接口监听器
     */
    public interface UpdateUserInfoDataListener{
        void updateUserInfoData(JSONObject jsonObject);
    }

    private UpdateUserInfoDataListener updateUserInfoDataListener;

    public void setUpdateUserInfoDataListener(UpdateUserInfoDataListener updateUserInfoDataListener) {
        this.updateUserInfoDataListener = updateUserInfoDataListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            String data = intent.getStringExtra("data");
            JSONArray jsonArray = new JSONArray(data);
            if(null != updateUserInfoDataListener)
                updateUserInfoDataListener.updateUserInfoData(jsonArray.getJSONObject(0));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
