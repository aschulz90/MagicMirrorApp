package com.blublabs.magicmirror.settings.mirror.modules.calendar;

import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.os.Parcel;

import com.android.databinding.library.baseAdapters.BR;
import com.blublabs.magicmirror.utils.Utils;
import com.blublabs.magicmirror.settings.mirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by andrs on 03.10.2016.
 */

public class CalendarMagicMirrorModule extends MagicMirrorModule {

    public enum TimeFormat{
        absolute,
        relative;

        public static TimeFormat from(String timeFormat) {

            if(timeFormat == null) {
                return null;
            }

            for(TimeFormat value : TimeFormat.values()) {
                if(timeFormat.equals(value.toString())) {
                    return value;
                }
            }

            return null;
        }
    }

    private static final String KEY_MAXIMUM_ENTRIES = "maximumEntries";
    private static final String KEY_MAXIMUM_NUMBER_OF_DAYS = "maximumNumberOfDays";
    private static final String KEY_DISPLAY_SYMBOL = "displaySymbol";
    private static final String KEY_DEFAULT_SYMBOL = "defaultSymbol";
    private static final String KEY_MAX_TITLE_LENGTH = "maxTitleLength";
    private static final String KEY_FETCH_INTERVALL = "fetchInterval";
    private static final String KEY_ANIMATION_SPEED = "animationSpeed";
    private static final String KEY_FADE = "fade";
    private static final String KEY_FADE_POINT = "fadePoint";
    private static final String KEY_DISPLAY_REPEATING_COUNT_TITLE = "displayRepeatingCountTitle";
    private static final String KEY_DATE_FORMAT = "dateFormat";
    private static final String KEY_TIME_FORMAT = "timeFormat";
    private static final String KEY_GET_RELATIVE = "getRelative";
    private static final String KEY_URGENCY = "urgency";
    private static final String KEY_TITLE_REPLACE_MAP = "titleReplace";
    private static final String KEY_CALENDARS = "calendars";
    private static final String KEY_BROADCAST_EVENTS = "broadcastEvents";
    private static final String KEY_HIDE_PRIVATE = "hidePrivate";

    private Map<String, String> titleReplaceMap = new HashMap<>();
    private final ObservableArrayList<Calendar> calendars = new ObservableArrayList<>();
    private Integer maximumEntries = null;
    private Integer maximumNumberOfDays = null;
    private boolean displaySymbol = true;
    private boolean broadcastEvents = true;
    private boolean hidePrivate = false;
    private String defaultSymbol = "";
    private Integer maxTitleLength = null;
    private Integer fetchInterval = null;
    private Integer animationSpeed = null;
    private boolean fade = true;
    private Double fadePoint = null;
    private boolean displayRepeatingCountTitle = false;
    private TimeFormat timeFormat = TimeFormat.relative;
    private String dateFormat = "";
    private Integer urgency = null;
    private Integer getRelative = null;

    private CalendarSettingsFragment fragment;

    public CalendarMagicMirrorModule(String name) {
        super(name);
    }

