package com.blublabs.magicmirror.settings.app.devices;

import android.os.Bundle;
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
import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.DividerItemDecoration;
import com.blublabs.magicmirror.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrs on 19.09.2016.
 */
public class DeviceListFragment extends Fragment {

    static final String EXTRA_SEPARATOR = " - ";

    private final List<String> deviceList = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter;
    private Menu toolbarMenu;

    private boolean isScanning = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_device_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.device_recycler_view);

        deviceListAdapter = new DeviceListAdapter(deviceList, getActivity());
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
                    public void onMagicMirrorDiscovered(String identifier, String extra) {
                        updateDevices(identifier, extra);
                    }

                    @Override
                    public void onScanFinished() {
                        isScanning = false;
                        setButtonText("Scan");

                        getView().findViewById(R.id.progress_circle).setVisibility(View.GONE);
                    }
                }, getActivity().getApplicationContext());
                getView().findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
            }
            else {

                getAdapter().stopScanForMagicMirrors();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateDevices(final String device, final String extra) {
        if(!deviceList.contains(device + EXTRA_SEPARATOR + extra) && !deviceList.contains(device)) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(Utils.isEmpty(extra)) {
                        deviceList.add(device);
                    }
                    else {
                        deviceList.add(device + EXTRA_SEPARATOR + extra);
                    }
                    deviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void setButtonText(final String newText) {

        if(getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                (toolbarMenu.findItem(R.id.action_scan)).setTitle(newText);
            }
        });
    }

    private IMagicMirrorAdapter getAdapter() {
        return MagicMirrorAdapterFactory.getAdapter(getActivity().getApplicationContext());
    }
}
