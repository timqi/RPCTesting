package com.timqi.rpctesting.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by qiqi on 16/1/27.
 */
public class TimerService extends Service {

    private TimerManagerImpl timerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        timerManager = new TimerManagerImpl();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return timerManager;
    }
}
