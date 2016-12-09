package com.blublabs.magicmirror.settings.mirror.modules.clock;

import android.databinding.Bindable;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;

import com.blublabs.magicmirror.BR;
import com.blublabs.magicmirror.common.Utils;
import com.blublabs.magicmirror.settings.mirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrs on 03.10.2016.
 */

public class ClockMagicMirrorModule extends MagicMirrorModule {

    public enum TimeFormat{
        Format12("12"),
        Format24("24"),
        Config("Default Config");

        private final String text;

        TimeFormat(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return getText();
        }

        public static TimeFormat from(String timeFormat) {

            if(timeFormat == null) {
                return TimeFormat.Config;
            }

            for(TimeFormat value : TimeFormat.values()) {
                if(timeFormat.equals(value.getText())) {
                    return value;
                }
            }

            return TimeFormat.Config;
        }
    }

    public enum DisplayType {
        digital,
        analog,
        both;

        public static DisplayType from(String displayType) {

            if(displayType == null) {
                return null;
            }

            for(DisplayType value : DisplayType.values()) {
                if(displayType.equals(value.toString())) {
                    return value;
                }
            }

            return null;
        }
    }

    public enum AnalogFace{
        simple("simple"),
        none("none"),
        face_001("face-001"),
        face_002("face-002"),
        face_003("face-003"),
        face_004("face-004"),
        face_005("face-005"),
        face_006("face-006"),
        face_007("face-007"),
        face_008("face-008"),
        face_009("face-009"),
        face_010("face-010"),
        face_011("face-011"),
        face_012("face-012");

        private final String text;

        AnalogFace(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return getText();
        }

        public static AnalogFace from(String analogFace) {

            if(analogFace == null) {
                return null;
            }

            for(AnalogFace value : AnalogFace.values()) {
                if(analogFace.equals(value.getText())) {
                    return value;
                }
            }

            return null;
        }
    }

    public enum AnalogPlacement {
        top,
        right,
        bottom,
        left;

        public static AnalogPlacement from(String displayType) {

            if(displayType == null) {
                return null;
            }

            for(AnalogPlacement value : AnalogPlacement.values()) {
                if(displayType.equals(value.toString())) {
                    return value;
                }
            }

            return null;
        }
    }

    public enum AnalogDatePlacement {
        top,
        bottom;

        public static AnalogDatePlacement from(String displayType) {

            if(displayType == null) {
                return null;
            }

            for(AnalogDatePlacement value : AnalogDatePlacement.values()) {
                if(displayType.equals(value.toString())) {
                    return value;
                }
            }

            return null;
        }
    }

    private static final String KEY_DATA_TIME_FORMAT = "timeFormat";
    private static final String KEY_DATA_DISPLAY_SECONDS = "displaySeconds";
    private static final String KEY_DATA_SHOW_PERIOD = "showPeriod";
    private static final String KEY_DATA_SHOW_PERIOD_UPPER = "showPeriodUpper";
    private static final String KEY_DATA_CLOCK_BOLD = "clockBold";
    private static final String KEY_DATA_SHOW_DATE = "showDate";
    private static final String KEY_DATA_DISPLAY_TYPE = "displayType";
    private static final String KEY_DATA_ANALOG_SIZE = "analogSize";
    private static final String KEY_DATA_ANALOG_FACE = "analogFace";
    private static final String KEY_DATA_SECONDS_COLOR = "secondsColor";
    private static final String KEY_DATA_ANALOG_PLACEMENT = "analogPlacement";
    private static final String KEY_DATA_ANALOG_DATE_PLACEMENT = "analogShowDate";

    private TimeFormat timeFormat = TimeFormat.Config;
    private boolean displaySeconds = true;
    private boolean showPeriod = true;
    private boolean showPeriodUpper = false;
    private boolean clockBold = false;
    private boolean showDate = true;
    private DisplayType displayType = DisplayType.digital;
    private Integer analogSize = null;
    private AnalogFace analogFace = AnalogFace.simple;
    private Integer secondsColor = Color.parseColor("#888888");
    private AnalogPlacement analogPlacement = AnalogPlacement.bottom;
    private AnalogDatePlacement analogDatePlacement = AnalogDatePlacement.top;
    private boolean analogShowDate = true;

    private ClockSettingsFragment fragment;

    public ClockMagicMirrorModule(String name) {
        super(name);
    }

