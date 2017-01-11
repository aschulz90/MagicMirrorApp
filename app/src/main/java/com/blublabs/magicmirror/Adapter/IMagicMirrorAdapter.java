package com.blublabs.magicmirror.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.blublabs.magicmirror.settings.mirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.wifi.WifiNetwork;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by andrs on 08.11.2016.
 */

public interface IMagicMirrorAdapter {

    String KEY_DEFAULT_MODULES = "default";
    String KEY_CUSTOM_MODULES = "custom";
    String KEY_CONFIG_MODULES = "modules";
    String KEY_CONFIG_PORT = "port";
    String KEY_CONFIG_KIOSKMODE = "kioskmode";
    String KEY_CONFIG_LANGUAGE = "language";
    String KEY_CONFIG_TIMEFORMAT = "timeFormat";
    String KEY_CONFIG_UNTIS = "units";

    void getModuleData(int index, MagicMirrorAdapterCallback callback);

    void setModuleData(int index, JSONObject data, MagicMirrorAdapterCallback callback);

    void getModuleList(MagicMirrorAdapterCallback callback);

    void getInstalledModuleList(MagicMirrorAdapterCallback callback);

    void addModule(JSONObject module, MagicMirrorAdapterCallback callback);

    void removeModule(int index, MagicMirrorAdapterCallback callback);

    void connectMirror(MagicMirrorAdapterCallback callback, String identifier, @NonNull Context context);

    void disconnectMirror();

    void scanForMagicMirrors(MagicMirrorAdapterCallback callback, @NonNull Context context);

    void stopScanForMagicMirrors();

    boolean isConnectedToMirror();

    void onResume();

    void onPause();

    void getMirrorConfig(MagicMirrorAdapterCallback callback);

    void setMirrorConfig(JSONObject data, MagicMirrorAdapterCallback callback);

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

        public void onConnectionError() {

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
