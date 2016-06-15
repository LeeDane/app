package com.leedane.cn.handler;

import android.content.Context;
import android.content.DialogInterface;

import com.leedane.cn.app.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 弹出框的处理类
 * Created by LeeDane on 2016/4/28.
 */
public class DialogHandler {

    /**
     * 展示获取到最新版本的弹出框
     * @param jsonObject
     */
    public static void showNewestVersion(final Context context, JSONObject jsonObject) {
        try{
            String version = jsonObject.getString("file_version");
            final String path = jsonObject.getString("path");
            String desc = jsonObject.getString("file_desc");
            long lenght = jsonObject.getLong("lenght");

            java.text.DecimalFormat df=new java.text.DecimalFormat("#.##");
            double d = lenght / 1024.00/ 1024.00;
            String size = df.format(d) +"M";
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setCancelable(true);
            builder.setIcon(R.drawable.menu_feedback);
            builder.setTitle("发现新版本号"+version);
            builder.setMessage(desc);
            builder.setPositiveButton(size,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            CommonHandler.openLink(context, path);
                        }
                    });
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
            builder.show();
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}
