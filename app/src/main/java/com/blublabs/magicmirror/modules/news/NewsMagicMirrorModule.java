package com.blublabs.magicmirror.modules.news;

import android.databinding.Bindable;
import android.os.Bundle;
import android.os.Parcel;

import com.blublabs.magicmirror.BR;
import com.blublabs.magicmirror.common.Utils;
import com.blublabs.magicmirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrs on 03.10.2016.
 */

public class NewsMagicMirrorModule extends MagicMirrorModule {

    public enum RemoveTags {
        none,
        title,
        description,
        both;

        public static RemoveTags from(String removeTags) {

            if(removeTags == null) {
                return null;
            }

            for(RemoveTags value : RemoveTags.values()) {
                if(removeTags.equals(value.toString())) {
                    return value;
                }
            }

            return null;
        }
    }

    private static final String KEY_DATA_FEEDS = "feeds";
    private static final String KEY_DATA_SHOW_SOURCE_TITLE = "showSourceTitle";
    private static final String KEY_DATA_SHOW_PUBLISH_DATE = "showPublishDate";
    private static final String KEY_DATA_SHOW_DESCRIPTION = "showDescription";
    private static final String KEY_DATA_RELOAD_INTERVAL = "reloadInterval";
    private static final String KEY_DATA_UPDATE_INTERVAL = "updateInterval";
    private static final String KEY_DATA_ANIMATION_SPEED = "animationSpeed";
    private static final String KEY_DATA_MAX_NEWS_ITEMS = "maxNewsItems";
    private static final String KEY_DATA_REMOVE_START_TAGS = "removeStartTags";
    private static final String KEY_DATA_START_TAGS = "startTags";
    private static final String KEY_DATA_REMOVE_END_TAGS = "removeEndTags";
    private static final String KEY_DATA_END_TAGS = "endTags";

    private List<Feed> feeds = new ArrayList<>();
    private boolean showSourceTitle = true;
    private boolean showPublishDate = true;
    private boolean showDescription = false;
    private Integer reloadInterval = null;
    private Integer updateInterval = null;
    private Integer animationSpeed = null;
    private Integer maxNewsItems = null;
    private RemoveTags removeStartTags = RemoveTags.none;
    private List<String> startTags = new ArrayList<>();
    private RemoveTags removeEndTags = RemoveTags.none;
    private List<String> endTags = new ArrayList<>();

    private NewsSettingsFragment fragment;

    public NewsMagicMirrorModule(String name) {
        super(name);
    }

    @Override
    public void setData(JSONObject data) throws JSONException {
        super.setData(data);

        if(data.has(KEY_DATA_CONFIG)) {

            JSONObject config = data.getJSONObject(KEY_DATA_CONFIG);

            if(config.has(KEY_DATA_FEEDS)) {

                JSONArray array = config.getJSONArray(KEY_DATA_FEEDS);

                for(int i = 0; i < array.length(); i++) {
                    feeds.add(new Feed(array.getJSONObject(i)));
                }
            }

            if(config.has(KEY_DATA_SHOW_SOURCE_TITLE)) {
                this.showSourceTitle = config.getBoolean(KEY_DATA_SHOW_SOURCE_TITLE);
            }

            if(config.has(KEY_DATA_SHOW_PUBLISH_DATE)) {
                this.showPublishDate = config.getBoolean(KEY_DATA_SHOW_PUBLISH_DATE);
            }

            if(config.has(KEY_DATA_SHOW_DESCRIPTION)) {
                this.showDescription = config.getBoolean(KEY_DATA_SHOW_DESCRIPTION);
            }

            if(config.has(KEY_DATA_RELOAD_INTERVAL)) {
                this.reloadInterval = config.getInt(KEY_DATA_RELOAD_INTERVAL);
            }

            if(config.has(KEY_DATA_UPDATE_INTERVAL)) {
                this.updateInterval = config.getInt(KEY_DATA_UPDATE_INTERVAL);
            }

            if(config.has(KEY_DATA_ANIMATION_SPEED)) {
                this.animationSpeed = config.getInt(KEY_DATA_ANIMATION_SPEED);
            }

            if(config.has(KEY_DATA_MAX_NEWS_ITEMS)) {
                this.maxNewsItems = config.getInt(KEY_DATA_MAX_NEWS_ITEMS);
            }

            if(config.has(KEY_DATA_REMOVE_START_TAGS)) {
                this.removeStartTags = RemoveTags.from(config.getString(KEY_DATA_REMOVE_START_TAGS));
            }

            if(config.has(KEY_DATA_START_TAGS)) {
                JSONArray array = config.getJSONArray(KEY_DATA_START_TAGS);

                for(int i = 0; i < array.length(); i++) {
                    startTags.add(array.getString(i));
                }
            }

            if(config.has(KEY_DATA_REMOVE_END_TAGS)) {
                this.removeEndTags = RemoveTags.from(config.getString(KEY_DATA_REMOVE_END_TAGS));
            }

            if(config.has(KEY_DATA_END_TAGS)) {
                JSONArray array = config.getJSONArray(KEY_DATA_END_TAGS);

                for(int i = 0; i < array.length(); i++) {
                    endTags.add(array.getString(i));
                }
            }
        }
    }

