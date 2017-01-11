package com.blublabs.magicmirror.settings.mirror.modules.compliments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.MyCustomLayoutManager;
import com.blublabs.magicmirror.databinding.FragmentModuleSettingsComplimentsBinding;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleSettingsFragment;

/**
 * Created by andrs on 28.09.2016.
 */

public class ComplimentsSettingsFragment extends ModuleSettingsFragment<ComplimentsMagicMirrorModule> {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentModuleSettingsComplimentsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_module_settings_compliments, container, false);
        final View view = binding.getRoot();
        binding.setModule(getModule());

        // morning list
        final RecyclerView morningRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMorning);
        ComplimentsListAdapter morningAdapter = new ComplimentsListAdapter(getActivity(), morningRecyclerView, getModule(), getModule().getComplimentsMorning(), "Morning");
        morningRecyclerView.setLayoutManager(new MyCustomLayoutManager(getActivity().getApplicationContext()));
        morningRecyclerView.setItemAnimator(new DefaultItemAnimator());
        morningRecyclerView.setAdapter(morningAdapter);
        morningRecyclerView.setNestedScrollingEnabled(false);

        // afternoon list
        final RecyclerView afternoonRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewAfternoon);
        ComplimentsListAdapter afternoonAdapter = new ComplimentsListAdapter(getActivity(), afternoonRecyclerView, getModule(), getModule().getComplimentsAfternoon(), "Afternoon");
        afternoonRecyclerView.setLayoutManager(new MyCustomLayoutManager(getActivity().getApplicationContext()));
        afternoonRecyclerView.setItemAnimator(new DefaultItemAnimator());
        afternoonRecyclerView.setAdapter(afternoonAdapter);
        afternoonRecyclerView.setNestedScrollingEnabled(false);

        // evening list
        final RecyclerView eveningRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewEvening);
        ComplimentsListAdapter eveningAdapter = new ComplimentsListAdapter(getActivity(), eveningRecyclerView, getModule(), getModule().getComplimentsEvening(), "Evening");
        eveningRecyclerView.setLayoutManager(new MyCustomLayoutManager(getActivity().getApplicationContext()));
        eveningRecyclerView.setItemAnimator(new DefaultItemAnimator());
        eveningRecyclerView.setAdapter(eveningAdapter);
        eveningRecyclerView.setNestedScrollingEnabled(false);

        return view;
    }
}
