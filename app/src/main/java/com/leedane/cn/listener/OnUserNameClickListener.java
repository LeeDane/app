package com.leedane.cn.listener;

import android.text.TextPaint;

/**
 * 用户名单击的TextView监听事件
 * Created by leedane on 2016/5/21.
 */
public interface OnUserNameClickListener {
    public void clickTextView(int toUserId, String username);
    public void setStyle(TextPaint ds);
}
