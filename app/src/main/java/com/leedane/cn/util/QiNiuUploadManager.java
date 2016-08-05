package com.leedane.cn.util;

import com.qiniu.android.storage.UploadManager;

/**
 * 七牛上传管理
 * Created by LeeDane on 2016/7/31.
 */
public class QiniuUploadManager {
    private static QiniuUploadManager qiniuUploadManager = null;

    private UploadManager uploadManager = null;
    private QiniuUploadManager(){
        uploadManager = new UploadManager(QiniuConfig.getInstance().getConfig());
    }

    public static synchronized QiniuUploadManager getInstance(){
        if(qiniuUploadManager == null){
            synchronized (QiniuUploadManager.class){
                qiniuUploadManager = new QiniuUploadManager();
            }
        }
        return qiniuUploadManager;
    }

    public UploadManager getUploadManager(){
        return uploadManager;
    }
}
