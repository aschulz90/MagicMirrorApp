package com.blublabs.magicmirror.modules.calendar;

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

import com.blublabs.magicmirror.R;

import java.util.ArrayList;
import java.util.Map;

class CalendarTitleReplaceListAdapter extends RecyclerView.Adapter<CalendarTitleReplaceListAdapter.MyViewHolder> {

    private final Map<String, String> titleReplaceMap;
    private final Context context;

    private final RecyclerView titleReplaceRecyclerView;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView key;
        TextView value;
        TextView separator;

        MyViewHolder(View view) {
            super(view);
            key = (TextView) view.findViewById(R.id.textViewKey);
            value = (TextView) view.findViewById(R.id.textViewValue);
            separator = (TextView) view.findViewById(R.id.textViewSeparator);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = titleReplaceRecyclerView.getChildLayoutPosition(view);
            ArrayList<Map.Entry<String, String>> titleReplaceList = new ArrayList<>(titleReplaceMap.entrySet());

            if(itemPosition < titleReplaceList.size()) {
                Map.Entry<String, String> item = titleReplaceList.get(itemPosition);
                showTitleReplaceDialog(item);
            }
            else {
                showTitleReplaceDialog(null);
            }
        }
    }

    CalendarTitleReplaceListAdapter(Map<String, String> titleReplaceMap, Context context, RecyclerView titleReplaceRecyclerView) {

        this.titleReplaceMap = titleReplaceMap;
        this.context = context;
        this.titleReplaceRecyclerView = titleReplaceRecyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_calendar_title_replace, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(position < titleReplaceMap.entrySet().size()) {
            final Map.Entry<String, String> element = new ArrayList<>(titleReplaceMap.entrySet()).get(position);
            holder.key.setText(element.getKey());
            holder.value.setText(element.getValue());
            holder.separator.setVisibility(View.VISIBLE);
            holder.value.setVisibility(View.VISIBLE);
        }
        else {
            holder.key.setText("Add Title Replacement");
            holder.separator.setVisibility(View.INVISIBLE);
            holder.value.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return titleReplaceMap.size() + 1;
    }

    private boolean validateEditTextValue(String value, TextInputLayout textInputLayout, String errorMessage) {
        if (value == null || value.isEmpty()) {
            textInputLayout.setError(errorMessage);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void showTitleReplaceDialog(final Map.Entry<String, String> entry) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_title_replace, null);
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
                            titleReplaceMap.remove(entry.getKey());
                            notifyDataSetChanged();
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

                if(titleReplaceMap.containsKey(key)) {
                    Toast.makeText(context, "Title Replacement with key '" + key + "' already exists!", Toast.LENGTH_LONG).show();
                    return;
                }
                else  if(!validateEditTextValue(key, keyEditLayout, "Title not set!")) {
                    return;
                }
                else if(!validateEditTextValue(value, valueEditLayout, "Title Replacement not set!")) {
                    return;
                }

                if(entry != null) {
                    titleReplaceMap.remove(entry.getKey());
                }
                titleReplaceMap.put(key, value);
                notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
    }
}
