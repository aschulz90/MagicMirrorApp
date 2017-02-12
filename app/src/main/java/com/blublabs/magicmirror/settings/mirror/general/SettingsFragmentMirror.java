package com.blublabs.magicmirror.settings.mirror.general;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;
import com.blublabs.magicmirror.common.QueryDialogPreference;
import com.blublabs.magicmirror.common.QueryPreferenceDialogFragmentCompat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrs on 06.12.2016.
 */

public class SettingsFragmentMirror extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String KEY_PREF_CONFIG_ADDRESS;
    private String KEY_PREF_CONFIG_PORT;
    private String KEY_PREF_CONFIG_KIOSKMODE;
    private String KEY_PREF_CONFIG_LANGUAGE;
    private String KEY_PREF_CONFIG_TIMEFORMAT;
    private String KEY_PREF_CONFIG_UNITS;

    private View progressBar = null;
    CoordinatorLayout coordinatorLayout = null;
    private boolean settingsLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        KEY_PREF_CONFIG_ADDRESS = getString(R.string.key_pref_mirror_config_address);
        KEY_PREF_CONFIG_PORT = getString(R.string.key_pref_mirror_config_port);
        KEY_PREF_CONFIG_KIOSKMODE = getString(R.string.key_pref_mirror_config_kioskmode);
        KEY_PREF_CONFIG_LANGUAGE = getString(R.string.key_pref_mirror_config_language);
        KEY_PREF_CONFIG_TIMEFORMAT = getString(R.string.key_pref_mirror_config_timeFormat);
        KEY_PREF_CONFIG_UNITS = getString(R.string.key_pref_mirror_config_units);

        LinearLayout view = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);

        coordinatorLayout = new CoordinatorLayout(getContext());
        coordinatorLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
        //view.addView(coordinatorLayout);

        if(!settingsLoaded) {
            progressBar = inflater.inflate(R.layout.module_list_progressbar, container, false);
            progressBar.setLayoutParams(new RelativeLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
            progressBar.setVisibility(View.VISIBLE);
            view.addView(progressBar);
        }

        return view;
    }

    private boolean needsRefresh(String key) {
        return KEY_PREF_CONFIG_UNITS.equals(key) || KEY_PREF_CONFIG_TIMEFORMAT.equals(key) || KEY_PREF_CONFIG_LANGUAGE.equals(key);
    }

    private void showRefreshMagicMirrorBar() {

        Snackbar.make(coordinatorLayout, "MagicMirror requires a refresh", Snackbar.LENGTH_INDEFINITE)
                .setAction("Refresh now", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getAdapter().executeQuery("REFRESH", new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                            @Override
                            public void onExecuteQuery(int status) {
                                if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {
                                    Snackbar.make(coordinatorLayout, "MagicMirror is being refreshed!!", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .show();
    }

    private boolean needsRestart(String key) {
        return KEY_PREF_CONFIG_ADDRESS.equals(key) || KEY_PREF_CONFIG_PORT.equals(key) || KEY_PREF_CONFIG_KIOSKMODE.equals(key);
    }

    private void showRestartMirrorBar() {

        Snackbar.make(coordinatorLayout, "MagicMirror requires a restart", Snackbar.LENGTH_INDEFINITE)
                .setAction("Refresh now", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getAdapter().executeQuery("RESTART", new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                            @Override
                            public void onExecuteQuery(int status) {
                                if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {
                                    Snackbar.make(coordinatorLayout, "MagicMirror is being refreshed!!", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .show();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        getAdapter().getMirrorConfig(new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
            @Override
            public void onGetMirrorConfig(int status, JSONObject config) {
                if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {

                    String address = null, port = null, language = null, units = null, timeformat = null;
                    boolean kioskmode = false;

                    try {

                        if(config.has(IMagicMirrorAdapter.KEY_CONFIG_ADDRESS)) {
                            address = config.getString(IMagicMirrorAdapter.KEY_CONFIG_ADDRESS);
                        }
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
                        if(config.has(IMagicMirrorAdapter.KEY_CONFIG_ELECTRON_OPTIONS) && config.getJSONObject(IMagicMirrorAdapter.KEY_CONFIG_ELECTRON_OPTIONS).has(IMagicMirrorAdapter.KEY_CONFIG_KIOSKMODE)) {
                            kioskmode = config.getJSONObject(IMagicMirrorAdapter.KEY_CONFIG_ELECTRON_OPTIONS).getBoolean(IMagicMirrorAdapter.KEY_CONFIG_KIOSKMODE);
                        }

                        getPreferenceManager().getSharedPreferences().edit()
                                .putString(KEY_PREF_CONFIG_ADDRESS, address)
                                .putString(KEY_PREF_CONFIG_PORT, port)
                                .putString(KEY_PREF_CONFIG_LANGUAGE, language)
                                .putString(KEY_PREF_CONFIG_TIMEFORMAT, timeformat)
                                .putString(KEY_PREF_CONFIG_UNITS, units)
                                .putBoolean(KEY_PREF_CONFIG_KIOSKMODE, kioskmode)
                                .apply();

                        addPreferencesFromResource(R.xml.settings_mirror);
                        settingsLoaded = true;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error while getting mirror config!", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Error while getting mirror config!", Toast.LENGTH_LONG).show();
                }

                if(progressBar != null) {
                    progressBar.setVisibility(View.GONE);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, final String key) {

        JSONObject config = new JSONObject();

        try {
            config.put(IMagicMirrorAdapter.KEY_CONFIG_ADDRESS, sharedPreferences.getString(KEY_PREF_CONFIG_ADDRESS, null));
            config.put(IMagicMirrorAdapter.KEY_CONFIG_PORT, Integer.parseInt(sharedPreferences.getString(KEY_PREF_CONFIG_PORT, "8080")));
            config.put(IMagicMirrorAdapter.KEY_CONFIG_TIMEFORMAT, Integer.parseInt(sharedPreferences.getString(KEY_PREF_CONFIG_TIMEFORMAT, "24")));
            config.put(IMagicMirrorAdapter.KEY_CONFIG_UNTIS, sharedPreferences.getString(KEY_PREF_CONFIG_UNITS, "metric"));
            config.put(IMagicMirrorAdapter.KEY_CONFIG_LANGUAGE, sharedPreferences.getString(KEY_PREF_CONFIG_LANGUAGE, "en"));
            if(sharedPreferences.getBoolean(KEY_PREF_CONFIG_KIOSKMODE, false)) {
                JSONObject electronOptions = new JSONObject();
                electronOptions.put(IMagicMirrorAdapter.KEY_CONFIG_KIOSKMODE, true);
                config.put(IMagicMirrorAdapter.KEY_CONFIG_ELECTRON_OPTIONS, electronOptions);
            }

            getAdapter().setMirrorConfig(config, new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                @Override
                public void onSetMirrorConfig(int status) {
                    if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_ERROR) {
                        Toast.makeText(getContext(), "Error while setting mirror config!", Toast.LENGTH_LONG).show();
                    }
                    else {

                        if (needsRefresh(key)) {
                            showRefreshMagicMirrorBar();
                        } else if (needsRestart(key)) {
                            showRestartMirrorBar();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private IMagicMirrorAdapter getAdapter() {
        return MagicMirrorAdapterFactory.getAdapter(getActivity().getApplicationContext());
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;
        if (preference instanceof QueryDialogPreference)
        {
            dialogFragment = new QueryPreferenceDialogFragmentCompat();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);
        }

        if (dialogFragment != null)
        {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        }
        else
        {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
