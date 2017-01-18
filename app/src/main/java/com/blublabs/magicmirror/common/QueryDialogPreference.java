package com.blublabs.magicmirror.common;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import com.blublabs.magicmirror.R;

/**
 * Created by Andreas Schulz on 14.01.2017.
 */

public class QueryDialogPreference extends DialogPreference {

    public QueryDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogTitle(context.getString(R.string.confirm));
        setDialogMessage(context.getString(R.string.confirm_execute_query) + " '" + getKey() + "'?");
    }
}
