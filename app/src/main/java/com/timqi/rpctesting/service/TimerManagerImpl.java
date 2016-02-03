package com.timqi.rpctesting.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by qiqi on 16/1/27.
 */
public class TimerManagerImpl extends Binder implements ITimerManager {

    public TimerManagerImpl() {
        this.attachInterface(this, DESCRIPTOR);
        startCount();
    }

    private long timerCount = 0;

    private void startCount() {
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        timerCount = aLong;
                    }
                });
    }

    @Override
    public long getTimerCount() throws RemoteException {
        return timerCount;
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTOR);
                return true;

            case TRANSACTION_getTimerCount:
                data.enforceInterface(DESCRIPTOR);
                reply.writeNoException();
                reply.writeLong(getTimerCount());
                return true;
        }
        return super.onTransact(code, data, reply, flags);
    }

    public static ITimerManager asInterface(IBinder obj) {
        if (obj == null) return null;

        IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
        if ((iin != null) && (iin instanceof ITimerManager)) return (ITimerManager) iin;

        return new TimerManagerImpl.Proxy(obj);
    }

    private static class Proxy implements ITimerManager {
        private IBinder mRemote;

        public Proxy(IBinder mRemote) {
            this.mRemote = mRemote;
        }

        @Override
        public IBinder asBinder() {
            return mRemote;
        }

        public String getInterfaceDescriptor() {
            return DESCRIPTOR;
        }

        @Override
        public long getTimerCount() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel replay = Parcel.obtain();
            Long result;
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                mRemote.transact(TRANSACTION_getTimerCount, data, replay, 0);
                replay.readException();
                result = replay.readLong();
            } finally {
                data.recycle();
                replay.recycle();
            }
            return result;
        }
    }
}
