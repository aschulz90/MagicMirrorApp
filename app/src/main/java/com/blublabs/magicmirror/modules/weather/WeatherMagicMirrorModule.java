package com.blublabs.magicmirror.modules.weather;

import android.databinding.Bindable;
import android.os.Bundle;
import android.os.Parcel;

import com.blublabs.magicmirror.BR;
import com.blublabs.magicmirror.common.Utils;
import com.blublabs.magicmirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by andrs on 03.10.2016.
 */

public class WeatherMagicMirrorModule extends MagicMirrorModule {

    public enum TemperatureUnit {
        Config("config"),
        Kelvin("default"),
        Celsius("metric"),
        Fahrenheit("imperial");

        private final String text;

        TemperatureUnit(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public static TemperatureUnit from(String text) {

            if(text == null) {
                return Config;
            }

            for(TemperatureUnit value : TemperatureUnit.values()) {
                if(text.equals(value.getText())) {
                    return value;
                }
            }

            return Config;
        }
    }

    private static final String KEY_DATA_LOCATION = "location";
    private static final String KEY_DATA_LOCATION_ID = "locationID";
    private static final String KEY_DATA_APPID = "appid";
    private static final String KEY_DATA_UNITS = "units";
    private static final String KEY_DATA_MAX_NUMBER_OF_DAYS = "maxNumberOfDays";
    private static final String KEY_DATA_UPDATE_INTERVAL = "updateInterval";
    private static final String KEY_DATA_ANIMATION_SPEED = "animationSpeed";
    private static final String KEY_DATA_LANG = "lang";
    private static final String KEY_DATA_FADE = "fade";
    private static final String KEY_DATA_FADE_POINT = "fadePoint";
    private static final String KEY_DATA_INITIAL_LOAD_DELAY = "initialLoadDelay";
    private static final String KEY_DATA_RETRY_DELAY = "retryDelay";
    private static final String KEY_DATA_API_VERSION = "apiVersion";
    private static final String KEY_DATA_API_BASE = "apiBase";
    private static final String KEY_DATA_WEATHER_ENDPOINT = "weatherEndpoint";
    private static final String KEY_DATA_ICON_TABLE = "iconTable";

    private String location = "";
    private String locationID = "";
    private String appid = "";
    private TemperatureUnit units = TemperatureUnit.Config;
    private Integer maxNumberOfDays = null;
    private Integer updateInterval = null;
    private Integer animationSpeed = null;
    private String lang = "";
    private boolean fade = true;
    private Double fadePoint = null;
    private Integer initialLoadDelay = null;
    private Integer retryDelay = null;
    private String apiVersion = "";
    private String apiBase = "";
    private String weatherEndpoint = "";
    private Map<String, String> iconTable = new TreeMap<>();

    private WeatherSettingsFragment fragment;

    public WeatherMagicMirrorModule(JSONObject data) throws JSONException {
        super(data);

        if(data.has(KEY_DATA_CONFIG)) {

            JSONObject config = data.getJSONObject(KEY_DATA_CONFIG);

            if(config.has(KEY_DATA_LOCATION)) {
                this.location = config.getString(KEY_DATA_LOCATION);
            }

            if(config.has(KEY_DATA_LOCATION_ID)) {
                this.locationID = config.getString(KEY_DATA_LOCATION_ID);
            }

            if(config.has(KEY_DATA_APPID)) {
                this.appid = config.getString(KEY_DATA_APPID);
            }

            if(config.has(KEY_DATA_UNITS)) {
                this.units = TemperatureUnit.from(config.getString(KEY_DATA_UNITS));
            }

            if(config.has(KEY_DATA_MAX_NUMBER_OF_DAYS)) {
                this.maxNumberOfDays = config.getInt(KEY_DATA_MAX_NUMBER_OF_DAYS);
            }

            if(config.has(KEY_DATA_UPDATE_INTERVAL)) {
                this.updateInterval = config.getInt(KEY_DATA_UPDATE_INTERVAL);
            }

            if(config.has(KEY_DATA_ANIMATION_SPEED)) {
                this.animationSpeed = config.getInt(KEY_DATA_ANIMATION_SPEED);
            }

            if(config.has(KEY_DATA_LANG)) {
                this.lang = config.getString(KEY_DATA_LANG);
            }

            if(config.has(KEY_DATA_FADE)) {
                this.fade = config.getBoolean(KEY_DATA_FADE);
            }

            if(config.has(KEY_DATA_FADE_POINT)) {
                this.fadePoint = config.getDouble(KEY_DATA_FADE_POINT);
            }

            if(config.has(KEY_DATA_INITIAL_LOAD_DELAY)) {
                this.initialLoadDelay = config.getInt(KEY_DATA_INITIAL_LOAD_DELAY);
            }

            if(config.has(KEY_DATA_RETRY_DELAY)) {
                this.retryDelay = config.getInt(KEY_DATA_RETRY_DELAY);
            }

            if(config.has(KEY_DATA_API_VERSION)) {
                this.apiVersion = config.getString(KEY_DATA_API_VERSION);
            }

            if(config.has(KEY_DATA_API_BASE)) {
                this.apiBase = config.getString(KEY_DATA_API_BASE);
            }

            if(config.has(KEY_DATA_WEATHER_ENDPOINT)) {
                this.weatherEndpoint = config.getString(KEY_DATA_WEATHER_ENDPOINT);
            }

            if(config.has(KEY_DATA_ICON_TABLE) && config.getJSONObject(KEY_DATA_ICON_TABLE).length() > 0) {
                JSONObject table = config.getJSONObject(KEY_DATA_ICON_TABLE);

                Iterator<String> nameItr = table.keys();
                while (nameItr.hasNext()) {
                    String name = nameItr.next();
                    iconTable.put(name, table.getString(name));
                }
            }
            else {
                setDefaultIconTable(iconTable);
            }
        }
    }

