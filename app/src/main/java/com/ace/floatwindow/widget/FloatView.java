package com.ace.floatwindow.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ace.floatwindow.FeatureManager;
import com.ace.floatwindow.Logger;
import com.ace.floatwindow.NetworkManager;
import com.ace.floatwindow.R;
import com.ace.floatwindow.data.EncryptPreference;
import com.ace.floatwindow.data.Preference;
import com.ace.floatwindow.model.FloatViewConfiguration;

/**
 * Created by JunBin on 2015/8/4.
 */
public abstract class FloatView {
    private final static String TAG = FloatView.class.getSimpleName();

    protected Context mContext;
    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams mFloatViewParams;
    protected View mContentView;
    protected boolean mIsShown = false;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float mTouchX;
    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float mTouchY;
    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float mViewX;
    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float mViewY;
    /**
     * 第一次按下时手指在屏幕上的横坐标值
     */
    private float mFirstTouchX;
    /**
     * 第一次按下时手指在屏幕上的纵坐标值
     */
    private float mFirstTouchY;

    private int mStatusBarHeight = 0;

    protected FloatViewConfiguration mConfiguration;

    public FloatView(final Context context) {
        mContext = context;
    }

    protected void initView(final int layoutId) {
        mConfiguration = FloatViewConfiguration.load(mContext);
        mContentView = LayoutInflater.from(mContext).inflate(layoutId, null);

        mFloatViewParams = new WindowManager.LayoutParams();
        mFloatViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mFloatViewParams.format = PixelFormat.RGBA_8888;
        mFloatViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mFloatViewParams.gravity = Gravity.LEFT | Gravity.TOP;
        mFloatViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatViewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        resetViewType();

        final EncryptPreference preference = EncryptPreference.getInstance(mContext, Preference.DEFAULT_PREFERENCE_NAME);
        final int viewX = preference.getInt(Preference.TRAFFIC_FLOAT_VIEW_X, Integer.MIN_VALUE);
        final int viewY = preference.getInt(Preference.TRAFFIC_FLOAT_VIEW_Y, Integer.MIN_VALUE);
        if (viewX != Integer.MIN_VALUE && viewY != Integer.MIN_VALUE) {
            mFloatViewParams.x = viewX;
            mFloatViewParams.y = viewY;
        } else {
            WindowManager windowManager = getWindowManager(mContext);
            int screenWidth = windowManager.getDefaultDisplay().getWidth();
            int screenHeight = windowManager.getDefaultDisplay().getHeight();
            mFloatViewParams.x = screenWidth;
            mFloatViewParams.y = screenHeight / 2;
            saveViewPosition(mContext);
        }
    }

