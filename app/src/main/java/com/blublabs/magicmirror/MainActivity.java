package com.blublabs.magicmirror;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;
import com.blublabs.magicmirror.settings.app.devices.DeviceListFragment;
import com.blublabs.magicmirror.settings.app.devices.HeaderDeviceListAdapter;
import com.blublabs.magicmirror.settings.app.devices.PairedDevicesSpinner;
import com.blublabs.magicmirror.settings.app.general.SettingsFragmentApp;
import com.blublabs.magicmirror.settings.mirror.general.SettingsFragmentMirror;
import com.blublabs.magicmirror.settings.mirror.modules.ModulesFragment;
import com.blublabs.magicmirror.settings.mirror.wifi.WifiSettingsFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private static final String KEY_LAST_SELECTED_ITEM = "lastSelectedItem";

    private DrawerLayout drawer;
    private NavigationView navDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private int selectedMenuItem = -1;

    private ArrayAdapter<String> navHeaderSpinnerDataAdapter;
    private List<String> spinnerDevicesList = new ArrayList<>();
    private PairedDevicesSpinner devicesSpinner;

    private Set<String> pairedDevicesList = new HashSet<>();
    private String defaultPairedDevice = null;

    public IMagicMirrorAdapter.MagicMirrorAdapterCallback deviceStateListener = new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {

        @Override
        public void onConnectedToMirror() {
            runOnUiThread(new Runnable() {
                public void run() {
                    ((TextView) devicesSpinner.findViewById(R.id.connect_status)).setText(getString(R.string.connected));
                    navDrawer.getMenu().findItem(R.id.nav_modules_fragment).setEnabled(true);
                    navDrawer.getMenu().findItem(R.id.nav_settings_general_fragment).setEnabled(true);
                    if(getAdapter().isAllowWifiSetup()) {
                        navDrawer.getMenu().findItem(R.id.nav_settings_wifi_fragment).setEnabled(true);
                    }
                }
            });
        }

        @Override
        public void onDisconnectedFromMirror() {
            runOnUiThread(new Runnable() {
                public void run() {
                    ((TextView) devicesSpinner.findViewById(R.id.connect_status)).setText(getString(R.string.disconnected));
                    navDrawer.getMenu().findItem(R.id.nav_modules_fragment).setEnabled(false);
                    navDrawer.getMenu().findItem(R.id.nav_settings_general_fragment).setEnabled(false);
                    navDrawer.getMenu().findItem(R.id.nav_settings_wifi_fragment).setEnabled(false);
                }});
        }
    };

    public MainActivity() {
        super();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setupNavMenu();

        if(savedInstanceState != null && savedInstanceState.containsKey(KEY_LAST_SELECTED_ITEM)) {
            selectedMenuItem = savedInstanceState.getInt(KEY_LAST_SELECTED_ITEM);
        }
        else {
            onNavigationItemSelected(navDrawer.getMenu().findItem(R.id.nav_main_fragment));
        }

        setupHeader();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        pairedDevicesList.addAll(sharedPref.getStringSet(getAdapter().getAdapterIdentifier() + "_" + getString(R.string.key_pref_paired_devices), new HashSet<String>()));
        defaultPairedDevice = sharedPref.getString(getAdapter().getAdapterIdentifier() + "_" + getString(R.string.key_pref_default_device), null);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(pairedDevicesList.size() == 0) {
            setDeviceSpinnerVisible(View.GONE);
        }
        else {
            navHeaderSpinnerDataAdapter.clear();
            navHeaderSpinnerDataAdapter.addAll(pairedDevicesList);
            navHeaderSpinnerDataAdapter.add(getString(R.string.default_device_list_entry));
            devicesSpinner.setSelection(defaultPairedDevice == null ? 0 : spinnerDevicesList.indexOf(defaultPairedDevice));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getAdapter().onPause();
        getAdapter().disconnectMirror();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAdapter().onResume();
    }

    protected final void setupToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(myToolbar != null) {
            setSupportActionBar(myToolbar);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_LAST_SELECTED_ITEM, selectedMenuItem);
    }

    private void setupNavMenu() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        navDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(navDrawer);
        setupToolbar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupHeader() {
        devicesSpinner = (PairedDevicesSpinner) navDrawer.getHeaderView(0).findViewById(R.id.nav_header_spinner);

        navHeaderSpinnerDataAdapter = new HeaderDeviceListAdapter(this, R.layout.item_device_spinner, spinnerDevicesList);
        navHeaderSpinnerDataAdapter.setDropDownViewResource(R.layout.item_device_spinner_dropdown);

        devicesSpinner.setAdapter(navHeaderSpinnerDataAdapter);
        devicesSpinner.setOnItemSelectedListener(this);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawer, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(this);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        selectItem(menuItem.getItemId());

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());

        return true;
    }

    private void selectItem(int id) {
        if(selectedMenuItem == id) {
            drawer.closeDrawers();
            return;
        }

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(id) {
            case R.id.nav_main_fragment:
                fragmentClass = MainFragment.class;
                break;
            case R.id.nav_modules_fragment:
                fragmentClass = ModulesFragment.class;
                break;
            case R.id.nav_devices_fragment:
                fragmentClass = DeviceListFragment.class;
                break;
            case R.id.nav_settings_wifi_fragment:
                fragmentClass = WifiSettingsFragment.class;
                break;
            case R.id.nav_settings_general_fragment:
                fragmentClass = SettingsFragmentMirror.class;
                break;
            case R.id.nav_settings_App_fragment:
                fragmentClass = SettingsFragmentApp.class;
                break;
            default:
                fragmentClass = MainFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Close the navigation drawer
        drawer.closeDrawers();

        selectedMenuItem = id;
    }

        @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position == spinnerDevicesList.indexOf(getString(R.string.default_device_list_entry))) {
            onNavigationItemSelected(navDrawer.getMenu().findItem(R.id.nav_devices_fragment));
            return;
        }

        setDefaultDevice((String) devicesSpinner.getSelectedItem());

        if(!getAdapter().isConnectedToMirror()) {
            connectToMirror();
        }
    }

    private void connectToMirror() {
        runOnUiThread(new Runnable() {
            public void run() {
                ((TextView) devicesSpinner.findViewById(R.id.connect_status)).setText(getString(R.string.connecting));
            }
        });

        getAdapter().scanForMagicMirrors(new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {

            boolean found = false;

            @Override
            public void onMagicMirrorDiscovered(String identifier, String extra) {
                if(defaultPairedDevice.equals(identifier)) {
                    found = true;
                    getAdapter().stopScanForMagicMirrors();
                    getAdapter().connectMirror(deviceStateListener, defaultPairedDevice);
                }
            }

            @Override
            public void onScanFinished() {
                if(!found) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) devicesSpinner.findViewById(R.id.connect_status)).setText(getString(R.string.disconnected));
                        }
                    });
                }
            }
        }, getApplicationContext());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setDeviceSpinnerVisible(final int visible) {
        runOnUiThread(new Runnable() {
            public void run() {
                devicesSpinner.setVisibility(visible);
            }
        });
    }

    public void addPairedDevice(String deviceAddress) {
        pairedDevicesList.add(deviceAddress);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPref.edit()
                .putStringSet(getAdapter().getAdapterIdentifier() + "_" + getString(R.string.key_pref_paired_devices), pairedDevicesList)
                .apply();

        setDefaultDevice(deviceAddress);

        updateDevices();
    }

    public void updateDevices(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        pairedDevicesList.clear();
        pairedDevicesList.addAll(sharedPref.getStringSet(getAdapter().getAdapterIdentifier() + "_" + getString(R.string.key_pref_paired_devices), new HashSet<String>()));
        defaultPairedDevice = sharedPref.getString(getAdapter().getAdapterIdentifier() + "_" + getString(R.string.key_pref_default_device), null);

        spinnerDevicesList.clear();
        spinnerDevicesList.addAll(pairedDevicesList);
        spinnerDevicesList.add(getString(R.string.default_device_list_entry));
        devicesSpinner.setSelection(spinnerDevicesList.indexOf(defaultPairedDevice));
        setDeviceSpinnerVisible(pairedDevicesList.isEmpty() ? View.GONE : View.VISIBLE);
        navHeaderSpinnerDataAdapter.notifyDataSetChanged();
    }

    public void setDefaultDevice(String deviceAddress) {

        defaultPairedDevice = deviceAddress;
        if(!devicesSpinner.getSelectedItem().equals(defaultPairedDevice)) {
            devicesSpinner.setSelection(spinnerDevicesList.indexOf(defaultPairedDevice));
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPref.edit()
                .putString(getAdapter().getAdapterIdentifier() + "_" + getString(R.string.key_pref_default_device), deviceAddress)
                .apply();
    }

    private IMagicMirrorAdapter getAdapter() {
        return MagicMirrorAdapterFactory.getAdapter(getApplicationContext());
    }
}
