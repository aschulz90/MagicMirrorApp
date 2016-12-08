package com.blublabs.magicmirror.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.blublabs.magicmirror.settings.mirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.wifi.WifiNetwork;
import com.idevicesinc.sweetblue.BleDevice;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by andrs on 08.11.2016.
 */

public interface IMagicMirrorAdapter {

    String KEY_DEFAULT_MODULES = "default";
    String KEY_CUSTOM_MODULES = "custom";

    String KEY_WIFI_STATUS = "status";
    String KEY_WIFI_AVAILABLE_NETWORKS = "availableNetworks";
    String KEY_WIFI_NETWORKS_SSID = "ssid";
    String KEY_WIFI_NETWORKS_MAC = "address";
    String KEY_WIFI_MAC = "bssid";
    String KEY_WIFI_SSID = "ssid";
    String KEY_WIFI_PASSPHRASE = "passphrase";

    void getModuleData(int index, MagicMirrorAdapterCallback callback);

    void setModuleData(int index, String data, MagicMirrorAdapterCallback callback);

    void getModuleList(MagicMirrorAdapterCallback callback);

    void getInstalledModuleList(MagicMirrorAdapterCallback callback);

    void addModule(JSONObject module, MagicMirrorAdapterCallback callback);

    void removeModule(int index, MagicMirrorAdapterCallback callback);

    void connectMirror(MagicMirrorAdapterCallback callback, String identifier);

    void disconnectMirror();

    void scanForMagicMirrors(MagicMirrorAdapterCallback callback, @NonNull Context context);

    void stopScanForMagicMirrors();

    boolean isConnectedToMirror();

    void onResume();

    void onPause();

    void getMirrorConfig(MagicMirrorAdapterCallback callback);

    void setMirrorConfig(String data, MagicMirrorAdapterCallback callback);

    boolean isAllowWifiSetup();

    void getWifiNetworks(MagicMirrorAdapterCallback callback);

    void connectToWifiNetwork(String ssid, String passphrase, MagicMirrorAdapterCallback callback);

    String getAdapterIdentifier();

    class MagicMirrorAdapterCallback {

        protected static final int STATUS_SUCCESS = 0;
        protected static final int STATUS_ERROR = 1;

        public void onGetModuleData(int status, JSONObject data){

        }

        public void onSetModuleData(int status) {

        }

        public void onGetModuleList(int status, List<MagicMirrorModule> modules) {

        }

        public void onGetInstalledModuleList(int status, Map<String, List<String>> installedModules) {

        }

        public void onAddModule(int status){

        }

        public void onRemoveModule(int status){

        }

        public void onConnectedToMirror() {

        }

        public void onDisconnectedFromMirror() {

        }

        public void onMagicMirrorDiscovered(String identifier, String extra) {

        }

        public void onScanFinished() {

        }

        public void onGetWifiNetworks(int status, List<WifiNetwork> networks) {

        }

        public void onConnectToWifiNetwork(int status) {

        }

        public void onGetMirrorConfig(int status, JSONObject config) {

        }

        public void onSetMirrorConfig(int status) {

        }
    }

}
