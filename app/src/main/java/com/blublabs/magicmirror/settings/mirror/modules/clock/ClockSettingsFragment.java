package com.blublabs.magicmirror.settings.mirror.modules.clock;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.databinding.FragmentModuleSettingsClockBinding;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

/**
 * Created by andrs on 28.09.2016.
 */

public class ClockSettingsFragment extends ModuleSettingsFragment<ClockMagicMirrorModule> {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentModuleSettingsClockBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_module_settings_clock, container, false);
        final View view = binding.getRoot();
        binding.setModule(getModule());

        Spinner timeFormatSpinner = (Spinner) view.findViewById(R.id.spinnerTimeFormat);
        Spinner displayTypeSpinner = (Spinner) view.findViewById(R.id.spinnerDisplayType);
        Spinner analogFaceSpinner = (Spinner) view.findViewById(R.id.spinnerAnalogFace);
        Spinner analogPlacementSpinner = (Spinner) view.findViewById(R.id.spinnerAnalogPlacement);
        Spinner analogDatePlacementSpinner = (Spinner) view.findViewById(R.id.spinnerShowAnalogDate);

        ArrayAdapter<ClockMagicMirrorModule.TimeFormat> timeFormatAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, ClockMagicMirrorModule.TimeFormat.values());
        timeFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<ClockMagicMirrorModule.DisplayType> displayTypeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, ClockMagicMirrorModule.DisplayType.values());
        displayTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<ClockMagicMirrorModule.AnalogFace> analogFaceAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, ClockMagicMirrorModule.AnalogFace.values());
        analogFaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<ClockMagicMirrorModule.AnalogPlacement> analogPlacementAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, ClockMagicMirrorModule.AnalogPlacement.values());
        analogPlacementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<ClockMagicMirrorModule.AnalogDatePlacement> analogDatePlacementAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, ClockMagicMirrorModule.AnalogDatePlacement.values());
        analogDatePlacementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeFormatSpinner.setAdapter(timeFormatAdapter);
        displayTypeSpinner.setAdapter(displayTypeAdapter);
        analogFaceSpinner.setAdapter(analogFaceAdapter);
        analogPlacementSpinner.setAdapter(analogPlacementAdapter);
        analogDatePlacementSpinner.setAdapter(analogDatePlacementAdapter);

        Button buttonColorCircle = (Button) view.findViewById(R.id.buttonColorCircle);
        ((GradientDrawable)buttonColorCircle.getBackground()).setColor(getModule().getSecondsColor());

        buttonColorCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ColorPickerDialogBuilder
                        .with(getActivity())
                        .setTitle("Choose Seconds Color")
                        .initialColor(getModule().getSecondsColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {

                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                getModule().setSecondsColor(selectedColor);
                                ((GradientDrawable)view.getBackground()).setColor(selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        return view;
    }

}
