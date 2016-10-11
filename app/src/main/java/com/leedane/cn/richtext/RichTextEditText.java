package com.leedane.cn.richtext;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * 富文本edittext
 * Created by LeeDane on 2016/10/11.
 */
public class RichTextEditText extends EditText implements
        View.OnFocusChangeListener, TextWatcher {
    /**
     * 控件是否有焦点
     */
    private boolean hasFoucs;

    //private Context context;
    public RichTextEditText(Context context) {
        this(context, null);
    }

    public RichTextEditText(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public RichTextEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean showContextMenu() {
        return false;
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return null;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu) {

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {

        /*ClipboardManager clip = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        switch (id) {
            case ID_COPY:
                clip.setText(mTransformed.subSequence(min, max));
                stopTextSelectionMode();
                return true;

            case ID_PASTE:
                CharSequence paste = clip.getText();

                if (paste != null && paste.length() > 0) {
                    long minMax = prepareSpacesAroundPaste(min, max, paste);
                    min = extractRangeStartFromLong(minMax);
                    max = extractRangeEndFromLong(minMax);
                    Selection.setSelection((Spannable) mText, max);
                    ((Editable) mText).replace(min, max, paste);
                    stopTextSelectionMode();
                }
                return true;*/
        return false;
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count,
                              int after) {
    }
}