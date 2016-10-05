package com.blublabs.magicmirror.modules.alert;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

/**
 * Created by andrs on 28.09.2016.
 */

public class AlertSettingsFragment extends ModuleSettingsFragment {

    protected enum AlertEffect {
        scale,
        slide,
        genie,
        jelly,
        flip,
        exploader,
        bouncyflip
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_module_settings_alert, container, false);

        Spinner alertSpinner = (Spinner) view.findViewById(R.id.effectAlertSpinner);
        Spinner notificationSpinner = (Spinner) view.findViewById(R.id.effectNotificationSpinner);

        ArrayAdapter<AlertEffect> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AlertEffect.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        alertSpinner.setAdapter(adapter);
        notificationSpinner.setAdapter(adapter);

        return view;
    }
}
