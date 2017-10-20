package com.leedane.cn.customview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.leedane.cn.app.R;
import com.leedane.cn.fragment.SendToolbarFragment;
import com.leedane.cn.util.ToastUtil;

/**
 * 右侧图片可以编辑的点击
 * Created by LeeDane on 2016/7/5.
 */
public class RightImgClickEditText extends EditText implements
        View.OnFocusChangeListener, TextWatcher {
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;
    /**
     * 控件是否有焦点
     */
    private boolean hasFoucs;

    private boolean showEmoji; //是否展示emoji表情

    //    private Context context;
    public RightImgClickEditText(Context context) {
        this(context, null);
    }

    public RightImgClickEditText(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public RightImgClickEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
//        	throw new NullPointerException("You can add drawableRight attribute in XML");
            mClearDrawable = getResources().getDrawable(R.drawable.emoji_click);
        }

        mClearDrawable.setBounds(0, 6, 80, 90);
        //默认设置隐藏图标
        setClearIconVisible(true);
        //设置焦点改变的监听
        setOnFocusChangeListener(this);
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    //里面写上自己想做的事情，也就是DrawableRight的触发事件
                    if(!showEmoji){
                        onEmojiImgClickListener.afterEmojiImgClick(true);
                        showEmoji = true;
                        mClearDrawable = getResources().getDrawable(R.drawable.emoji_click);
                        mClearDrawable.setBounds(0, 6, 80, 90);
                        //默认设置隐藏图标
                        setClearIconVisible(true);
                    }else{
                        onEmojiImgClickListener.afterEmojiImgClick(false);
                        showEmoji = false;
                        mClearDrawable = getResources().getDrawable(R.drawable.emoji_no_click);
                        mClearDrawable.setBounds(0, 6, 80, 90);
                        //默认设置隐藏图标
                        setClearIconVisible(true);
                    }
                }else{
                    onEmojiImgClickListener.afterEmojiImgClick(false);
                    showEmoji = false;
                    mClearDrawable = getResources().getDrawable(R.drawable.emoji_no_click);
                    mClearDrawable.setBounds(0, 6, 80, 90);
                    //默认设置隐藏图标
                    setClearIconVisible(true);
                }
            }
        }

        return super.onTouchEvent(event);
    }

    public interface OnEmojiImgClickListener{
        void afterEmojiImgClick(boolean showEmoji);
    }

    public OnEmojiImgClickListener onEmojiImgClickListener;

    public void setOnEmojiImgClickListener(OnEmojiImgClickListener onEmojiImgClickListener) {
        this.onEmojiImgClickListener = onEmojiImgClickListener;
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        /*if (hasFocus) {
            //setClearIconVisible(getText().length() > 0);
            setClearIconVisible(true);
            //notificationEmojiShow(false);
       } else {
            notificationEmojiShow(true);
        }*/
    }

    private void notificationEmojiShow(boolean show){
        //控制是否展示emoji
        if(show){
            onEmojiImgClickListener.afterEmojiImgClick(true);
            //mClearDrawable = getResources().getDrawable(R.drawable.emoji_no_click);
            showEmoji = false;
        }else{
            onEmojiImgClickListener.afterEmojiImgClick(show);
            //mClearDrawable = getResources().getDrawable(R.drawable.emoji_click);
            showEmoji = true;
        }
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        //如果你想让它一直显示DrawableRight的图标，并且还需要让它触发事件，直接把null改成mClearDrawable即可
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
        if(!visible){
            onEmojiImgClickListener.afterEmojiImgClick(false);
            //mClearDrawable = getResources().getDrawable(R.drawable.emoji_no_click);
            showEmoji = false;
        }
    }

    //后面的代码无需更改，只需要粘贴进去即可，如果有不需要的可以删除，当然不删除也不会出错。

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count,
                              int after) {
        /*if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }*/
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }
}