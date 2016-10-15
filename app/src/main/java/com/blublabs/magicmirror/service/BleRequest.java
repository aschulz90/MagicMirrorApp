package com.blublabs.magicmirror.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCallback;

/**
 * Created by andrs on 09.10.2016.
 */

public abstract class BleRequest {

    public enum RequestType {
        SCAN,
        DEVICE_CONNECT,
        CHARACTERISTIC_READ,
        CHARACTERISTIC_WRITE
    }

    private final RequestType type;
    private final BluetoothGattCallback gattCallback;
    private final BluetoothAdapter.LeScanCallback scanCallback;
    private final Object[] extra;

    public BleRequest(RequestType type, BluetoothGattCallback gattCallback, BluetoothAdapter.LeScanCallback scanCallback, Object... extra) {

        if(type == null) {
            throw new IllegalArgumentException("type cannot be null!");
        }

        switch(type) {
            case SCAN:
                if(scanCallback == null) {
                    throw new IllegalArgumentException("scanCallback cannot be null for RequestType SCAN!");
                }
            break;
            case DEVICE_CONNECT:
                //fall through
            case CHARACTERISTIC_READ:
                //fall through
            case CHARACTERISTIC_WRITE:
                if(gattCallback == null) {
                    throw new IllegalArgumentException("gattCallback cannot be null for RequestType DEVICE_CONNECT, CHARACTERISTIC_READ and CHARACTERISTIC_WRITE!");
                }
                break;
        }


        this.scanCallback = scanCallback;
        this.gattCallback = gattCallback;
        this.type = type;
        this.extra = extra;
    }

    public abstract void onError(String errormessage);

    public BluetoothGattCallback getGattCallback() {
        return gattCallback;
    }

    public BluetoothAdapter.LeScanCallback getScanCallback() {
        return scanCallback;
    }

    public RequestType getType() {
        return type;
    }

    public Object[] getExtra() {
        return extra;
    }
}
