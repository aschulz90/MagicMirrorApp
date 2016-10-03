package com.blublabs.magicmirror.common;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.blublabs.magicmirror.service.BleService;

import java.lang.ref.WeakReference;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by andrs on 19.09.2016.
 */
public class MagicMirrorFragment extends Fragment {

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

                    MagicMirrorFragment.this.service = new Messenger(service);
                    try {
                        Message msg = Message.obtain(null,
                                BleService.MSG_REGISTER);
                        if (msg != null) {
                            msg.replyTo = messenger;
                            MagicMirrorFragment.this.service.send(msg);
                        } else {
                            MagicMirrorFragment.this.service = null;
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Error connecting to BleService",
                                e);
                        MagicMirrorFragment.this.service = null;
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    service = null;
                }
            };

    public MagicMirrorFragment() {
        super();

        messenger = new Messenger(new MagicMirrorFragment.IncomingHandler(this));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serviceIntent = new Intent("MagicMirror.BleService");
        serviceIntent.setPackage("com.blublabs.magicmirror");
    }

    @Override
    public void onStop() {
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
                getActivity().unbindService(connection);
            }
        }
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().bindService(serviceIntent, connection, BIND_AUTO_CREATE);
    }

    private static class IncomingHandler extends Handler {
        private final WeakReference<MagicMirrorFragment> fragment;

        public IncomingHandler(MagicMirrorFragment fragment) {
            this.fragment = new WeakReference<MagicMirrorFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            MagicMirrorFragment fragment = this.fragment.get();

            if (fragment != null) {
                fragment.handleMessage(msg);
            }
            super.handleMessage(msg);
        }
    }

    protected void handleMessage(Message msg) {
        Bundle data = null;

        switch (msg.what) {
            case BleService.MSG_STATE_CHANGED:
                onStateChanged(BleService.State.values()[msg.arg1]);
                break;
            case BleService.MSG_DEVICE_FOUND:
                data = msg.getData();
                if (data != null && data.containsKey(BleService.KEY_MAC_ADDRESSES)) {
                    onDeviceDiscovered(data.getStringArray(BleService.KEY_MAC_ADDRESSES));
                }
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

    protected void onDeviceDiscovered(String[] devices) {

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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            }
            else {
                Toast.makeText(getActivity(), "Location permission is required for BLE scanning to work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startScan();
            }
            else {
                Toast.makeText(getActivity(), "Bluetooth needs to be enabled for BLE scanning to work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void startScan() {

        if (Build.VERSION.SDK_INT >= 23) {

            int permissionCheck = getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
        }

        Message msg = Message.obtain(null, BleService.MSG_START_SCAN);
        if (msg != null) {
            try {
                service.send(msg);
            } catch (RemoteException e) {
                Log.w(TAG, "Lost connection to service", e);
                getActivity().unbindService(connection);
            }
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
            success = false;
        }
        return success;
    }
}
