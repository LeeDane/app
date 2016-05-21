package com.leedane.cn.customview;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.leedane.cn.listener.OnZanUserNumberClickListener;

/**
 * 点击数量的点击
 * Created by leedane on 2016/5/21.
 */
public class ZanUserNumberClickableSpan extends ClickableSpan {

    private OnZanUserNumberClickListener onZanNumberClickListener;

    private String tableName;
    private int tableId;
    private int zanNumber;

    public ZanUserNumberClickableSpan(int zanNumber, String tableName, int tableId, OnZanUserNumberClickListener onZanNumberClickListener){
        this.onZanNumberClickListener = onZanNumberClickListener;
        this.tableName = tableName;
        this.tableId = tableId;
        this.zanNumber = zanNumber;
    }

    @Override
    public void onClick(View widget) {
        onZanNumberClickListener.clickTextView(zanNumber, tableName, tableId);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        onZanNumberClickListener.setStyle(ds);
    }
}
