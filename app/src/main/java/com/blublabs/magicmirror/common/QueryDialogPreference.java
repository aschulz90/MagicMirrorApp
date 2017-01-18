package com.blublabs.magicmirror.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by Andreas Schulz on 14.01.2017.
 */

public class QueryDialogPreference extends DialogPreference {

    public QueryDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogTitle("Confirm");
        setDialogMessage("Do you really want to execute the query '" + getKey() + "'?");
    }
}
