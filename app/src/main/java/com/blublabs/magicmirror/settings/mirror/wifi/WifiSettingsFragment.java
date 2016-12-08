package com.blublabs.magicmirror.settings.mirror.wifi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;
import com.blublabs.magicmirror.common.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrs on 05.12.2016.
 */

public class WifiSettingsFragment extends Fragment {

    private List<WifiNetwork> wifiList = new ArrayList<>();
    private WifiListAdapter wifiListAdapter;
    private RecyclerView recyclerView;

    private IMagicMirrorAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_settings_wifi, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.wifi_recycler_view);

        wifiListAdapter = new WifiListAdapter(wifiList, getActivity(), this, recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(wifiListAdapter);

        if(getAdapter().isAllowWifiSetup()) {
            getAdapter().getWifiNetworks(new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                @Override
                public void onGetWifiNetworks(int status, List<WifiNetwork> networks) {
                    if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {
                        view.findViewById(R.id.progress_circle).setVisibility(View.GONE);
                        wifiList.addAll(networks);
                        wifiListAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(getContext(), "Error while getting available wifi networks ", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            view.findViewById(R.id.progress_circle).setVisibility(View.GONE);
        }

        return view;
    }

    public IMagicMirrorAdapter getAdapter() {
        if(adapter == null) {
            adapter = MagicMirrorAdapterFactory.getAdapter(getActivity().getApplicationContext());
        }

        return adapter;
    }
}