    @Override
    public void setData(JSONObject data) throws JSONException {
        super.setData(data);

        if(data.has(KEY_DATA_CONFIG)) {

            JSONObject config = data.getJSONObject(KEY_DATA_CONFIG);

            if(config.has(KEY_DATA_TIME_FORMAT)) {
                this.timeFormat = TimeFormat.from(config.getString(KEY_DATA_TIME_FORMAT));
            }

            if(config.has(KEY_DATA_DISPLAY_SECONDS)) {
                this.displaySeconds = config.getBoolean(KEY_DATA_DISPLAY_SECONDS);
            }

            if(config.has(KEY_DATA_SHOW_PERIOD)) {
                this.showPeriod = config.getBoolean(KEY_DATA_SHOW_PERIOD);
            }

            if(config.has(KEY_DATA_SHOW_PERIOD_UPPER)) {
                this.showPeriodUpper = config.getBoolean(KEY_DATA_SHOW_PERIOD_UPPER);
            }

            if(config.has(KEY_DATA_CLOCK_BOLD)) {
                this.clockBold = config.getBoolean(KEY_DATA_CLOCK_BOLD);
            }

            if(config.has(KEY_DATA_SHOW_DATE)) {
                this.showDate = config.getBoolean(KEY_DATA_SHOW_DATE);
            }

            if(config.has(KEY_DATA_DISPLAY_TYPE)) {
                this.displayType = DisplayType.from(config.getString(KEY_DATA_DISPLAY_TYPE));
            }

            if(config.has(KEY_DATA_ANALOG_SIZE)) {
                String size = config.getString(KEY_DATA_ANALOG_SIZE).replace("px", "");

                this.analogSize = Integer.parseInt(size);
            }

            if(config.has(KEY_DATA_ANALOG_FACE)) {
                this.analogFace = AnalogFace.from(config.getString(KEY_DATA_ANALOG_FACE));
            }

            if(config.has(KEY_DATA_SECONDS_COLOR)) {
                this.secondsColor = Color.parseColor(config.getString(KEY_DATA_SECONDS_COLOR));
            }

            if(config.has(KEY_DATA_ANALOG_PLACEMENT)) {
                this.analogPlacement = AnalogPlacement.from(config.getString(KEY_DATA_ANALOG_PLACEMENT));
            }

            if(config.has(KEY_DATA_ANALOG_DATE_PLACEMENT)) {
                if(config.get(KEY_DATA_ANALOG_DATE_PLACEMENT) instanceof Boolean) {
                    this.analogShowDate = config.getBoolean(KEY_DATA_ANALOG_DATE_PLACEMENT);
                }
                else {
                    this.analogDatePlacement = AnalogDatePlacement.from(config.getString(KEY_DATA_ANALOG_DATE_PLACEMENT));
                }
            }
        }
    }

    @Bindable
    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(TimeFormat timeFormat) {

        if(timeFormat == this.timeFormat) {
            return;
        }

        this.timeFormat = timeFormat;
        notifyPropertyChanged(BR.timeFormat);
    }

    @Bindable
    public boolean isDisplaySeconds() {
        return displaySeconds;
    }

    public void setDisplaySeconds(boolean displaySeconds) {

        if(displaySeconds == this.displaySeconds) {
            return;
        }

        this.displaySeconds = displaySeconds;
        notifyPropertyChanged(BR.displaySeconds);
    }

    @Bindable
    public boolean isShowPeriod() {
        return showPeriod;
    }

    public void setShowPeriod(boolean showPeriod) {

        if(showPeriod == this.showPeriod) {
            return;
        }

        this.showPeriod = showPeriod;
        notifyPropertyChanged(BR.showPeriod);
    }

    @Bindable
    public boolean isShowPeriodUpper() {
        return showPeriodUpper;
    }

    public void setShowPeriodUpper(boolean showPeriodUpper) {

        if(showPeriodUpper == this.showPeriodUpper) {
            return;
        }

        this.showPeriodUpper = showPeriodUpper;
        notifyPropertyChanged(BR.showPeriodUpper);
    }

    @Bindable
    public boolean isClockBold() {
        return clockBold;
    }

    public void setClockBold(boolean clockBold) {

        if(clockBold == this.clockBold) {
            return;
        }

        this.clockBold = clockBold;
        notifyPropertyChanged(BR.clockBold);
    }

    @Bindable
    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {

        if(showDate == this.showDate) {
            return;
        }

        this.showDate = showDate;
        notifyPropertyChanged(BR.showDate);
    }

