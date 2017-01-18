package com.blublabs.magicmirror.settings.mirror.modules.custom;

import android.os.Bundle;
import android.os.Parcel;

import com.blublabs.magicmirror.utils.Utils;
import com.blublabs.magicmirror.settings.mirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;

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

        this.data = data;

        super.setData(data);

        ((CustomSettingsFragment) getAdditionalSettingsFragment()).setData(data);
    }

    private CustomMagicMirrorModule(Parcel source) {
        super(source);

        JSONObject savedData = null;

        try {
            String data = source.readString();
            if(!Utils.isEmpty(data)) {
                savedData = new JSONObject(source.readString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.data = savedData;
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
    public boolean parameterRequiresRefresh(int id) {
        return true;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(this.data == null ? "" : this.data.toString());
    }
}
