package com.leedane.cn.handler;

import android.content.Context;

import com.leedane.cn.bean.ChatBean;
import com.leedane.cn.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 聊天相关的设置处理
 * Created by LeeDane on 2016/5/4.
 */
public class ChatHandler {

    /**
     * 加载本地的聊天实体
     * @param context
     * @return
     */
    public static List<ChatBean> getLocalChatBeans(Context context){
        List<ChatBean> chatBeans = new ArrayList<>();
        ChatBean chatBean = new ChatBean();
        chatBean.setAccount("leedane");
        chatBean.setContent("你好啊");
        chatBean.setId(1);
        chatBean.setCreateTime(DateUtil.DateToString(new Date()));
        chatBeans.add(chatBean);

        ChatBean chatBean1 = new ChatBean();
        chatBean1.setAccount("andy");
        chatBean1.setContent("我是andy");
        chatBean1.setId(5);
        chatBean1.setCreateTime(DateUtil.DateToString(new Date()));
        chatBeans.add(chatBean1);
        return chatBeans;
    }
}
