package com.blublabs.magicmirror.modules;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.blublabs.magicmirror.BR;
import com.blublabs.magicmirror.common.Utils;
import com.blublabs.magicmirror.modules.alert.AlertMagicMirrorModule;
import com.blublabs.magicmirror.modules.calendar.CalendarMagicMirrorModule;
import com.blublabs.magicmirror.modules.helloworld.HelloWorldMagicMirrorModule;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrs on 30.08.2016.
 *
 */
public abstract class MagicMirrorModule extends BaseObservable implements Parcelable {

    protected enum PositionRegion {
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

    private String name = null;
    private boolean active;
    private PositionRegion position = PositionRegion.none;
    private String header = null;

    private Runnable updateHandler = null;

    private final static String KEY_DATA_NAME = "module";
    private final static String KEY_DATA_POSITION = "position";
    private final static String KEY_DATA_HEADER = "header";
    protected final static String KEY_DATA_CONFIG = "config";

    public MagicMirrorModule(JSONObject data) throws JSONException {
        this.name = data.getString(KEY_DATA_NAME);
        this.active = true;
        if(data.has(KEY_DATA_POSITION)) {
            this.position = PositionRegion.from(data.getString(KEY_DATA_POSITION));
        }
        if(data.has(KEY_DATA_HEADER)) {
            header = data.getString(KEY_DATA_HEADER);
        }
    }

    protected MagicMirrorModule(Parcel source) {
        this.name = source.readString();
        this.active = source.readByte() == 1;
        this.position = PositionRegion.values()[source.readInt()];
        this.header = source.readString();
    }

    public void setUpdateHandler(Runnable runnable){
        this.updateHandler = runnable;
    }

    public Runnable getUpdateHandler() {
        return updateHandler;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {

        if(active == this.active) {
            return;
        }

        this.active = active;
        notifyPropertyChanged(BR.active);
    }

    @Bindable
    public PositionRegion getPosition() {
        return position;
    }

    public void setPosition(PositionRegion position) {
        this.position = position;
        notifyPropertyChanged(BR.position);
    }

    @Bindable
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
        notifyPropertyChanged(BR.header);
    }

    public abstract ModuleSettingsFragment getAdditionalSettingsFragment();

    public JSONObject toJson() throws JSONException {

        JSONObject json = new JSONObject();

        json.put(KEY_DATA_NAME, getName());

        if(getPosition() != PositionRegion.none) {
            json.put(KEY_DATA_POSITION, getPosition());
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
        dest.writeInt(position.ordinal());
        dest.writeString(header);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MagicMirrorModule that = (MagicMirrorModule) o;

        if (isActive() != that.isActive()) return false;
        if (!getName().equals(that.getName())) return false;
        if (getPosition() != that.getPosition()) return false;
        return getHeader() != null ? getHeader().equals(that.getHeader()) : that.getHeader() == null;

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + (isActive() ? 1 : 0);
        result = 31 * result + (getPosition() != null ? getPosition().hashCode() : 0);
        result = 31 * result + (getHeader() != null ? getHeader().hashCode() : 0);
        return result;
    }

    public static MagicMirrorModule getModuleForData(JSONObject data) throws JSONException {

        final String name = data.getString("module");

        switch(name) {
            case "alert":
                return new AlertMagicMirrorModule(data);
            case "hello_world":
                return new HelloWorldMagicMirrorModule(data);
            case "calendar":
                return new CalendarMagicMirrorModule(data);
            default:
                return null;
        }
    }
}