    @Override
    public void setData(JSONObject data) throws JSONException {

        if(data.has(KEY_DATA_CONFIG)) {
            JSONObject config = data.getJSONObject(KEY_DATA_CONFIG);

            if (config.has(KEY_MAXIMUM_ENTRIES)) {
                maximumEntries = config.getInt(KEY_MAXIMUM_ENTRIES);
            }

            if (config.has(KEY_MAXIMUM_NUMBER_OF_DAYS)) {
                maximumNumberOfDays = config.getInt(KEY_MAXIMUM_NUMBER_OF_DAYS);
            }

            if (config.has(KEY_DISPLAY_SYMBOL)) {
                displaySymbol = config.getBoolean(KEY_DISPLAY_SYMBOL);
            }

            if (config.has(KEY_DEFAULT_SYMBOL)) {
                defaultSymbol = config.getString(KEY_DEFAULT_SYMBOL);
            }

            if (config.has(KEY_MAX_TITLE_LENGTH)) {
                maxTitleLength = config.getInt(KEY_MAX_TITLE_LENGTH);
            }

            if (config.has(KEY_FETCH_INTERVALL)) {
                fetchInterval = config.getInt(KEY_FETCH_INTERVALL);
            }

            if (config.has(KEY_ANIMATION_SPEED)) {
                animationSpeed = config.getInt(KEY_ANIMATION_SPEED);
            }

            if (config.has(KEY_FADE)) {
                fade = config.getBoolean(KEY_FADE);
            }

            if (config.has(KEY_FADE_POINT)) {
                fadePoint = config.getDouble(KEY_FADE_POINT);
            }

            if (config.has(KEY_DISPLAY_REPEATING_COUNT_TITLE)) {
                displayRepeatingCountTitle = config.getBoolean(KEY_DISPLAY_REPEATING_COUNT_TITLE);
            }

            if (config.has(KEY_TIME_FORMAT)) {
                timeFormat = TimeFormat.from(config.getString(KEY_TIME_FORMAT));
            }

            if (config.has(KEY_URGENCY)) {
                urgency = config.getInt(KEY_URGENCY);
            }

            if (config.has(KEY_GET_RELATIVE)) {
                getRelative = config.getInt(KEY_GET_RELATIVE);
            }

            if (config.has(KEY_BROADCAST_EVENTS)) {
                broadcastEvents = config.getBoolean(KEY_BROADCAST_EVENTS);
            }

            if (config.has(KEY_HIDE_PRIVATE)) {
                hidePrivate = config.getBoolean(KEY_HIDE_PRIVATE);
            }

            if (config.has(KEY_DATE_FORMAT)) {
                dateFormat = config.getString(KEY_DATE_FORMAT);
            }

            if (config.has(KEY_TITLE_REPLACE_MAP)) {
                JSONObject json = config.getJSONObject(KEY_TITLE_REPLACE_MAP);
                Iterator<String> nameItr = json.keys();
                while (nameItr.hasNext()) {
                    String name = nameItr.next();
                    titleReplaceMap.put(name, json.getString(name));
                }
            }

            if (config.has(KEY_CALENDARS)) {

                JSONArray calendarsArray = config.getJSONArray(KEY_CALENDARS);

                for (int i = 0; i < calendarsArray.length(); i++) {
                    calendars.add(new Calendar(calendarsArray.getJSONObject(i)));
                }
            }
        }

        super.setData(data);
    }

    @Bindable
    public Map<String, String> getTitleReplaceMap() {
        return titleReplaceMap;
    }

    @Bindable
    public List<Calendar> getCalendars() {
        return calendars;
    }

    @Bindable
    public Integer getMaximumEntries() {
        return maximumEntries;
    }

    public void setMaximumEntries(Integer maximumEntries) {

        if(Utils.objectsEqual(this.maximumEntries, maximumEntries)) {
            return;
        }

        this.maximumEntries = maximumEntries;
        notifyPropertyChanged(BR.maximumEntries);
    }

    @Bindable
    public Integer getMaximumNumberOfDays() {
        return maximumNumberOfDays;
    }

    public void setMaximumNumberOfDays(Integer maximumNumberOfDays) {

        if(Utils.objectsEqual(this.maximumNumberOfDays, maximumNumberOfDays)) {
            return;
        }

        this.maximumNumberOfDays = maximumNumberOfDays;
        notifyPropertyChanged(BR.maximumNumberOfDays);
    }

    @Bindable
    public boolean isDisplaySymbol() {
        return displaySymbol;
    }

    public void setDisplaySymbol(boolean displaySymbol) {

        if(displaySymbol == this.displaySymbol) {
            return;
        }

        this.displaySymbol = displaySymbol;
        notifyPropertyChanged(BR.displaySymbol);
    }

    @Bindable
    public String getDefaultSymbol() {
        return defaultSymbol;
    }

    public void setDefaultSymbol(String defaultSymbol) {

        if(Utils.objectsEqual(this.defaultSymbol, defaultSymbol)) {
            return;
        }

        this.defaultSymbol = defaultSymbol;
        notifyPropertyChanged(BR.defaultSymbol);
    }

    @Bindable
    public Integer getMaxTitleLength() {
        return maxTitleLength;
    }

    public void setMaxTitleLength(Integer maxTitleLength) {

        if(Utils.objectsEqual(this.maxTitleLength, maxTitleLength)) {
            return;
        }

        this.maxTitleLength = maxTitleLength;
        notifyPropertyChanged(BR.maxTitleLength);
    }

    @Bindable
    public Integer getFetchInterval() {
        return fetchInterval;
    }

