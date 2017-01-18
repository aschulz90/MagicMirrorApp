package com.blublabs.magicmirror.settings.mirror.modules.forecast;

import android.databinding.Bindable;
import android.os.Bundle;
import android.os.Parcel;

import com.android.databinding.library.baseAdapters.BR;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;
import com.blublabs.magicmirror.settings.mirror.modules.weather.WeatherMagicMirrorModule;
import com.blublabs.magicmirror.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by andrs on 03.10.2016.
 */

public class ForecastMagicMirrorModule extends WeatherMagicMirrorModule {

    private static final String KEY_DATA_MAX_NUMBER_OF_DAYS = "maxNumberOfDays";
    private static final String KEY_DATA_FADE = "fade";
    private static final String KEY_DATA_FADE_POINT = "fadePoint";
    private static final String KEY_DATA_FORECASTENDPOINT = "forecastEndpoint";
    private static final String KEY_DATA_SHOW_RAIN_AMOUNT = "showRainAmount";

    private Integer maxNumberOfDays = null;
    private boolean fade = true;
    private boolean showRainAmount = false;
    private Double fadePoint = null;
    private String forecastEndpoint = null;

    private ForecastSettingsFragment fragment;

    public ForecastMagicMirrorModule(String name) {
        super(name);
    }

    @Override
    public void setData(JSONObject data) throws JSONException {

        if(data.has(KEY_DATA_CONFIG)) {

            JSONObject config = data.getJSONObject(KEY_DATA_CONFIG);

            if(config.has(KEY_DATA_FORECASTENDPOINT)) {
                forecastEndpoint = config.getString(KEY_DATA_FORECASTENDPOINT);
            }

            if(config.has(KEY_DATA_MAX_NUMBER_OF_DAYS)) {
                this.maxNumberOfDays = config.getInt(KEY_DATA_MAX_NUMBER_OF_DAYS);
            }

            if(config.has(KEY_DATA_FADE)) {
                this.fade = config.getBoolean(KEY_DATA_FADE);
            }

            if(config.has(KEY_DATA_FADE_POINT)) {
                this.fadePoint = config.getDouble(KEY_DATA_FADE_POINT);
            }

            if(config.has(KEY_DATA_SHOW_RAIN_AMOUNT)) {
                this.showRainAmount = config.getBoolean(KEY_DATA_SHOW_RAIN_AMOUNT);
            }
        }

        super.setData(data);
    }

    @Bindable
    public String getForecastEndpoint() {
        return forecastEndpoint;
    }

    public void setForecastEndpoint(String forecastEndpoint) {
        if(Utils.objectsEqual(this.forecastEndpoint, forecastEndpoint)) {
            return;
        }

        this.forecastEndpoint = forecastEndpoint;
        notifyPropertyChanged(BR.forecastEndpoint);
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
    public boolean isShowRainAmount() {
        return showRainAmount;
    }

    public void setShowRainAmount(boolean showRainAmount) {
        if(this.showRainAmount == showRainAmount) {
            return;
        }

        this.showRainAmount = showRainAmount;
        notifyPropertyChanged(BR.showRainAmount);
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        if(fragment == null) {
            fragment = new ForecastSettingsFragment();
            Bundle bundle = new Bundle(1);
            bundle.putParcelable(ModuleSettingsFragment.KEY_MODULE, this);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    public JSONObject toJson() throws JSONException {

        JSONObject json = super.toJson();
        JSONObject config = json.has(KEY_DATA_CONFIG) ? json.getJSONObject(KEY_DATA_CONFIG) : new JSONObject();

        if(!Utils.isEmpty(forecastEndpoint) && !"forecast/daily".equals(forecastEndpoint)) {
            config.put(KEY_DATA_FORECASTENDPOINT, forecastEndpoint);
        }

        if(maxNumberOfDays != null) {
            config.put(KEY_DATA_MAX_NUMBER_OF_DAYS, maxNumberOfDays);
        }

        if(!fade) {
            config.put(KEY_DATA_FADE, false);
        }

        if(fadePoint != null) {
            config.put(KEY_DATA_FADE_POINT, fadePoint);
        }

        if(showRainAmount) {
            config.put(KEY_DATA_SHOW_RAIN_AMOUNT, true);
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
            case BR.forecastEndpoint:
                return true;
            default:
                return super.parameterRequiresRefresh(id);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(forecastEndpoint);
        dest.writeValue(this.maxNumberOfDays);
        dest.writeByte(this.fade ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showRainAmount ? (byte) 1 : (byte) 0);
        dest.writeValue(this.fadePoint);
    }

    private ForecastMagicMirrorModule(Parcel in) {
        super(in);
        forecastEndpoint = in.readString();
        this.maxNumberOfDays = (Integer) in.readValue(Integer.class.getClassLoader());
        this.fade = in.readByte() != 0;
        this.showRainAmount = in.readByte() != 0;
        this.fadePoint = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<ForecastMagicMirrorModule> CREATOR = new Creator<ForecastMagicMirrorModule>() {
        @Override
        public ForecastMagicMirrorModule createFromParcel(Parcel source) {
            return new ForecastMagicMirrorModule(source);
        }

        @Override
        public ForecastMagicMirrorModule[] newArray(int size) {
            return new ForecastMagicMirrorModule[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ForecastMagicMirrorModule that = (ForecastMagicMirrorModule) o;

        if (isFade() != that.isFade()) return false;

        if (getMaxNumberOfDays() != null ? !getMaxNumberOfDays().equals(that.getMaxNumberOfDays()) : that.getMaxNumberOfDays() != null)
            return false;

        if (getFadePoint() != null ? !getFadePoint().equals(that.getFadePoint()) : that.getFadePoint() != null)
            return false;

        return Objects.equals(getForecastEndpoint(), that.getForecastEndpoint());

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getForecastEndpoint() != null ? getForecastEndpoint().hashCode() : 0);
        result = 31 * result + (getMaxNumberOfDays() != null ? getMaxNumberOfDays().hashCode() : 0);
        result = 31 * result + (isFade() ? 1 : 0);
        result = 31 * result + (getFadePoint() != null ? getFadePoint().hashCode() : 0);

        return result;
    }
}
