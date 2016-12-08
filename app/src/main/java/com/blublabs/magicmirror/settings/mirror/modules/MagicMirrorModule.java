package com.blublabs.magicmirror.settings.mirror.modules;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.blublabs.magicmirror.BR;
import com.blublabs.magicmirror.common.Utils;
import com.blublabs.magicmirror.settings.mirror.modules.alert.AlertMagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.calendar.CalendarMagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.clock.ClockMagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.compliments.ComplimentsMagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.custom.CustomMagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.helloworld.HelloWorldMagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.news.NewsMagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.weather.WeatherMagicMirrorModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by andrs on 30.08.2016.
 *
 */
public abstract class MagicMirrorModule extends BaseObservable implements Parcelable {

    public enum PositionRegion {
        none,
        top_bar,
        bottom_bar,
        top_left,
        bottom_left,
        top_center,
        bottom_center,
        top_right,
        bottom_right,
        upper_third,
        middle_center,
        lower_third;

        public static PositionRegion from(String position) {

            if(position == null) {
                return null;
            }

            for(PositionRegion value : PositionRegion.values()) {
                if(position.equals(value.toString())) {
                    return value;
                }
            }

            return null;
        }
    }

    private String name = "";
    private boolean active;
    private PositionRegion region = PositionRegion.none;
    private String header = "";
    private final UUID id;

    private Runnable updateHandler = null;
    private boolean initialized = false;

    public final static String KEY_DATA_NAME = "module";
    private final static String KEY_DATA_POSITION = "position";
    private final static String KEY_DATA_HEADER = "header";
    public final static String KEY_DATA_CONFIG = "config";

    public MagicMirrorModule(String name) {
        this.name = name;
        this.active = false;
        this.id = UUID.randomUUID();
    }

    protected MagicMirrorModule(Parcel source) {
        this.name = source.readString();
        this.active = source.readByte() == 1;
        this.region = PositionRegion.values()[source.readInt()];
        this.header = source.readString();
        this.id = UUID.fromString(source.readString());
    }

    public void setData(JSONObject data) throws JSONException {
        if(data.has(KEY_DATA_POSITION)) {
            this.region = PositionRegion.from(data.getString(KEY_DATA_POSITION));
        }
        if(data.has(KEY_DATA_HEADER)) {
            header = data.getString(KEY_DATA_HEADER);
        }

        notifyChange();
    }

    public void setUpdateHandler(Runnable runnable){
        this.updateHandler = runnable;
    }

    public Runnable getUpdateHandler() {
        return updateHandler;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Bindable
    public PositionRegion getRegion() {
        return region;
    }

    public void setRegion(PositionRegion region) {

        if(region == this.region) {
            return;
        }

        this.region = region;
        notifyPropertyChanged(BR.position);
    }

    @Bindable
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {

        if(Utils.objectsEqual(this.header, header)) {
            return;
        }

        this.header = header;
        notifyPropertyChanged(BR.header);
    }

    public UUID getId() {
        return id;
    }

    public abstract ModuleSettingsFragment getAdditionalSettingsFragment();

    public JSONObject toJson() throws JSONException {

        JSONObject json = new JSONObject();

        json.put(KEY_DATA_NAME, getName());

        if(getRegion() != PositionRegion.none) {
            json.put(KEY_DATA_POSITION, getRegion());
        }

        if(!Utils.isEmpty(getHeader())) {
            json.put(KEY_DATA_HEADER, getHeader());
        }

        return json;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeInt(region.ordinal());
        dest.writeString(header);
        dest.writeString(id.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MagicMirrorModule that = (MagicMirrorModule) o;

        if (isActive() != that.isActive()) return false;
        if (!getId().equals(that.getId())) return false;
        if (!getName().equals(that.getName())) return false;
        if (getRegion() != that.getRegion()) return false;
        return getHeader() != null ? getHeader().equals(that.getHeader()) : that.getHeader() == null;

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + (isActive() ? 1 : 0);
        result = 31 * result + (getRegion() != null ? getRegion().hashCode() : 0);
        result = 31 * result + (getHeader() != null ? getHeader().hashCode() : 0);
        return result;
    }

    public static MagicMirrorModule getModuleForName(String name) {

        switch(name) {
            case "alert":
                return new AlertMagicMirrorModule(name);
            case "helloworld":
                return new HelloWorldMagicMirrorModule(name);
            case "calendar":
                return new CalendarMagicMirrorModule(name);
            case "clock":
                return new ClockMagicMirrorModule(name);
            case "compliments":
                return new ComplimentsMagicMirrorModule(name);
            case "newsfeed":
                return new NewsMagicMirrorModule(name);
            case "weatherforecast":
                return new WeatherMagicMirrorModule(name);
            default:
                return new CustomMagicMirrorModule(name);
        }
    }
}
