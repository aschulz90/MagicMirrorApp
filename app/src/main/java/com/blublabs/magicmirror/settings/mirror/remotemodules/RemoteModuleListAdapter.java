package com.blublabs.magicmirror.settings.mirror.remotemodules;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;
import com.blublabs.magicmirror.databinding.CardRemoteModulesBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreas Schulz on 06.02.2017.
 */

public class RemoteModuleListAdapter extends RecyclerView.Adapter<RemoteModuleListAdapter.MyViewHolder> {

    private final Context context;
    private final RecyclerView recyclerView;
    private final List<RemoteModule> remoteModuleList;
    private final View progress;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final CardRemoteModulesBinding binding;

        MyViewHolder(CardRemoteModulesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {

            int itemPosition = recyclerView.getChildLayoutPosition(view);
            final RemoteModule module = remoteModuleList.get(itemPosition);

            if(module.isInstalled()) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setMessage("Do you really want to install this module?")
                    .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            installModule(module.getUrl());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

    private void installModule(String url) {
        progress.setVisibility(View.VISIBLE);
        final IMagicMirrorAdapter adapter = MagicMirrorAdapterFactory.getAdapter(context.getApplicationContext());

        adapter.installModule(url, new IMagicMirrorAdapter.MagicMirrorAdapterCallback(){
            @Override
            public void onInstallModule(int status) {
                if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {

                    Snackbar.make(recyclerView, "Module successfuly installed, MagicMirror requires a restart!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Restart now", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    adapter.executeQuery("RESTART", new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                                        @Override
                                        public void onExecuteQuery(int status) {
                                            if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {
                                                Snackbar.make(recyclerView, "MagicMirror is being restarted!!", Snackbar.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .show();
                }
                else {
                    Toast.makeText(context, "Error while installing a module!", Toast.LENGTH_LONG).show();
                }
                progress.setVisibility(View.GONE);
            }
        });
    }

    RemoteModuleListAdapter(Context context, RecyclerView recyclerView, List<RemoteModule> remoteModuleList, View progress) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.remoteModuleList = remoteModuleList;
        this.progress = progress;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardRemoteModulesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.card_remote_modules, null, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        RemoteModule module = remoteModuleList.get(position);

        holder.binding.setModule(module);
        TextView text = (TextView) holder.binding.getRoot().findViewById(R.id.remote_module_url);
        text.setText(Html.fromHtml("<a href=\"" + module.getUrl() + "\">View on GitHub</a> "));
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public int getItemCount() {
        return remoteModuleList.size();
    }

}
