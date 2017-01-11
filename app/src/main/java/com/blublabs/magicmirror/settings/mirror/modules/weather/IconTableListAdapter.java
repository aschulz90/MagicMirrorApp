package com.blublabs.magicmirror.settings.mirror.modules.weather;

/**
 * Created by andrs on 30.08.2016.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blublabs.magicmirror.BR;
import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.utils.Utils;

import java.util.ArrayList;
import java.util.Map;

public class IconTableListAdapter extends RecyclerView.Adapter<IconTableListAdapter.MyViewHolder> {

    private final Context context;

    private final RecyclerView iconTableRecyclerView;
    private final WeatherMagicMirrorModule module;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView text;

        MyViewHolder(View view) {
            super(view);
            text = (TextView) view;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = iconTableRecyclerView.getChildLayoutPosition(view);
            ArrayList<Map.Entry<String, String>> iconTableList = new ArrayList<>(module.getIconTable().entrySet());

            Map.Entry<String, String> item = iconTableList.get(itemPosition);
            showIconDialog(item);
        }
    }

    public IconTableListAdapter(WeatherMagicMirrorModule module, Context context, RecyclerView iconTableRecyclerView) {

        this.module = module;
        this.context = context;
        this.iconTableRecyclerView = iconTableRecyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Map.Entry<String, String> element = new ArrayList<>(module.getIconTable().entrySet()).get(position);
        holder.text.setText(element.getKey() + " : " + element.getValue());

    }

    @Override
    public int getItemCount() {
        return module.getIconTable().size();
    }

    public void showIconDialog(final Map.Entry<String, String> entry) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_icontable, null);
        final EditText keyEditText = (EditText) dialogView.findViewById(R.id.input_key);
        final TextInputLayout keyEditLayout = (TextInputLayout) dialogView.findViewById(R.id.input_layout_key);
        final EditText valueEditText = (EditText) dialogView.findViewById(R.id.input_value);
        final TextInputLayout valueEditLayout = (TextInputLayout) dialogView.findViewById(R.id.input_layout_value);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(dialogView);
        if(entry == null) {
            keyEditText.setText("");
            valueEditText.setText("");
        }
        else {
            keyEditText.setText(entry.getKey());
            valueEditText.setText(entry.getValue());
        }

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        if(entry != null) {
            alertDialogBuilder.setNeutralButton("Remove",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            module.getIconTable().remove(entry.getKey());
                            notifyDataSetChanged();
                            module.notifyPropertyChanged(BR.titleReplaceMap);
                        }
                    });
        }

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        // override onClickListener to not close the dialog on failed validation
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String key = keyEditText.getText().toString();
                String value = valueEditText.getText().toString();

                if(module.getIconTable().containsKey(key)) {
                    Toast.makeText(context, "Title Replacement with text '" + key + "' already exists!", Toast.LENGTH_LONG).show();
                    return;
                }
                else  if(!Utils.validateEditTextValue(key, keyEditLayout, "Title not set!")) {
                    return;
                }
                else if(!Utils.validateEditTextValue(value, valueEditLayout, "Title Replacement not set!")) {
                    return;
                }

                if(entry != null) {
                    module.getIconTable().remove(entry.getKey());
                }
                module.getIconTable().put(key, value);
                module.notifyPropertyChanged(BR.titleReplaceMap);
                notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
    }
}
