package com.blublabs.magicmirror.modules.helloworld;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.blublabs.magicmirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;
import com.blublabs.magicmirror.modules.alert.AlertMagicMirrorModule;
import com.blublabs.magicmirror.modules.alert.AlertSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrs on 03.10.2016.
 */

public class HelloWorldMagicMirrorModule extends MagicMirrorModule {

    public static final Parcelable.Creator<HelloWorldMagicMirrorModule> CREATOR =
            new Parcelable.Creator<HelloWorldMagicMirrorModule>() {
                @Override
                public HelloWorldMagicMirrorModule createFromParcel(Parcel source) {
                    return new HelloWorldMagicMirrorModule(source);
                }
                @Override
                public HelloWorldMagicMirrorModule[] newArray(int size) {
                    return new HelloWorldMagicMirrorModule[size];
                }
            };

    public HelloWorldMagicMirrorModule(JSONObject data) throws JSONException {
        super(data);
    }

    private HelloWorldMagicMirrorModule(Parcel source) {
        super(source);
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        HelloWorldSettingsFragment fragment = new HelloWorldSettingsFragment();

        Bundle bundle = new Bundle(1);
        bundle.putParcelable(ModuleSettingsFragment.KEY_MODULE, this);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }
}
