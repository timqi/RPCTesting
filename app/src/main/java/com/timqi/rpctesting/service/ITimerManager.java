package com.timqi.rpctesting.service;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

/**
 * Created by qiqi on 16/1/27.
 */
public interface ITimerManager extends IInterface {

    String DESCRIPTOR = "com.timqi.rpctesing.ITimerManger";
    int TRANSACTION_getTimerCount = IBinder.FIRST_CALL_TRANSACTION + 0;

    long getTimerCount() throws RemoteException;
}
