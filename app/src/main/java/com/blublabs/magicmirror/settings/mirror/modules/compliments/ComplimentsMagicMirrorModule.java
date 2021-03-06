package com.blublabs.magicmirror.settings.mirror.modules.compliments;

import android.databinding.Bindable;
import android.os.Bundle;
import android.os.Parcel;

import com.android.databinding.library.baseAdapters.BR;
import com.blublabs.magicmirror.utils.Utils;
import com.blublabs.magicmirror.settings.mirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by andrs on 03.10.2016.
 */

public class ComplimentsMagicMirrorModule extends MagicMirrorModule {

    private static final String KEY_DATA_UPDATE_INTERVALL = "updateInterval";
    private static final String KEY_DATA_FADE_SPEED = "fadeSpeed";
    private static final String KEY_DATA_COMPLIMENTS = "compliments";
    private static final String KEY_DATA_COMPLIMENTS_MORNING = "morning";
    private static final String KEY_DATA_COMPLIMENTS_AFTERNOON = "afternoon";
    private static final String KEY_DATA_COMPLIMENTS_EVENING = "evening";
    private static final String KEY_DATA_REMOTE_FILE = "remoteFile";

    private static final List<String> COMPLIMENTS_DEFAULT_MORNING = Arrays.asList("Good morning, handsome!", "Enjoy your day!", "How was your sleep?");
    private static final List<String> COMPLIMENTS_DEFAULT_AFTERNOON = Arrays.asList("Hello, beauty!", "You look sexy!", "Looking good today!");
    private static final List<String> COMPLIMENTS_DEFAULT_EVENING = Arrays.asList("Wow, you look hot!", "You look nice!", "Hi, sexy!");

    private Integer updateInterval = null;
    private Integer fadeSpeed = null;
    private String remoteFile = "";
    private List<String> complimentsMorning = new ArrayList<>();
    private List<String> complimentsAfternoon = new ArrayList<>();
    private List<String> complimentsEvening = new ArrayList<>();

    private ComplimentsSettingsFragment fragment = null;

    public ComplimentsMagicMirrorModule(String name) {
        super(name);
    }

