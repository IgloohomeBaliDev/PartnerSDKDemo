package com.partnersdkdemo.module.bluetooth;

import android.bluetooth.le.ScanSettings;
import android.content.Context;

import co.igloohome.ble.lock.BleManager;
import co.igloohome.ble.lock.IglooLock;
import io.reactivex.Observable;


public class LockManager {

    private BleManager bleManager;
    private static LockManager instance = null;

    private LockManager(Context context)
    {
        bleManager = new BleManager(context);
    }
    public static LockManager getInstance(Context context) {
        if (instance == null) {
            instance = new LockManager(context);
        }
        return instance;
    }

    public Observable<IglooLock> scanForLock(String lockName, ScanSettings scanSettings) {
        return Observable.defer(() ->
                getPassiveScanner(scanSettings)
                        .filter(lock->lock.getName().equals(lockName)));
    }

    public Observable<IglooLock> getPassiveScanner(ScanSettings scanSettings){
        if(scanSettings == null){
            ScanSettings.Builder builderScanSettings = new ScanSettings.Builder();
            builderScanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
            return Observable.defer (()  -> bleManager.scan(builderScanSettings.build()));
        }else{
            return Observable.defer (()  -> bleManager.scan(scanSettings));
        }
    }
}