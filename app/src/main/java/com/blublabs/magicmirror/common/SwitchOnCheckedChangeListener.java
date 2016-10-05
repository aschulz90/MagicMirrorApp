package com.blublabs.magicmirror.common;

import android.view.View;
import android.widget.CompoundButton;

/**
 * Created by andrs on 05.10.2016.
 */

public class SwitchOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

    private final View[] views;

    public SwitchOnCheckedChangeListener(View... views) {
        this.views = views;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setEnabled(views, isChecked);
    }

    public static void setEnabled(final View[] views, boolean enabled) {
        for(View view : views) {
            if(view != null) {
                view.setEnabled(enabled);
            }
        }
    }
}
