package com.blublabs.magicmirror.modules.alert;

import com.blublabs.magicmirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

/**
 * Created by andrs on 03.10.2016.
 */

public class AlertMagicMirrorModule extends MagicMirrorModule {


    public AlertMagicMirrorModule(boolean active, PositionRegion position) {
        super("Alert", active, position);
    }

    @Override
    public ModuleSettingsFragment getAddtionalSettingsFragment() {
        return new AlertSettingsFragment();
    }
}
