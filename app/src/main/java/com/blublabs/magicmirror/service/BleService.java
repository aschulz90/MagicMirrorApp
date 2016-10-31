package com.blublabs.magicmirror.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.blublabs.magicmirror.utils.GzipUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

/**
 * Created by andrs on 14.09.2016.
 *
 */
@TargetApi(21)
public class BleService extends Service implements BluetoothAdapter.LeScanCallback {

    public static final int MSG_REGISTER = 1;
    public static final int MSG_UNREGISTER = 2;
    public static final int MSG_REQUEST = 3;
    public static final int MSG_STOP_SCAN = 4;
    public static final int MSG_STATE_CHANGED = 5;
    public static final int MSG_DEVICE_DISCONNECT = 6;
    public static final int MSG_SCAN_STOPPED = 9;

    public static final String TAG = "BleService";
    private static final long SCAN_PERIOD = 5000;
    public static final String KEY_MAC_ADDRESSES = "KEY_MAC_ADDRESSES";

    public static final String TARGET_DEVICE_NAME = "MagicMirror";
    public static final UUID UUID_SERVICE_WIFI = UUID.fromString("0000210f-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_WIFI = UUID.fromString("000035cd-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_SERVICE_NOTIFY = UUID.fromString("0000200f-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARACTERISTIC_NOTIFY = UUID.fromString("000034cd-0000-1000-8000-00805f9b34fb");

    private final Messenger messenger;
    private final List<Messenger> clients = new LinkedList<Messenger>();

    private final IncomingHandler incomingHandler;
    private final Map<String,BluetoothDevice> devices = new HashMap<String, BluetoothDevice>();

    public enum State {
        UNKNOWN,
        IDLE,
        SCANNING,
        BLUETOOTH_OFF,
        CONNECTING,
        CONNECTED,
        DISCONNECTING
    }

    private State state = State.UNKNOWN;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner leScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt gatt = null;
    private boolean servicesDiscovered = false;

    private Queue<BleRequest> requestQueue = new LinkedList<>();

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            onDeviceFound(result.getDevice());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            Log.v(TAG, "Connection State Changed: " + (newState == BluetoothProfile.STATE_CONNECTED ? "Connected" : "Disconnected"));
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                setState(State.CONNECTED);
            } else {
                gatt.close();
                BleService.this.gatt = null;
                setState(State.IDLE);
                requestQueue.remove().onError("");
                return;
            }

            requestQueue.remove().getGattCallback().onConnectionStateChange(gatt, status, newState);
            executeRequest();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            servicesDiscovered = true;
            BleRequest request = requestQueue.peek();
            if(request != null && (request.getType() == BleRequest.RequestType.CHARACTERISTIC_READ || request.getType() == BleRequest.RequestType.CHARACTERISTIC_WRITE)) {
                executeRequest();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            /*String lValue = "";
            try {
                lValue = GzipUtil.decompress(characteristic.getValue());
            } catch (IOException e) {
                Log.e("gattCallback", "Error while decompressing value", e);
            }

            Log.i("gattCallback", "Wifi Characteristic Value: " + lValue);*/
            requestQueue.remove().getGattCallback().onCharacteristicRead(gatt, characteristic, status);
            executeRequest();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            /*Log.i("gattCallback", "Wifi Characteristic Write Status: " + status);*/
            requestQueue.remove().getGattCallback().onCharacteristicWrite(gatt, characteristic, status);
            executeRequest();
        }
    };

    public BleService() {

        incomingHandler = new IncomingHandler(this);
        messenger = new Messenger(new IncomingHandler(this));
    }

