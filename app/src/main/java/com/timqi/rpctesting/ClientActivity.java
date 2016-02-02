package com.timqi.rpctesting;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.timqi.rpctesting.serviceaidl.ITimerManager;
import com.timqi.rpctesting.serviceaidl.TimerService;

import java.util.Timer;
import java.util.TimerTask;

public class ClientActivity extends AppCompatActivity {

    private ITimerManager timerManager;
    private boolean willShowTimer = true;

    private ServiceConnection serviceConnection
            = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerManager = ITimerManager.Stub.asInterface(service);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (willShowTimer) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {

                                    long count = timerManager.getTimerCount();
                                    ((TextView) findViewById(R.id.tv)).setText(count + "");
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }, 0, 1000);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (willShowTimer) {
                    willShowTimer = false;
                    ((TextView) findViewById(R.id.btn)).setText("start");
                } else {
                    willShowTimer = true;
                    ((TextView) findViewById(R.id.btn)).setText("stop");
                }

            }
        });

        Intent intent = new Intent(this, TimerService.class);
        startService(intent);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(serviceConnection);
    }
}
