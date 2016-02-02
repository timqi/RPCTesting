package com.timqi.rpctesting.serviceaidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by qiqi on 16/1/27.
 */
public class TimerService extends Service {

    private long timerCount = 0;
    private Timer timer = new Timer();

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("####  create service");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerCount++;

                System.out.println("#######  backgroud: "+timerCount);
            }
        }, 0, 1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return timerManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("####  destroy service");
        timer.cancel();
    }

    private ITimerManager.Stub timerManager
            = new ITimerManager.Stub() {
        @Override
        public long getTimerCount() throws RemoteException {
            return timerCount;
        }
    };
}
