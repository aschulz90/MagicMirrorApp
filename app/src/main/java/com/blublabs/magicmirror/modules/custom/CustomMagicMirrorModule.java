package com.blublabs.magicmirror.modules.custom;

import android.os.Bundle;
import android.os.Parcel;

import com.blublabs.magicmirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrs on 03.10.2016.
 */

public class CustomMagicMirrorModule extends MagicMirrorModule {

    private JSONObject data;

    private CustomSettingsFragment fragment;

    public static final Creator<CustomMagicMirrorModule> CREATOR =
            new Creator<CustomMagicMirrorModule>() {
                @Override
                public CustomMagicMirrorModule createFromParcel(Parcel source) {
                    return new CustomMagicMirrorModule(source);
                }
                @Override
                public CustomMagicMirrorModule[] newArray(int size) {
                    return new CustomMagicMirrorModule[size];
                }
            };

    public CustomMagicMirrorModule(String name) {
        super(name);
    }

    @Override
    public void setData(JSONObject data) throws JSONException {

        super.setData(data);

        this.data = data;

        ((CustomSettingsFragment) getAdditionalSettingsFragment()).setData(data);
    }

    private CustomMagicMirrorModule(Parcel source) {
        super(source);

        JSONObject savedData = null;

        try {
            savedData = new JSONObject(source.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.data = savedData;
    }

    public JSONObject getData() throws JSONException {

        if(data != null && data.has(KEY_DATA_CONFIG) && data.getJSONObject(KEY_DATA_CONFIG).length() == 0) {
            data.remove(KEY_DATA_CONFIG);
        }

        return data;
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        if(fragment == null) {
            fragment = new CustomSettingsFragment();
            Bundle bundle = new Bundle(1);
            bundle.putParcelable(ModuleSettingsFragment.KEY_MODULE, this);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return data;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(this.data.toString());
    }
}
