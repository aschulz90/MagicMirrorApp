package com.blublabs.magicmirror.modules.calendar;

import com.blublabs.magicmirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

/**
 * Created by andrs on 03.10.2016.
 */

public class CalendarMagicMirrorModule extends MagicMirrorModule {


    public CalendarMagicMirrorModule(boolean active, PositionRegion position) {
        super("Calendar", active, position);
    }

    @Override
    public ModuleSettingsFragment getAddtionalSettingsFragment() {
        return new CalendarSettingsFragment();
    }
}
