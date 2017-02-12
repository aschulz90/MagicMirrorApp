package com.blublabs.magicmirror.settings.mirror.remotemodules;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andreas Schulz on 06.02.2017.
 */

public class RemoteModule {

    private static final String KEY_AUTHOR = "author";
    private static final String KEY_DESCRIPTION = "desc";
    private static final String KEY_NAME = "longname";
    private static final String KEY_URL = "url";
    private static final String KEY_ID = "id";

    private final String author;
    private final String description;
    private final String name;
    private final String displayName;
    private final String url;
    private final String id;
    private boolean installed = false;

    RemoteModule(JSONObject remoteModule) throws JSONException {
        author = remoteModule.getString(KEY_AUTHOR);
        description = remoteModule.getString(KEY_DESCRIPTION);
        name = remoteModule.getString(KEY_NAME);
        displayName = formatName(name);
        url = remoteModule.getString(KEY_URL);
        id = remoteModule.getString(KEY_ID);
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isInstalled() {
        return installed;
    }

    public String getId() {
        return id;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    private String formatName(String name) {
        // leading 'MM-' or 'MMM-'
        String formated = name.replaceAll("M{2,}-", "");
        // '_' or '-'
        formated = formated.replaceAll("[-|_]", " ");
        // spaces between camel case
        formated = formated.replaceAll("([a-z])([A-Z])", "$1 $2");

        // capitalize after a space
        char[] array = formated.toCharArray();
        array[0] = ("" + array[0]).toUpperCase().charAt(0);
        for(int i = 1; i < array.length; i++) {
            if(array[i - 1] == ' ') {
                array[i] = ("" + array[i]).toUpperCase().charAt(0);
            }
        }

        return new String(array);
    }

}
