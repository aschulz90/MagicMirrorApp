package com.blublabs.magicmirror.modules;

/**
 * Created by andrs on 30.08.2016.
 */

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.blublabs.magicmirror.BR;
import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.ExpandAndScrollAnimation;
import com.blublabs.magicmirror.common.Utils;
import com.blublabs.magicmirror.databinding.CardModulesBinding;

import java.util.List;

class MagicMirrorModuleListAdapter extends RecyclerView.Adapter<MagicMirrorModuleListAdapter.MyViewHolder> {

    private final RecyclerView recyclerView;
    private final List<MagicMirrorModule> moduleList;
    private final Context appContext;
    private final Fragment parentFragment;

    class MyViewHolder extends RecyclerView.ViewHolder {
        public CardModulesBinding binding;
        public TextView title;
        public Spinner positionSpinner;
        public SwitchCompat enabledSwitch;
        public LinearLayout settingsContent;
        public Observable.OnPropertyChangedCallback propertyChangeCallBack;
        public FrameLayout frameLayout;

        MyViewHolder(CardModulesBinding binding) {
            super(binding.getRoot());
            this.title = (TextView) binding.getRoot().findViewById(R.id.info_text);
            this.positionSpinner = (Spinner) binding.getRoot().findViewById(R.id.position_spinner);
            this.enabledSwitch = (SwitchCompat) binding.getRoot().findViewById(R.id.switch_compat);
            this.settingsContent = (LinearLayout) binding.getRoot().findViewById(R.id.module_settings_content);
            this.binding = binding;

            this.frameLayout = new FrameLayout(appContext);
            this.settingsContent.addView(this.frameLayout);
        }
    }


    MagicMirrorModuleListAdapter(List<MagicMirrorModule> moduleList, Context applicationContext, RecyclerView recyclerView, ModulesFragment modulesFragment) {
        this.moduleList = moduleList;
        this.appContext = applicationContext;
        this.parentFragment = modulesFragment;
        this.recyclerView = recyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardModulesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_modules, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final MagicMirrorModule element = moduleList.get(position);
        holder.binding.setModule(element);

        // Position Spinner
        ArrayAdapter<MagicMirrorModule.PositionRegion> spinnerAdapter = new ArrayAdapter<>(parentFragment.getActivity(), android.R.layout.simple_spinner_item, MagicMirrorModule.PositionRegion.values());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.positionSpinner.setAdapter(spinnerAdapter);
        if(element.getRegion() != null) {
            holder.positionSpinner.setSelection(element.getRegion().ordinal());
        }

        // Active Switch
        element.removeOnPropertyChangedCallback(holder.propertyChangeCallBack);
        holder.propertyChangeCallBack = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if(i == BR.active) {
                    holder.settingsContent.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    Animation a;
                    if (!((MagicMirrorModule) observable).isActive()) {
                        a = new ExpandAndScrollAnimation(holder.settingsContent.getHeight(), 0, holder.settingsContent, recyclerView, holder.itemView);
                    } else {
                        a = new ExpandAndScrollAnimation(holder.settingsContent.getHeight(), holder.settingsContent.getMeasuredHeight(), holder.settingsContent, recyclerView, holder.itemView);
                    }
                    a.setDuration(200);

                    holder.settingsContent.startAnimation(a);
                }
            }
        };
        element.addOnPropertyChangedCallback(holder.propertyChangeCallBack);
        holder.settingsContent.setVisibility(element.isActive() ? View.VISIBLE : View.GONE);

        // add custom settings fragment
        final ModuleSettingsFragment settingsFragment = element.getAdditionalSettingsFragment();

        if(settingsFragment != null) {
            FragmentManager fragmentManager = parentFragment.getChildFragmentManager();
            holder.frameLayout.setId(position + 1);
            fragmentManager.beginTransaction().replace(holder.frameLayout.getId(), settingsFragment).commit();
        }
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }
}
