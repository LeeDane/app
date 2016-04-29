package com.leedane.cn.handler;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 评论相关的处理类
 * Created by LeeDane on 2016/3/27.
 */
public class CommentHandler {
    /**
     * 调用系统方法拨打电话
     * @param context
     * @param phoneNumber
     */
    public static void call(Context context, String phoneNumber){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(context.checkSelfPermission(Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED) {
                Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri
                        .parse("tel:" + phoneNumber));
                context.startActivity(dialIntent);
            }else{
                //
            }
        }else{
            Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri
                    .parse("tel:" + phoneNumber));
            context.startActivity(dialIntent);
        }
    }

    /**
     * 获取评论的请求
     * @param listener
     * @param params
     */
    public static void getCommentsRequest(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        requestBean.setServerMethod("leedane/comment_paging.action");
        TaskLoader.getInstance().startTaskForResult(TaskType.LOAD_COMMENT, listener, requestBean);
    }

    /**
     * 添加评论
     * @param listener
     * @param params
     */
    public static void sendComment(TaskListener listener, Map<String, Object> params){
        HttpRequestBean requestBean = new HttpRequestBean();
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        if(!params.containsKey("content")){
            params.put("content", "评论了这条信息");
        }
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/comment_add.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.ADD_COMMENT, listener, requestBean);
    }

    /**
     * 删除评论
     * @param listener
     * @param commentId
     * @param createUserId
     */
    public static void deleteComment(TaskListener listener, int commentId, int createUserId){
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("cid",commentId);
        params.put("create_user_id", createUserId);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/comment_delete.action");
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_COMMENT, listener, requestBean);
    }
}
