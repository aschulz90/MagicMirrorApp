package com.blublabs.magicmirror.devices;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;
import com.blublabs.magicmirror.service.BleService;
import com.blublabs.magicmirror.common.MagicMirrorFragment;
import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by andrs on 19.09.2016.
 */
public class DeviceListFragment extends Fragment {

    private List<String> deviceList = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter;
    private RecyclerView recyclerView;
    private Menu toolbarMenu;

    private boolean isScanning = false;

    private IMagicMirrorAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_device_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.device_recycler_view);

        deviceListAdapter = new DeviceListAdapter(deviceList, getActivity(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(deviceListAdapter);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_devices, menu);

        toolbarMenu = menu;

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_scan) {
            if(!isScanning) {

                isScanning = true;
                setButtonText("Stop");
                deviceList.clear();
                deviceListAdapter.notifyDataSetChanged();
                getAdapter().scanForMagicMirrors(new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                    @Override
                    public void onMagicMirrorDiscovered(Object identifier) {
                        updateDevices((String) identifier);
                    }

                    @Override
                    public void onScanFinished() {
                        isScanning = false;
                        setButtonText("Scan");

                        getView().findViewById(R.id.progress_circle).setVisibility(View.GONE);
                    }
                });
                getView().findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
            }
            else {

                getAdapter().stopScanForMagicMirrors();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onScanStopped(String[] devices) {

    }

    private void updateDevices(String device) {
        if(!deviceList.contains(device)) {
            deviceList.add(device);
            deviceListAdapter.notifyDataSetChanged();
        }
    }

    private void setButtonText(final String newText) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                (toolbarMenu.findItem(R.id.action_scan)).setTitle(newText);
            }
        });
    }

    private IMagicMirrorAdapter getAdapter() {
        if(adapter == null) {
            adapter = MagicMirrorAdapterFactory.getAdapter(MagicMirrorAdapterFactory.AdapteryType.BLE, getActivity().getApplicationContext());
        }

        return adapter;
    }
}
