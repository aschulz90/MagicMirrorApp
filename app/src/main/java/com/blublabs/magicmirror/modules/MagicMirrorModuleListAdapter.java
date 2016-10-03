package com.blublabs.magicmirror.modules;

/**
 * Created by andrs on 30.08.2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blublabs.magicmirror.R;

import java.util.List;

public class MagicMirrorModuleListAdapter extends RecyclerView.Adapter<MagicMirrorModuleListAdapter.MyViewHolder> {

    private List<MagicMirrorModule> moduleList;
    private Context mAppContext;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public Spinner positionSpinner;
        public CheckBox enabledCheckbox;
        public View settingsContent;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.info_text);
            positionSpinner = (Spinner) view.findViewById(R.id.position_spinner);
            enabledCheckbox = (CheckBox) view.findViewById(R.id.enabled_checkbox);
            settingsContent = view.findViewById(R.id.module_settings_content);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }


    public MagicMirrorModuleListAdapter(List<MagicMirrorModule> moduleList, Context applicationContext) {
        this.moduleList = moduleList;
        this.mAppContext = applicationContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_modules, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final MagicMirrorModule element = moduleList.get(position);
        holder.title.setText(element.getName());

        ArrayAdapter<MagicMirrorModule.PositionRegion> spinnerAdapter = new ArrayAdapter<>(mAppContext, R.layout.item_module_spinner, MagicMirrorModule.PositionRegion.values());
        holder.positionSpinner.setAdapter(spinnerAdapter);
        holder.positionSpinner.setSelection(element.getPosition().ordinal());

        //in some cases, it will prevent unwanted situations
        holder.enabledCheckbox.setOnCheckedChangeListener(null);
        holder.enabledCheckbox.setChecked(element.isActive());
        holder.settingsContent.setVisibility(element.isActive() ? View.VISIBLE : View.GONE);

        holder.enabledCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                element.setActive(isChecked);
                holder.settingsContent.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }
}