    protected void initTouchEvent() {
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mConfiguration != null && mConfiguration.lockFloatView()) {
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mViewX = event.getX();
                        mViewY = event.getY();
                        mFirstTouchX = mTouchX = event.getRawX();
                        mFirstTouchY = mTouchY = event.getRawY() - getStatusBarHeight();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        mTouchX = event.getRawX();
                        mTouchY = event.getRawY() - getStatusBarHeight();
                        updateViewPosition();
                        break;

                    case MotionEvent.ACTION_UP:
                        if (FeatureManager.ShowCloseButtonInFloatView) {
                            if (Math.abs(mFirstTouchX - mTouchX) < 5 && Math.abs(mFirstTouchY - mTouchY) < 5) {
                                // Click event
                                final View closeView = findViewById(R.id.float_win_iv_close);
                                if (closeView.getVisibility() == View.VISIBLE) {
                                    closeView.setVisibility(View.GONE);
                                } else {
                                    closeView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    public boolean isShown() {
        return mIsShown;
    }

    public void show() {
        if (mIsShown) {
            return;
        }

        mIsShown = true;
        getWindowManager(mContext).addView(mContentView, mFloatViewParams);
    }

    public void hide() {
        if (!mIsShown) {
            return;
        }

        mIsShown = false;
        getWindowManager(mContext).removeView(mContentView);
    }

    protected WindowManager getWindowManager(final Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        return mWindowManager;
    }

    public View findViewById(final int id) {
        if (mContentView != null) {
            return mContentView.findViewById(id);
        }

        return null;
    }

    public void bindData(final Object data) {
    }

    private void updateViewPosition() {
        mFloatViewParams.x = (int) (mTouchX - mViewX);
        mFloatViewParams.y = Math.max((int) (mTouchY - mViewY), 0);
        getWindowManager(mContext).updateViewLayout(mContentView, mFloatViewParams);
        saveViewPosition(mContext);
    }

    protected void saveViewPosition(final Context context) {
        final EncryptPreference preference = EncryptPreference.getInstance(context, Preference.DEFAULT_PREFERENCE_NAME);
        preference.putInt(Preference.TRAFFIC_FLOAT_VIEW_X, mFloatViewParams.x);
        preference.putInt(Preference.TRAFFIC_FLOAT_VIEW_Y, mFloatViewParams.y);
        preference.commit();
        Logger.d(TAG, String.format("save view position, x=[%s], y=[%s]", mFloatViewParams.x, mFloatViewParams.y));
    }

    public void setConfiguration(final FloatViewConfiguration configuration) {
        mConfiguration = configuration;
        onConfigurationChanged();
    }

    public FloatViewConfiguration getConfiguration() {
        return mConfiguration;
    }

    protected void onConfigurationChanged() {
        if (mConfiguration != null) {
            if (mConfiguration.isShowTrafficFloatView()) {
                final EncryptPreference preference = EncryptPreference.getInstance(mContext, Preference.DEFAULT_PREFERENCE_NAME);
                mFloatViewParams.x = preference.getInt(Preference.TRAFFIC_FLOAT_VIEW_X, Integer.MIN_VALUE);
                mFloatViewParams.y = preference.getInt(Preference.TRAFFIC_FLOAT_VIEW_Y, Integer.MIN_VALUE);

                getWindowManager(mContext).updateViewLayout(mContentView, mFloatViewParams);
            }
        }
    }

    public void onViewTypeChanged() {
        resetViewType();
        if (mConfiguration == null || mConfiguration.isShowTrafficFloatView()) {
            getWindowManager(mContext).removeViewImmediate(mContentView);
            getWindowManager(mContext).addView(mContentView, mFloatViewParams);
        }
    }

    public void onNetworkTypeChanged() {
        final int type = NetworkManager.getNetworkConnectType();
        if (type == NetworkManager.TYPE_MOBILE) {
            ((ImageView) findViewById(R.id.float_win_tv_type)).setImageResource(R.drawable.float_icon_data);
        } else {
            ((ImageView) findViewById(R.id.float_win_tv_type)).setImageResource(R.drawable.float_icon_wifi);
        }
    }

    private void resetViewType() {
        if (mConfiguration != null && mConfiguration.allowDragToStatusBar()) {
            mFloatViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            mFloatViewParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        } else {
            mFloatViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            mFloatViewParams.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }
        Logger.d(TAG, String.format("reset view type, type=[%s], flags=[%s]", mFloatViewParams.type, mFloatViewParams.flags));
    }

    private int getStatusBarHeight() {
        if (mConfiguration != null && mConfiguration.allowDragToStatusBar()) {
            return 0;
        } else {
            if (mStatusBarHeight <= 0) {
                int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    mStatusBarHeight = mContext.getResources().getDimensionPixelSize(resourceId);
                }

                if (mStatusBarHeight > 0) {
                    EncryptPreference.getInstance(mContext, Preference.DEFAULT_PREFERENCE_NAME).putInt(Preference.STATUS_BAR_HEIGHT, mStatusBarHeight).commit();
                }
            }

            return mStatusBarHeight;
        }
    }
}
