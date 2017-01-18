package com.blublabs.magicmirror.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.blublabs.magicmirror.settings.mirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.utils.GzipUtil;
import com.blublabs.magicmirror.settings.mirror.wifi.WifiNetwork;
import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceState;
import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.BleManagerConfig;
import com.idevicesinc.sweetblue.BleManagerState;
import com.idevicesinc.sweetblue.utils.Interval;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.idevicesinc.sweetblue.BleManager.DiscoveryListener.LifeCycle.DISCOVERED;
import static com.idevicesinc.sweetblue.BleManager.DiscoveryListener.LifeCycle.REDISCOVERED;

/**
 * Created by andrs on 24.11.2016.
 */

final class BleMagicMirrorAdapter implements IMagicMirrorAdapter {

    private static final int SCAN_TIME = 5;
    private static final String TARGET_DEVICE_NAME = "MagicMirror";

    private static final String KEY_WIFI_STATUS = "status";
    private static final String KEY_WIFI_AVAILABLE_NETWORKS = "availableNetworks";
    private static final String KEY_WIFI_NETWORKS_SSID = "ssid";
    private static final String KEY_WIFI_NETWORKS_MAC = "address";
    private static final String KEY_WIFI_MAC = "bssid";
    private static final String KEY_WIFI_SSID = "ssid";
    private static final String KEY_WIFI_PASSPHRASE = "passphrase";

