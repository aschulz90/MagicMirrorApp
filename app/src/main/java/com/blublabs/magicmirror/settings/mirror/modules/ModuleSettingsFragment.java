package com.blublabs.magicmirror.settings.mirror.modules;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by andrs on 28.09.2016.
 */

public class ModuleSettingsFragment<T extends MagicMirrorModule> extends Fragment {

    public static final String KEY_MODULE = "key module";

    private T module;

    protected T getModule() {
        return module;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            module = getArguments().getParcelable(KEY_MODULE);
        }
    }
}
