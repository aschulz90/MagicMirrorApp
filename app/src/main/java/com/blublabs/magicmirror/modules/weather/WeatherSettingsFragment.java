package com.blublabs.magicmirror.modules.weather;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.MyCustomLayoutManager;
import com.blublabs.magicmirror.databinding.FragmentModuleSettingsHelloworldBinding;
import com.blublabs.magicmirror.databinding.FragmentModuleSettingsWeatherBinding;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

/**
 * Created by andrs on 28.09.2016.
 */

public class WeatherSettingsFragment extends ModuleSettingsFragment<WeatherMagicMirrorModule> {

    private FragmentModuleSettingsWeatherBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_module_settings_weather, container, false);
        final View view = binding.getRoot();
        binding.setModule(getModule());

        // time format
        Spinner unitsSpinner = (Spinner) view.findViewById(R.id.spinnerUnits);
        ArrayAdapter<WeatherMagicMirrorModule.TemperatureUnit> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, WeatherMagicMirrorModule.TemperatureUnit.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(adapter);

        // The icon table can be quite large, so a separate add buton is used and the list is limited in height and scrollable
        // icon table
        final RecyclerView iconTableList = (RecyclerView) view.findViewById(R.id.recyclerViewIconTable);
        final IconTableListAdapter iconTableAdapter = new IconTableListAdapter(getModule(), getActivity(), iconTableList);
        iconTableList.setLayoutManager(new MyCustomLayoutManager(getActivity().getApplicationContext()));
        iconTableList.setItemAnimator(new DefaultItemAnimator());
        iconTableList.setAdapter(iconTableAdapter);
        iconTableList.setNestedScrollingEnabled(true);

        final View addIconView = view.findViewById(R.id.textViewAddIcon);
        addIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconTableAdapter.showIconDialog(null);
            }
        });

        return view;
    }
}
