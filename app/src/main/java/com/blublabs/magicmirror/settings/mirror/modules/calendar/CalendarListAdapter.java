package com.blublabs.magicmirror.settings.mirror.modules.calendar;

/**
 * Created by andrs on 30.08.2016.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blublabs.magicmirror.BR;
import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.utils.Utils;
import com.blublabs.magicmirror.databinding.DialogCalendarBinding;

class CalendarListAdapter extends RecyclerView.Adapter<CalendarListAdapter.MyViewHolder> {

    private final Context context;
    private final RecyclerView calendarRecyclerView;
    private final CalendarMagicMirrorModule module;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView text;

        MyViewHolder(View view) {
            super(view);
            text = (TextView) view;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = calendarRecyclerView.getChildLayoutPosition(view);

            if(itemPosition < module.getCalendars().size()) {
                showCalendarDialog(module.getCalendars().get(itemPosition));
            }
            else {
                showCalendarDialog(null);
            }
        }
    }

    CalendarListAdapter(CalendarMagicMirrorModule module, Context context, RecyclerView calendarRecyclerView) {

        this.module = module;
        this.context = context;
        this.calendarRecyclerView = calendarRecyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if(position < module.getCalendars().size()) {
            holder.text.setText(module.getCalendars().get(position).toString());
        }
        else {
            holder.text.setText("Add calendar");
        }
    }

    @Override
    public int getItemCount() {
        return module.getCalendars().size() + 1;
    }

    private void showCalendarDialog(final Calendar calendar) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        DialogCalendarBinding dialogViewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_calendar, null, false);
        View dialogView = dialogViewBinding.getRoot();
        final TextInputLayout urlEditLayout = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutUrl);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(dialogView);

        final Calendar newCalendar;
        if(calendar == null) {
            newCalendar = new Calendar();
            dialogViewBinding.setCalendar(newCalendar);
        }
        else {
            newCalendar = null;
            dialogViewBinding.setCalendar(calendar);
        }

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // will be replaced
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        if(calendar != null) {
            alertDialogBuilder.setNeutralButton("Remove",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            module.getCalendars().remove(calendar);
                            notifyDataSetChanged();
                            module.notifyPropertyChanged(BR.calendars);
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

                Calendar testCalendar = calendar == null ? newCalendar : calendar;

                if(calendar == null && module.getCalendars().contains(newCalendar)) {
                    Toast.makeText(context, "Calendar '" + newCalendar + "' already exists!", Toast.LENGTH_LONG).show();
                    return;
                }
                else  if(!Utils.validateEditTextValue(testCalendar.getUrl(), urlEditLayout, "URL not set!")) {
                    return;
                }

                if(calendar == null) {
                    module.getCalendars().add(newCalendar);
                }
                notifyDataSetChanged();
                module.notifyPropertyChanged(BR.calendars);
                alertDialog.dismiss();
            }
        });
    }
}
