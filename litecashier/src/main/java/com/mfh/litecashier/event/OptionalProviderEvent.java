package com.mfh.litecashier.event;

import android.os.Bundle;

/**
 * 自有供应商
 * Created by kun on 15/9/23.
 */
public class OptionalProviderEvent {
    public static final int EVENT_ID_RELOAD_DATA = 0X01;//初始化数据
    public static final int EVENT_ID_REFRESH_DATA = 0X02;//刷新数据
    public static final int EVENT_ID_RELAOD_ITEM_DATA = 0X03;//刷新数据

    private int eventId;
    private Bundle args;

    public OptionalProviderEvent(int eventId) {
        this.eventId = eventId;
    }

    public OptionalProviderEvent(int eventId, Bundle args) {
        this.eventId = eventId;
        this.args = args;
    }

    public int getEventId() {
        return eventId;
    }

    public Bundle getArgs() {
        return args;
    }
}
