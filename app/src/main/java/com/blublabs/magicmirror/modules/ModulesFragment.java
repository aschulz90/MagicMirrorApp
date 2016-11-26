package com.blublabs.magicmirror.modules;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.databinding.Observable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;
import com.blublabs.magicmirror.common.MagicMirrorFragment;
import com.blublabs.magicmirror.service.BleService;
import com.idevicesinc.sweetblue.BleDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by andrs on 21.09.2016.
 */

public class ModulesFragment extends Fragment {

    private ModuleScrollView moduleScrollView;
    private View progressBar;

    private IMagicMirrorAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_modules, container, false);

        moduleScrollView = (ModuleScrollView) view.findViewById(R.id.card_recycler_view);
        moduleScrollView.setParentFragment(this);
        moduleScrollView.setModuleDataAdapter(getAdapter());

        progressBar = view.findViewById(R.id.progressBar1);

        if(savedInstanceState != null) {

        }

        return view;
    }

    private IMagicMirrorAdapter getAdapter() {
        if(adapter == null) {
            adapter = MagicMirrorAdapterFactory.getAdapter(MagicMirrorAdapterFactory.AdapteryType.BLE, getActivity().getApplicationContext());
        }

        return adapter;
    }
}
