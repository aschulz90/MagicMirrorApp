package com.blublabs.magicmirror.settings.mirror.modules.updatenotification;

import android.databinding.Bindable;
import android.os.Bundle;
import android.os.Parcel;

import com.android.databinding.library.baseAdapters.BR;
import com.blublabs.magicmirror.settings.mirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;
import com.blublabs.magicmirror.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrs on 03.10.2016.
 */

public class UpdateNotificationMagicMirrorModule extends MagicMirrorModule {

    private static final String KEY_DATA_UPDATE_INTERVALL = "updateInterval";

    private Integer updateInterval = null;

    private UpdateNotificationSettingsFragment fragment;

    public static final Creator<UpdateNotificationMagicMirrorModule> CREATOR =
            new Creator<UpdateNotificationMagicMirrorModule>() {
                @Override
                public UpdateNotificationMagicMirrorModule createFromParcel(Parcel source) {
                    return new UpdateNotificationMagicMirrorModule(source);
                }
                @Override
                public UpdateNotificationMagicMirrorModule[] newArray(int size) {
                    return new UpdateNotificationMagicMirrorModule[size];
                }
            };

    public UpdateNotificationMagicMirrorModule(String name) {
        super(name);
    }

    @Override
    public void setData(JSONObject data) throws JSONException {
        super.setData(data);

        if(data.has(KEY_DATA_CONFIG)) {

            JSONObject config = data.getJSONObject(KEY_DATA_CONFIG);

            if(config.has(KEY_DATA_UPDATE_INTERVALL)) {
                this.updateInterval = config.getInt(KEY_DATA_UPDATE_INTERVALL);
            }
        }
    }

    private UpdateNotificationMagicMirrorModule(Parcel source) {
        super(source);

        this.updateInterval = source.readInt();
    }

    @Bindable
    public Integer getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(Integer updateInterval) {
        if(Utils.objectsEqual(this.updateInterval, updateInterval)) {
            return;
        }

        this.updateInterval = updateInterval;
        notifyPropertyChanged(BR.updateInterval);
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        if(fragment == null) {
            fragment = new UpdateNotificationSettingsFragment();
            Bundle bundle = new Bundle(1);
            bundle.putParcelable(ModuleSettingsFragment.KEY_MODULE, this);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    public JSONObject toJson() throws JSONException {

        JSONObject json = super.toJson();
        JSONObject config = new JSONObject();

        if(updateInterval != null && updateInterval.compareTo(Integer.parseInt("600000")) != 0) {
            config.put(KEY_DATA_UPDATE_INTERVALL, updateInterval);
        }

        if(config.length() > 0) {
            json.put(KEY_DATA_CONFIG, config);
        }

        return json;
    }

    @Override
    public boolean parameterRequiresRefresh(int id) {
        switch (id) {
            case BR.updateInterval:
                return true;
            default:
                return super.parameterRequiresRefresh(id);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeInt(updateInterval);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UpdateNotificationMagicMirrorModule that = (UpdateNotificationMagicMirrorModule) o;

        return getUpdateInterval() != null ? getUpdateInterval().equals(that.getUpdateInterval()) : that.getUpdateInterval() == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getUpdateInterval() != null ? getUpdateInterval().hashCode() : 0);
        return result;
    }
}
