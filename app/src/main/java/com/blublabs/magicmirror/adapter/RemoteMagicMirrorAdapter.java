package com.blublabs.magicmirror.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.settings.mirror.modules.MagicMirrorModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.android.volley.Request.Method.GET;

/**
 * Created by andrs on 08.12.2016.
 */

public class RemoteMagicMirrorAdapter implements IMagicMirrorAdapter {

    private static final String KEY_IS_DEFAULT = "isDefaultModule";
    private static final String KEY_NAME_LONG = "longname";

    private Map<MagicMirrorAdapterCallback, List<ScanIpTask>> callbacks = new HashMap<>();
    private Set<InetAddress> cachedAddresses = new HashSet<>();

    private String currentConnectedMirror = null;
    private MagicMirrorAdapterCallback currentConnectionCallback = null;

    private final RequestQueue requestQueue;

    private JSONObject config = null;

    RemoteMagicMirrorAdapter(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public void getModuleData(int index, MagicMirrorAdapterCallback callback) {

        try {
            if(config.has(KEY_CONFIG_MODULES)) {

                JSONArray moduleList = new JSONArray(config.getJSONArray(KEY_CONFIG_MODULES).toString());

                JSONObject moduleJson = moduleList.getJSONObject(index);
                callback.onGetModuleData(MagicMirrorAdapterCallback.STATUS_SUCCESS, moduleJson);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onGetModuleData(MagicMirrorAdapterCallback.STATUS_ERROR, null);
        }
    }

    @Override
    public void setModuleData(final int index, final JSONObject data, final MagicMirrorAdapterCallback callback) {
        try {
            if(config.has(KEY_CONFIG_MODULES)) {
                final JSONObject newConfig = new JSONObject(config.toString());
                JSONArray moduleList = newConfig.getJSONArray(KEY_CONFIG_MODULES);
                moduleList.put(index, data);

                requestQueue.add(new JsonObjectRequest(getRequestUrl(currentConnectedMirror) + "post?data=config", newConfig, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        config = newConfig;
                        callback.onSetModuleData(MagicMirrorAdapterCallback.STATUS_SUCCESS);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onSetModuleData(MagicMirrorAdapterCallback.STATUS_ERROR);
                    }
                }));
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onSetModuleData(MagicMirrorAdapterCallback.STATUS_ERROR);
        }
    }

    @Override
    public void getModuleList(final MagicMirrorAdapterCallback callback) {

        // AsyncTask, because parsing a lot of JSON is quite demanding...
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                List<MagicMirrorModule> modules = new ArrayList<>();

                try {
                    if(config.has(KEY_CONFIG_MODULES)) {

                        JSONArray moduleList = config.getJSONArray(KEY_CONFIG_MODULES);

                        for (int i = 0; i < moduleList.length(); i++) {
                            JSONObject moduleJson = moduleList.getJSONObject(i);
                            final MagicMirrorModule module = MagicMirrorModule.getModuleForName(moduleJson.getString(MagicMirrorModule.KEY_DATA_NAME));

                            if (module != null) {
                                modules.add(module);
                                module.setData(moduleJson);
                                module.setInitialized(true);
                            }
                        }
                    }

                    callback.onGetModuleList(MagicMirrorAdapterCallback.STATUS_SUCCESS, modules);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    callback.onGetModuleList(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                }

                return null;
            }
        }.execute();
    }

