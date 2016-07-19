package com.leedane.cn.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 有对autolink属性处理的TextView
 * Created by LeeDane on 2015/11/19.
 */
public class AutoLinkTextView extends TextView{

    private Context mContext;

    public AutoLinkTextView(Context context) {
        super(context);
        this.mContext = context;
    }

    public AutoLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        ClickableSpan[] links = ((Spannable) this.getText()).getSpans(getSelectionStart(),
                getSelectionEnd(), ClickableSpan.class);
        if (links.length != 0) {
            links[0].onClick(this);
            return true;
        }else{
            return false;
        }
    }
}
