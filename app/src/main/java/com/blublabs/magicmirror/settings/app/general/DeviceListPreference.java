package com.blublabs.magicmirror.settings.app.general;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceManager;
import android.util.AttributeSet;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by andrs on 07.12.2016.
 */

public class DeviceListPreference extends ListPreference {

    public DeviceListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        ArrayList<String> devices = new ArrayList<>();
        devices.addAll(preferences.getStringSet(getAdapter().getAdapterIdentifier() + "_" + context.getString(R.string.key_pref_paired_devices), new HashSet<String>()));

        setEntries(devices.toArray(new CharSequence[devices.size()]));
        setEntryValues(devices.toArray(new CharSequence[devices.size()]));
        int index = devices.indexOf(preferences.getString(getAdapter().getAdapterIdentifier() + "_" + context.getString(R.string.key_pref_default_device), ""));
        if(index >= 0) {
            setValueIndex(index);
        }
    }

    public DeviceListPreference(Context context) {
        super(context);
    }

    private IMagicMirrorAdapter getAdapter() {
        return MagicMirrorAdapterFactory.getAdapter(getContext().getApplicationContext());
    }

}
