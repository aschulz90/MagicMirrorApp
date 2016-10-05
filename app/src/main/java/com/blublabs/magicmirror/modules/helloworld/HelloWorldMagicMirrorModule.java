package com.blublabs.magicmirror.modules.helloworld;

import com.blublabs.magicmirror.modules.MagicMirrorModule;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

/**
 * Created by andrs on 03.10.2016.
 */

public class HelloWorldMagicMirrorModule extends MagicMirrorModule {


    public HelloWorldMagicMirrorModule(boolean active, MagicMirrorModule.PositionRegion position) {
        super("Hello World", active, position);
    }

    @Override
    public ModuleSettingsFragment getAddtionalSettingsFragment() {
        return new HelloWorldSettingsFragment();
    }
}
