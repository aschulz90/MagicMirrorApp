package com.blublabs.magicmirror.settings.mirror.modules.alert;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.databinding.FragmentModuleSettingsAlertBinding;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;

/**
 * Created by Andreas Schulz on 28.09.2016.
 */

public class AlertSettingsFragment extends ModuleSettingsFragment<AlertMagicMirrorModule> {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentModuleSettingsAlertBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_module_settings_alert, container, false);
        final View view = binding.getRoot();
        binding.setModule(getModule());

        Spinner alertSpinner = (Spinner) view.findViewById(R.id.effectAlertSpinner);
        Spinner notificationSpinner = (Spinner) view.findViewById(R.id.effectNotificationSpinner);
        Spinner positionSpinner = (Spinner) view.findViewById(R.id.spinnerPosition);

        ArrayAdapter<AlertMagicMirrorModule.AlertEffect> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AlertMagicMirrorModule.AlertEffect.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<AlertMagicMirrorModule.Position> positionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AlertMagicMirrorModule.Position.values());
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        alertSpinner.setAdapter(adapter);
        notificationSpinner.setAdapter(adapter);
        positionSpinner.setAdapter(positionAdapter);

        return view;
    }
}
