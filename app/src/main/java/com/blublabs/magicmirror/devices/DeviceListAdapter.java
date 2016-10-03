package com.blublabs.magicmirror.devices;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blublabs.magicmirror.MainActivity;
import com.blublabs.magicmirror.R;

import java.util.List;

/**
 * Created by andrs on 18.09.2016.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.MyViewHolder> {

    private List<String> deviceList;
    private final Context mAppContext;
    private final DeviceListFragment fragment;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView deviceAddress;

        public MyViewHolder(View view) {
            super(view);
            deviceAddress = (TextView) view.findViewById(R.id.macAdress);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            MainActivity activity = (MainActivity) mAppContext;
            activity.addPairedDevice(deviceAddress.getText().toString());
        }
    }

    public DeviceListAdapter(List<String> deviceList, Context applicationContext, DeviceListFragment fragment) {
        this.deviceList = deviceList;
        this.mAppContext = applicationContext;
        this.fragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_device, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String macAdress = deviceList.get(position);
        holder.deviceAddress.setText(macAdress);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

}
