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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.timqi.rpctesting.service.ITimerManager;
import com.timqi.rpctesting.service.TimerManagerImpl;
import com.timqi.rpctesting.service.TimerService;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ClientActivity extends AppCompatActivity {

    private ITimerManager timerManager;
    private TextView tvShowTimer;
    private Button btnSwitch;

    private Subscription subscription;
    private Action1<Long> timerAction
            = new Action1<Long>() {
        @Override
        public void call(Long aLong) {
            if ((boolean) btnSwitch.getTag()) {
                try {
                    tvShowTimer.setText(timerManager.getTimerCount() + "");
                } catch (RemoteException e) {
                    Toast.makeText(ClientActivity.this, "RemoteException", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    };

    private ServiceConnection serviceConnection
            = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerManager = TimerManagerImpl.asInterface(service);
            subscription = Observable.interval(1, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(timerAction);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvShowTimer = (TextView) findViewById(R.id.tv);
        btnSwitch = (Button) findViewById(R.id.btn);
        btnSwitch.setTag(true);
        btnSwitch.setOnClickListener(onBtnClickListener);

        Intent intent = new Intent(this, TimerService.class);
        startService(intent);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }


    private View.OnClickListener onBtnClickListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean f = (boolean) v.getTag();
            if (f) {
                v.setTag(false);
                btnSwitch.setText("START");
            } else {
                v.setTag(true);
                btnSwitch.setText("STOP");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
        unbindService(serviceConnection);
    }
}
