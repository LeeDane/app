package com.leedane.cn.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.leedane.cn.app.R;

/**
 * 自定义右侧带分割竖线的TextView
 * Created by LeeDane on 2015/11/19.
 */
public class RightBorderTextView extends TextView{

    private Context mContext;

    public RightBorderTextView(Context context) {
        super(context);
        this.mContext = context;
    }

    public RightBorderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public RightBorderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint();

        //getResources().getColor(R.color.colorPrimary);//这个方法已经过时，用下面的方法实现
        //p.setColor(Color.RED);
        p.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

        //设置线的宽度
        p.setStrokeWidth((float) 5.0);
        canvas.drawLine(this.getWidth(), this.getHeight()/4, this.getWidth(), this.getHeight()/4*3, p);
    }
}
