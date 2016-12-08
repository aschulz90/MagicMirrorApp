package com.blublabs.magicmirror.settings.app.devices;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blublabs.magicmirror.R;

import java.util.List;

/**
 * Created by andrs on 24.09.2016.
 */

public class HeaderDeviceListAdapter extends ArrayAdapter {

    public HeaderDeviceListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_spinner, parent, false);

        TextView label=(TextView) view.findViewById(R.id.device_spinner_text);
        label.setText((String) getItem(position));

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_spinner_dropdown, parent, false);

        TextView label=(TextView) view.findViewById(R.id.device_spinner_dropdown_text);
        label.setText((String) getItem(position));

        return view;
    }

}
