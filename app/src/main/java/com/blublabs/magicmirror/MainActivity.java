package com.blublabs.magicmirror;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blublabs.magicmirror.common.MagicMirrorActivity;
import com.blublabs.magicmirror.common.MagicMirrorFragment;
import com.blublabs.magicmirror.devices.DeviceListFragment;
import com.blublabs.magicmirror.devices.HeaderDeviceListAdapter;
import com.blublabs.magicmirror.devices.PairedDevicesSpinner;
import com.blublabs.magicmirror.modules.ModuleFragment;
import com.blublabs.magicmirror.service.BleService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends MagicMirrorActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private static final String KEY_LAST_SELECTED_ITEM = "lastSelectedItem";

    private DrawerLayout drawer;
    private NavigationView navDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private int selectedMenuItem = -1;

    private ArrayAdapter<String> navHeaderSpinnerDataAdapter;
    private List<String> spinnerDevicesList = new ArrayList<>();
    private PairedDevicesSpinner devicesSpinner;

    public MainActivity() {
        super();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavMenu();

        if(savedInstanceState != null && savedInstanceState.containsKey(KEY_LAST_SELECTED_ITEM)) {
            selectedMenuItem = savedInstanceState.getInt(KEY_LAST_SELECTED_ITEM);
        }
        else {
            onNavigationItemSelected(navDrawer.getMenu().findItem(R.id.nav_main_fragment));
        }

        setupHeader();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(getPairedDevicesList().size() == 0) {
            setDeviceSpinnerVisible(View.GONE);
        }
        else {
            navHeaderSpinnerDataAdapter.clear();
            navHeaderSpinnerDataAdapter.addAll(getPairedDevicesList());
            navHeaderSpinnerDataAdapter.add(getString(R.string.default_device_list_entry));
            devicesSpinner.setSelection(getDefaultPairedDevice() == null ? 0 : spinnerDevicesList.indexOf(getDefaultPairedDevice()));
        }
    }

    @Override
    protected void onBleServiceConnected() {
        Message msg = Message.obtain(null, BleService.MSG_START_SCAN);
        if (msg != null) {
            sendMessage(msg);
        }
    }

    @Override
    protected void onDeviceDiscovered(String[] devices) {

        if(selectedMenuItem == R.id.nav_settings_fragment) {
            return;
        }

        String defaultDevice = getDefaultPairedDevice();

        if(defaultDevice != null && Arrays.asList(devices).contains(defaultDevice)) {
            connectToDevice(defaultDevice);
            Message msg = Message.obtain(null, BleService.MSG_STOP_SCAN);
            if (msg != null) {
                sendMessage(msg);
            }
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
        MagicMirrorFragment fragment = null;
        Class fragmentClass;
        switch(id) {
            case R.id.nav_main_fragment:
                fragmentClass = MainFragment.class;
                break;
            case R.id.nav_modules_fragment:
                fragmentClass = ModuleFragment.class;
                break;
            case R.id.nav_settings_fragment:
                fragmentClass = DeviceListFragment.class;
                break;
            default:
                fragmentClass = MainFragment.class;
        }

        try {
            fragment = (MagicMirrorFragment) fragmentClass.newInstance();
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
        else {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void addPairedDevice(String deviceAddress) {
        super.addPairedDevice(deviceAddress);

        spinnerDevicesList.clear();
        spinnerDevicesList.addAll(getPairedDevicesList());
        spinnerDevicesList.add(getString(R.string.default_device_list_entry));
        devicesSpinner.setSelection(spinnerDevicesList.indexOf(deviceAddress));
        setDeviceSpinnerVisible(View.VISIBLE);
        navHeaderSpinnerDataAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStateChanged(BleService.State newState) {
        super.onStateChanged(newState);

        switch (newState) {
            case CONNECTED:
                setConnectStatusText(getString(R.string.connected));
                break;
            case IDLE:
                setConnectStatusText(getString(R.string.disconnected));
                break;
            default:
                break;
        }
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
}
