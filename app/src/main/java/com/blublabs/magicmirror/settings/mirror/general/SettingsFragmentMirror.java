package com.blublabs.magicmirror.settings.mirror.general;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrs on 06.12.2016.
 */

public class SettingsFragmentMirror extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    String KEY_PREF_CONFIG_PORT = "";
    String KEY_PREF_CONFIG_KIOSKMODE = "";
    String KEY_PREF_CONFIG_LANGUAGE = "";
    String KEY_PREF_CONFIG_TIMEFORMAT = "";
    String KEY_PREF_CONFIG_UNTIS = "";

    View progressBar = null;
    private boolean settingsLoaded = false;

    private IMagicMirrorAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout view = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);

        if(!settingsLoaded) {
            progressBar = inflater.inflate(R.layout.module_list_progressbar, container, false);
            progressBar.setLayoutParams(new RelativeLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
            progressBar.setVisibility(View.VISIBLE);
            view.addView(progressBar);
        }

        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        KEY_PREF_CONFIG_PORT = getString(R.string.key_pref_mirror_config_port);
        KEY_PREF_CONFIG_KIOSKMODE = getString(R.string.key_pref_mirror_config_kioskmode);
        KEY_PREF_CONFIG_LANGUAGE = getString(R.string.key_pref_mirror_config_language);
        KEY_PREF_CONFIG_TIMEFORMAT = getString(R.string.key_pref_mirror_config_timeFormat);
        KEY_PREF_CONFIG_UNTIS = getString(R.string.key_pref_mirror_config_units);

        getAdapter().getMirrorConfig(new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
            @Override
            public void onGetMirrorConfig(int status, JSONObject config) {
                if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {

                    String port = null, language = null, units = null, timeformat = null;
                    boolean kioskmode = false;

                    try {

                        if(config.has(IMagicMirrorAdapter.KEY_CONFIG_PORT)) {
                            port = config.getString(IMagicMirrorAdapter.KEY_CONFIG_PORT);
                        }
                        if(config.has(IMagicMirrorAdapter.KEY_CONFIG_LANGUAGE)) {
                            language = config.getString(IMagicMirrorAdapter.KEY_CONFIG_LANGUAGE);
                        }
                        if(config.has(IMagicMirrorAdapter.KEY_CONFIG_TIMEFORMAT)) {
                            timeformat = config.getString(IMagicMirrorAdapter.KEY_CONFIG_TIMEFORMAT);
                        }
                        if(config.has(IMagicMirrorAdapter.KEY_CONFIG_UNTIS)) {
                            units = config.getString(IMagicMirrorAdapter.KEY_CONFIG_UNTIS);
                        }
                        if(config.has(IMagicMirrorAdapter.KEY_CONFIG_KIOSKMODE)) {
                            kioskmode = config.getBoolean(IMagicMirrorAdapter.KEY_CONFIG_KIOSKMODE);
                        }

                        getPreferenceManager().getSharedPreferences().edit()
                                .putString(KEY_PREF_CONFIG_PORT, port)
                                .putString(KEY_PREF_CONFIG_LANGUAGE, language)
                                .putString(KEY_PREF_CONFIG_TIMEFORMAT, timeformat)
                                .putString(KEY_PREF_CONFIG_UNTIS, units)
                                .putBoolean(KEY_PREF_CONFIG_KIOSKMODE, kioskmode)
                                .apply();

                        addPreferencesFromResource(R.xml.settings_mirror);
                        settingsLoaded = true;
                        if(progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error while getting mirror config!", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Error while getting mirror config!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        JSONObject config = new JSONObject();

        try {
            config.put(IMagicMirrorAdapter.KEY_CONFIG_PORT, Integer.parseInt(sharedPreferences.getString(KEY_PREF_CONFIG_PORT, "8080")));
            config.put(IMagicMirrorAdapter.KEY_CONFIG_KIOSKMODE, sharedPreferences.getBoolean(KEY_PREF_CONFIG_KIOSKMODE, false));
            config.put(IMagicMirrorAdapter.KEY_CONFIG_TIMEFORMAT, Integer.parseInt(sharedPreferences.getString(KEY_PREF_CONFIG_TIMEFORMAT, "24")));
            config.put(IMagicMirrorAdapter.KEY_CONFIG_UNTIS, sharedPreferences.getString(KEY_PREF_CONFIG_UNTIS, "metric"));
            config.put(IMagicMirrorAdapter.KEY_CONFIG_LANGUAGE, sharedPreferences.getString(KEY_PREF_CONFIG_LANGUAGE, "en"));

            getAdapter().setMirrorConfig(config, new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                @Override
                public void onSetMirrorConfig(int status) {
                    if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_ERROR) {
                        Toast.makeText(getContext(), "Error while setting mirror config!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private IMagicMirrorAdapter getAdapter() {
        if(adapter == null) {
            adapter = MagicMirrorAdapterFactory.getAdapter(getActivity().getApplicationContext());
        }

        return adapter;
    }
}
