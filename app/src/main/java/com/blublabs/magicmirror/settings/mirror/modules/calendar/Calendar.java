package com.blublabs.magicmirror.settings.mirror.modules.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import com.blublabs.magicmirror.common.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrs on 13.10.2016.
 */

public class Calendar implements Parcelable{

    private static final String KEY_CALENDAR_URL = "url";
    private static final String KEY_CALENDAR_SYMBOL = "symbol";
    private static final String KEY_CALENDAR_REPEATING_COUNT_TITLE = "repeatingCountTitle";
    private static final String KEY_CALENDAR_USER = "user";
    private static final String KEY_CALENDAR_PASS = "pass";

    private String url = null;
    private String symbol = null;
    private String repeatingCountTitle = null;
    private String user = null;
    private String pass = null;

    public Calendar(JSONObject data) throws JSONException {
        if (data.has(KEY_CALENDAR_URL)) {
            url = data.getString(KEY_CALENDAR_URL);
        }

        if (data.has(KEY_CALENDAR_SYMBOL)) {
            symbol = data.getString(KEY_CALENDAR_SYMBOL);
        }

        if (data.has(KEY_CALENDAR_REPEATING_COUNT_TITLE)) {
            repeatingCountTitle = data.getString(KEY_CALENDAR_REPEATING_COUNT_TITLE);
        }

        if (data.has(KEY_CALENDAR_USER)) {
            user = data.getString(KEY_CALENDAR_USER);
        }

        if (data.has(KEY_CALENDAR_PASS)) {
            pass = data.getString(KEY_CALENDAR_PASS);
        }
    }

    protected Calendar(Parcel in) {
        url = in.readString();
        symbol = in.readString();
        repeatingCountTitle = in.readString();
        user = in.readString();
        pass = in.readString();
    }

    public static final Creator<Calendar> CREATOR = new Creator<Calendar>() {
        @Override
        public Calendar createFromParcel(Parcel in) {
            return new Calendar(in);
        }

        @Override
        public Calendar[] newArray(int size) {
            return new Calendar[size];
        }
    };

    public Calendar() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getRepeatingCountTitle() {
        return repeatingCountTitle;
    }

    public void setRepeatingCountTitle(String repeatingCountTitle) {
        this.repeatingCountTitle = repeatingCountTitle;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(symbol);
        dest.writeString(repeatingCountTitle);
        dest.writeString(user);
        dest.writeString(pass);
    }

    @Override
    public String toString() {

        String value = "{";

        if(url != null) {
            value += "url:" + url;
        }

        if(symbol != null) {
            value += ", symbol:" + symbol;
        }

        if(repeatingCountTitle != null) {
            value += ", repeatingCountTitle:" + repeatingCountTitle;
        }

        if(user != null) {
            value += ", user:" + user;
        }

        if(pass != null) {
            value += ", pass:" + pass;
        }

        value += "}";

        return value;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(KEY_CALENDAR_URL, getUrl());
        json.put(KEY_CALENDAR_SYMBOL, getSymbol());

        if(!Utils.isEmpty(getRepeatingCountTitle())) {
            json.put(KEY_CALENDAR_REPEATING_COUNT_TITLE, getRepeatingCountTitle());
        }

        if(!Utils.isEmpty(getUser())) {
            json.put(KEY_CALENDAR_USER, getUser());
        }

        if(!Utils.isEmpty(getPass())) {
            json.put(KEY_CALENDAR_PASS, getPass());
        }

        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Calendar calendar = (Calendar) o;

        if (getUrl() != null ? !getUrl().equals(calendar.getUrl()) : calendar.getUrl() != null)
            return false;
        if (getSymbol() != null ? !getSymbol().equals(calendar.getSymbol()) : calendar.getSymbol() != null)
            return false;
        if (getRepeatingCountTitle() != null ? !getRepeatingCountTitle().equals(calendar.getRepeatingCountTitle()) : calendar.getRepeatingCountTitle() != null)
            return false;
        if (getUser() != null ? !getUser().equals(calendar.getUser()) : calendar.getUser() != null)
            return false;
        return getPass() != null ? getPass().equals(calendar.getPass()) : calendar.getPass() == null;

    }

    @Override
    public int hashCode() {
        int result = getUrl() != null ? getUrl().hashCode() : 0;
        result = 31 * result + (getSymbol() != null ? getSymbol().hashCode() : 0);
        result = 31 * result + (getRepeatingCountTitle() != null ? getRepeatingCountTitle().hashCode() : 0);
        result = 31 * result + (getUser() != null ? getUser().hashCode() : 0);
        result = 31 * result + (getPass() != null ? getPass().hashCode() : 0);
        return result;
    }
}
