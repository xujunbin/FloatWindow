package com.ace.floatwindow.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.ace.floatwindow.Logger;
import com.ace.floatwindow.R;

/**
 * Created by JunBin on 2015/8/16.
 */
public class IntervalSelectorView extends View {
    private final static int MaxInterval = 5;
    private Paint mBgPaint;
    private Paint mNumberPaint;
    private Paint mCursorPaint;

    private int mInterval = 1;

    /**
     * 文字字号大小
     */
    private float mTextSize;
    /**
     * 文字在圆内缩进距离
     */
    private float mTextDecent;
    private float mDensity = 1.0f;

    /**
     * 背景圆的中心点X坐标
     */
    private float mCenterX;
    /**
     * 背景圆的中心点Y坐标
     */
    private float mCenterY;
    /**
     * 整个背景圆的半径
     */
    private float mRadius;

    public IntervalSelectorView(Context context) {
        super(context);
        initPaints();
    }

    public IntervalSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void initPaints() {
        mDensity = getResources().getDisplayMetrics().density;
        mTextSize = getResources().getDimensionPixelSize(R.dimen.text_size_20);
        mTextDecent = mTextSize + mDensity * 5;

        mBgPaint = new Paint();
        mBgPaint.setColor(0xFFDFDFDF);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setAntiAlias(true);

        mNumberPaint = new Paint();
        mNumberPaint.setColor(0xFF636363);
        mNumberPaint.setStyle(Paint.Style.FILL);
        mNumberPaint.setTextSize(mTextSize);
        mNumberPaint.setTypeface(Typeface.MONOSPACE);
        mNumberPaint.setAntiAlias(true);
        mNumberPaint.setTextAlign(Paint.Align.CENTER);
        mNumberPaint.setStrokeWidth(2 * mDensity);

        mCursorPaint = new Paint();
        mCursorPaint.setColor(0xFFABCECB);
        mCursorPaint.setStyle(Paint.Style.FILL);
        mCursorPaint.setAntiAlias(true);
        mCursorPaint.setStrokeWidth(2 * mDensity);
    }

    public void setInterval(final int interval) {
        mInterval = interval / 1000;
    }

    public int getInterval() {
        return mInterval * 1000;
    }

    private double getTouchedAngle(final float x, final float y) {
        // 通过三角形的三边长计算角度
        final double AB = mRadius;
        final double BC = Math.sqrt(Math.pow(mCenterX - x, 2) + Math.pow(mCenterY - y, 2));
        final double AC = Math.sqrt(Math.pow(mCenterX - x, 2) + Math.pow(mCenterY - mRadius - y, 2));
        final double angle = 180 * Math.acos((BC * BC - AC * AC + AB * AB) / (2 * AB * BC)) / Math.PI;
        Logger.d("getTouchedAngle", "angle=" + angle);

        boolean changed = false;
        final float averageAngle = 360.0f / MaxInterval;
        if (x < mCenterX) {
            if (angle < averageAngle / 2) {
                if (mInterval != 1) {
                    mInterval = 1;
                    changed = true;
                }
            } else if (angle < (averageAngle + averageAngle / 2)) {
                if (mInterval != 5) {
                    mInterval = 5;
                    changed = true;
                }
            } else {
                if (mInterval != 4) {
                    mInterval = 4;
                    changed = true;
                }
            }
        } else {
            if (angle < averageAngle / 2) {
                if (mInterval != 1) {
                    mInterval = 1;
                    changed = true;
                }
            } else if (angle < (averageAngle + averageAngle / 2)) {
                if (mInterval != 2) {
                    mInterval = 2;
                    changed = true;
                }
            } else {
                if (mInterval != 3) {
                    mInterval = 3;
                    changed = true;
                }
            }
        }

        if (changed) {
            playSoundEffect(SoundEffectConstants.CLICK);
            invalidate();
        }
        return angle;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    getTouchedAngle(event.getX(), event.getY());
                    return true;
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onDraw(final Canvas canvas) {
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
        mRadius = (getHeight() > getWidth() ? getWidth() : getHeight()) / 2;
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mBgPaint);

        final int startAngle = -90;
        final float averageAngle = 360.0f / MaxInterval;
        for (int i = 1; i <= MaxInterval; i++) {
            final String text = String.valueOf(i);
            final float angle = startAngle + (i - 1) * averageAngle;
            final Rect rect = new Rect();
            mNumberPaint.getTextBounds(text, 0, text.length(), rect);
            final float x = (float)(mCenterX + (mRadius - mTextDecent) * Math.cos(angle * Math.PI / 180));
            final float y = (float)(mCenterY + (mRadius - mTextDecent) * Math.sin(angle * Math.PI / 180));

            if (mInterval == i) {
                final float selectedCircleRadius = (rect.width() > rect.height() ? rect.width() : rect.height()) / 2 + mDensity * 10;
                canvas.drawCircle(x, y, selectedCircleRadius, mCursorPaint);
                canvas.drawLine(mCenterX, mCenterY, x, y, mCursorPaint);
            }

            canvas.drawText(text, x, y + rect.height() / 2, mNumberPaint);
        }

        canvas.drawPoint(mCenterX, mCenterY, mNumberPaint);
    }
}
