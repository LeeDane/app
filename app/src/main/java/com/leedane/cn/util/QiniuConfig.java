package com.leedane.cn.util;

import com.qiniu.android.common.Zone;
import com.qiniu.android.storage.Configuration;

/**
 * 七牛上传配置的单例模式类
 * Created by LeeDane on 2016/7/29.
 */
public class QiniuConfig {

    private static QiniuConfig qiniuConfig = null;

    private Configuration config = null;
    private QiniuConfig(){
        config = new Configuration.Builder()
                .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认256K
                .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认512K
                .connectTimeout(20) // 链接超时。默认10秒
                .responseTimeout(60) // 服务器响应超时。默认60秒
                .recorder(null)  // recorder分片上传时，已上传片记录器。默认null
                        //.recorder(recorder, keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。默认 Zone.zone0
                .build();
    }

    public static synchronized QiniuConfig getInstance(){
        if(qiniuConfig == null){
            synchronized (QiniuConfig.class){
                qiniuConfig = new QiniuConfig();
            }
        }

        return qiniuConfig;
    }
    public Configuration getConfig(){
        return config;
    }
}
