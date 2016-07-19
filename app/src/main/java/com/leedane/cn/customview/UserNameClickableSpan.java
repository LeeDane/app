package com.leedane.cn.customview;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.leedane.cn.listener.*;

/**
 * 点击文本
 * Created by leedane on 2016/5/19.
 */
public class UserNameClickableSpan extends ClickableSpan {

    private OnUserNameClickListener onUserNameClickListener;

    String text;
    private int toUserId;
    public UserNameClickableSpan(int toUserId, String text, OnUserNameClickListener  onUserNameClickListener){
        this.onUserNameClickListener = onUserNameClickListener;
        this.text = text;
        this.toUserId = toUserId;
    }

    @Override
    public void onClick(View widget) {
        onUserNameClickListener.clickTextView(toUserId, text);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        onUserNameClickListener.setStyle(ds);
    }
}
