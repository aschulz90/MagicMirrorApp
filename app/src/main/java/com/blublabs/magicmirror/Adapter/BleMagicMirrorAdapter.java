package com.blublabs.magicmirror.adapter;

import android.content.Context;
import android.os.Handler;

import com.blublabs.magicmirror.modules.MagicMirrorModule;
import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceState;
import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.BleManagerConfig;
import com.idevicesinc.sweetblue.BleManagerState;
import com.idevicesinc.sweetblue.utils.Interval;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.idevicesinc.sweetblue.BleManager.DiscoveryListener.LifeCycle.DISCOVERED;
import static com.idevicesinc.sweetblue.BleManager.DiscoveryListener.LifeCycle.REDISCOVERED;

/**
 * Created by andrs on 24.11.2016.
 */

final class BleMagicMirrorAdapter implements IMagicMirrorAdapter {

    private static final int SCAN_TIME = 5;
    private static final String TARGET_DEVICE_NAME = "MagicMirror";

    private static final UUID SERVICE_MAGICMIRROR_APP_INTERFACE = UUID.fromString("0000280F-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE_LIST = UUID.fromString("000038cd-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE = UUID.fromString("000039cd-0000-1000-8000-00805f9b34fb");

    private final BleManager bleMgrInstance;

    private BleDevice connectedDevice = null;

    private List<MagicMirrorAdapterCallback> pendingScanCallback = new ArrayList<>();
    private final Handler scanDelayHandler = new Handler();
    private Runnable scanDelayThread = new Runnable() {
        @Override
        public void run() {
            for(MagicMirrorAdapterCallback callback : pendingScanCallback) {
                callback.onScanFinished();
            }
            pendingScanCallback.clear();
        }
    };
    private final BleManagerConfig.ScanFilter scanFilter = new BleManagerConfig.ScanFilter() {
        @Override
        public Please onEvent(ScanEvent e) {

            return Please.acknowledgeIf(e.name_native().equals(TARGET_DEVICE_NAME));
        }
    };

    BleMagicMirrorAdapter(Context contex) {

        BleManagerConfig config = new BleManagerConfig();
        config.loggingEnabled = true;
        config.manageCpuWakeLock = false;

        bleMgrInstance = BleManager.get(contex, config);
    }

    @Override
    public void getModuleData(int index, final MagicMirrorAdapterCallback callback) {

        if(index < 0) {
            callback.onGetModuleData(MagicMirrorAdapterCallback.STATUS_ERROR, null);
        }

        connectedDevice.write(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE, String.valueOf(index).getBytes(), new BleDevice.ReadWriteListener() {
            @Override
            public void onEvent(ReadWriteEvent e) {

                if(e.wasSuccess()) {

                    connectedDevice.read(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE, new BleDevice.ReadWriteListener() {
                        @Override
                        public void onEvent(ReadWriteEvent e) {
                            if(e.wasSuccess()) {

                                try {
                                    callback.onGetModuleData(MagicMirrorAdapterCallback.STATUS_SUCCESS, new JSONObject(e.data_string()));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                    callback.onGetModuleData(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                                }
                            }
                            else {
                                callback.onGetModuleData(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                            }
                        }
                    });
                }
                else {
                    callback.onGetModuleData(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                }
            }
        });
    }

    @Override
    public void setModuleData(int index, String data, final MagicMirrorAdapterCallback callback) {

        if(index < 0) {
            callback.onSetModuleData(MagicMirrorAdapterCallback.STATUS_ERROR);
        }

        connectedDevice.write(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE_LIST, ("REPLACE||" + index + "||" + data).getBytes(), new BleDevice.ReadWriteListener() {
            @Override
            public void onEvent(ReadWriteEvent e) {
                callback.onSetModuleData(e.wasSuccess() ? MagicMirrorAdapterCallback.STATUS_SUCCESS : MagicMirrorAdapterCallback.STATUS_ERROR);
            }
        });
    }

    @Override
    public void getModuleList(final MagicMirrorAdapterCallback callback) {

        connectedDevice.read(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE_LIST, new BleDevice.ReadWriteListener() {
            @Override
            public void onEvent(ReadWriteEvent e) {
                if(e.wasSuccess()) {

                    List<MagicMirrorModule> modules = new ArrayList<>();

                    try {
                        JSONArray moduleList = new JSONArray(e.data_string());

                        for(int i = 0; i < moduleList.length(); i++) {
                            final MagicMirrorModule module = MagicMirrorModule.getModuleForName(moduleList.getString(i));

                            if(module != null) {
                                modules.add(module);
                            }
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    callback.onGetModuleList(MagicMirrorAdapterCallback.STATUS_SUCCESS, modules);
                }
                else {
                    callback.onGetModuleList(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                }
            }
        });
    }

    @Override
    public void removeModule(int index, final MagicMirrorAdapterCallback callback) {

        if(index < 0) {
            callback.onRemoveModule(MagicMirrorAdapterCallback.STATUS_ERROR);
        }

        connectedDevice.write(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE_LIST, ("REMOVE||" + index).getBytes(), new BleDevice.ReadWriteListener() {
            @Override
            public void onEvent(ReadWriteEvent e) {
                callback.onRemoveModule(e.wasSuccess() ? MagicMirrorAdapterCallback.STATUS_SUCCESS : MagicMirrorAdapterCallback.STATUS_ERROR);
            }
        });
    }

    @Override
    public void connectMirror(final BleDevice.StateListener stateListener, Object identifier) {
        if(!(identifier instanceof String)) {
            throw new IllegalArgumentException("The 'identifier' argument needs to be of type 'String' and contain a Mac-Address!");
        }

        final BleDevice deviceToConnect = bleMgrInstance.getDevice((String) identifier);

        if(BleDevice.NULL.equals(deviceToConnect)) {
            throw new IllegalArgumentException("The device to connect to was not yet discovered, start a scan first!");
        }

        if(connectedDevice != null) {
            connectedDevice.disconnect();
        }

        deviceToConnect.connect(new BleDevice.StateListener() {

            @Override
            public void onEvent(StateEvent e) {

                if(e.didEnter(BleDeviceState.DISCONNECTED)) {
                    if(connectedDevice != null && connectedDevice.equals(deviceToConnect)) {
                        connectedDevice = null;
                    }
                }
                else if(e.didEnter(BleDeviceState.CONNECTED)) {
                    connectedDevice = deviceToConnect;
                }

                stateListener.onEvent(e);
            }
        });
    }

    @Override
    public void disconnectMirror() {
        if(connectedDevice != null) {
            connectedDevice.disconnect();
        }
    }

    @Override
    public void scanForMagicMirrors(final MagicMirrorAdapterCallback callback) {

        bleMgrInstance.startScan(Interval.secs(SCAN_TIME), scanFilter, new BleManager.DiscoveryListener() {
            @Override
            public void onEvent(DiscoveryEvent discoveryEvent) {
                if(discoveryEvent.was(DISCOVERED) || discoveryEvent.was(REDISCOVERED)) {
                    for(MagicMirrorAdapterCallback callback : pendingScanCallback) {
                        callback.onMagicMirrorDiscovered(discoveryEvent.macAddress());
                    }
                }
            }
        });

        if(!pendingScanCallback.contains(callback)) {
            pendingScanCallback.add(callback);
        }

        scanDelayHandler.removeCallbacks(scanDelayThread);
        scanDelayHandler.postDelayed(scanDelayThread, SCAN_TIME * 1000);
    }

    @Override
    public void stopScanForMagicMirrors() {
        if(bleMgrInstance.is(BleManagerState.SCANNING) || bleMgrInstance.is(BleManagerState.STARTING_SCAN)) {
            bleMgrInstance.stopScan();
            scanDelayHandler.removeCallbacks(scanDelayThread);

            for(MagicMirrorAdapterCallback callback : pendingScanCallback) {
                callback.onScanFinished();
            }
            pendingScanCallback.clear();
        }
    }

    @Override
    public boolean isConnectedToMirror() {

        return connectedDevice != null && connectedDevice.is(BleDeviceState.CONNECTED);
    }

    @Override
    public void onResume() {
        bleMgrInstance.onResume();
    }

    @Override
    public void onPause() {
        bleMgrInstance.onPause();
    }
}
