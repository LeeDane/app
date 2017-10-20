package com.leedane.cn.listener;

import android.text.TextPaint;

/**
 * 点赞数量的TextView监听事件
 * Created by leedane on 2016/5/21.
 */
public interface OnZanUserNumberClickListener {
    void clickTextView(int number, String tableName, int tableId);
    void setStyle(TextPaint ds);
}