    @Override
    public void getInstalledModuleList(final MagicMirrorAdapterCallback callback) {
        requestQueue.add(new JsonArrayRequest(getRequestUrl(currentConnectedMirror) + "get?data=modulesInstalled", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Map<String, List<String>> installedModules = new HashMap<>();
                    List<String> defaultModules = new ArrayList<>();
                    List<String> customModules = new ArrayList<>();
                    installedModules.put(KEY_DEFAULT_MODULES, defaultModules);
                    installedModules.put(KEY_CUSTOM_MODULES, customModules);

                    for(int i = 0; i < response.length(); i++) {
                        JSONObject module = response.getJSONObject(i);
                        boolean isDefault = module.getBoolean(KEY_IS_DEFAULT);
                        String name = module.getString(KEY_NAME_LONG);
                        if(isDefault) {
                            defaultModules.add(name);
                        }
                        else {
                            customModules.add(name);
                        }
                    }

                    callback.onGetInstalledModuleList(MagicMirrorAdapterCallback.STATUS_SUCCESS, installedModules);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onGetInstalledModuleList(MagicMirrorAdapterCallback.STATUS_ERROR, null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onGetInstalledModuleList(MagicMirrorAdapterCallback.STATUS_ERROR, null);
            }
        }));
    }

    @Override
    public void addModule(JSONObject module, final MagicMirrorAdapterCallback callback) {
        try {
            if(config.has(KEY_CONFIG_MODULES)) {
                final JSONArray moduleList = config.getJSONArray(KEY_CONFIG_MODULES);

                moduleList.put(module);

                requestQueue.add(new JsonObjectRequest(getRequestUrl(currentConnectedMirror) + "post?data=config", config, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onAddModule(MagicMirrorAdapterCallback.STATUS_SUCCESS);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        moduleList.remove(moduleList.length() - 1);
                        callback.onAddModule(MagicMirrorAdapterCallback.STATUS_ERROR);
                    }
                }));
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onAddModule(MagicMirrorAdapterCallback.STATUS_ERROR);
        }
    }

    @Override
    public void removeModule(final int index, final MagicMirrorAdapterCallback callback) {
        try {
            if(config.has(KEY_CONFIG_MODULES)) {
                final JSONObject newConfig = new JSONObject(config.toString());
                JSONArray moduleList = newConfig.getJSONArray(KEY_CONFIG_MODULES);
                moduleList.remove(index);

                requestQueue.add(new JsonObjectRequest(getRequestUrl(currentConnectedMirror) + "post?data=config", newConfig, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        config = newConfig;
                        callback.onRemoveModule(MagicMirrorAdapterCallback.STATUS_SUCCESS);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onRemoveModule(MagicMirrorAdapterCallback.STATUS_ERROR);
                    }
                }));
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            callback.onRemoveModule(MagicMirrorAdapterCallback.STATUS_ERROR);
        }
    }

    @Override
    public void connectMirror(final MagicMirrorAdapterCallback callback, final String identifier, @NonNull Context context) {

        requestQueue.add(new JsonObjectRequest(getRequestUrl(identifier) + "get?data=config", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                currentConnectedMirror = identifier;
                currentConnectionCallback = callback;
                currentConnectionCallback.onConnectedToMirror();
                config = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(RemoteMagicMirrorAdapter.class.getSimpleName(), "Error getting config!");
                currentConnectionCallback.onConnectionError();
            }
        }));
    }

    @Override
    public void disconnectMirror() {
        currentConnectedMirror = null;
        // in case a new connect happens in onDisconnectedFromMirror()
        MagicMirrorAdapterCallback temp = currentConnectionCallback;
        currentConnectionCallback = null;

        if(temp != null) {
            temp.onDisconnectedFromMirror();
        }
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

    private void publish(InetAddress address, String extra, MagicMirrorAdapterCallback callback) {
        String hostname = address.getHostName();
        callback.onMagicMirrorDiscovered(address.getHostAddress(), hostname.substring(0, hostname.indexOf(".")) + extra);
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
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    @Override
    public void getMirrorConfig(MagicMirrorAdapterCallback callback) {
        try {
            JSONObject configData = new JSONObject(config.toString());
            configData.remove(KEY_CONFIG_MODULES);
            callback.onGetMirrorConfig(MagicMirrorAdapterCallback.STATUS_SUCCESS, configData);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onGetMirrorConfig(MagicMirrorAdapterCallback.STATUS_ERROR, null);
        }
    }

    @Override
    public void setMirrorConfig(JSONObject data, final MagicMirrorAdapterCallback callback) {

        try {
            final JSONObject newConfig = new JSONObject(config.toString());

            if(data.has(KEY_CONFIG_PORT)) {
                newConfig.put(KEY_CONFIG_PORT, data.getInt(KEY_CONFIG_PORT));
            }
            else {
                newConfig.remove(KEY_CONFIG_PORT);
            }

            if(data.has(KEY_CONFIG_LANGUAGE)) {
                newConfig.put(KEY_CONFIG_LANGUAGE, data.getString(KEY_CONFIG_LANGUAGE));
            }
            else {
                newConfig.remove(KEY_CONFIG_LANGUAGE);
            }

            if(data.has(KEY_CONFIG_KIOSKMODE)) {
                newConfig.put(KEY_CONFIG_KIOSKMODE, data.getBoolean(KEY_CONFIG_KIOSKMODE));
            }
            else {
                newConfig.remove(KEY_CONFIG_KIOSKMODE);
            }

            if(data.has(KEY_CONFIG_TIMEFORMAT)) {
                newConfig.put(KEY_CONFIG_TIMEFORMAT, data.getInt(KEY_CONFIG_TIMEFORMAT));
            }
            else {
                newConfig.remove(KEY_CONFIG_TIMEFORMAT);
            }

            if(data.has(KEY_CONFIG_UNTIS)) {
                newConfig.put(KEY_CONFIG_UNTIS, data.getString(KEY_CONFIG_UNTIS));
            }
            else {
                newConfig.remove(KEY_CONFIG_UNTIS);
            }

            requestQueue.add(new JsonObjectRequest(getRequestUrl(currentConnectedMirror) + "post?data=config", newConfig, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    config = newConfig;
                    callback.onSetMirrorConfig(MagicMirrorAdapterCallback.STATUS_SUCCESS);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onSetMirrorConfig(MagicMirrorAdapterCallback.STATUS_ERROR);
                }
            }));
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onSetMirrorConfig(MagicMirrorAdapterCallback.STATUS_ERROR);
        }
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

    private String getRequestUrl(String ip) {
        return "http://" + ip + ":8080/";
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
                            publish(inetAddress, "", callback);
                        }
                        else if (isReachable(client, TIMEOUT, context)){
                            publish(inetAddress, "", callback);
                            getCachedAddresses().add(inetAddress);
                        }
                    }catch (IllegalStateException e) {

                        publish(inetAddress, " !Connection refused!", callback);
                    }

                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }

            return null;
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
