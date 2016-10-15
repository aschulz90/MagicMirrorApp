package com.blublabs.magicmirror.common;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.service.BleRequest;
import com.blublabs.magicmirror.service.BleService;
import com.blublabs.magicmirror.utils.GzipUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by andrs on 16.09.2016.
 *
 */
public abstract class MagicMirrorActivity extends AppCompatActivity {

    private static final String KEY_PAIRED_DEVICES = "pairedDevices";
    private static final String KEY_DEFAULT_DEVICE = "defaultDevices";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int ENABLE_BT_REQUEST_CODE = 2;

    public static final String TAG = "BluetoothLE";

    private final Messenger messenger;
    private Intent serviceIntent;
    private Messenger service = null;
    private BleService.State state = BleService.State.UNKNOWN;
    private ServiceConnection connection =
            new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {

                    MagicMirrorActivity.this.service = new Messenger(service);
                    try {
                        Message msg = Message.obtain(null,
                                BleService.MSG_REGISTER);
                        if (msg != null) {
                            msg.replyTo = messenger;
                            MagicMirrorActivity.this.service.send(msg);
                            onBleServiceConnected();
                        } else {
                            MagicMirrorActivity.this.service = null;
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Error connecting to BleService", e);
                        MagicMirrorActivity.this.service = null;
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    onBleServiceDisconnected();
                    MagicMirrorActivity.this.service = null;
                }
            };

    private Set<String> pairedDevicesList = new HashSet<>();
    private String defaultPairedDevice = null;

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            MagicMirrorActivity.this.onConnectionStateChange(status, newState);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            MagicMirrorActivity.this.onCharacteristicRead(characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            MagicMirrorActivity.this.onCharacteristicWrite(characteristic, status);
        }
    };

    public MagicMirrorActivity() {
        super();

        messenger = new Messenger(new IncomingHandler(this));
    }

    public void onConnectionStateChange(int status, int newState) {

    }

    public void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status) {

    }

    public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status) {

    }

    protected void onBleServiceConnected() {

    }

    protected void onBleServiceDisconnected() {

    }

    protected final void setupToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(myToolbar != null) {
            setSupportActionBar(myToolbar);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        serviceIntent = new Intent("MagicMirror.BleService");
        serviceIntent.setPackage("com.blublabs.magicmirror");

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        pairedDevicesList.addAll(sharedPref.getStringSet(KEY_PAIRED_DEVICES, new HashSet<String>()));
        defaultPairedDevice = sharedPref.getString(KEY_DEFAULT_DEVICE, null);
    }

    @Override
    protected void onStop() {
        if (service != null) {
            try {
                Message msg = Message.obtain(null,
                        BleService.MSG_UNREGISTER);
                if (msg != null) {
                    msg.replyTo = messenger;
                    service.send(msg);
                }
            } catch (Exception e) {
                Log.w(TAG, "Error unregistering with BleService",  e);
                service = null;
            } finally {
                getApplicationContext().unbindService(connection);
            }
        }

        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getApplicationContext().bindService(serviceIntent, connection, BIND_AUTO_CREATE);
    }

    private static class IncomingHandler extends Handler {
        private final WeakReference<MagicMirrorActivity> activity;

        IncomingHandler(MagicMirrorActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MagicMirrorActivity activity = this.activity.get();

            if (activity != null) {
                activity.handleMessage(msg);
            }
            super.handleMessage(msg);
        }
    }

    protected void handleMessage(Message msg) {
        Bundle data;

        switch (msg.what) {
            case BleService.MSG_STATE_CHANGED:
                onStateChanged(BleService.State.values()[msg.arg1]);
                break;
            case BleService.MSG_SCAN_STOPPED:
                data = msg.getData();
                if (data != null && data.containsKey(BleService.KEY_MAC_ADDRESSES)) {
                    onScanStopped(data.getStringArray(BleService.KEY_MAC_ADDRESSES));
                }
                break;
            default:
                break;
        }
    }

    protected void onScanStopped(String[] devices) {

    }

    protected void onDeviceDiscovered(String devices) {

    }

    protected BleService.State getState() {
        return state;
    }

    protected void onStateChanged(BleService.State newState) {
        state = newState;
        switch (state) {
            case SCANNING:
                break;
            case BLUETOOTH_OFF:
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_CODE);
                break;
            case CONNECTED:
                break;
            case IDLE:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults){
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            }
            else {
                Toast.makeText(this, "Location permission is required for BLE scanning to work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startScan();
            }
            else {
                Toast.makeText(this, "Bluetooth needs to be enabled for BLE scanning to work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void startScan() {

        if (Build.VERSION.SDK_INT >= 23) {

            int permissionCheck = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
        }

        Message msg = Message.obtain(null, BleService.MSG_REQUEST);
        if (msg != null) {
            msg.obj = new BleRequest(BleRequest.RequestType.SCAN, null, new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

                    onDeviceDiscovered(device.getAddress());
                }
            }, null) {

                @Override
                public void onError(String errormessage) {

                }
            };
            sendMessage(msg);
        }
    }

    public void connectToDevice(String deviceAddress) {

        if(deviceAddress == null) {
            return;
        }

        Message msg = Message.obtain(null, BleService.MSG_REQUEST);
        if (msg != null) {
            msg.obj = new BleRequest(BleRequest.RequestType.DEVICE_CONNECT, gattCallback, null, deviceAddress) {

                @Override
                public void onError(String errormessage) {

                }
            };
            sendMessage(msg);
        }
    }

    protected void sendMessage(Message msg) {

        sendMessage(service, msg);
    }

    private boolean sendMessage(Messenger messenger, Message msg) {
        boolean success = true;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            Log.w(TAG, "Lost connection to client", e);
            service = null;
            success = false;
        }
        return success;
    }

    public void addPairedDevice(String deviceAddress) {
        getPairedDevicesList().add(deviceAddress);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        sharedPref.edit()
                .putStringSet(KEY_PAIRED_DEVICES, getPairedDevicesList())
                .apply();

        setDefaultDevice(deviceAddress);
    }

    public void setDefaultDevice(String deviceAddress) {

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        sharedPref.edit()
                .putString(KEY_DEFAULT_DEVICE, deviceAddress)
                .apply();
    }

    public String getDefaultPairedDevice() {
        return defaultPairedDevice;
    }

    public Set<String> getPairedDevicesList() {
        return pairedDevicesList;
    }
}
