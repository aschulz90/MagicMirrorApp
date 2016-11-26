package com.blublabs.magicmirror.adapter;

import android.content.Context;

import kotlin.NotImplementedError;

/**
 * Created by andrs on 24.11.2016.
 */

public class MagicMirrorAdapterFactory {

    public enum AdapteryType {
        BLE
    }

    private static BleMagicMirrorAdapter bleMagicMirrorAdapterInstance = null;

    public static IMagicMirrorAdapter getAdapter(AdapteryType type, Context context) {
        switch (type) {

            case BLE:
                if(bleMagicMirrorAdapterInstance == null) {
                    bleMagicMirrorAdapterInstance = new BleMagicMirrorAdapter(context);
                }
                return bleMagicMirrorAdapterInstance;
            default:
                throw new NotImplementedError("This MagicMirrorAdapter is not yet implemented!");
        }
    }

}
