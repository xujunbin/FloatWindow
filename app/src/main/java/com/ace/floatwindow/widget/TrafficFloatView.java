package com.ace.floatwindow.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ace.floatwindow.FeatureManager;
import com.ace.floatwindow.R;
import com.ace.floatwindow.data.EncryptPreference;
import com.ace.floatwindow.data.Preference;
import com.ace.floatwindow.model.TrafficInfo;
import com.ace.floatwindow.utils.TrafficUtils;

/**
 * Created by JunBin on 2015/8/4.
 */
public class TrafficFloatView extends FloatView {
    private TrafficInfo mTrafficInfo;

    public TrafficFloatView(final Context context) {
        super(context);
        initViews();
        initTouchEvent();
    }

    private void initViews() {
        super.initView(R.layout.float_window_bg);

        if (FeatureManager.ShowCloseButtonInFloatView) {
            final View closeView = findViewById(R.id.float_win_iv_close);
            closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeView.setVisibility(View.GONE);
                    FloatWindowManager.getInstance().hideFloatView(mContext);
                }
            });
        }
    }

    @Override
    public void bindData(final Object data) {
        if (!TrafficInfo.class.isInstance(data)) {
            return;
        }

        final TrafficInfo trafficInfo = (TrafficInfo)data;
        if (mTrafficInfo == null) {
            if (mConfiguration.isSplitUpDownTraffic()) {
                ((TextView) findViewById(R.id.float_win_tv_speed)).setText(TrafficUtils.getSpeed(mContext, 0, 0));
            } else {
                ((TextView) findViewById(R.id.float_win_tv_speed)).setText(TrafficUtils.getSpeed(mContext, 0));
            }
        } else {
            final TrafficUtils.TrafficCompareResult result = TrafficUtils.compare(trafficInfo, mTrafficInfo);
            if (mConfiguration.isSplitUpDownTraffic()) {
                ((TextView)findViewById(R.id.float_win_tv_speed)).setText(TrafficUtils.getSpeed(mContext, result.getTotalTx() / mConfiguration.getTrafficRefreshIntervalInSecond(), result.getTotalRx() / mConfiguration.getTrafficRefreshIntervalInSecond()));
            } else {
                ((TextView)findViewById(R.id.float_win_tv_speed)).setText(TrafficUtils.getSpeed(mContext, (result.getTotalRx() + result.getTotalTx()) / mConfiguration.getTrafficRefreshIntervalInSecond()));
            }
        }
        mTrafficInfo = trafficInfo;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    protected void onConfigurationChanged() {
        super.onConfigurationChanged();
    }
}
