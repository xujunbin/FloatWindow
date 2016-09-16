package com.ace.floatwindow.eventbus;

/**
 * Created by JunBin on 2016/1/11.
 */
public class EventBusInfo {
    public static final int NETWORK_STATE_CHANGED = 1;
    public static final int NETWORK_TYPE_CHANGED = 2;

    private final int mEventType;

    public EventBusInfo(final int type) {
        mEventType = type;
    }

    public int getEventType() {
        return mEventType;
    }
}