    private void setDefaultIconTable(Map<String, String> map) {

        map.clear();

        map.put("01d", "wi-day-sunny");
        map.put("02d", "wi-day-cloudy");
        map.put("03d", "wi-cloudy");
        map.put("04d", "wi-cloudy-windy");
        map.put("09d", "wi-showers");
        map.put("10d", "wi-rain");
        map.put("11d", "wi-thunderstorm");
        map.put("13d", "wi-snow");
        map.put("50d", "wi-fog");
        map.put("01n", "wi-night-clear");
        map.put("02n", "wi-night-cloudy");
        map.put("03n", "wi-night-cloudy");
        map.put("04n", "wi-night-cloudy");
        map.put("09n", "wi-night-showers");
        map.put("10n", "wi-night-rain");
        map.put("11n", "wi-night-thunderstorm");
        map.put("13n", "wi-night-snow");
        map.put("50n", "wi-night-alt-cloudy-windy");
    }

    @Bindable
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {

        if(Utils.objectsEqual(this.location, location)) {
            return;
        }

        this.location = location;
        notifyPropertyChanged(BR.location);
    }

    @Bindable
    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {

        if(Utils.objectsEqual(this.locationID, locationID)) {
            return;
        }

        this.locationID = locationID;
        notifyPropertyChanged(BR.locationID);
    }

    @Bindable
    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {

        if(Utils.objectsEqual(this.appid, appid)) {
            return;
        }

        this.appid = appid;
        notifyPropertyChanged(BR.appid);
    }

    @Bindable
    public TemperatureUnit getUnits() {
        return units;
    }

    public void setUnits(TemperatureUnit units) {

        if(this.units == units) {
            return;
        }

        this.units = units;
        notifyPropertyChanged(BR.units);
    }

    @Bindable
    public Integer getMaxNumberOfDays() {
        return maxNumberOfDays;
    }