    public void setFetchInterval(Integer fetchInterval) {

        if(Utils.objectsEqual(this.fetchInterval, fetchInterval)) {
            return;
        }

        this.fetchInterval = fetchInterval;
        notifyPropertyChanged(BR.fetchInterval);
    }

    @Bindable
    public Integer getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(Integer animationSpeed) {

        if(Utils.objectsEqual(this.animationSpeed, animationSpeed)) {
            return;
        }

        this.animationSpeed = animationSpeed;
        notifyPropertyChanged(BR.animationSpeed);
    }

    @Bindable
    public boolean isFade() {
        return fade;
    }

    public void setFade(boolean fade) {

        if(fade == this.fade) {
            return;
        }

        this.fade = fade;
        notifyPropertyChanged(BR.fade);
    }

    @Bindable
    public Double getFadePoint() {
        return fadePoint;
    }

    public void setFadePoint(Double fadePoint) {

        if(Utils.objectsEqual(this.fadePoint, fadePoint)) {
            return;
        }

        this.fadePoint = fadePoint;
        notifyPropertyChanged(BR.fadePoint);
    }

    @Bindable
    public boolean isDisplayRepeatingCountTitle() {
        return displayRepeatingCountTitle;
    }

    public void setDisplayRepeatingCountTitle(boolean displayRepeatingCountTitle) {

        if(displayRepeatingCountTitle == this.displayRepeatingCountTitle) {
            return;
        }

        this.displayRepeatingCountTitle = displayRepeatingCountTitle;
        notifyPropertyChanged(BR.displayRepeatingCountTitle);
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
    public Integer getUrgency() {
        return urgency;
    }

    public void setUrgency(Integer urgency) {

        if(Utils.objectsEqual(this.urgency, urgency)) {
            return;
        }

        this.urgency = urgency;
        notifyPropertyChanged(BR.urgency);
    }

    @Bindable
    public boolean isBroadcastEvents() {
        return broadcastEvents;
    }

    public void setBroadcastEvents(boolean broadcastEvents) {
        if(broadcastEvents == this.broadcastEvents) {
            return;
        }

        this.broadcastEvents = broadcastEvents;
        notifyPropertyChanged(BR.broadcastEvents);
    }

    @Bindable
    public boolean isHidePrivate() {
        return hidePrivate;
    }

    public void setHidePrivate(boolean hidePrivate) {
        if(hidePrivate == this.hidePrivate) {
            return;
        }

        this.hidePrivate = hidePrivate;
        notifyPropertyChanged(BR.hidePrivate);
    }

    @Bindable
    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        if(Utils.objectsEqual(this.dateFormat, dateFormat)) {
            return;
        }

        this.dateFormat = dateFormat;
        notifyPropertyChanged(BR.dateFormat);
    }

    @Bindable
    public Integer getGetRelative() {
        return getRelative;
    }

