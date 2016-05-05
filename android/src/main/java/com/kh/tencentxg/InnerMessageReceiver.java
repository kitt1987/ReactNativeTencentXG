package com.kh.tencentxg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by KH on 5/5/16.
 */
public class InnerMessageReceiver extends BroadcastReceiver {
    private static final String LogTag = "[TXG]Inner Receiver";

    TencentXGModule rnModule;
    public InnerMessageReceiver(TencentXGModule module) {
        super();
        this.rnModule = module;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.rnModule.sendEvent(intent);
    }
}
