package com.blublabs.magicmirror.settings.mirror.modules.news;

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

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.Utils;

import java.util.List;

/**
 * Created by andrs on 19.10.2016.
 */

public class TagsListAdapter extends RecyclerView.Adapter<TagsListAdapter.MyViewHolder> {

    private final Context context;
    private final RecyclerView complimentsRecyclerView;
    private final List<String> tags;
    private final NewsMagicMirrorModule module;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;

        MyViewHolder(View view) {
            super(view);
            text = (TextView) view;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = complimentsRecyclerView.getChildLayoutPosition(view);

            if(itemPosition < tags.size()) {
                showComplimentDialog(tags.get(itemPosition));
            }
            else {
                showComplimentDialog(null);
            }
        }
    }

    TagsListAdapter(Context context, RecyclerView complimentRecyclerView, NewsMagicMirrorModule module, List<String> tags) {
        this.context = context;
        this.complimentsRecyclerView = complimentRecyclerView;
        this.tags = tags;
        this.module = module;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if(position < tags.size()) {
            holder.text.setText(tags.get(position));
        }
        else {
            holder.text.setText("Add Tag");
        }
    }

    @Override
    public int getItemCount() {
        return tags.size() + 1;
    }

    private void showComplimentDialog(final String compliment) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_tags, null, false);
        final TextInputLayout complimentEditLayout = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutTag);
        final EditText complimentEditText = (EditText) dialogView.findViewById(R.id.editTextTag);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(dialogView);

        if(compliment != null) {
            complimentEditText.setText(compliment);
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

        if(compliment != null) {
            alertDialogBuilder.setNeutralButton("Remove",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            tags.remove(compliment);
                            notifyDataSetChanged();
                            module.notifyChange();
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

                final String newCompliment = complimentEditText.getText().toString();

                if(!Utils.validateEditTextValue(newCompliment, complimentEditLayout, "Tag not set!")) {
                    return;
                }

                if(compliment == null) {
                   tags.add(newCompliment);
                }
                else {
                    tags.set(tags.indexOf(compliment), newCompliment);
                }
                notifyDataSetChanged();
                module.notifyChange();
                alertDialog.dismiss();
            }
        });
    }

}
