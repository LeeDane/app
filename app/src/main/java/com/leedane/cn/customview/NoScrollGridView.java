package com.leedane.cn.customview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 不滚动的gridview
 * Created by LeeDane on 2016/10/13.
 */
public class NoScrollGridView extends GridView {
    public NoScrollGridView(Context context) {
        super(context);
        this.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * 设置不滚�?
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
