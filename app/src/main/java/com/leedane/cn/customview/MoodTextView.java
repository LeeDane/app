package com.leedane.cn.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class MoodTextView extends TextView {

	private boolean dispatchToParent;

	public MoodTextView(Context context) {
		super(context);
		init(context);
	}

	public MoodTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MoodTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		//setHighlightColor(0);
	}

	public boolean handleTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (action != MotionEvent.ACTION_UP
				&& action != MotionEvent.ACTION_DOWN) {
			return true;
		} else {
			int x = (int) event.getX();
			int y = (int) event.getY();

			x -= getTotalPaddingLeft();
			y -= getTotalPaddingTop();

			x += getScrollX();
			y += getScrollY();
			Layout layout = getLayout();
			int line = layout.getLineForVertical(y);
			int offset = layout.getOffsetForHorizontal(line, x);
			
			float width = layout.getLineWidth(line);
			if (y > width) {
				offset = y;
			}

			if (!(getText() instanceof Spannable)) {
				return true;
			} else {
				Spannable span = (Spannable) getText();
				ClickableSpan[] clickSpan = span.getSpans(offset, offset,
						ClickableSpan.class);
				//am[] aam = span.getSpans(offset, offset, am.class);
//if (aam != null && aam.length != 0)
//return false;
				return clickSpan == null || clickSpan.length == 0;
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		MovementMethod mm = getMovementMethod();
		CharSequence text = getText();
		if (mm != null && (text instanceof Spannable)
				&& handleTouchEvent(event)) {
			mm.onTouchEvent(this, (Spannable) text, event);
			if (dispatchToParent) {
				return false;
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean performLongClick() {
		MovementMethod mm = getMovementMethod();
		//if (mm != null && (mm instanceof MyLinkMovementMethod))
			//((MyLinkMovementMethod) mm).a(this);
		return super.performLongClick();
	}

	public void setDispatchToParent(boolean flag) {
		dispatchToParent = flag;
	}
}
