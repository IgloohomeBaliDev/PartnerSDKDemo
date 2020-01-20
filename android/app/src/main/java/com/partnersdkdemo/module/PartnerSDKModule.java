package com.partnersdkdemo.module;

import android.app.Activity;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.partnersdkdemo.module.bluetooth.LockManager;

import io.reactivex.disposables.Disposable;

public class PartnerSDKModule extends ReactContextBaseJavaModule{
    private static ReactApplicationContext reactContext;
    private LockManager lockManager;
    private Disposable scanner ;
    private static final String MODULE_NAME  = "PartnerSDKModule";
    private static final String SCAN_BLUETOOTH_EVENT  = "scanLock";

    PartnerSDKModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
        lockManager = LockManager.getInstance(reactContext);
    }

    /**
     *  This is the module name used by javascript(front end)
     */
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    /**
     *  Use this method to start scanning bluetooth devices.
     *  Remember to call stopScan when scan finish.
     *
     * @param onError callback
     */
    @ReactMethod
    public void startScan(Callback onError){
        Activity activity = reactContext.getCurrentActivity();
        assert activity != null;
        scanner = lockManager.getPassiveScanner(null)
                .subscribe(s-> {
                            WritableMap param = Arguments.createMap();
                            param.putString("name", s.getName());
                            param.putBoolean("isActive", s.getActive());
                            param.putInt("rssi", s.getRssi());
                            sendEvent(SCAN_BLUETOOTH_EVENT, param);
                        }, e->{
                            Log.d(SCAN_BLUETOOTH_EVENT, e.getMessage());
                            onError.invoke(e.toString());
                        }
                );
    }
    /**
     *  Remember to call stopScan when scan finish.
     */
    @ReactMethod
    public void stopScan(){
        scanner.dispose();
    }

    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        scanner.dispose();
    }

    /**
     *  Use this method to unlock a lock.
     *
     * @param bluetoothDeviceName String
     */
    @ReactMethod
    public void unlock(String bluetoothDeviceName){

    }

    private void sendEvent(String eventName, Object data){
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, data);
    }
}
