package com.blublabs.magicmirror.settings.mirror.modules.helloworld;

import android.databinding.Bindable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.blublabs.magicmirror.BR;
import com.blublabs.magicmirror.common.Utils;
import com.blublabs.magicmirror.settings.mirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrs on 03.10.2016.
 */

public class HelloWorldMagicMirrorModule extends MagicMirrorModule {

    private static final String KEY_DATA_TEXT = "text";

    private String text = "";

    private HelloWorldSettingsFragment fragment;

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

    public HelloWorldMagicMirrorModule(String name) {
        super(name);
    }

    @Override
    public void setData(JSONObject data) throws JSONException {
        super.setData(data);

        if(data.has(KEY_DATA_CONFIG)) {

            JSONObject config = data.getJSONObject(KEY_DATA_CONFIG);

            if(config.has(KEY_DATA_TEXT)) {
                this.text = config.getString(KEY_DATA_TEXT);
            }
        }
    }

    private HelloWorldMagicMirrorModule(Parcel source) {
        super(source);

        this.text = source.readString();
    }

    @Bindable
    public String getText() {
        return text;
    }

    public void setText(String text) {

        if(Utils.objectsEqual(this.text, text)) {
            return;
        }

        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        if(fragment == null) {
            fragment = new HelloWorldSettingsFragment();
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

        if(!Utils.isEmpty(text)) {
            config.put(KEY_DATA_TEXT, text);
        }

        if(config.length() > 0) {
            json.put(KEY_DATA_CONFIG, config);
        }

        return json;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(this.text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        HelloWorldMagicMirrorModule that = (HelloWorldMagicMirrorModule) o;

        return getText() != null ? getText().equals(that.getText()) : that.getText() == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getText() != null ? getText().hashCode() : 0);
        return result;
    }
}