    @Bindable
    public DisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(DisplayType displayType) {

        if(displayType == this.displayType) {
            return;
        }

        this.displayType = displayType;
        notifyPropertyChanged(BR.displayType);
    }

    @Bindable
    public Integer getAnalogSize() {
        return analogSize;
    }

    public void setAnalogSize(Integer analogSize) {

        if(Utils.objectsEqual(this.analogSize, analogSize)) {
            return;
        }

        this.analogSize = analogSize;
        notifyPropertyChanged(BR.analogSize);
    }

    @Bindable
    public AnalogFace getAnalogFace() {
        return analogFace;
    }

    public void setAnalogFace(AnalogFace analogFace) {

        if(analogFace == this.analogFace) {
            return;
        }

        this.analogFace = analogFace;
        notifyPropertyChanged(BR.analogFace);
    }

    @Bindable
    public Integer getSecondsColor() {
        return secondsColor;
    }

    public void setSecondsColor(Integer secondsColor) {

        if(Utils.objectsEqual(this.secondsColor, secondsColor)) {
            return;
        }

        this.secondsColor = secondsColor;
        notifyPropertyChanged(BR.secondsColor);
    }

    @Bindable
    public AnalogPlacement getAnalogPlacement() {
        return analogPlacement;
    }

    public void setAnalogPlacement(AnalogPlacement analogPlacement) {

        if(analogPlacement == this.analogPlacement) {
            return;
        }

        this.analogPlacement = analogPlacement;
        notifyPropertyChanged(BR.analogPlacement);
    }

    @Bindable
    public AnalogDatePlacement getAnalogDatePlacement() {
        return analogDatePlacement;
    }

    public void setAnalogDatePlacement(AnalogDatePlacement analogDatePlacement) {

        if(analogDatePlacement == this.analogDatePlacement) {
            return;
        }

        this.analogDatePlacement = analogDatePlacement;
        notifyPropertyChanged(BR.analogDatePlacement);
    }

    @Bindable
    public boolean isAnalogShowDate() {
        return analogShowDate;
    }