    public void setMaxNumberOfDays(Integer maxNumberOfDays) {

        if(Utils.objectsEqual(this.maxNumberOfDays, maxNumberOfDays)) {
            return;
        }

        this.maxNumberOfDays = maxNumberOfDays;
        notifyPropertyChanged(BR.maxNumberOfDays);
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
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {

        if(Utils.objectsEqual(this.lang, lang)) {
            return;
        }

        this.lang = lang;
        notifyPropertyChanged(BR.lang);
    }

    @Bindable
    public boolean isFade() {
        return fade;
    }

    public void setFade(boolean fade) {

        if(this.fade == fade) {
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
    public Integer getInitialLoadDelay() {
        return initialLoadDelay;
    }

    public void setInitialLoadDelay(Integer initialLoadDelay) {

        if(Utils.objectsEqual(this.initialLoadDelay, initialLoadDelay)) {
            return;
        }

        this.initialLoadDelay = initialLoadDelay;
        notifyPropertyChanged(BR.initialLoadDelay);
    }

    @Bindable
    public Integer getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(Integer retryDelay) {

        if(Utils.objectsEqual(this.retryDelay, retryDelay)) {
            return;
        }

        this.retryDelay = retryDelay;
        notifyPropertyChanged(BR.retryDelay);
    }

    @Bindable
    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {

        if(Utils.objectsEqual(this.apiVersion, apiVersion)) {
            return;
        }

        this.apiVersion = apiVersion;
        notifyPropertyChanged(BR.apiVersion);
    }

    @Bindable
    public String getApiBase() {
        return apiBase;
    }

    public void setApiBase(String apiBase) {

        if(Utils.objectsEqual(this.apiBase, apiBase)) {
            return;
        }

        this.apiBase = apiBase;
        notifyPropertyChanged(BR.apiBase);
    }

    @Bindable
    public String getWeatherEndpoint() {
        return weatherEndpoint;
    }

    public void setWeatherEndpoint(String weatherEndpoint) {

        if(Utils.objectsEqual(this.weatherEndpoint, weatherEndpoint)) {
            return;
        }

        this.weatherEndpoint = weatherEndpoint;
        notifyPropertyChanged(BR.weatherEndpoint);
    }

    @Bindable
    public Map<String, String> getIconTable() {
        return iconTable;
    }

    public void setIconTable(Map<String, String> iconTable) {
        this.iconTable = iconTable;
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        if(fragment == null) {
            fragment = new WeatherSettingsFragment();
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

        if(!Utils.isEmpty(location) && !"New York".equals(location)) {
            config.put(KEY_DATA_LOCATION, location);
        }

        if(!Utils.isEmpty(locationID)) {
            config.put(KEY_DATA_LOCATION_ID, locationID);
        }

        if(!Utils.isEmpty(appid)) {
            config.put(KEY_DATA_APPID, appid);
        }

        if(units != TemperatureUnit.Config) {
            config.put(KEY_DATA_UNITS, units);
        }

        if(maxNumberOfDays != null) {
            config.put(KEY_DATA_MAX_NUMBER_OF_DAYS, maxNumberOfDays);
        }

        if(updateInterval != null) {
            config.put(KEY_DATA_UPDATE_INTERVAL, updateInterval);
        }

        if(animationSpeed != null) {
            config.put(KEY_DATA_ANIMATION_SPEED, animationSpeed);
        }

        if(!Utils.isEmpty(lang)) {
            config.put(KEY_DATA_LANG, lang);
        }

        if(!fade) {
            config.put(KEY_DATA_FADE, false);
        }

        if(fadePoint != null) {
            config.put(KEY_DATA_FADE_POINT, fadePoint);
        }

        if(initialLoadDelay != null) {
            config.put(KEY_DATA_INITIAL_LOAD_DELAY, initialLoadDelay);
        }

        if(retryDelay != null) {
            config.put(KEY_DATA_RETRY_DELAY, retryDelay);
        }

        if(!Utils.isEmpty(apiVersion) && !"2.5".equals(apiVersion)) {
            config.put(KEY_DATA_API_VERSION, apiVersion);
        }

        if(!Utils.isEmpty(apiBase) && !"http://api.openweathermap.org/objectData/".equals(apiBase)) {
            config.put(KEY_DATA_API_BASE, apiBase);
        }

        if(!Utils.isEmpty(weatherEndpoint) && !"forecast/daily".equals(weatherEndpoint)) {
            config.put(KEY_DATA_WEATHER_ENDPOINT, weatherEndpoint);
        }

        Map<String, String> defaultIconTable = new HashMap<>();
        setDefaultIconTable(defaultIconTable);
        if(!iconTable.isEmpty() && !defaultIconTable.equals(iconTable)) {
            JSONObject map = new JSONObject();

            for(Map.Entry<String, String> entry : iconTable.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }

            config.put(KEY_DATA_ICON_TABLE, map);
        }

        if(config.length() > 0) {
            json.put(KEY_DATA_CONFIG, config);
        }

        return json;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.location);
        dest.writeString(this.locationID);
        dest.writeString(this.appid);
        dest.writeInt(this.units == null ? -1 : this.units.ordinal());
        dest.writeValue(this.maxNumberOfDays);
        dest.writeValue(this.updateInterval);
        dest.writeValue(this.animationSpeed);
        dest.writeString(this.lang);
        dest.writeByte(this.fade ? (byte) 1 : (byte) 0);
        dest.writeValue(this.fadePoint);
        dest.writeValue(this.initialLoadDelay);
        dest.writeValue(this.retryDelay);
        dest.writeString(this.apiVersion);
        dest.writeString(this.apiBase);
        dest.writeString(this.weatherEndpoint);
        dest.writeInt(this.iconTable.size());
        for (Map.Entry<String, String> entry : this.iconTable.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    protected WeatherMagicMirrorModule(Parcel in) {
        super(in);
        this.location = in.readString();
        this.locationID = in.readString();
        this.appid = in.readString();
        int tmpUnits = in.readInt();
        this.units = tmpUnits == -1 ? null : TemperatureUnit.values()[tmpUnits];
        this.maxNumberOfDays = (Integer) in.readValue(Integer.class.getClassLoader());
        this.updateInterval = (Integer) in.readValue(Integer.class.getClassLoader());
        this.animationSpeed = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lang = in.readString();
        this.fade = in.readByte() != 0;
        this.fadePoint = (Double) in.readValue(Double.class.getClassLoader());
        this.initialLoadDelay = (Integer) in.readValue(Integer.class.getClassLoader());
        this.retryDelay = (Integer) in.readValue(Integer.class.getClassLoader());
        this.apiVersion = in.readString();
        this.apiBase = in.readString();
        this.weatherEndpoint = in.readString();
        int iconTableSize = in.readInt();
        this.iconTable = new HashMap<String, String>(iconTableSize);
        for (int i = 0; i < iconTableSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.iconTable.put(key, value);
        }
    }

    public static final Creator<WeatherMagicMirrorModule> CREATOR = new Creator<WeatherMagicMirrorModule>() {
        @Override
        public WeatherMagicMirrorModule createFromParcel(Parcel source) {
            return new WeatherMagicMirrorModule(source);
        }

        @Override
        public WeatherMagicMirrorModule[] newArray(int size) {
            return new WeatherMagicMirrorModule[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WeatherMagicMirrorModule that = (WeatherMagicMirrorModule) o;

        if (isFade() != that.isFade()) return false;
        if (!getLocation().equals(that.getLocation())) return false;
        if (!getLocationID().equals(that.getLocationID())) return false;
        if (!getAppid().equals(that.getAppid())) return false;
        if (getUnits() != that.getUnits()) return false;
        if (getMaxNumberOfDays() != null ? !getMaxNumberOfDays().equals(that.getMaxNumberOfDays()) : that.getMaxNumberOfDays() != null)
            return false;
        if (getUpdateInterval() != null ? !getUpdateInterval().equals(that.getUpdateInterval()) : that.getUpdateInterval() != null)
            return false;
        if (getAnimationSpeed() != null ? !getAnimationSpeed().equals(that.getAnimationSpeed()) : that.getAnimationSpeed() != null)
            return false;
        if (!getLang().equals(that.getLang())) return false;
        if (getFadePoint() != null ? !getFadePoint().equals(that.getFadePoint()) : that.getFadePoint() != null)
            return false;
        if (getInitialLoadDelay() != null ? !getInitialLoadDelay().equals(that.getInitialLoadDelay()) : that.getInitialLoadDelay() != null)
            return false;
        if (getRetryDelay() != null ? !getRetryDelay().equals(that.getRetryDelay()) : that.getRetryDelay() != null)
            return false;
        if (!getApiVersion().equals(that.getApiVersion())) return false;
        if (!getApiBase().equals(that.getApiBase())) return false;
        if (!getWeatherEndpoint().equals(that.getWeatherEndpoint())) return false;
        return getIconTable().equals(that.getIconTable());

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getLocation().hashCode();
        result = 31 * result + getLocationID().hashCode();
        result = 31 * result + getAppid().hashCode();
        result = 31 * result + getUnits().hashCode();
        result = 31 * result + (getMaxNumberOfDays() != null ? getMaxNumberOfDays().hashCode() : 0);
        result = 31 * result + (getUpdateInterval() != null ? getUpdateInterval().hashCode() : 0);
        result = 31 * result + (getAnimationSpeed() != null ? getAnimationSpeed().hashCode() : 0);
        result = 31 * result + getLang().hashCode();
        result = 31 * result + (isFade() ? 1 : 0);
        result = 31 * result + (getFadePoint() != null ? getFadePoint().hashCode() : 0);
        result = 31 * result + (getInitialLoadDelay() != null ? getInitialLoadDelay().hashCode() : 0);
        result = 31 * result + (getRetryDelay() != null ? getRetryDelay().hashCode() : 0);
        result = 31 * result + getApiVersion().hashCode();
        result = 31 * result + getApiBase().hashCode();
        result = 31 * result + getWeatherEndpoint().hashCode();
        result = 31 * result + getIconTable().hashCode();
        return result;
    }
}
