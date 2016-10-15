package com.blublabs.magicmirror.modules.alert;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.blublabs.magicmirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrs on 03.10.2016.
 */

public class AlertMagicMirrorModule extends MagicMirrorModule {

    public static final Parcelable.Creator<AlertMagicMirrorModule> CREATOR =
            new Parcelable.Creator<AlertMagicMirrorModule>() {
                @Override
                public AlertMagicMirrorModule createFromParcel(Parcel source) {
                    return new AlertMagicMirrorModule(source);
                }
                @Override
                public AlertMagicMirrorModule[] newArray(int size) {
                    return new AlertMagicMirrorModule[size];
                }
            };

    public AlertMagicMirrorModule(JSONObject data) throws JSONException {
        super(data);
    }

    private AlertMagicMirrorModule(Parcel source) {
        super(source);
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        AlertSettingsFragment fragment = new AlertSettingsFragment();

        Bundle bundle = new Bundle(1);
        bundle.putParcelable(ModuleSettingsFragment.KEY_MODULE, this);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return super.toJson();
    }
}
