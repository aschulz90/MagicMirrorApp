package com.blublabs.magicmirror.modules;

/**
 * Created by andrs on 30.08.2016.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.ExpandAndScrollAnimation;

import java.util.List;

class MagicMirrorModuleListAdapter extends RecyclerView.Adapter<MagicMirrorModuleListAdapter.MyViewHolder> {

    private final RecyclerView recyclerView;
    private final List<MagicMirrorModule> moduleList;
    private final Context appContext;
    private final Fragment parentFragment;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public Spinner positionSpinner;
        public SwitchCompat enabledSwitch;
        public LinearLayout settingsContent;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.info_text);
            positionSpinner = (Spinner) view.findViewById(R.id.position_spinner);
            enabledSwitch = (SwitchCompat) view.findViewById(R.id.switch_compat);
            settingsContent = (LinearLayout) view.findViewById(R.id.module_settings_content);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_modules, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final MagicMirrorModule element = moduleList.get(position);
        holder.title.setText(element.getName());

        ArrayAdapter<MagicMirrorModule.PositionRegion> spinnerAdapter = new ArrayAdapter<>(parentFragment.getActivity(), android.R.layout.simple_spinner_item, MagicMirrorModule.PositionRegion.values());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.positionSpinner.setAdapter(spinnerAdapter);
        holder.positionSpinner.setSelection(element.getPosition().ordinal());

        //in some cases, it will prevent unwanted situations
        holder.enabledSwitch.setOnCheckedChangeListener(null);
        holder.enabledSwitch.setChecked(element.isActive());
        holder.settingsContent.setVisibility(element.isActive() ? View.VISIBLE : View.GONE);

        holder.enabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            element.setActive(isChecked);

            holder.settingsContent.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Animation a;
            if (!isChecked) {
                a = new ExpandAndScrollAnimation(holder.settingsContent.getHeight(), 0, holder.settingsContent, recyclerView, holder.itemView);
            } else {
                a = new ExpandAndScrollAnimation(holder.settingsContent.getHeight(), holder.settingsContent.getMeasuredHeight(), holder.settingsContent, recyclerView, holder.itemView);
            }
            a.setDuration(200);

            holder.settingsContent.startAnimation(a);
            }
        });

        // add custom settings fragment
        ModuleSettingsFragment settingsFragment = element.getAddtionalSettingsFragment();

        if(settingsFragment != null) {
            FragmentManager fragmentManager = parentFragment.getChildFragmentManager();
            FrameLayout frameLayout = new FrameLayout(appContext);
            frameLayout.setId(position+1); //since id cannot be zero
            popBackStack(fragmentManager, frameLayout.getId());
            holder.settingsContent.addView(frameLayout);
            fragmentManager.beginTransaction().replace(frameLayout.getId(), settingsFragment).commit();
        }
    }

    private static void popBackStack(FragmentManager fragmentManager, int numBackStack) {
        int fragCount = fragmentManager.getBackStackEntryCount();
        for(int i = 0; i < fragCount-numBackStack; i++){
            fragmentManager.popBackStack();
        }
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }
}
