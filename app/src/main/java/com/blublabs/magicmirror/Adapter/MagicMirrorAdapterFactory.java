package com.blublabs.magicmirror.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.blublabs.magicmirror.R;

/**
 * Created by andrs on 24.11.2016.
 */

public class MagicMirrorAdapterFactory {

    private enum AdapterType {
        BLE("ble"),
        REMOTE("remote");

        private final String text;

        AdapterType(String text) {
            this.text = text;
        }

        public static AdapterType from(String text) {
            for(AdapterType adapterType : AdapterType.values()) {
                if(adapterType.getText().equals(text)) {
                    return adapterType;
                }
            }

            return BLE;
        }

        public String getText() {
            return text;
        }
    }

    private static BleMagicMirrorAdapter bleMagicMirrorAdapterInstance = null;
    private static RemoteMagicMirrorAdapter remoteMagicMirrorAdapterInstance = null;

    public static IMagicMirrorAdapter getAdapter(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String type = pref.getString(context.getString(R.string.key_pref_app_adapter), "ble");

        switch (AdapterType.from(type)) {

            case BLE:
                if(bleMagicMirrorAdapterInstance == null) {
                    bleMagicMirrorAdapterInstance = new BleMagicMirrorAdapter(context);
                }
                return bleMagicMirrorAdapterInstance;
            case REMOTE:
                if(remoteMagicMirrorAdapterInstance == null) {
                    remoteMagicMirrorAdapterInstance = new RemoteMagicMirrorAdapter(context);
                }
                return remoteMagicMirrorAdapterInstance;
            default:
                throw new RuntimeException("This MagicMirrorAdapter is not yet implemented!");
        }
    }

}