    public void setAnalogShowDate(boolean analogShowDate) {

        if(analogShowDate == this.analogShowDate) {
            return;
        }

        this.analogShowDate = analogShowDate;
        notifyPropertyChanged(BR.analogShowDate);
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        if(fragment == null) {
            fragment = new ClockSettingsFragment();
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

        if(timeFormat != TimeFormat.Config) {
            config.put(KEY_DATA_TIME_FORMAT, Integer.parseInt(timeFormat.getText()));
        }

        if(!displaySeconds) {
            config.put(KEY_DATA_DISPLAY_SECONDS, false);
        }

        if(!showPeriod) {
            config.put(KEY_DATA_SHOW_PERIOD, false);
        }

        if(showPeriodUpper) {
            config.put(KEY_DATA_SHOW_PERIOD_UPPER, true);
        }

        if(clockBold) {
            config.put(KEY_DATA_CLOCK_BOLD, true);
        }

        if(!showDate) {
            config.put(KEY_DATA_SHOW_DATE, false);
        }

        if(displayType != DisplayType.digital) {
            config.put(KEY_DATA_DISPLAY_TYPE, displayType);
        }

        if(analogSize != null) {
            config.put(KEY_DATA_ANALOG_SIZE, analogSize + "px");
        }

        if(analogFace != AnalogFace.simple) {
            config.put(KEY_DATA_ANALOG_FACE, analogFace);
        }

        if(secondsColor != Color.parseColor("#888888")) {
            config.put(KEY_DATA_SECONDS_COLOR, String.format("#%06X", 0xFFFFFF & secondsColor));
        }

        if(analogPlacement != AnalogPlacement.bottom) {
            config.put(KEY_DATA_ANALOG_PLACEMENT, analogPlacement);
        }

        if(!analogShowDate) {
            config.put(KEY_DATA_ANALOG_DATE_PLACEMENT, false);
        }
        else if(analogDatePlacement != AnalogDatePlacement.top) {
            config.put(KEY_DATA_ANALOG_DATE_PLACEMENT, analogDatePlacement);
        }

        if(config.length() > 0) {
            json.put(KEY_DATA_CONFIG, config);
        }

        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClockMagicMirrorModule that = (ClockMagicMirrorModule) o;

        if (isDisplaySeconds() != that.isDisplaySeconds()) return false;
        if (isShowPeriod() != that.isShowPeriod()) return false;
        if (isShowPeriodUpper() != that.isShowPeriodUpper()) return false;
        if (isClockBold() != that.isClockBold()) return false;
        if (isShowDate() != that.isShowDate()) return false;
        if (isAnalogShowDate() != that.isAnalogShowDate()) return false;
        if (getTimeFormat() != that.getTimeFormat()) return false;
        if (getDisplayType() != that.getDisplayType()) return false;
        if (getAnalogSize() != null ? !getAnalogSize().equals(that.getAnalogSize()) : that.getAnalogSize() != null)
            return false;
        if (getAnalogFace() != that.getAnalogFace()) return false;
        if (getSecondsColor() != null ? !getSecondsColor().equals(that.getSecondsColor()) : that.getSecondsColor() != null)
            return false;
        if (getAnalogPlacement() != that.getAnalogPlacement()) return false;
        return getAnalogDatePlacement() == that.getAnalogDatePlacement();

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getTimeFormat().hashCode();
        result = 31 * result + (isDisplaySeconds() ? 1 : 0);
        result = 31 * result + (isShowPeriod() ? 1 : 0);
        result = 31 * result + (isShowPeriodUpper() ? 1 : 0);
        result = 31 * result + (isClockBold() ? 1 : 0);
        result = 31 * result + (isShowDate() ? 1 : 0);
        result = 31 * result + getDisplayType().hashCode();
        result = 31 * result + (getAnalogSize() != null ? getAnalogSize().hashCode() : 0);
        result = 31 * result + getAnalogFace().hashCode();
        result = 31 * result + (getSecondsColor() != null ? getSecondsColor().hashCode() : 0);
        result = 31 * result + getAnalogPlacement().hashCode();
        result = 31 * result + getAnalogDatePlacement().hashCode();
        result = 31 * result + (isAnalogShowDate() ? 1 : 0);
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.timeFormat == null ? -1 : this.timeFormat.ordinal());
        dest.writeByte(this.displaySeconds ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showPeriod ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showPeriodUpper ? (byte) 1 : (byte) 0);
        dest.writeByte(this.clockBold ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showDate ? (byte) 1 : (byte) 0);
        dest.writeInt(this.displayType == null ? -1 : this.displayType.ordinal());
        dest.writeValue(this.analogSize);
        dest.writeInt(this.analogFace == null ? -1 : this.analogFace.ordinal());
        dest.writeValue(this.secondsColor);
        dest.writeInt(this.analogPlacement == null ? -1 : this.analogPlacement.ordinal());
        dest.writeInt(this.analogDatePlacement == null ? -1 : this.analogDatePlacement.ordinal());
        dest.writeByte(this.analogShowDate ? (byte) 1 : (byte) 0);
    }

    private ClockMagicMirrorModule(Parcel in) {
        super(in);
        int tmpTimeFormat = in.readInt();
        this.timeFormat = tmpTimeFormat == -1 ? null : TimeFormat.values()[tmpTimeFormat];
        this.displaySeconds = in.readByte() != 0;
        this.showPeriod = in.readByte() != 0;
        this.showPeriodUpper = in.readByte() != 0;
        this.clockBold = in.readByte() != 0;
        this.showDate = in.readByte() != 0;
        int tmpDisplayType = in.readInt();
        this.displayType = tmpDisplayType == -1 ? null : DisplayType.values()[tmpDisplayType];
        this.analogSize = (Integer) in.readValue(Integer.class.getClassLoader());
        int tmpAnalogFace = in.readInt();
        this.analogFace = tmpAnalogFace == -1 ? null : AnalogFace.values()[tmpAnalogFace];
        this.secondsColor = (Integer) in.readValue(Integer.class.getClassLoader());
        int tmpAnalogPlacement = in.readInt();
        this.analogPlacement = tmpAnalogPlacement == -1 ? null : AnalogPlacement.values()[tmpAnalogPlacement];
        int tmpAnalogDatePlacement = in.readInt();
        this.analogDatePlacement = tmpAnalogDatePlacement == -1 ? null : AnalogDatePlacement.values()[tmpAnalogDatePlacement];
        this.analogShowDate = in.readByte() != 0;
    }

    public static final Creator<ClockMagicMirrorModule> CREATOR = new Creator<ClockMagicMirrorModule>() {
        @Override
        public ClockMagicMirrorModule createFromParcel(Parcel source) {
            return new ClockMagicMirrorModule(source);
        }

        @Override
        public ClockMagicMirrorModule[] newArray(int size) {
            return new ClockMagicMirrorModule[size];
        }
    };
}
