package com.leedane.cn.listener;

import android.text.TextPaint;

/**
 * 用户名单击的TextView监听事件
 * Created by leedane on 2016/5/21.
 */
public interface OnUserNameClickListener {
    void clickTextView(int toUserId, String username);
    void setStyle(TextPaint ds);
}
