package com.leedane.cn.task;

/**
 * Created by LeeDane on 2015/10/11.
 */
public enum TaskType {

    HOME_LOADBLOGS,			//首页加载博客信息
    HOME_LOADBLOGIMAGES,		//首页加载博客的图片
    DELETE_BLOG,             //删除文章
    LOGIN_DO,     //登录
    REGISTER_DO, //注册
    DOWNLOAD_FILE, //下载文件信息
    UPLOAD_FILE, //上传文件信息
    MERGE_PORT_FILE, //合并断点文件信息
    DELETE_PORT_FILE, //删除断点文件信息
    FILE_LOAD, //加载服务器上的上传文件
    LOADNETWORK_BLOG_IMAGE, //加载博客的网络图片
    IS_FRIEND,   //判断是否是朋友
    IS_FAN,   //判断是否粉他或她
    ADD_FRIEND,   //加好友
    AGREE_FRIEND, //同意添加好友
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
    ADD_BLOG, //发表博客
    ADD_GALLERY, //加入图库
    DELETE_GALLERY, //删除图库
    ADD_COMMENT, //添加评论
    ADD_TRANSMIT, //添加转发
    ADD_ZAN, //添加赞
    ADD_ATTENTION, //添加关注
    ADD_COLLECTION, //添加收藏
    ADD_REPORT, //增加举报
    ADD_CHAT, //发聊天信息
    ADD_TAG, //添加标签
    ADD_FINANCIAL, //新增记账
    ADD_FINANCIAL_LOCATION, //新增记账位置
    PUBLISH_CHAT_BG, //发布聊天背景
    LOAD_COMMENT, //加载评论列表
    LOAD_TRANSMIT, //加载转发列表
    LOAD_ZAN, //加载赞列表
    LOAD_ZAN_USER, //加载点赞用户列表
    LOAD_ATTENTION, //加载关注列表
    LOAD_MY_FAN, //加载我的粉丝列表
    LOAD_MY_ATTENTION, //加载我关注的用户列表
    LOAD_COLLECTION, //加载收藏列表
    LOAD_CIRCLEOFFRIEND, //加载朋友圈数据
    LOAD_SCORE, //加载积分历史列表
    LOAD_LOGIN_HISTORY, //加载登录历史列表
    LOAD_CHAT, //加载聊天列表
    LOAD_SEARCH_USER, //加载搜索用户列表
    LOAD_SEARCH_BLOG, //加载搜索博客列表
    LOAD_SEARCH_MOOD, //加载搜索心情列表
    LOAD_FILE, //加载文件列表
    LOAD_CHAT_BG_SELECT_WEB,//获取聊天背景的选择图片
    LOAD_TOPIC, //加载心情列表
    LOAD_FINANCIAL_LOCATION, //加载记账位置列表
    DO_LOGIN_PHONE, //手机登录
    DO_GET_LOGIN_CODE, //获取手机登录的验证码
    GET_APP_VERSION, //检查APP版本
    LOAD_FRIENDS_PAGING, //获取已经跟我成为好友关系的分页列表
    LOAD_NOT_YET_FRIENDS_PAGING, //获取暂时未跟我成为好友关系的分页列表
    LOAD_NOTIFICATION,  //获取通知列表
    LOAD_USER_INFO_DATA, //加载用户的基本数据
    LOAD_USER_FRIENS, //加载用户的好友列表
    LOAD_HEAD_PATH, //加载用户新头像
    LOAD_NO_READ_CHAT, //加载未读的聊天记录
    LOAD_ALL_FINANCIAL, //获取全部的记账记录
    LOAD_USER_INFO, //个人中心获取用户的基本信息
    CANCEL_FAN, //不再成为TA的粉丝
    CANCEL_FRIEND, //解除好友关系
    DELETE_COMMENT, //删除评论
    DELETE_TRANSMIT, //删除转发
    DELETE_COLLECTION, //删除收藏记录
    DELETE_ZAN, //删除点赞记录
    DELETE_ATTENTION, //删除关注记录
    DELETE_CHAT, //删除聊天列表
    DELETE_FILE, //删除文件
    DELETE_NOTIFICATION, //删除通知
    DELETE_FINANCIAL, //删除记账记录
    DELETE_FINANCIAL_LOCATION, //删除记账位置记录
    UPDATE_HANDER, //更新用户头像
    UPDATE_USER_BASE, //更新用户的基本信息
    UPDATE_LOGIN_PSW, //更新登录密码
    UPDATE_COMMENT_STATUS, //更新评论状态
    UPDATE_TRANSMIT_STATUS, //更新转发状态
    UPDATE_CHAT_READ_STATUS,//更新聊天信息为已读状态
    UPDATE_FINANCIAL_LOCATION,//更新记账位置
    FANYI, //翻译
    QINIU_TOKEN, //七牛云存储token
    VERIFY_CHAT_BG, //校验聊天背景
    SEND_EMAIL, //发送电子邮件
    SYNCHRONOUS_FINANCIAL, //同步记账记录
    FORCE_ALL, //强制与云端同步
    SMART_ALL, //智能与云端同步
    SCAN_LOGIN, //扫码登陆
    CANCEL_SCAN_LOGIN, //取消二维码登录
}