    @Bindable
    public List<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    @Bindable
    public boolean isShowSourceTitle() {
        return showSourceTitle;
    }

    public void setShowSourceTitle(boolean showSourceTitle) {

        if(this.showSourceTitle == showSourceTitle) {
            return;
        }

        this.showSourceTitle = showSourceTitle;
    }

    @Bindable
    public boolean isShowPublishDate() {
        return showPublishDate;
    }

    public void setShowPublishDate(boolean showPublishDate) {

        if(this.showPublishDate == showPublishDate) {
            return;
        }

        this.showPublishDate = showPublishDate;
    }

    @Bindable
    public boolean isShowDescription() {
        return showDescription;
    }

    public void setShowDescription(boolean showDescription) {

        if(this.showDescription == showDescription) {
            return;
        }

        this.showDescription = showDescription;
    }

    @Bindable
    public Integer getReloadInterval() {
        return reloadInterval;
    }

    public void setReloadInterval(Integer reloadInterval) {
        if(Utils.objectsEqual(this.reloadInterval, reloadInterval)) {
            return;
        }

        this.reloadInterval = reloadInterval;
        notifyPropertyChanged(BR.reloadInterval);
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
    public Integer getMaxNewsItems() {
        return maxNewsItems;
    }

    public void setMaxNewsItems(Integer maxNewsItems) {

        if(Utils.objectsEqual(this.maxNewsItems, maxNewsItems)) {
            return;
        }

        this.maxNewsItems = maxNewsItems;
        notifyPropertyChanged(BR.maxNewsItems);
    }

    @Bindable
    public RemoveTags getRemoveStartTags() {
        return removeStartTags;
    }

    public void setRemoveStartTags(RemoveTags removeStartTags) {
        if(this.removeStartTags == removeStartTags) {
            return;
        }

        this.removeStartTags = removeStartTags;
        notifyPropertyChanged(BR.removeStartTags);
    }

    @Bindable
    public List<String> getStartTags() {
        return startTags;
    }

    public void setStartTags(List<String> startTags) {
        this.startTags = startTags;
    }

    @Bindable
    public RemoveTags getRemoveEndTags() {
        return removeEndTags;
    }

    public void setRemoveEndTags(RemoveTags removeEndTags) {

        if(this.removeEndTags == removeEndTags) {
            return;
        }

        this.removeEndTags = removeEndTags;
        notifyPropertyChanged(BR.removeEndTags);
    }

    @Bindable
    public List<String> getEndTags() {
        return endTags;
    }

    public void setEndTags(List<String> endTags) {
        this.endTags = endTags;
    }

    @Override
    public ModuleSettingsFragment getAdditionalSettingsFragment() {

        if(fragment == null) {
            fragment = new NewsSettingsFragment();
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

        if(feeds.size() > 0) {
            JSONArray array = new JSONArray();

            for(Feed feed : feeds) {
                array.put(feed.toJSON());
            }

            config.put(KEY_DATA_FEEDS, array);
        }

        if(!showSourceTitle) {
            config.put(KEY_DATA_SHOW_SOURCE_TITLE, false);
        }

        if(!showPublishDate) {
            config.put(KEY_DATA_SHOW_SOURCE_TITLE, false);
        }

        if(showDescription) {
            config.put(KEY_DATA_SHOW_SOURCE_TITLE, true);
        }

        if(reloadInterval != null) {
            config.put(KEY_DATA_RELOAD_INTERVAL, reloadInterval);
        }

        if(updateInterval != null) {
            config.put(KEY_DATA_RELOAD_INTERVAL, updateInterval);
        }

        if(animationSpeed != null) {
            config.put(KEY_DATA_RELOAD_INTERVAL, animationSpeed);
        }

        if(maxNewsItems != null) {
            config.put(KEY_DATA_MAX_NEWS_ITEMS, maxNewsItems);
        }

        if(removeStartTags != RemoveTags.none) {
            config.put(KEY_DATA_REMOVE_START_TAGS, removeStartTags);
        }

        if(startTags.size() > 0) {
            JSONArray array = new JSONArray();

            for(String tag : startTags) {
                array.put(tag);
            }

            config.put(KEY_DATA_START_TAGS, array);
        }

        if(removeEndTags != RemoveTags.none) {
            config.put(KEY_DATA_REMOVE_END_TAGS, removeEndTags);
        }

        if(endTags.size() > 0) {
            JSONArray array = new JSONArray();

            for(String tag : endTags) {
                array.put(tag);
            }

            config.put(KEY_DATA_END_TAGS, array);
        }

        if(config.length() > 0) {
            json.put(KEY_DATA_CONFIG, config);
        }

        return json;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.feeds);
        dest.writeByte(this.showSourceTitle ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showPublishDate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showDescription ? (byte) 1 : (byte) 0);
        dest.writeValue(this.reloadInterval);
        dest.writeValue(this.updateInterval);
        dest.writeValue(this.animationSpeed);
        dest.writeValue(this.maxNewsItems);
        dest.writeInt(this.removeStartTags == null ? -1 : this.removeStartTags.ordinal());
        dest.writeStringList(this.startTags);
        dest.writeInt(this.removeEndTags == null ? -1 : this.removeEndTags.ordinal());
        dest.writeStringList(this.endTags);
    }

    private NewsMagicMirrorModule(Parcel in) {
        super(in);
        this.feeds = in.createTypedArrayList(Feed.CREATOR);
        this.showSourceTitle = in.readByte() != 0;
        this.showPublishDate = in.readByte() != 0;
        this.showDescription = in.readByte() != 0;
        this.reloadInterval = (Integer) in.readValue(Integer.class.getClassLoader());
        this.updateInterval = (Integer) in.readValue(Integer.class.getClassLoader());
        this.animationSpeed = (Integer) in.readValue(Integer.class.getClassLoader());
        this.maxNewsItems = (Integer) in.readValue(Integer.class.getClassLoader());
        int tmpRemoveStartTags = in.readInt();
        this.removeStartTags = tmpRemoveStartTags == -1 ? null : RemoveTags.values()[tmpRemoveStartTags];
        this.startTags = in.createStringArrayList();
        int tmpRemoveEndTags = in.readInt();
        this.removeEndTags = tmpRemoveEndTags == -1 ? null : RemoveTags.values()[tmpRemoveEndTags];
        this.endTags = in.createStringArrayList();
    }

    public static final Creator<NewsMagicMirrorModule> CREATOR = new Creator<NewsMagicMirrorModule>() {
        @Override
        public NewsMagicMirrorModule createFromParcel(Parcel source) {
            return new NewsMagicMirrorModule(source);
        }

        @Override
        public NewsMagicMirrorModule[] newArray(int size) {
            return new NewsMagicMirrorModule[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NewsMagicMirrorModule that = (NewsMagicMirrorModule) o;

        if (isShowSourceTitle() != that.isShowSourceTitle()) return false;
        if (isShowPublishDate() != that.isShowPublishDate()) return false;
        if (isShowDescription() != that.isShowDescription()) return false;
        if (!getFeeds().equals(that.getFeeds())) return false;
        if (getReloadInterval() != null ? !getReloadInterval().equals(that.getReloadInterval()) : that.getReloadInterval() != null)
            return false;
        if (getUpdateInterval() != null ? !getUpdateInterval().equals(that.getUpdateInterval()) : that.getUpdateInterval() != null)
            return false;
        if (getAnimationSpeed() != null ? !getAnimationSpeed().equals(that.getAnimationSpeed()) : that.getAnimationSpeed() != null)
            return false;
        if (getMaxNewsItems() != null ? !getMaxNewsItems().equals(that.getMaxNewsItems()) : that.getMaxNewsItems() != null)
            return false;
        if (getRemoveStartTags() != that.getRemoveStartTags()) return false;
        if (!getStartTags().equals(that.getStartTags())) return false;
        if (getRemoveEndTags() != that.getRemoveEndTags()) return false;
        return getEndTags().equals(that.getEndTags());

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getFeeds().hashCode();
        result = 31 * result + (isShowSourceTitle() ? 1 : 0);
        result = 31 * result + (isShowPublishDate() ? 1 : 0);
        result = 31 * result + (isShowDescription() ? 1 : 0);
        result = 31 * result + (getReloadInterval() != null ? getReloadInterval().hashCode() : 0);
        result = 31 * result + (getUpdateInterval() != null ? getUpdateInterval().hashCode() : 0);
        result = 31 * result + (getAnimationSpeed() != null ? getAnimationSpeed().hashCode() : 0);
        result = 31 * result + (getMaxNewsItems() != null ? getMaxNewsItems().hashCode() : 0);
        result = 31 * result + getRemoveStartTags().hashCode();
        result = 31 * result + getStartTags().hashCode();
        result = 31 * result + getRemoveEndTags().hashCode();
        result = 31 * result + getEndTags().hashCode();
        return result;
    }
}
