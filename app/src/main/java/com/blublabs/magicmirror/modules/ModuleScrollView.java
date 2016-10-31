package com.blublabs.magicmirror.modules;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.ExpandAndScrollAnimation;
import com.blublabs.magicmirror.databinding.CardModulesBinding;

import java.util.ArrayList;

/**
 * Created by andrs on 22.10.2016.
 */

public class ModuleScrollView extends NestedScrollView {

    private final ArrayList<MagicMirrorModule> moduleList = new ArrayList<>();
    private Fragment parentFragment = null;
    private LinearLayout linearLayout = null;

    public ModuleScrollView(Context context) {
        super(context);
    }

    public ModuleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModuleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setParentFragment(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public void addView(View child) {
        if(linearLayout == null) {
            linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            super.addView(linearLayout);
        }

        linearLayout.addView(child);
        linearLayout.requestLayout();
    }

    public void addModule(final MagicMirrorModule module) {
        if(!moduleList.contains(module)) {
            moduleList.add(module);

            final CardModulesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.card_modules, this, false);
            binding.setModule(module);
            addView(binding.getRoot());

            final LinearLayout settingsContent = (LinearLayout) binding.getRoot().findViewById(R.id.module_settings_content);
            final Spinner positionSpinner = (Spinner) binding.getRoot().findViewById(R.id.position_spinner);
            final ImageView expandIndicator = (ImageView) binding.getRoot().findViewById(R.id.indicatorExpand);
            final View titleBar = binding.getRoot().findViewById(R.id.titleBar);

            // Position Spinner
            ArrayAdapter<MagicMirrorModule.PositionRegion> spinnerAdapter = new ArrayAdapter<>(parentFragment.getActivity(), android.R.layout.simple_spinner_item, MagicMirrorModule.PositionRegion.values());
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            positionSpinner.setAdapter(spinnerAdapter);

            settingsContent.setVisibility(module.isActive() ? View.VISIBLE : View.GONE);
            expandIndicator.setRotation(module.isActive() ? 180 : 0);

            titleBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    module.setActive(!module.isActive());

                    if(titleBar.getAnimation() == null) {
                        settingsContent.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        Animation a;
                        if (!module.isActive()) {
                            a = new ExpandAndScrollAnimation(settingsContent.getHeight(), 0, settingsContent, ModuleScrollView.this, binding.getRoot(), expandIndicator);
                        } else {
                            a = new ExpandAndScrollAnimation(settingsContent.getHeight(), settingsContent.getMeasuredHeight(), settingsContent, ModuleScrollView.this, binding.getRoot(), expandIndicator);
                        }
                        a.setDuration(200);

                        titleBar.startAnimation(a);
                    }
                }
            });

            // add custom settings fragment
            final ModuleSettingsFragment settingsFragment = module.getAdditionalSettingsFragment();

            if(parentFragment != null && settingsFragment != null) {
                FrameLayout frameLayout = new FrameLayout(getContext());
                frameLayout.setId(Math.abs(module.hashCode()));
                settingsContent.addView(frameLayout);
                FragmentManager fragmentManager = parentFragment.getChildFragmentManager();
                fragmentManager.beginTransaction().replace(frameLayout.getId(), settingsFragment).commit();
            }

        }
    }

    public void removeModule(MagicMirrorModule module) {
        if(moduleList.contains(module)) {
            moduleList.remove(module);
        }
    }

    public ArrayList<MagicMirrorModule> getModuleList() {
        return moduleList;
    }
}
