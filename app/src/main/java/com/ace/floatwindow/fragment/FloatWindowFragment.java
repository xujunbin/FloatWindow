package com.ace.floatwindow.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.ace.floatwindow.Logger;
import com.ace.floatwindow.R;
import com.ace.floatwindow.data.EncryptPreference;
import com.ace.floatwindow.data.Preference;
import com.ace.floatwindow.model.FloatViewConfiguration;
import com.ace.floatwindow.service.DeamonService;
import com.ace.floatwindow.service.IDeamonServiceInterface;
import com.ace.floatwindow.widget.ExpandableLinearLayout;
import com.ace.floatwindow.widget.GeneralDialog;
import com.ace.floatwindow.widget.IntervalSelectorView;
import com.ace.floatwindow.widget.ToggleButton;
import com.umeng.fb.fragment.FeedbackFragment;
import com.umeng.fb.model.Conversation;

/**
 * A placeholder fragment containing a simple view.
 */
public class FloatWindowFragment extends BaseFragment {
    private final static String TAG = FloatWindowFragment.class.getSimpleName();

    private IDeamonServiceInterface mDeamonService;

    public FloatWindowFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity.bindService(new Intent(mContext, DeamonService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        mActivity.unbindService(mServiceConnection);
        super.onDetach();
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_main, container, false);
        initViews();
        return mContentView;
    }

