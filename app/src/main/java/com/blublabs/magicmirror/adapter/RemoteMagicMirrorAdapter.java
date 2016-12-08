package com.blublabs.magicmirror.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.widget.Toast;

import com.blublabs.magicmirror.common.Utils;
import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleManager;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kotlin.NotImplementedError;

/**
 * Created by andrs on 08.12.2016.
 */

public class RemoteMagicMirrorAdapter implements IMagicMirrorAdapter {

    private Map<MagicMirrorAdapterCallback, List<ScanIpTask>> callbacks = new HashMap<>();
    private Set<InetAddress> cachedAddresses = new HashSet<>();

    private String currentConnectedMirror = null;
    private MagicMirrorAdapterCallback currentConnectionCallback = null;

    RemoteMagicMirrorAdapter(Context context) {
    }

    @Override
    public void getModuleData(int index, MagicMirrorAdapterCallback callback) {

    }

    @Override
    public void setModuleData(int index, String data, MagicMirrorAdapterCallback callback) {

    }

    @Override
    public void getModuleList(MagicMirrorAdapterCallback callback) {

    }

    @Override
    public void getInstalledModuleList(MagicMirrorAdapterCallback callback) {

    }

    @Override
    public void addModule(JSONObject module, MagicMirrorAdapterCallback callback) {

    }

    @Override
    public void removeModule(int index, MagicMirrorAdapterCallback callback) {

    }

    @Override
    public void connectMirror(MagicMirrorAdapterCallback callback, String identifier) {
        currentConnectedMirror = identifier;
        currentConnectionCallback = callback;
        currentConnectionCallback.onConnectedToMirror();
    }

    @Override
    public void disconnectMirror() {
        currentConnectedMirror = null;
        // in case a new connect happens in onDisconnectedFromMirror()
        MagicMirrorAdapterCallback temp = currentConnectionCallback;
        currentConnectionCallback = null;

        temp.onDisconnectedFromMirror();
    }

    @Override
    public void scanForMagicMirrors(MagicMirrorAdapterCallback callback, @NonNull Context context) {

        if(!getCallbacks().containsKey(callback)) {

            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

            String subnet = ip.substring(0, ip.lastIndexOf(".") + 1);

            final List<ScanIpTask> scanTasks = new ArrayList<>();

            for(int i = 2; i < 255; i = i + 10) {
                scanTasks.add(new ScanIpTask(subnet, i, Math.min(254, i + 9), callback, context));
            }

            getCallbacks().put(callback, scanTasks);

            for(ScanIpTask task : scanTasks) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    @Override
    public void stopScanForMagicMirrors() {
        Set<Map.Entry<MagicMirrorAdapterCallback, List<ScanIpTask>>> callbacks = getCallbacks().entrySet();

        for (Map.Entry<MagicMirrorAdapterCallback, List<ScanIpTask>> entry : callbacks) {
            for (ScanIpTask task : entry.getValue()) {
                task.cancel(true);
            }

            entry.getKey().onScanFinished();
        }

        getCallbacks().clear();
    }

    @Override
    public boolean isConnectedToMirror() {
        return currentConnectedMirror != null;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void getMirrorConfig(MagicMirrorAdapterCallback callback) {

    }

    @Override
    public void setMirrorConfig(String data, MagicMirrorAdapterCallback callback) {

    }

    @Override
    public boolean isAllowWifiSetup() {
        return false;
    }

    @Override
    public void getWifiNetworks(MagicMirrorAdapterCallback callback) {
        throw new UnsupportedOperationException("This adapter allows no Wifi setup!");
    }

    @Override
    public void connectToWifiNetwork(String ssid, String passphrase, MagicMirrorAdapterCallback callback) {
        throw new UnsupportedOperationException("This adapter allows no Wifi setup!");
    }

    @Override
    public String getAdapterIdentifier() {
        return "wifi";
    }

    private synchronized Map<MagicMirrorAdapterCallback, List<ScanIpTask>> getCallbacks() {
        return callbacks;
    }

    private synchronized Set<InetAddress> getCachedAddresses() {
        return cachedAddresses;
    }

    private class ScanIpTask extends AsyncTask<Void, String, Void> {

        final String subnet;
        final int lower;
        final int upper;
        final MagicMirrorAdapterCallback callback;
        final Context context;

        static final int TIMEOUT = 300;

        ScanIpTask(String subnet, int lower, int upper, MagicMirrorAdapterCallback callback, @NonNull Context context) {
            this.subnet = subnet;
            this.lower = lower;
            this.upper = upper;
            this.callback = callback;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = lower; i <= upper; i++) {

                if(isCancelled()) {
                    break;
                }

                String client = subnet + i;

                try {
                    InetAddress inetAddress = InetAddress.getByName(client);

                    try {
                        if(getCachedAddresses().contains(inetAddress)) {
                            publish(inetAddress, "");
                        }
                        else if (isReachable(client, TIMEOUT, context)){
                            publish(inetAddress, "");
                            getCachedAddresses().add(inetAddress);
                        }
                    }catch (IllegalStateException e) {

                        publish(inetAddress, " !Connection refused!");
                    }

                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }

            return null;
        }

        private void publish(InetAddress address, String extra) {
            String hostname = address.getHostName();
            callback.onMagicMirrorDiscovered(address.getHostAddress(), hostname.substring(0, hostname.indexOf(".")) + extra);
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            getCallbacks().get(callback).remove(this);
            if(getCallbacks().get(callback).isEmpty()) {
                getCallbacks().remove(callback);
                callback.onScanFinished();
            }

        }

        private boolean isReachable(String ip, int timeout, @NonNull Context context) throws IOException {
            // First, check we have any sort of connectivity
            final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
            boolean isReachable = false;

            if (netInfo != null && netInfo.isConnected()) {
                // Some sort of connection is open, check if server is reachable
                URL url = new URL("http://" + ip + ":8080/remote.html");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "Android Application");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(timeout);
                urlc.connect();

                if(urlc.getResponseCode() == 403){
                    throw new IllegalStateException("Access forbidden");
                }

                isReachable = (urlc.getResponseCode() == 200);
            }

            return isReachable;
        }
    }
}
