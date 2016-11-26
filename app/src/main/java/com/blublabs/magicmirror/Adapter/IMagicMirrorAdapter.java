package com.blublabs.magicmirror.adapter;

import com.blublabs.magicmirror.modules.MagicMirrorModule;
import com.idevicesinc.sweetblue.BleDevice;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by andrs on 08.11.2016.
 */

public interface IMagicMirrorAdapter {

    void getModuleData(int index, MagicMirrorAdapterCallback callback);

    void setModuleData(int index, String data, MagicMirrorAdapterCallback callback);

    void getModuleList(MagicMirrorAdapterCallback callback);

    void removeModule(int index, MagicMirrorAdapterCallback callback);

    void connectMirror(BleDevice.StateListener stateListener, Object mirror);

    void disconnectMirror();

    void scanForMagicMirrors(MagicMirrorAdapterCallback callback);

    void stopScanForMagicMirrors();

    boolean isConnectedToMirror();

    void onResume();

    void onPause();

    class MagicMirrorAdapterCallback {

        protected static final int STATUS_SUCCESS = 0;
        protected static final int STATUS_ERROR = 1;

        public void onGetModuleData(int status, JSONObject data){

        }

        public void onSetModuleData(int status) {

        }

        public void onGetModuleList(int status, List<MagicMirrorModule> modules) {

        }

        public void onRemoveModule(int status){

        }

        public void onMagicMirrorDiscovered(Object identifier) {

        }

        public void onScanFinished() {

        }
    }

}
