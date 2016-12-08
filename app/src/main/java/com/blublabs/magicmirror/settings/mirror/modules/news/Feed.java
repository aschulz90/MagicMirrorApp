package com.blublabs.magicmirror.settings.mirror.modules.news;

import android.os.Parcel;
import android.os.Parcelable;

import com.blublabs.magicmirror.common.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrs on 21.10.2016.
 */

public class Feed implements Parcelable {

    private static final String KEY_DATA_FEEDS_TITLE = "title";
    private static final String KEY_DATA_FEEDS_URL = "url";
    private static final String KEY_DATA_FEEDS_ENCODING = "encoding";

    private String title = null;
    private String url = null;
    private String encoding = null;

    public Feed(JSONObject data) throws JSONException {

        if(data.has(KEY_DATA_FEEDS_TITLE)) {
            this.title = data.getString(KEY_DATA_FEEDS_TITLE);
        }

        if(data.has(KEY_DATA_FEEDS_URL)) {
            this.url = data.getString(KEY_DATA_FEEDS_URL);
        }

        if(data.has(KEY_DATA_FEEDS_ENCODING)) {
            this.encoding = data.getString(KEY_DATA_FEEDS_ENCODING);
        }
    }

    public Feed() {

    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        if(!Utils.isEmpty(this.title)) {
            json.put(KEY_DATA_FEEDS_TITLE, this.title);
        }

        if(!Utils.isEmpty(this.url)) {
            json.put(KEY_DATA_FEEDS_URL, this.url);
        }

        if(!Utils.isEmpty(this.encoding)) {
            json.put(KEY_DATA_FEEDS_ENCODING, this.encoding);
        }

        return json;
    }

    protected Feed(Parcel in) {
        title = in.readString();
        url = in.readString();
        encoding = in.readString();
    }

    public static final Creator<Feed> CREATOR = new Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(encoding);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feed feed = (Feed) o;

        if (getTitle() != null ? !getTitle().equals(feed.getTitle()) : feed.getTitle() != null)
            return false;
        if (getUrl() != null ? !getUrl().equals(feed.getUrl()) : feed.getUrl() != null)
            return false;
        return getEncoding() != null ? getEncoding().equals(feed.getEncoding()) : feed.getEncoding() == null;

    }

    @Override
    public int hashCode() {
        int result = getTitle() != null ? getTitle().hashCode() : 0;
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        result = 31 * result + (getEncoding() != null ? getEncoding().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String string = "";

        String value = "{";

        if(title != null) {
            value += "title:" + title;
        }

        if(url != null) {
            value += ", url:" + url;
        }

        if(encoding != null) {
            value += ", encoding:" + encoding;
        }

        value += "}";

        return value;
    }
}
