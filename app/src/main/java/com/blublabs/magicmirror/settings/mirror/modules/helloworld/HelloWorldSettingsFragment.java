package com.blublabs.magicmirror.settings.mirror.modules.helloworld;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.databinding.FragmentModuleSettingsHelloworldBinding;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;

/**
 * Created by andrs on 28.09.2016.
 */

public class HelloWorldSettingsFragment extends ModuleSettingsFragment<HelloWorldMagicMirrorModule> {

    private FragmentModuleSettingsHelloworldBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_module_settings_helloworld, container, false);
        final View view = binding.getRoot();
        binding.setModule(getModule());

        return view;
    }
}
