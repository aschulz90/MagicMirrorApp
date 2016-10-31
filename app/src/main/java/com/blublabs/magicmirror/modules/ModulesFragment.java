package com.blublabs.magicmirror.modules;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.databinding.Observable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.MagicMirrorFragment;
import com.blublabs.magicmirror.common.MyCustomLayoutManager;
import com.blublabs.magicmirror.common.Utils;
import com.blublabs.magicmirror.service.BleService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by andrs on 21.09.2016.
 */

public class ModulesFragment extends MagicMirrorFragment {

    private Map<String,String> moduleUuidMap = new HashMap<>();
    private ModuleScrollView moduleScrollView;
    private View progressBar;

    private Handler delayHandler = new Handler();
    private static final int UPDATE_DELAY = 2000;

    private static final String KEY_MODULE_LIST = "module list";

    private static final UUID SERVICE_MAGICMIRROR_APP_INTERFACE = UUID.fromString("0000280F-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE_LIST = UUID.fromString("000038cd-0000-1000-8000-00805f9b34fb");
    private static final String CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE = "-0000-1000-8000-00805f9b34fb";

    private Observable.OnPropertyChangedCallback moduleChangedCallback =  new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {

            if(observable instanceof MagicMirrorModule) {
                final MagicMirrorModule module = (MagicMirrorModule) observable;

                if(module.getUpdateHandler() == null) {
                    module.setUpdateHandler(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                UUID characteristicUuid = UUID.fromString(Utils.padLeft(moduleUuidMap.get(module.getName()), 8, '0') + CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE);
                                writeCharacteristic(SERVICE_MAGICMIRROR_APP_INTERFACE, characteristicUuid, module.toJson().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            finally {
                                module.setUpdateHandler(null);
                            }
                        }
                    });
                }
                else {
                    delayHandler.removeCallbacks(module.getUpdateHandler());
                }

                delayHandler.postDelayed(module.getUpdateHandler(), UPDATE_DELAY);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_modules, container, false);

        moduleScrollView = (ModuleScrollView) view.findViewById(R.id.card_recycler_view);
        moduleScrollView.setParentFragment(this);

        progressBar = view.findViewById(R.id.progressBar1);

        if(savedInstanceState != null) {
            ArrayList<MagicMirrorModule> moduleList = savedInstanceState.getParcelableArrayList(KEY_MODULE_LIST);
            for(MagicMirrorModule module : moduleList) {
                module.addOnPropertyChangedCallback(moduleChangedCallback);
                moduleScrollView.addModule(module);
            }
        }
        else if(getState() == BleService.State.CONNECTED) {
            readCharacteristic(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE_LIST);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_MODULE_LIST, moduleScrollView.getModuleList());
    }

    @Override
    protected void onStateChanged(BleService.State newState) {
        super.onStateChanged(newState);

        switch (newState) {
            case CONNECTED:
                if(moduleScrollView.getModuleList().isEmpty()) {
                    readCharacteristic(SERVICE_MAGICMIRROR_APP_INTERFACE, CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE_LIST);
                }
                break;
            case IDLE:
                break;
        }
    }

    @Override
    protected void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status) {
        if(status == BluetoothGatt.GATT_SUCCESS) {
            if(CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE_LIST.equals(characteristic.getUuid())) {
                try {
                    JSONObject moduleList = new JSONObject(characteristic.getStringValue(0));

                    Iterator<?> keys = moduleList.keys();

                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                        String uuid = moduleList.getString(key);
                        moduleUuidMap.put(key,uuid);
                        readCharacteristic(SERVICE_MAGICMIRROR_APP_INTERFACE, UUID.fromString(Utils.padLeft(uuid, 8, '0') + CHARACTERISTIC_MAGICMIRROR_APP_INTERFACE_MODULE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    final MagicMirrorModule module = MagicMirrorModule.getModuleForData(new JSONObject(characteristic.getStringValue(0)));

                    if(module != null) {

                        module.addOnPropertyChangedCallback(moduleChangedCallback);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                moduleScrollView.addModule(module);
                                if(moduleUuidMap.size() == moduleScrollView.getModuleList().size()) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) {

    }
}