    private void startScan() {

        if(state == State.SCANNING) {
            return;
        }

        if (bluetoothAdapter == null) {
            BluetoothManager bluetoothMgr = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothMgr.getAdapter();
        }

        if (Build.VERSION.SDK_INT >= 21 && leScanner == null) {
            leScanner = bluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();
        }

        devices.clear();
        setState(State.SCANNING);

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            setState(State.BLUETOOTH_OFF);
        } else {
            incomingHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < 21) {
                bluetoothAdapter.startLeScan(this);
            } else {
                leScanner.startScan(filters, settings, scanCallback);
            }
        }
    }

    private void stopScan() {

        if(state != State.SCANNING) {
            return;
        }

        if (Build.VERSION.SDK_INT < 21) {
            bluetoothAdapter.stopLeScan(this);
        } else {
            leScanner.stopScan(scanCallback);
        }

        Message msg = Message.obtain(null, MSG_SCAN_STOPPED);
        if (msg != null) {
            Bundle bundle = new Bundle();
            String[] addresses = devices.keySet().toArray(new String[devices.size()]);
            bundle.putStringArray(KEY_MAC_ADDRESSES, addresses);
            msg.setData(bundle);
            sendMessage(msg);
        }

        if(requestQueue.peek().getType() == BleRequest.RequestType.SCAN) {
            requestQueue.remove();
            executeRequest();
        }

        setState(gatt == null ? State.IDLE : State.CONNECTED);
        Log.d(TAG, "Scan Stopped");
    }

    private void onDeviceFound(BluetoothDevice device) {
        if (device != null && !devices.containsValue(device) && TARGET_DEVICE_NAME.equals(device.getName())) {
            devices.put(device.getAddress(), device);
            Log.d(TAG, "Added " + device.getName() + ": " + device.getAddress());

            BleRequest request = requestQueue.peek();

            if(request.getType() == BleRequest.RequestType.SCAN) {
                request.getScanCallback().onLeScan(device, 0, null);
            }
            else if(request.getType() == BleRequest.RequestType.DEVICE_CONNECT && request.getExtra()[0].equals(device.getAddress())) {
                stopScan();
                connect((String) request.getExtra()[0]);
            }
        }
    }

    public void connect(String macAddress) {
        BluetoothDevice device = devices.get(macAddress);
        if (device != null) {
            gatt = device.connectGatt(this, false, gattCallback);
        }
        else {
            startScan();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return messenger.getBinder();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        onDeviceFound(device);
    }

    private static class IncomingHandler extends Handler {
        private final WeakReference<BleService> mService;

        IncomingHandler(BleService service) {
            mService = new WeakReference<BleService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            BleService service = mService.get();
            if (service != null) {
                switch (msg.what) {
                    case MSG_REGISTER:
                        service.clients.add(msg.replyTo);
                        if(service.gatt != null && service.state != State.CONNECTED) {
                            service.gatt.connect();
                        }
                        else {
                            Message stateMsg = service.getStateMessage();
                            if (stateMsg != null) {
                                service.sendMessage(msg.replyTo, stateMsg);
                            }
                        }
                        Log.d(TAG, "Registered: " + msg.replyTo);
                        break;
                    case MSG_UNREGISTER:
                        service.clients.remove(msg.replyTo);
                        Log.d(TAG, "Unegistered: " + msg.replyTo);
                        if(service.clients.isEmpty() && service.state == State.CONNECTED) {
                            if(service.gatt != null) {
                                service.gatt.disconnect();
                                service.gatt.close();
                            }
                            else {
                                service.setState(State.IDLE);
                            }
                        }
                        break;
                    case MSG_STOP_SCAN:
                        service.stopScan();
                        Log.d(TAG, "Stop Scan");
                        break;
                    case MSG_DEVICE_DISCONNECT:
                        Log.d(TAG, "Disconnect from Device");
                        if (service.state == State.CONNECTED && service.gatt != null) {
                            service.gatt.disconnect();
                        }
                        break;
                    case MSG_REQUEST:
                        if(msg.obj == null || !(msg.obj instanceof BleRequest)) {
                            throw new IllegalArgumentException("BleRequest nicht in message gesetzt!");
                        }
                        service.addRequest((BleRequest) msg.obj);


                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    private void addRequest(BleRequest request) {
        requestQueue.add(request);

        if(requestQueue.size() == 1) {
            executeRequest();
        }
    }

    private void executeRequest() {
        if(requestQueue.isEmpty()) {
            return;
        }

        final BleRequest request = requestQueue.peek();

        switch (request.getType()) {

            case SCAN:
                startScan();
                Log.d(TAG, "Start Scan");
                break;
            case DEVICE_CONNECT:
                if(request.getExtra()[0] instanceof String) {
                    Log.d(TAG, "Connect to " + request.getExtra()[0]);
                    connect((String) request.getExtra()[0]);
                }
                else {
                    requestQueue.remove();
                    executeRequest();
                }
                break;
            case CHARACTERISTIC_READ:
                if(request.getExtra().length == 2 && request.getExtra()[0] instanceof UUID && request.getExtra()[1] instanceof UUID) {
                    Log.d(TAG, "Read Service " + request.getExtra()[0] + " Characteristic " + request.getExtra()[1]);
                    readCharacteristic((UUID) request.getExtra()[0], (UUID) request.getExtra()[1]);
                }
                else {
                    requestQueue.remove();
                    executeRequest();
                }
                break;
            case CHARACTERISTIC_WRITE:
                if(request.getExtra().length == 3 && request.getExtra()[0] instanceof UUID && request.getExtra()[1] instanceof UUID && request.getExtra()[2] instanceof String) {
                    Log.d(TAG, "Write Service " + request.getExtra()[0] + " Characteristic " + request.getExtra()[1] + " Value " + request.getExtra()[2]);
                    writeCharacteristic((UUID) request.getExtra()[0], (UUID) request.getExtra()[1], (String) request.getExtra()[2]);
                }
                else {
                    requestQueue.remove();
                    executeRequest();
                }
                break;
        }
    }

    private void readCharacteristic(UUID service, UUID characteristic) {

        if(state != State.CONNECTED) {
            requestQueue.remove();
            executeRequest();
            return;
        }

        if(!servicesDiscovered) {
            gatt.discoverServices();
            return;
        }

        BluetoothGattService bleService = gatt.getService(service);
        if(bleService != null) {
            BluetoothGattCharacteristic readCharacteristic = bleService.getCharacteristic(characteristic);

            if(readCharacteristic != null) {
                gatt.readCharacteristic(readCharacteristic);
            }
        }
    }

    private void writeCharacteristic(UUID service, UUID characteristic, String value) {

        if(state != State.CONNECTED) {
            requestQueue.remove();
            executeRequest();
            return;
        }

        if(!servicesDiscovered) {
            gatt.discoverServices();
        }

        BluetoothGattService bleService = gatt.getService(service);
        if(bleService != null) {
            BluetoothGattCharacteristic writeCharacteristic = bleService.getCharacteristic(characteristic);

            if (writeCharacteristic != null) {
                writeCharacteristic.setValue(value);
                gatt.writeCharacteristic(writeCharacteristic);
            }
        }
    }

    private void setState(State newState) {
        if (state != newState) {
            state = newState;
            Message msg = getStateMessage();
            if (msg != null) {
                sendMessage(msg);
            }
        }
    }

    private Message getStateMessage() {
        Message msg = Message.obtain(null, MSG_STATE_CHANGED);
        if (msg != null) {
            msg.arg1 = state.ordinal();
        }
        return msg;
    }

    private void sendMessage(Message msg) {
        for (int i = clients.size() - 1; i >= 0; i--) {
            Messenger messenger = clients.get(i);
            Message copyMsg = new Message();
            copyMsg.copyFrom(msg);
            if (!sendMessage(messenger, copyMsg)) {
                clients.remove(messenger);
            }
        }
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
