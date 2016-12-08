package com.blublabs.magicmirror.settings.app.general;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.blublabs.magicmirror.MainActivity;
import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;

/**
 * Created by andrs on 07.12.2016.
 */

public class SettingsFragmentApp extends PreferenceFragmentCompat {

    private IMagicMirrorAdapter adapter = null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_app);

        Preference devicePref = findPreference(getString(R.string.key_pref_default_device));
        final MainActivity activity = (MainActivity) getActivity();

        devicePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                final String id = (String) newValue;

                activity.setDefaultDevice(id);

                if(getAdapter().isConnectedToMirror()) {

                    getAdapter().disconnectMirror();
                }

                return true;
            }
        });

        Preference adapterPref = findPreference(getString(R.string.key_pref_app_adapter));

        adapterPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if(getAdapter().isConnectedToMirror()) {

                    getAdapter().disconnectMirror();
                }

                PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext())
                        .edit()
                        .putString(activity.getString(R.string.key_pref_app_adapter), (String) newValue)
                        .apply();

                activity.updateDevices();

                return true;
            }
        });
    }

    private IMagicMirrorAdapter getAdapter() {
        return MagicMirrorAdapterFactory.getAdapter(getActivity().getApplicationContext());
    }
}
