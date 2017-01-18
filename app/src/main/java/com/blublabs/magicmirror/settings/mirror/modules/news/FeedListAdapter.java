package com.blublabs.magicmirror.settings.mirror.modules.news;

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
import com.blublabs.magicmirror.databinding.DialogFeedBinding;

class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.MyViewHolder> {

    private final Context context;
    private final RecyclerView feedRecyclerView;
    private final NewsMagicMirrorModule module;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView text;

        MyViewHolder(View view) {
            super(view);
            text = (TextView) view;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = feedRecyclerView.getChildLayoutPosition(view);

            if(itemPosition < module.getFeeds().size()) {
                showCalendarDialog(module.getFeeds().get(itemPosition));
            }
            else {
                showCalendarDialog(null);
            }
        }
    }

    FeedListAdapter(NewsMagicMirrorModule module, Context context, RecyclerView feedRecyclerView) {

        this.module = module;
        this.context = context;
        this.feedRecyclerView = feedRecyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if(position < module.getFeeds().size()) {
            holder.text.setText(module.getFeeds().get(position).toString());
        }
        else {
            holder.text.setText(context.getText(R.string.add_feed));
        }
    }

    @Override
    public int getItemCount() {
        return module.getFeeds().size() + 1;
    }

    private void showCalendarDialog(final Feed feed) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        DialogFeedBinding dialogViewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_feed, null, false);
        View dialogView = dialogViewBinding.getRoot();
        final TextInputLayout urlEditLayout = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutUrl);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(dialogView);

        final Feed newFeed;
        if(feed == null) {
            newFeed = new Feed();
            dialogViewBinding.setFeed(newFeed);
        }
        else {
            newFeed = null;
            dialogViewBinding.setFeed(feed);
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

        if(feed != null) {
            alertDialogBuilder.setNeutralButton("Remove",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            module.getFeeds().remove(feed);
                            notifyDataSetChanged();
                            module.notifyPropertyChanged(BR.feeds);
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
                Feed testFeed = feed == null ? newFeed : feed;

                if(feed == null && module.getFeeds().contains(newFeed)) {
                    Toast.makeText(context, "Calendar '" + newFeed + "' already exists!", Toast.LENGTH_LONG).show();
                    return;
                }
                else  if(!Utils.validateEditTextValue(testFeed.getUrl(), urlEditLayout, "URL not set!")) {
                    return;
                }

                if(feed == null) {
                    module.getFeeds().add(newFeed);
                }
                notifyDataSetChanged();
                module.notifyPropertyChanged(BR.feeds);
                alertDialog.dismiss();
            }
        });
    }
}
