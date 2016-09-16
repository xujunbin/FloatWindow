package com.ace.floatwindow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by JunBin on 2015/8/15.
 */
public class ExpandableLinearLayout extends LinearLayout {
    private Scroller mScroller;
    private ViewGroup.LayoutParams mLayoutParams;
    private int mScrollerDuration = 500;

    private int mViewHeight = 0;

    public ExpandableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLayoutParams = getLayoutParams();
        mScroller = new Scroller(context);
    }

    public void expand(final boolean animation) {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }

        if (mViewHeight <= 0) {
            measure(0, 0);
        }

        mScroller.abortAnimation();
        if (animation) {
            mScroller.startScroll(0, 0, 0, mViewHeight, mScrollerDuration);
        } else {
            mScroller.setFinalY(mViewHeight);
        }
        invalidate();
    }

    public void collapse(final boolean animation) {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }

        if (mViewHeight <= 0) {
            measure(0, 0);
        }

        mScroller.abortAnimation();
        if (animation) {
            mScroller.startScroll(0, mViewHeight, 0, -mViewHeight, mScrollerDuration);
        } else {
            mScroller.setFinalY(0);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mViewHeight <= 0) {
            mViewHeight = getHeight();
        }
    }

    @Override
    public void computeScroll() {
        if (isInEditMode()) {
            return;
        }

        if (mScroller.computeScrollOffset()) {
            if (mLayoutParams == null) {
                mLayoutParams = getLayoutParams();
            }
            mLayoutParams.height = mScroller.getCurrY();
            setLayoutParams(mLayoutParams);
        }
    }
}
