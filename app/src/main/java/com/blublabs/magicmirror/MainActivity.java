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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;
import com.blublabs.magicmirror.common.MagicMirrorFragment;
import com.blublabs.magicmirror.devices.DeviceListFragment;
import com.blublabs.magicmirror.devices.HeaderDeviceListAdapter;
import com.blublabs.magicmirror.devices.PairedDevicesSpinner;
import com.blublabs.magicmirror.modules.ModulesFragment;
import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private static final String KEY_LAST_SELECTED_ITEM = "lastSelectedItem";
    private static final String KEY_PAIRED_DEVICES = "pairedDevices";
    private static final String KEY_DEFAULT_DEVICE = "defaultDevices";

    private DrawerLayout drawer;
    private NavigationView navDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private int selectedMenuItem = -1;

    private ArrayAdapter<String> navHeaderSpinnerDataAdapter;
    private List<String> spinnerDevicesList = new ArrayList<>();
    private PairedDevicesSpinner devicesSpinner;

    private Set<String> pairedDevicesList = new HashSet<>();
    private String defaultPairedDevice = null;

    private IMagicMirrorAdapter adapter = null;

    private BleDevice.StateListener deviceStateListener = new BleDevice.StateListener() {
        @Override
        public void onEvent(BleDevice.StateListener.StateEvent stateEvent) {
            if(stateEvent.didEnter(BleDeviceState.CONNECTED)) {
                setConnectStatusText(getString(R.string.connected));
            }
            else if(stateEvent.didEnter(BleDeviceState.DISCONNECTED)) {
                setConnectStatusText(getString(R.string.disconnected));
            }
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

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        pairedDevicesList.addAll(sharedPref.getStringSet(KEY_PAIRED_DEVICES, new HashSet<String>()));
        defaultPairedDevice = sharedPref.getString(KEY_DEFAULT_DEVICE, null);
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
            case R.id.nav_settings_fragment:
                fragmentClass = DeviceListFragment.class;
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
            onNavigationItemSelected(navDrawer.getMenu().findItem(R.id.nav_settings_fragment));
            setDeviceSpinnerVisible(View.GONE);
        }
        else if(!getAdapter().isConnectedToMirror()) {

            setDefaultDevice((String) devicesSpinner.getSelectedItem());

            getAdapter().scanForMagicMirrors(new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                @Override
                public void onMagicMirrorDiscovered(Object identifier) {
                    if(defaultPairedDevice.equals(identifier)) {
                        adapter.stopScanForMagicMirrors();
                        adapter.connectMirror(deviceStateListener, defaultPairedDevice);
                    }
                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setConnectStatusText(final String newText) {
        runOnUiThread(new Runnable() {
            public void run() {
                ((TextView) devicesSpinner.findViewById(R.id.connect_status)).setText(newText);
            }
        });
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

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        sharedPref.edit()
                .putStringSet(KEY_PAIRED_DEVICES, pairedDevicesList)
                .apply();

        setDefaultDevice(deviceAddress);

        spinnerDevicesList.clear();
        spinnerDevicesList.addAll(pairedDevicesList);
        spinnerDevicesList.add(getString(R.string.default_device_list_entry));
        devicesSpinner.setSelection(spinnerDevicesList.indexOf(deviceAddress));
        setDeviceSpinnerVisible(View.VISIBLE);
        navHeaderSpinnerDataAdapter.notifyDataSetChanged();
    }

    public void setDefaultDevice(String deviceAddress) {

        defaultPairedDevice = deviceAddress;

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        sharedPref.edit()
                .putString(KEY_DEFAULT_DEVICE, deviceAddress)
                .apply();
    }

    private IMagicMirrorAdapter getAdapter() {
        if(adapter == null) {
            adapter = MagicMirrorAdapterFactory.getAdapter(MagicMirrorAdapterFactory.AdapteryType.BLE, getApplicationContext());
        }

        return adapter;
    }
}
