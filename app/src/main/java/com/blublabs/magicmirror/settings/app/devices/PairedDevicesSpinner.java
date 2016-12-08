package com.blublabs.magicmirror.settings.app.devices;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by andrs on 25.09.2016.
 */

public class PairedDevicesSpinner extends Spinner {

    public PairedDevicesSpinner(Context context) { super(context); }

    public PairedDevicesSpinner(Context context, AttributeSet attrs) { super(context, attrs); }

    public PairedDevicesSpinner(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    @Override
    public void setSelection(int position, boolean animate) {

        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        if (getOnItemSelectedListener() != null && sameSelected) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position) {

        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (getOnItemSelectedListener() != null && sameSelected) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }
}