    private void initViews() {
        final ExpandableLinearLayout expandableLinearLayout = (ExpandableLinearLayout)findViewById(R.id.main_ll_start_with_boot);
        expandableLinearLayout.measure(0, 0);

        final FloatViewConfiguration configuration = FloatViewConfiguration.load(mContext);
        final ToggleButton trafficFloatWindowButton = (ToggleButton)findViewById(R.id.main_tb_traffic_float);
        if (configuration.isShowTrafficFloatView()) {
            trafficFloatWindowButton.setToggleOn();
        } else {
            trafficFloatWindowButton.setToggleOff();
            expandableLinearLayout.collapse(false);
        }
        trafficFloatWindowButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                getPreference().putBoolean(Preference.SHOW_TRAFFIC_FLOAT_WINDOW, on).commit();
                toggleTrafficFloatSettingsView(on);
                if (mDeamonService != null) {
                    try {
                        if (on) {
                            mDeamonService.startTrafficFloatWindow();
                        } else {
                            mDeamonService.stopTrafficFloatWindow();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        final ToggleButton autoStartFloatButton = (ToggleButton)findViewById(R.id.main_tb_start_with_boot);
        if (configuration.isAutoStartWithBoot()) {
            autoStartFloatButton.setToggleOn();
        } else {
            autoStartFloatButton.setToggleOff();
        }
        autoStartFloatButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                getPreference().putBoolean(Preference.AUTO_START_FLOAT_WINDOW_WITH_BOOT, on).commit();
            }
        });

        final ToggleButton splitUpDownTrafficButton = (ToggleButton)findViewById(R.id.main_tb_split_up_down_traffic);
        if (configuration.isSplitUpDownTraffic()) {
            splitUpDownTrafficButton.setToggleOn();
        } else {
            splitUpDownTrafficButton.setToggleOff();
        }
        splitUpDownTrafficButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                getPreference().putBoolean(Preference.SPLIT_UP_DOWN_TRAFFIC, on).commit();
                if (mDeamonService != null) {
                    try {
                        mDeamonService.onConfigurationChanged();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        final ToggleButton dragStatusBarButton = (ToggleButton)findViewById(R.id.main_tb_drag_to_status_bar);
        if (configuration.allowDragToStatusBar()) {
            dragStatusBarButton.setToggleOn();
        } else {
            dragStatusBarButton.setToggleOff();
        }
        dragStatusBarButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                final EncryptPreference preference = getPreference();
                final int statusBarHeight = preference.getInt(Preference.STATUS_BAR_HEIGHT, 0);
                final int yPosition = preference.getInt(Preference.TRAFFIC_FLOAT_VIEW_Y, 0);
                if (on) {
                    preference.putInt(Preference.TRAFFIC_FLOAT_VIEW_Y, yPosition + statusBarHeight);
                } else {
                    preference.putInt(Preference.TRAFFIC_FLOAT_VIEW_Y, Math.max(0, yPosition - statusBarHeight));
                }
                preference.putBoolean(Preference.ALLOW_DRAG_TO_STATUS_BAR, on);
                final boolean success = preference.commit();
                Logger.d(TAG, String.format("drag status bar toggle changed: %s, success: %s", on, success));
                if (mDeamonService != null) {
                    try {
                        mDeamonService.onViewTypeChanged(on);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        final ToggleButton lockFloatViewButton = (ToggleButton)findViewById(R.id.main_tb_lock_view);
        if (configuration.lockFloatView()) {
            lockFloatViewButton.setToggleOn();
        } else {
            lockFloatViewButton.setToggleOff();
        }
        lockFloatViewButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                getPreference().putBoolean(Preference.LOCK_FLOAT_VIEW, on).commit();
                if (mDeamonService != null) {
                    try {
                        mDeamonService.onConfigurationChanged();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        final ToggleButton autoHideButton = (ToggleButton)findViewById(R.id.main_tb_auto_hide);
        if (configuration.autoHideIfNoNetwork()) {
            autoHideButton.setToggleOn();
        } else {
            autoHideButton.setToggleOff();
        }
        autoHideButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                getPreference().putBoolean(Preference.AUTO_HIDE_IF_NO_NETWORK, on).commit();
                if (mDeamonService != null) {
                    try {
                        mDeamonService.onConfigurationChanged();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.main_ll_refresh_interval).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIntervalSelectorDialog();
            }
        });
        onRefreshIntervalChanged(configuration.getTrafficRefreshInterval());
    }

    private void toggleTrafficFloatSettingsView(final boolean show) {
        final ExpandableLinearLayout expandableLinearLayout = (ExpandableLinearLayout)findViewById(R.id.main_ll_start_with_boot);
        if (show) {
            expandableLinearLayout.expand(true);
        } else {
            expandableLinearLayout.collapse(true);
        }
    }

    private void onRefreshIntervalChanged(final int interval) {
        final TextView refreshInterval = (TextView)findViewById(R.id.main_tv_refresh_interval);
        refreshInterval.setText(getString(R.string.main_refresh_interval_time, interval / 1000));
    }

    private void showIntervalSelectorDialog() {
        final EncryptPreference preference = getPreference();
        final int interval = preference.getInt(Preference.TRAFFIC_REFRESH_INTERVAL, 1000);
        final IntervalSelectorView contentView = new IntervalSelectorView(mContext);
        contentView.setInterval(interval);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                final ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                if (contentView.getHeight() > contentView.getWidth()) {
                    lp.height = contentView.getWidth();
                } else {
                    lp.width = contentView.getHeight();
                }
                contentView.setLayoutParams(lp);
            }
        });

        new GeneralDialog.Builder(mActivity)
            .setTitle(R.string.main_refresh_interval)
            .setContentView(contentView)
            .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dialog = null;

                    final int newInterval = contentView.getInterval();
                    if (newInterval != interval) {
                        onRefreshIntervalChanged(newInterval);
                        preference.putInt(Preference.TRAFFIC_REFRESH_INTERVAL, newInterval).commit();
                        if (mDeamonService != null) {
                            try {
                                mDeamonService.onConfigurationChanged();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            })
            .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dialog = null;
                }
            })
            .create()
            .show();
    }

    private void goFeedbackFragment() {
        final EncryptPreference preference = getPreference();
        String conversationId = preference.getString(Preference.FEEDBACK_CONVERSATION_ID, null);
        if (TextUtils.isEmpty(conversationId)) {
            final Conversation conversation = Conversation.newInstance(mContext);
            conversationId = conversation.getId();
            preference.putString(Preference.FEEDBACK_CONVERSATION_ID, conversationId).commit();
        }
        final FeedbackFragment fragment = FeedbackFragment.newInstance(conversationId);
        goNextFragment(fragment);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDeamonService = IDeamonServiceInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDeamonService = null;
        }
    };
}