    private static final UUID SERVICE_MAGICMIRROR_APP_INTERFACE = UUID.fromString("0000280F-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE_LIST = UUID.fromString("000038cd-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE = UUID.fromString("000039cd-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_DEFAULT_MODULE_LIST = UUID.fromString("000040cd-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MIRROR_CONFIG = UUID.fromString("000041cd-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MIRROR_QUERY = UUID.fromString("000042cd-0000-1000-8000-00805f9b34fb");

    private static final UUID SERVICE_MAGICMIRROR_APP_INTERFACE_WIFI = UUID.fromString("0000210F-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_WIFI_READ = UUID.fromString("000035cd-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_WIFI_WRITE = UUID.fromString("000036cd-0000-1000-8000-00805f9b34fb");

    private final BleManager bleMgrInstance;

    private BleDevice connectedDevice = null;

    private final List<MagicMirrorAdapterCallback> pendingScanCallback = new ArrayList<>();
    private final Handler scanDelayHandler = new Handler();
    private final Runnable scanDelayThread = new Runnable() {
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

    BleMagicMirrorAdapter(Context context) {

        BleManagerConfig config = new BleManagerConfig();
        config.loggingEnabled = true;
        config.manageCpuWakeLock = false;

        bleMgrInstance = BleManager.get(context, config);
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
    public void setModuleData(int index, JSONObject data, final MagicMirrorAdapterCallback callback) {

        if(index < 0) {
            callback.onSetModuleData(MagicMirrorAdapterCallback.STATUS_ERROR);
        }

        connectedDevice.write(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE_LIST, ("REPLACE||" + index + "||" + data.toString()).getBytes(), new BleDevice.ReadWriteListener() {
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
                        callback.onGetModuleList(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                        return;
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
    public void getInstalledModuleList(final MagicMirrorAdapterCallback callback) {
        connectedDevice.read(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_DEFAULT_MODULE_LIST, new BleDevice.ReadWriteListener() {
            @Override
            public void onEvent(ReadWriteEvent e) {
                if(e.wasSuccess()) {

                    Map<String, List<String>> installedModules = new HashMap<>();
                    List<String> defaultModules = new ArrayList<>();
                    List<String> customModules = new ArrayList<>();
                    installedModules.put(KEY_DEFAULT_MODULES, defaultModules);
                    installedModules.put(KEY_CUSTOM_MODULES, customModules);

                    try {
                        JSONObject wrapper = new JSONObject(e.data_string());
                        JSONArray defaultModulesArray = wrapper.getJSONArray(KEY_DEFAULT_MODULES);
                        JSONArray customModulesArray = wrapper.getJSONArray(KEY_CUSTOM_MODULES);

                        for(int i = 0; i < defaultModulesArray.length(); i++) {
                            defaultModules.add(defaultModulesArray.getString(i));
                        }

                        for(int i = 0; i < customModulesArray.length(); i++) {
                            customModules.add(customModulesArray.getString(i));
                        }

                        callback.onGetInstalledModuleList(MagicMirrorAdapterCallback.STATUS_SUCCESS, installedModules);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        callback.onGetModuleList(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                    }
                }
                else {
                    callback.onGetModuleList(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                }
            }
        });
    }

    @Override
    public void addModule(JSONObject module, final MagicMirrorAdapterCallback callback) {
        connectedDevice.write(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_DEFAULT_MODULE_LIST, module.toString().getBytes(), new BleDevice.ReadWriteListener() {

            @Override
            public void onEvent(ReadWriteEvent e) {
                callback.onAddModule(e.wasSuccess() ? MagicMirrorAdapterCallback.STATUS_SUCCESS : MagicMirrorAdapterCallback.STATUS_ERROR);
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
    public void connectMirror(final MagicMirrorAdapterCallback callback, final String identifier, @NonNull final Context context) {
        final BleDevice deviceToConnect = bleMgrInstance.getDevice(identifier);

        if(BleDevice.NULL.equals(deviceToConnect)) {
            // not yet discovered so scan for it first
            scanForMagicMirrors(new MagicMirrorAdapterCallback() {

                boolean found = false;

                @Override
                public void onMagicMirrorDiscovered(String id, String extra) {
                    if(identifier.equals(id)) {
                        found = true;
                        stopScanForMagicMirrors();
                        connectMirror(callback, id, context);
                    }
                }

                @Override
                public void onScanFinished() {
                    if(!found) {
                        callback.onConnectionError();
                    }
                }
            }, context);
            return;
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
                        callback.onDisconnectedFromMirror();
                    }
                }
                else if(e.didEnter(BleDeviceState.CONNECTED)) {
                    connectedDevice = deviceToConnect;
                    callback.onConnectedToMirror();
                }
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
    public void scanForMagicMirrors(final MagicMirrorAdapterCallback callback, @NonNull Context context) {

        bleMgrInstance.startScan(Interval.secs(SCAN_TIME), scanFilter, new BleManager.DiscoveryListener() {
            @Override
            public void onEvent(DiscoveryEvent discoveryEvent) {
                if(discoveryEvent.was(DISCOVERED) || discoveryEvent.was(REDISCOVERED)) {
                    for(MagicMirrorAdapterCallback callback : pendingScanCallback) {
                        callback.onMagicMirrorDiscovered(discoveryEvent.macAddress(), "");
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

    @Override
    public void getMirrorConfig(final MagicMirrorAdapterCallback callback) {
        connectedDevice.read(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MIRROR_CONFIG, new BleDevice.ReadWriteListener() {
            @Override
            public void onEvent(ReadWriteEvent e) {
                if(e.wasSuccess()) {
                    try {
                        JSONObject config = new JSONObject(e.data_string());
                        callback.onGetMirrorConfig(MagicMirrorAdapterCallback.STATUS_SUCCESS, config);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        callback.onGetMirrorConfig(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                    }
                }
                else {
                    callback.onGetMirrorConfig(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                }
            }
        });
    }

    @Override
    public void setMirrorConfig(JSONObject data, final MagicMirrorAdapterCallback callback) {
        connectedDevice.write(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MIRROR_CONFIG, data.toString().getBytes(), new BleDevice.ReadWriteListener() {
            @Override
            public void onEvent(ReadWriteEvent e) {
                callback.onSetMirrorConfig(e.wasSuccess() ? MagicMirrorAdapterCallback.STATUS_SUCCESS : MagicMirrorAdapterCallback.STATUS_ERROR);
            }
        });
    }

    @Override
    public boolean isAllowWifiSetup() {
        return true;
    }

    @Override
    public void getWifiNetworks(final MagicMirrorAdapterCallback callback) {
        connectedDevice.read(SERVICE_MAGICMIRROR_APP_INTERFACE_WIFI, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_WIFI_READ, new BleDevice.ReadWriteListener() {
            @Override
            public void onEvent(ReadWriteEvent e) {
                if(e.wasSuccess()) {

                    List<WifiNetwork> networks = new ArrayList<>();

                    try {
                        String value = GzipUtil.decompress(e.data());

                        JSONObject wrapper = new JSONObject(value);
                        JSONArray availableNetworks = wrapper.getJSONArray(KEY_WIFI_AVAILABLE_NETWORKS);

                        for(int i = 0; i < availableNetworks.length(); i++) {
                            JSONObject network = availableNetworks.getJSONObject(i);
                            networks.add(new WifiNetwork(network.getString(KEY_WIFI_NETWORKS_SSID), network.getString(KEY_WIFI_NETWORKS_MAC)));
                        }

                        if(wrapper.has(KEY_WIFI_STATUS)) {
                            JSONObject status = wrapper.getJSONObject(KEY_WIFI_STATUS);
                            WifiNetwork current = new WifiNetwork(status.getString(KEY_WIFI_SSID), status.getString(KEY_WIFI_MAC));

                            if(networks.contains(current)) {
                                networks.get(networks.indexOf(current)).setConnected(true);
                            }
                        }

                    } catch (JSONException | IOException e1) {
                        e1.printStackTrace();
                        callback.onGetWifiNetworks(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                        return;
                    }

                    callback.onGetWifiNetworks(MagicMirrorAdapterCallback.STATUS_SUCCESS, networks);
                }
                else {
                    callback.onGetWifiNetworks(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                }
            }
        });
    }

    @Override
    public void connectToWifiNetwork(@NonNull String ssid, @NonNull String passphrase, final MagicMirrorAdapterCallback callback) {

        JSONObject payload = new JSONObject();

        try {
            payload.put(KEY_WIFI_SSID, ssid);
            payload.put(KEY_WIFI_PASSPHRASE, passphrase);

            connectedDevice.write(SERVICE_MAGICMIRROR_APP_INTERFACE_WIFI, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_WIFI_WRITE, payload.toString().getBytes(), new BleDevice.ReadWriteListener() {
                @Override
                public void onEvent(ReadWriteEvent e) {
                    callback.onConnectToWifiNetwork(e.wasSuccess() ? MagicMirrorAdapterCallback.STATUS_SUCCESS : MagicMirrorAdapterCallback.STATUS_ERROR);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onConnectToWifiNetwork(MagicMirrorAdapterCallback.STATUS_ERROR);
        }
    }

    @Override
    public void executeQuery(String query, final MagicMirrorAdapterCallback callback) {
        connectedDevice.write(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MIRROR_QUERY, query.getBytes(), new BleDevice.ReadWriteListener() {
            @Override
            public void onEvent(ReadWriteEvent e) {
                callback.onExecuteQuery(e.wasSuccess() ? MagicMirrorAdapterCallback.STATUS_SUCCESS : MagicMirrorAdapterCallback.STATUS_ERROR);
            }
        });
    }

    @Override
    public String getAdapterIdentifier() {
        return "ble";
    }
}
