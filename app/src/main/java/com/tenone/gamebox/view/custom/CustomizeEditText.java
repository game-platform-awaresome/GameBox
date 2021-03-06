package com.tenone.gamebox.view.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.tenone.gamebox.R;

public class CustomizeEditText extends AppCompatAutoCompleteTextView implements
		OnFocusChangeListener, TextWatcher {

	private Drawable mClearDrawable = null;

	private boolean hasFoucs;
	Context cxt;

	public CustomizeEditText(Context context) {
		this(context, null);
		this.cxt = context;
	}

	public CustomizeEditText(Context context, AttributeSet attrs) {

		this(context, attrs, android.R.attr.editTextStyle);
		this.cxt = context;
	}

	public CustomizeEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.cxt = context;
		init();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
			mClearDrawable = cxt.getResources().getDrawable(
					R.drawable.icon_edit_delete);
		}
		if (mClearDrawable != null) {
			mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
					mClearDrawable.getIntrinsicHeight());
		}
		setClearIconVisible(false);

		setOnFocusChangeListener(this);

		addTextChangedListener(this);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {

				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));

				if (touchable) {
					this.setText("");
				}
			}
		}

		return super.onTouchEvent(event);
	}

	public void onFocusChange(View v, boolean hasFocus) {
		this.hasFoucs = hasFocus;
		if (hasFocus) {
			setClearIconVisible(getText().length() > 0);
		} else {
			setClearIconVisible(false);
		}
	}

	protected void setClearIconVisible(boolean visible) {
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		if (hasFoucs) {
			setClearIconVisible(s.length() > 0);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	public void setShakeAnimation() {
		this.setAnimation(shakeAnimation(5));
	}

	public static Animation shakeAnimation(int counts) {
		Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
		translateAnimation.setInterpolator(new CycleInterpolator(counts));
		translateAnimation.setDuration(1000);
		return translateAnimation;
	}

}
