package com.blublabs.magicmirror.settings.mirror.modules.alert;

import android.databinding.Bindable;
import android.os.Bundle;
import android.os.Parcel;

import com.android.databinding.library.baseAdapters.BR;
import com.blublabs.magicmirror.utils.Utils;
import com.blublabs.magicmirror.settings.mirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andreas Schulz on 03.10.2016.
 */

public class AlertMagicMirrorModule extends MagicMirrorModule {

    public enum AlertEffect {
        scale,
        slide,
        genie,
        jelly,
        flip,
        exploader,
        bouncyflip;

        public static AlertEffect from(String effect) {

            if(effect == null) {
                return null;
            }

            for(AlertEffect value : AlertEffect.values()) {
                if(effect.equals(value.toString())) {
                    return value;
                }
            }

            return null;
        }
    }

    public enum Position {
        left,
        center,
        right;

        public static Position from(String position) {

            if(position == null) {
                return null;
            }

            for(Position value : Position.values()) {
                if(position.equals(value.toString())) {
                    return value;
                }
            }

            return null;
        }
    }

    private static final String KEY_DATA_ALERT_EFFECT = "alert_effect";
    private static final String KEY_DATA_NOTIFICATION_EFFECT = "effect";
    private static final String KEY_DATA_DISPLAY_TIME = "display_time";
    private static final String KEY_DATA_POSITION = "position";
    private static final String KEY_DATA_WELCOME_MESSAGE = "welcome_message";

    private AlertEffect notificationEffect = AlertEffect.slide;
    private AlertEffect alertEffect = AlertEffect.jelly;
    private Double displayTime = null;
    private Position position = Position.center;
    private String welcomeMessage = "";

    private AlertSettingsFragment fragment;

    public AlertMagicMirrorModule(String name) {
        super(name);
    }

    @Override
    public void setData(JSONObject data) throws JSONException {
        super.setData(data);

        if(data.has(KEY_DATA_CONFIG)) {

            JSONObject config = data.getJSONObject(KEY_DATA_CONFIG);

            if(config.has(KEY_DATA_ALERT_EFFECT)) {
                this.alertEffect = AlertEffect.from(config.getString(KEY_DATA_ALERT_EFFECT));
            }

            if(config.has(KEY_DATA_NOTIFICATION_EFFECT)) {
                this.notificationEffect = AlertEffect.from(config.getString(KEY_DATA_NOTIFICATION_EFFECT));
            }

            if(config.has(KEY_DATA_DISPLAY_TIME)) {
                this.displayTime = config.getDouble(KEY_DATA_DISPLAY_TIME);
            }

            if(config.has(KEY_DATA_POSITION)) {
                this.position = Position.from(config.getString(KEY_DATA_POSITION));
            }

            if(config.has(KEY_DATA_WELCOME_MESSAGE)) {
                this.welcomeMessage = config.getString(KEY_DATA_WELCOME_MESSAGE);
            }
        }
    }

    @Bindable
    public AlertEffect getNotificationEffect() {
        return notificationEffect;
    }

    public void setNotificationEffect(AlertEffect notificationEffect) {

        if(notificationEffect == this.notificationEffect) {
            return;
        }

        this.notificationEffect = notificationEffect;
        notifyPropertyChanged(BR.notificationEffect);
    }

    @Bindable
    public AlertEffect getAlertEffect() {
        return alertEffect;
    }

    public void setAlertEffect(AlertEffect alertEffect) {

        if(alertEffect == this.alertEffect) {
            return;
        }

        this.alertEffect = alertEffect;
        notifyPropertyChanged(BR.alertEffect);
    }

    @Bindable
    public Double getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(Double displayTime) {

        if(Utils.objectsEqual(this.displayTime, displayTime)) {
            return;
        }

        this.displayTime = displayTime;
        notifyPropertyChanged(BR.displayTime);
    }

    @Bindable
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {

        if(position == this.position) {
            return;
        }

        this.position = position;
        notifyPropertyChanged(BR.position);
    }

    @Bindable
    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {

        if(Utils.objectsEqual(this.welcomeMessage, welcomeMessage)) {
            return;
        }

        this.welcomeMessage = welcomeMessage;
        notifyPropertyChanged(BR.welcomeMessage);
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        if(fragment == null) {
            fragment = new AlertSettingsFragment();
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

        if(alertEffect != AlertEffect.jelly) {
            config.put(KEY_DATA_ALERT_EFFECT, alertEffect);
        }

        if(notificationEffect != AlertEffect.slide) {
            config.put(KEY_DATA_NOTIFICATION_EFFECT, notificationEffect);
        }

        if(displayTime != null) {
            config.put(KEY_DATA_DISPLAY_TIME, displayTime);
        }

        if(position != Position.center) {
            config.put(KEY_DATA_POSITION, position);
        }

        if(!Utils.isEmpty(welcomeMessage)) {
            config.put(KEY_DATA_WELCOME_MESSAGE, welcomeMessage);
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

        AlertMagicMirrorModule that = (AlertMagicMirrorModule) o;

        if (getNotificationEffect() != that.getNotificationEffect()) return false;
        if (getAlertEffect() != that.getAlertEffect()) return false;
        if (getDisplayTime() != null ? !getDisplayTime().equals(that.getDisplayTime()) : that.getDisplayTime() != null)
            return false;
        if (getPosition() != that.getPosition()) return false;
        return getWelcomeMessage() != null ? getWelcomeMessage().equals(that.getWelcomeMessage()) : that.getWelcomeMessage() == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getNotificationEffect().hashCode();
        result = 31 * result + getAlertEffect().hashCode();
        result = 31 * result + (getDisplayTime() != null ? getDisplayTime().hashCode() : 0);
        result = 31 * result + getPosition().hashCode();
        result = 31 * result + (getWelcomeMessage() != null ? getWelcomeMessage().hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.notificationEffect == null ? -1 : this.notificationEffect.ordinal());
        dest.writeInt(this.alertEffect == null ? -1 : this.alertEffect.ordinal());
        dest.writeValue(this.displayTime);
        dest.writeInt(this.position == null ? -1 : this.position.ordinal());
        dest.writeString(this.welcomeMessage);
    }

    private AlertMagicMirrorModule(Parcel in) {
        super(in);
        int tmpNotificationEffect = in.readInt();
        this.notificationEffect = tmpNotificationEffect == -1 ? null : AlertEffect.values()[tmpNotificationEffect];
        int tmpAlertEffect = in.readInt();
        this.alertEffect = tmpAlertEffect == -1 ? null : AlertEffect.values()[tmpAlertEffect];
        this.displayTime = (Double) in.readValue(Double.class.getClassLoader());
        int tmpPosition = in.readInt();
        this.position = tmpPosition == -1 ? null : Position.values()[tmpPosition];
        this.welcomeMessage = in.readString();
    }

    public static final Creator<AlertMagicMirrorModule> CREATOR = new Creator<AlertMagicMirrorModule>() {
        @Override
        public AlertMagicMirrorModule createFromParcel(Parcel source) {
            return new AlertMagicMirrorModule(source);
        }

        @Override
        public AlertMagicMirrorModule[] newArray(int size) {
            return new AlertMagicMirrorModule[size];
        }
    };
}
