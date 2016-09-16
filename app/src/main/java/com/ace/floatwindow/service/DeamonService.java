package com.ace.floatwindow.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.ace.floatwindow.FeatureManager;
import com.ace.floatwindow.Logger;
import com.ace.floatwindow.NetworkManager;
import com.ace.floatwindow.data.EncryptPreference;
import com.ace.floatwindow.data.Preference;
import com.ace.floatwindow.database.TrafficDataAdapter;
import com.ace.floatwindow.database.TrafficDataHelper;
import com.ace.floatwindow.eventbus.EventBusInfo;
import com.ace.floatwindow.model.FloatViewConfiguration;
import com.ace.floatwindow.model.TrafficInfo;
import com.ace.floatwindow.widget.FloatView;
import com.ace.floatwindow.widget.FloatWindowManager;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class DeamonService extends Service {
    private final static String TAG = "DeamonService";

    private final static int MESSAGE_TRAFFIC_CHANGED = 1;
    private final static int MESSAGE_VIEW_TYPE_CHANGED = 2;

    private Handler mHandler;
    private DatabaseObserver mDatabaseObserver;
    private CheckTrafficTask mCheckTrafficTask;

    private FloatView mTrafficFloatView;

    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION) || action.equalsIgnoreCase(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                Logger.d(TAG, "received network action: " + action);
                NetworkManager.updateNetworkState(getApplicationContext());
            }
        }
    };

    public DeamonService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_TRAFFIC_CHANGED:
                        final TrafficInfo trafficInfo = (TrafficInfo)msg.obj;
                        if (trafficInfo != null) {
                            updateTrafficFloatView(trafficInfo);
                        }
                        break;

                    case MESSAGE_VIEW_TYPE_CHANGED:
                        final boolean overlayStatusBar = msg.arg1 > 0;
                        onViewTypeChanged(overlayStatusBar);
                        break;
                }
            }
        };
        mDatabaseObserver = new DatabaseObserver(mHandler);
        FloatWindowManager.getInstance().registerListener(mFloatViewListener);
        EventBus.getDefault().register(this);

        final Context context = getApplicationContext();
        NetworkManager.init(context);
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mNetworkReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final FloatViewConfiguration configuration = FloatViewConfiguration.load(getApplicationContext());
        if (configuration.isShowTrafficFloatView()) {
            startFloatWindow();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  mBinder;
    }

    @Override
    public void onDestroy() {
        FloatWindowManager.getInstance().unregisterListener(mFloatViewListener);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * To handle event bus info, so keep it anyway.
     * @param eventBusInfo The event bus info
     */
    public void onEventMainThread(final EventBusInfo eventBusInfo) {
        if (eventBusInfo != null) {
            switch (eventBusInfo.getEventType()) {
                case EventBusInfo.NETWORK_STATE_CHANGED:
                    final FloatViewConfiguration configuration = FloatViewConfiguration.load(getApplicationContext());
                    if (configuration.isShowTrafficFloatView()) {
                        startFloatWindow();
                    } else {
                        stopFloatWindow();
                    }
                    break;

                case EventBusInfo.NETWORK_TYPE_CHANGED:
                    if (mTrafficFloatView != null) {
                        mTrafficFloatView.onNetworkTypeChanged();
                    }
                    break;
            }
        }
    }

    private void updateTrafficFloatView(final TrafficInfo trafficInfo) {
        if (mTrafficFloatView != null) {
            mTrafficFloatView.bindData(trafficInfo);
        }
    }

    private void startCheckTrafficDataThread() {
        if (mCheckTrafficTask == null) {
            mCheckTrafficTask = new CheckTrafficTask(getApplicationContext());
        }
        mCheckTrafficTask.start();
    }

    private void stopCheckTrafficDataThread() {
        if (mCheckTrafficTask != null) {
            mCheckTrafficTask.stop();
        }
    }

    private void startDatabaseObserver() {
        Logger.d(TAG, "start database observer");
        final ContentResolver resolver = getContentResolver();
        resolver.registerContentObserver(TrafficDataAdapter.CONTENT_URI, true, mDatabaseObserver);
    }

    private void stopDatabaseObserver() {
        Logger.d(TAG, "stop database observer");
        final ContentResolver resolver = getContentResolver();
        resolver.unregisterContentObserver(mDatabaseObserver);
    }

    private void  startFloatWindow() {
        FloatWindowManager.getInstance().showFloatView(getApplicationContext());
        FloatWindowManager.getInstance().updateNetworkType();
    }

    private void  stopFloatWindow() {
        FloatWindowManager.getInstance().hideFloatView(getApplicationContext());
    }

    private void onViewTypeChanged(final boolean overlayStatusBar) {
        if (mTrafficFloatView != null) {
            final FloatViewConfiguration configuration = FloatViewConfiguration.load(getApplicationContext());
            configuration.setAllowDragToStatusBar(overlayStatusBar);
            mTrafficFloatView.setConfiguration(configuration);
            mTrafficFloatView.onViewTypeChanged();
        }
    }

    private FloatWindowManager.IFloatWindowListener mFloatViewListener = new FloatWindowManager.IFloatWindowListener() {
        @Override
        public void onFloatWindowShown(FloatView floatView) {
            mTrafficFloatView = floatView;
            startDatabaseObserver();
            startCheckTrafficDataThread();
            MobclickAgent.onPageStart("DeamonService");
            MobclickAgent.onResume(getApplicationContext());
        }

        @Override
        public void onFloatWindowHidden(FloatView floatView) {
            mTrafficFloatView = null;
            stopDatabaseObserver();
            stopCheckTrafficDataThread();
            MobclickAgent.onPageEnd("DeamonService");
            MobclickAgent.onPause(getApplicationContext());
        }
    };

    private IDeamonServiceInterface.Stub mBinder = new IDeamonServiceInterface.Stub() {
        @Override
        public void startTrafficFloatWindow() throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    final FloatViewConfiguration configuration = FloatViewConfiguration.load(getApplicationContext());
                    if (configuration.isShowTrafficFloatView()) {
                        Logger.d(TAG, "startFloatWindow");
                        DeamonService.this.startFloatWindow();
                    }
                }
            });
        }

        @Override
        public void stopTrafficFloatWindow() throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Logger.d(TAG, "stopFloatWindow");
                    DeamonService.this.stopFloatWindow();
                }
            });
        }

        @Override
        public void onViewTypeChanged(boolean overlayStatusBar) throws RemoteException {
            mHandler.removeMessages(MESSAGE_VIEW_TYPE_CHANGED);
            final Message msg = mHandler.obtainMessage(MESSAGE_VIEW_TYPE_CHANGED);
            msg.arg1 = overlayStatusBar ? 1 : 0;
            mHandler.sendMessageDelayed(msg, 200);
        }

        @Override
        public void onConfigurationChanged() throws RemoteException {
            final FloatViewConfiguration configuration = FloatViewConfiguration.load(getApplicationContext());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (configuration.isShowTrafficFloatView()) {
                        DeamonService.this.startFloatWindow();
                    } else {
                        DeamonService.this.stopFloatWindow();
                    }

                    if (mTrafficFloatView != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTrafficFloatView.setConfiguration(configuration);
                            }
                        });
                    }
                }
            });

            if (mCheckTrafficTask != null) {
                mCheckTrafficTask.setRefreshInterval(configuration.getTrafficRefreshInterval());
            }
        }
    };

    class  DatabaseObserver extends ContentObserver {
        public DatabaseObserver(final Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Logger.d(TAG, "Database changed");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Logger.d(TAG, String.format("Database changed, uri=[%s]", uri));
        }
    }

    class CheckTrafficTask {
        private final static String TAG = "CheckTrafficTask";

        private Context mContext;
        private Thread mThread;
        private boolean mRunning;
        private int mRefreshInterval = 1000;
        private Calendar mCalendar;

        public CheckTrafficTask(final Context context) {
            mContext = context;
        }

        public void start() {
            Logger.d(TAG, "thread start");
            if (!mRunning) {
                mRunning = true;

                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mRunning) {
                            final long mobileRX = TrafficStats.getMobileRxBytes();
                            final long mobileRXP = TrafficStats.getMobileRxPackets();
                            final long mobileTX = TrafficStats.getMobileTxBytes();
                            final long mobileTXP = TrafficStats.getMobileTxPackets();
                            final long totalRX = TrafficStats.getTotalRxBytes();
                            final long totalRXP = TrafficStats.getTotalRxPackets();
                            final long totalTX = TrafficStats.getTotalTxBytes();
                            final long totalTXP = TrafficStats.getTotalTxPackets();

                            final TrafficInfo trafficInfo = new TrafficInfo(getCurrentDayTimestamp(System.currentTimeMillis()));
                            trafficInfo.setMobileRx(mobileRX);
                            trafficInfo.setMobileRxp(mobileRXP);
                            trafficInfo.setMobileTx(mobileTX);
                            trafficInfo.setMobileTxp(mobileTXP);
                            trafficInfo.setTotalRx(totalRX);
                            trafficInfo.setTotalRxp(totalRXP);
                            trafficInfo.setTotalTx(totalTX);
                            trafficInfo.setTotalTxp(totalTXP);

                            if (FeatureManager.SupportDataStatistic) {
                                TrafficDataHelper.update(mContext.getContentResolver(), trafficInfo);
                            }

                            final Message msg = mHandler.obtainMessage(MESSAGE_TRAFFIC_CHANGED);
                            msg.obj = trafficInfo;
                            msg.sendToTarget();

                            if (mRunning) {
                                synchronized (CheckTrafficTask.this) {
                                    try {
                                        CheckTrafficTask.this.wait(mRefreshInterval);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            if (!mRunning) {
                                break;
                            }
                        }
                    }
                }, "CheckTrafficTask");
                mThread.start();
            }
        }

        public void stop() {
            Logger.d(TAG, "thread stop");
            if (mRunning) {
                mRunning = false;
                mThread = null;
                mCalendar = null;

                synchronized (CheckTrafficTask.this) {
                    CheckTrafficTask.this.notify();
                }
            }
        }

        public void setRefreshInterval(final int duration) {
            mRefreshInterval = duration;
            synchronized (CheckTrafficTask.this) {
                CheckTrafficTask.this.notify();
            }
        }

        /**
         * 获取当天最早的一个timestamp
         * @return 返回当天0时0分0秒的时间
         */
        private long getCurrentDayTimestamp(final long timestamp) {
            if (mCalendar == null) {
                mCalendar = Calendar.getInstance();
            }

            mCalendar.setTimeInMillis(timestamp);
            mCalendar.set(Calendar.MILLISECOND, 0);
            mCalendar.set(Calendar.SECOND, 0);
            mCalendar.set(Calendar.MINUTE, 0);
            mCalendar.set(Calendar.HOUR_OF_DAY, 0);
            return mCalendar.getTimeInMillis();
        }
    }
}