    public void setGetRelative(Integer getRelative) {
        if(Utils.objectsEqual(this.getRelative, getRelative)) {
            return;
        }

        this.getRelative = getRelative;
        notifyPropertyChanged(BR.getRelative);
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        if(fragment == null) {
            fragment = new CalendarSettingsFragment();
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

        if(!titleReplaceMap.isEmpty()) {
            JSONObject map = new JSONObject();

            for(Map.Entry<String, String> entry : titleReplaceMap.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }

            config.put(KEY_TITLE_REPLACE_MAP, map);
        }

        if(!calendars.isEmpty()) {

            JSONArray array = new JSONArray();

            for (Calendar calendar : calendars) {
                array.put(calendar.toJSON());
            }

            config.put(KEY_CALENDARS, array);
        }

        if(maximumEntries != null) {
            config.put(KEY_MAXIMUM_ENTRIES, maximumEntries);
        }

        if(maximumNumberOfDays != null) {
            config.put(KEY_MAXIMUM_NUMBER_OF_DAYS, maximumNumberOfDays);
        }

        if(!displaySymbol) {
            config.put(KEY_DISPLAY_SYMBOL, false);
        }

        if(!Utils.isEmpty(defaultSymbol)) {
            config.put(KEY_DEFAULT_SYMBOL, defaultSymbol);
        }

        if(maxTitleLength != null) {
            config.put(KEY_MAX_TITLE_LENGTH, maxTitleLength);
        }

        if(fetchInterval != null) {
            config.put(KEY_FETCH_INTERVALL, fetchInterval);
        }

        if(animationSpeed != null) {
            config.put(KEY_ANIMATION_SPEED, animationSpeed);
        }

        if(!fade) {
            config.put(KEY_FADE, false);
        }

        if(fadePoint != null) {
            config.put(KEY_FADE_POINT, fadePoint);
        }

        if(displayRepeatingCountTitle) {
            config.put(KEY_DISPLAY_REPEATING_COUNT_TITLE, true);
        }

        if(timeFormat != TimeFormat.relative) {
            config.put(KEY_TIME_FORMAT, timeFormat);
        }

        if(urgency != null) {
            config.put(KEY_URGENCY, urgency);
        }

        if(Utils.isNotEmtpy(dateFormat)) {
            config.put(KEY_DATE_FORMAT, dateFormat);
        }

        if(getRelative != null) {
            config.put(KEY_GET_RELATIVE, getRelative);
        }

        if(!broadcastEvents) {
            config.put(KEY_BROADCAST_EVENTS, false);
        }

        if(hidePrivate) {
            config.put(KEY_HIDE_PRIVATE, true);
        }

        if(config.length() > 0) {
            json.put(KEY_DATA_CONFIG, config);
        }

        return json;
    }

    @Override
    public boolean parameterRequiresRefresh(int id) {
        switch (id) {
            case BR.fetchInterval:
            case BR.calendars:
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

        CalendarMagicMirrorModule that = (CalendarMagicMirrorModule) o;

        if (isDisplaySymbol() != that.isDisplaySymbol()) return false;
        if (isBroadcastEvents() != that.isBroadcastEvents()) return false;
        if (isHidePrivate() != that.isHidePrivate()) return false;
        if (isFade() != that.isFade()) return false;
        if (isDisplayRepeatingCountTitle() != that.isDisplayRepeatingCountTitle()) return false;
        if (getTitleReplaceMap() != null ? !getTitleReplaceMap().equals(that.getTitleReplaceMap()) : that.getTitleReplaceMap() != null)
            return false;
        if (getCalendars() != null ? !getCalendars().equals(that.getCalendars()) : that.getCalendars() != null)
            return false;
        if (getMaximumEntries() != null ? !getMaximumEntries().equals(that.getMaximumEntries()) : that.getMaximumEntries() != null)
            return false;
        if (getMaximumNumberOfDays() != null ? !getMaximumNumberOfDays().equals(that.getMaximumNumberOfDays()) : that.getMaximumNumberOfDays() != null)
            return false;
        if (getDefaultSymbol() != null ? !getDefaultSymbol().equals(that.getDefaultSymbol()) : that.getDefaultSymbol() != null)
            return false;
        if (getMaxTitleLength() != null ? !getMaxTitleLength().equals(that.getMaxTitleLength()) : that.getMaxTitleLength() != null)
            return false;
        if (getFetchInterval() != null ? !getFetchInterval().equals(that.getFetchInterval()) : that.getFetchInterval() != null)
            return false;
        if (getAnimationSpeed() != null ? !getAnimationSpeed().equals(that.getAnimationSpeed()) : that.getAnimationSpeed() != null)
            return false;
        if (getFadePoint() != null ? !getFadePoint().equals(that.getFadePoint()) : that.getFadePoint() != null)
            return false;
        if (getTimeFormat() != that.getTimeFormat()) return false;
        if (getDateFormat() != null ? !getDateFormat().equals(that.getDateFormat()) : that.getDateFormat() != null)
            return false;
        if (getUrgency() != null ? !getUrgency().equals(that.getUrgency()) : that.getUrgency() != null)
            return false;
        if (getGetRelative() != null ? !getGetRelative().equals(that.getGetRelative()) : that.getGetRelative() != null)
            return false;
        return fragment != null ? fragment.equals(that.fragment) : that.fragment == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getTitleReplaceMap() != null ? getTitleReplaceMap().hashCode() : 0);
        result = 31 * result + (getCalendars() != null ? getCalendars().hashCode() : 0);
        result = 31 * result + (getMaximumEntries() != null ? getMaximumEntries().hashCode() : 0);
        result = 31 * result + (getMaximumNumberOfDays() != null ? getMaximumNumberOfDays().hashCode() : 0);
        result = 31 * result + (isDisplaySymbol() ? 1 : 0);
        result = 31 * result + (isBroadcastEvents() ? 1 : 0);
        result = 31 * result + (isHidePrivate() ? 1 : 0);
        result = 31 * result + (getDefaultSymbol() != null ? getDefaultSymbol().hashCode() : 0);
        result = 31 * result + (getMaxTitleLength() != null ? getMaxTitleLength().hashCode() : 0);
        result = 31 * result + (getFetchInterval() != null ? getFetchInterval().hashCode() : 0);
        result = 31 * result + (getAnimationSpeed() != null ? getAnimationSpeed().hashCode() : 0);
        result = 31 * result + (isFade() ? 1 : 0);
        result = 31 * result + (getFadePoint() != null ? getFadePoint().hashCode() : 0);
        result = 31 * result + (isDisplayRepeatingCountTitle() ? 1 : 0);
        result = 31 * result + (getTimeFormat() != null ? getTimeFormat().hashCode() : 0);
        result = 31 * result + (getDateFormat() != null ? getDateFormat().hashCode() : 0);
        result = 31 * result + (getUrgency() != null ? getUrgency().hashCode() : 0);
        result = 31 * result + (getGetRelative() != null ? getGetRelative().hashCode() : 0);
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
        dest.writeInt(this.titleReplaceMap.size());
        for (Map.Entry<String, String> entry : this.titleReplaceMap.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeParcelableArray(calendars.toArray(new Calendar[calendars.size()]), 0);
        dest.writeValue(this.maximumEntries);
        dest.writeValue(this.maximumNumberOfDays);
        dest.writeByte(this.displaySymbol ? (byte) 1 : (byte) 0);
        dest.writeByte(this.broadcastEvents ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hidePrivate ? (byte) 1 : (byte) 0);
        dest.writeString(this.defaultSymbol);
        dest.writeValue(this.maxTitleLength);
        dest.writeValue(this.fetchInterval);
        dest.writeValue(this.animationSpeed);
        dest.writeByte(this.fade ? (byte) 1 : (byte) 0);
        dest.writeValue(this.fadePoint);
        dest.writeByte(this.displayRepeatingCountTitle ? (byte) 1 : (byte) 0);
        dest.writeInt(this.timeFormat == null ? -1 : this.timeFormat.ordinal());
        dest.writeString(this.dateFormat);
        dest.writeValue(this.urgency);
        dest.writeValue(this.getRelative);
    }

    private CalendarMagicMirrorModule(Parcel in) {
        super(in);
        int titleReplaceMapSize = in.readInt();
        this.titleReplaceMap = new HashMap<>(titleReplaceMapSize);
        for (int i = 0; i < titleReplaceMapSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.titleReplaceMap.put(key, value);
        }
        Calendar[] calendars = (Calendar[]) in.readParcelableArray(Calendar.class.getClassLoader());
        this.calendars.addAll(Arrays.asList(calendars));
        this.maximumEntries = (Integer) in.readValue(Integer.class.getClassLoader());
        this.maximumNumberOfDays = (Integer) in.readValue(Integer.class.getClassLoader());
        this.displaySymbol = in.readByte() != 0;
        this.broadcastEvents = in.readByte() != 0;
        this.hidePrivate = in.readByte() != 0;
        this.defaultSymbol = in.readString();
        this.maxTitleLength = (Integer) in.readValue(Integer.class.getClassLoader());
        this.fetchInterval = (Integer) in.readValue(Integer.class.getClassLoader());
        this.animationSpeed = (Integer) in.readValue(Integer.class.getClassLoader());
        this.fade = in.readByte() != 0;
        this.fadePoint = (Double) in.readValue(Double.class.getClassLoader());
        this.displayRepeatingCountTitle = in.readByte() != 0;
        int tmpTimeFormat = in.readInt();
        this.timeFormat = tmpTimeFormat == -1 ? null : TimeFormat.values()[tmpTimeFormat];
        this.dateFormat = in.readString();
        this.urgency = (Integer) in.readValue(Integer.class.getClassLoader());
        this.getRelative = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<CalendarMagicMirrorModule> CREATOR = new Creator<CalendarMagicMirrorModule>() {
        @Override
        public CalendarMagicMirrorModule createFromParcel(Parcel source) {
            return new CalendarMagicMirrorModule(source);
        }

        @Override
        public CalendarMagicMirrorModule[] newArray(int size) {
            return new CalendarMagicMirrorModule[size];
        }
    };
}