    @Override
    public void setData(JSONObject data) throws JSONException {
        if(data.has(KEY_DATA_CONFIG)) {

            JSONObject config = data.getJSONObject(KEY_DATA_CONFIG);

            if(config.has(KEY_DATA_UPDATE_INTERVALL)) {
                this.updateInterval = config.getInt(KEY_DATA_UPDATE_INTERVALL) / 1000 / 60;
            }

            if(config.has(KEY_DATA_FADE_SPEED)) {
                this.fadeSpeed = config.getInt(KEY_DATA_FADE_SPEED);
            }

            if(config.has(KEY_DATA_REMOTE_FILE)) {
                this.remoteFile = config.getString(KEY_DATA_REMOTE_FILE);
            }

            if(config.has(KEY_DATA_COMPLIMENTS)) {
                JSONObject compliments = config.getJSONObject(KEY_DATA_COMPLIMENTS);

                if(compliments.has(KEY_DATA_COMPLIMENTS_MORNING)) {
                    JSONArray array = compliments.getJSONArray(KEY_DATA_COMPLIMENTS_MORNING);

                    for (int i = 0; i < array.length(); i++) {
                        complimentsMorning.add(array.getString(i));
                    }
                }

                if(compliments.has(KEY_DATA_COMPLIMENTS_AFTERNOON)) {
                    JSONArray array = compliments.getJSONArray(KEY_DATA_COMPLIMENTS_AFTERNOON);

                    for (int i = 0; i < array.length(); i++) {
                        complimentsAfternoon.add(array.getString(i));
                    }
                }

                if(compliments.has(KEY_DATA_COMPLIMENTS_EVENING)) {
                    JSONArray array = compliments.getJSONArray(KEY_DATA_COMPLIMENTS_EVENING);

                    for (int i = 0; i < array.length(); i++) {
                        complimentsEvening.add(array.getString(i));
                    }
                }
            }
        }

        if(complimentsMorning.isEmpty() && complimentsAfternoon.isEmpty() && complimentsEvening.isEmpty()) {
            complimentsMorning.addAll(COMPLIMENTS_DEFAULT_MORNING);
            complimentsAfternoon.addAll(COMPLIMENTS_DEFAULT_AFTERNOON);
            complimentsEvening.addAll(COMPLIMENTS_DEFAULT_EVENING);
        }

        super.setData(data);
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

    @Bindable
    public Integer getFadeSpeed() {
        return fadeSpeed;
    }

    public void setFadeSpeed(Integer fadeSpeed) {

        if(Utils.objectsEqual(this.fadeSpeed, fadeSpeed)) {
            return;
        }

        this.fadeSpeed = fadeSpeed;
        notifyPropertyChanged(BR.fadeSpeed);
    }

    @Bindable
    public String getRemoteFile() {
        return remoteFile;
    }

    public void setRemoteFile(String remoteFile) {
        if(Utils.objectsEqual(this.remoteFile, remoteFile)) {
            return;
        }

        this.remoteFile = remoteFile;
        notifyPropertyChanged(BR.remoteFile);
    }

    @Bindable
    public List<String> getComplimentsMorning() {
        return complimentsMorning;
    }

    @Bindable
    public List<String> getComplimentsAfternoon() {
        return complimentsAfternoon;
    }

    @Bindable
    public List<String> getComplimentsEvening() {
        return complimentsEvening;
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        if(fragment == null) {
            fragment = new ComplimentsSettingsFragment();
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

        if(this.updateInterval != null) {
            config.put(KEY_DATA_UPDATE_INTERVALL, this.updateInterval * 1000 * 60);
        }

        if(this.fadeSpeed != null) {
            config.put(KEY_DATA_FADE_SPEED, this.fadeSpeed);
        }

        if(Utils.isNotEmtpy(this.remoteFile)) {
            config.put(KEY_DATA_REMOTE_FILE, this.remoteFile);
        }

        JSONObject compliments = new JSONObject();

        if(!this.complimentsMorning.isEmpty() && !this.complimentsMorning.equals(COMPLIMENTS_DEFAULT_MORNING)) {
            JSONArray array = new JSONArray();

            for(String compliment : this.complimentsMorning) {
                array.put(compliment);
            }

            compliments.put(KEY_DATA_COMPLIMENTS_MORNING, array);
        }

        if(!this.complimentsAfternoon.isEmpty() && !this.complimentsAfternoon.equals(COMPLIMENTS_DEFAULT_AFTERNOON)) {
            JSONArray array = new JSONArray();

            for(String compliment : this.complimentsAfternoon) {
                array.put(compliment);
            }

            compliments.put(KEY_DATA_COMPLIMENTS_AFTERNOON, array);
        }

        if(!this.complimentsEvening.isEmpty() && !this.complimentsEvening.equals(COMPLIMENTS_DEFAULT_EVENING)) {
            JSONArray array = new JSONArray();

            for(String compliment : this.complimentsEvening) {
                array.put(compliment);
            }

            compliments.put(KEY_DATA_COMPLIMENTS_EVENING, array);
        }

        if(compliments.length() > 0) {
            config.put(KEY_DATA_COMPLIMENTS, compliments);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ComplimentsMagicMirrorModule that = (ComplimentsMagicMirrorModule) o;

        if (getUpdateInterval() != null ? !getUpdateInterval().equals(that.getUpdateInterval()) : that.getUpdateInterval() != null)
            return false;
        if (getFadeSpeed() != null ? !getFadeSpeed().equals(that.getFadeSpeed()) : that.getFadeSpeed() != null)
            return false;
        if (getRemoteFile() != null ? !getRemoteFile().equals(that.getRemoteFile()) : that.getRemoteFile() != null)
            return false;
        if (getComplimentsMorning() != null ? !getComplimentsMorning().equals(that.getComplimentsMorning()) : that.getComplimentsMorning() != null)
            return false;
        if (getComplimentsAfternoon() != null ? !getComplimentsAfternoon().equals(that.getComplimentsAfternoon()) : that.getComplimentsAfternoon() != null)
            return false;
        if (getComplimentsEvening() != null ? !getComplimentsEvening().equals(that.getComplimentsEvening()) : that.getComplimentsEvening() != null)
            return false;
        return fragment != null ? fragment.equals(that.fragment) : that.fragment == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getUpdateInterval() != null ? getUpdateInterval().hashCode() : 0);
        result = 31 * result + (getFadeSpeed() != null ? getFadeSpeed().hashCode() : 0);
        result = 31 * result + (getRemoteFile() != null ? getRemoteFile().hashCode() : 0);
        result = 31 * result + (getComplimentsMorning() != null ? getComplimentsMorning().hashCode() : 0);
        result = 31 * result + (getComplimentsAfternoon() != null ? getComplimentsAfternoon().hashCode() : 0);
        result = 31 * result + (getComplimentsEvening() != null ? getComplimentsEvening().hashCode() : 0);
        result = 31 * result + (fragment != null ? fragment.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.updateInterval);
        dest.writeValue(this.fadeSpeed);
        dest.writeStringList(this.complimentsMorning);
        dest.writeStringList(this.complimentsAfternoon);
        dest.writeStringList(this.complimentsEvening);
        dest.writeString(remoteFile);
    }

    private ComplimentsMagicMirrorModule(Parcel in) {
        super(in);
        this.updateInterval = (Integer) in.readValue(Integer.class.getClassLoader());
        this.fadeSpeed = (Integer) in.readValue(Integer.class.getClassLoader());
        this.complimentsMorning = in.createStringArrayList();
        this.complimentsAfternoon = in.createStringArrayList();
        this.complimentsEvening = in.createStringArrayList();
        this.remoteFile = in.readString();
    }

    public static final Creator<ComplimentsMagicMirrorModule> CREATOR = new Creator<ComplimentsMagicMirrorModule>() {
        @Override
        public ComplimentsMagicMirrorModule createFromParcel(Parcel source) {
            return new ComplimentsMagicMirrorModule(source);
        }

        @Override
        public ComplimentsMagicMirrorModule[] newArray(int size) {
            return new ComplimentsMagicMirrorModule[size];
        }
    };
}
