package com.leedane.cn.task;

/**
 * Created by LeeDane on 2015/10/11.
 */
public enum TaskType {

    HOME_LOADBLOGS,			//首页加载博客信息
    HOME_LOADBLOGIMAGES,		//首页加载博客的图片
    DELETE_BLOG,             //删除文章
    LOGIN_DO,     //登录
    DOWNLOAD_FILE, //下载文件信息
    UPLOAD_FILE, //上传文件信息
    MERGE_PORT_FILE, //合并断点文件信息
    DELETE_PORT_FILE, //删除断点文件信息
    FILE_LOAD, //加载服务器上的上传文件
    LOADNETWORK_BLOG_IMAGE, //加载博客的网络图片
    IS_FRIEND,   //判断是否是朋友
    IS_FAN,   //判断是否粉他或她
    ADD_FRIEND,   //加好友
    ADD_FAN,  //成为他/她的粉丝
    IS_SIGN_IN, //判断是否签到
    DO_SIGN_IN, //签到
    SEND_MOOD_DRAFT, //发表心情草稿
    SEND_MOOD_NORMAL, //发表心情
    SEND_MOOD_PHOTO, //发送心情图片
    DELETE_MOOD, //删除心情
    DETAIL_MOOD, //查看心情详细
    DETAIL_MOOD_IMAGE, //查看心情详细的图片
    PERSONAL_LOADMOODS, //个人中心加载心情列表
    DO_GALLERY, //加载图库
    ADD_GALLERY, //加入图库
    DELETE_GALLERY, //删除图库
    ADD_COMMENT, //添加评论
    ADD_TRANSMIT, //添加转发
    ADD_ZAN, //添加赞
    ADD_ATTENTION, //添加关注
    ADD_COLLECTION, //添加收藏
    ADD_REPORT, //增加举报
    LOAD_COMMENT, //加载评论列表
    LOAD_TRANSMIT, //加载转发列表
    LOAD_ZAN, //加载赞列表
    LOAD_ATTENTION, //加载关注列表
    LOAD_MY_FAN, //加载我的粉丝列表
    LOAD_MY_ATTENTION, //加载我关注的用户列表
    LOAD_COLLECTION, //加载收藏列表
    LOAD_CIRCLEOFFRIEND, //加载朋友圈数据
    DO_LOGIN_PHONE, //手机登录
    DO_GET_LOGIN_CODE, //获取手机登录的验证码
    GET_APP_VERSION, //检查APP版本
    LOAD_FRIENDS_PAGING, //获取已经跟我成为好友关系的分页列表
    LOAD_REQUEST_PAGING, //获取我发送的好友请求列表
    LOAD_RESPONSE_PAGING, //获取等待我同意的好友关系列表
    LOAD_NOTIFICATION,  //获取通知列表
    LOAD_USER_INFO_DATA, //加载用户的基本数据
    CANCEL_FAN, //不再成为TA的粉丝
    DELETE_COMMENT, //删除评论
    DELETE_TRANSMIT, //删除转发
    DELETE_COLLECTION, //删除收藏记录
    DELETE_ZAN, //删除点赞记录
    DELETE_ATTENTION, //删除关注记录
}
