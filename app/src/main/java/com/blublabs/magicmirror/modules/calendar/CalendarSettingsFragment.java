package com.blublabs.magicmirror.modules.calendar;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.databinding.library.baseAdapters.BR;
import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.MyCustomLayoutManager;
import com.blublabs.magicmirror.common.SwitchOnCheckedChangeListener;
import com.blublabs.magicmirror.common.Utils;
import com.blublabs.magicmirror.databinding.FragmentModuleSettingsCalendarBinding;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

/**
 * Created by andrs on 28.09.2016.
 */

public class CalendarSettingsFragment extends ModuleSettingsFragment<CalendarMagicMirrorModule> {

    public enum TimeFormat{
        absolute,
        relative;

        public static TimeFormat from(String position) {

            if(position == null) {
                return null;
            }

            for(TimeFormat value : TimeFormat.values()) {
                if(position.equals(value.toString())) {
                    return value;
                }
            }

            return null;
        }
    }

    private CalendarListAdapter calendarsAdapter;
    private CalendarTitleReplaceListAdapter titleReplaceAdapter;
    private FragmentModuleSettingsCalendarBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_module_settings_calendar, container, false);
        final View view = binding.getRoot();
        binding.setModule(getModule());

        checkSwitchValueChanged(BR.displaySymbol);
        checkSwitchValueChanged(BR.fade);

        // time format
        Spinner timeFormatSpinner = (Spinner) view.findViewById(R.id.spinnerTimeFormat);
        ArrayAdapter<TimeFormat> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, TimeFormat.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFormatSpinner.setAdapter(adapter);
        timeFormatSpinner.setSelection(adapter.getPosition(getModule().getTimeFormat()));

        // calendars list
        final RecyclerView calendarListView = (RecyclerView) view.findViewById(R.id.calendars_recycler_view);
        calendarsAdapter = new CalendarListAdapter(getModule(), getActivity(), calendarListView);
        calendarListView.setLayoutManager(new MyCustomLayoutManager(getActivity().getApplicationContext()));
        calendarListView.setItemAnimator(new DefaultItemAnimator());
        calendarListView.setAdapter(calendarsAdapter);

        // title replace list
        final RecyclerView titleReplaceList = (RecyclerView) view.findViewById(R.id.title_replace_recycler_view);
        titleReplaceAdapter = new CalendarTitleReplaceListAdapter(getModule(), getActivity(), titleReplaceList);
        titleReplaceList.setLayoutManager(new MyCustomLayoutManager(getActivity().getApplicationContext()));
        titleReplaceList.setItemAnimator(new DefaultItemAnimator());
        titleReplaceList.setAdapter(titleReplaceAdapter);

        /*Tooltip.make(getActivity(),
                new Tooltip.Builder(101)
                        .anchor(view.findViewById(R.id.textViewMaxDays), Tooltip.Gravity.RIGHT)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 0)
                        .activateDelay(900)
                        .showDelay(400)
                        .text("Android PopupWindow with Tooltip Arrow right side of button or view or layout")
                        .maxWidth(600)
                        .withArrow(true)
                        .withOverlay(false).build()
        ).show();*/

        return view;
    }

    private void checkSwitchValueChanged(int valueId) {
        switch (valueId) {
            case BR.displaySymbol:
                // display symbol switch
                final SwitchCompat displaySymbolSwitch = (SwitchCompat) binding.getRoot().findViewById(R.id.switchDisplaySymbol);
                final View[] defaultSymbolViews = new View[] {binding.getRoot().findViewById(R.id.textViewDefaultSymbol), binding.getRoot().findViewById(R.id.editTextDefaultSymbol)};
                SwitchOnCheckedChangeListener.setEnabled(defaultSymbolViews, getModule().isDisplaySymbol());
                break;

            case BR.fade:
                // fade switch
                final SwitchCompat fadeSwitch = (SwitchCompat) binding.getRoot().findViewById(R.id.switchFade);
                final View[] fadeViews = new View[] {binding.getRoot().findViewById(R.id.textViewFadePoint), binding.getRoot().findViewById(R.id.editTextFadePoint)};
                SwitchOnCheckedChangeListener.setEnabled(fadeViews, getModule().isFade());
                break;
            default:
                break;
        }
    }

}
