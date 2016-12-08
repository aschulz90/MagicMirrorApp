package com.blublabs.magicmirror.settings.mirror.wifi;

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
import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.common.Utils;

import java.util.List;

/**
 * Created by andrs on 18.09.2016.
 */
public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.MyViewHolder> {

    private List<WifiNetwork> wifiList;
    private final Context appContext;
    private final WifiSettingsFragment fragment;
    private final RecyclerView wifiRecylcerView;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView deviceAddress;
        public TextView ssid;
        public TextView connectionStatus;

        public MyViewHolder(View view) {
            super(view);
            deviceAddress = (TextView) view.findViewById(R.id.macAdress);
            ssid = (TextView) view.findViewById(R.id.ssid);
            connectionStatus = (TextView) view.findViewById(R.id.connected);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = wifiRecylcerView.getChildLayoutPosition(view);
            WifiNetwork network = wifiList.get(itemPosition);

            if(!network.isConnected()) {
                showConnectWifiDialog(network.getSsid());
            }
        }
    }

    public WifiListAdapter(List<WifiNetwork> wifiList, Context applicationContext, WifiSettingsFragment fragment, RecyclerView wifiRecylcerView) {
        this.wifiList = wifiList;
        this.appContext = applicationContext;
        this.fragment = fragment;
        this.wifiRecylcerView = wifiRecylcerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wifi, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        WifiNetwork network = wifiList.get(position);

        holder.ssid.setText(network.getSsid());
        holder.deviceAddress.setText(network.getMacAddress());
        holder.connectionStatus.setText(network.isConnected() ? "Connected" : "");
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    private void showConnectWifiDialog(final String ssid) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(appContext);

        View dialogView = LayoutInflater.from(appContext).inflate(R.layout.dialog_wifi, null, false);
        final TextInputLayout passphraseEditLayout = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutPassphrase);
        final EditText passphraseEditText = (EditText) dialogView.findViewById(R.id.editTextPassphrase);
        final TextView ssidView = (TextView) dialogView.findViewById(R.id.textviewSsid);
        ssidView.setText(ssid);

        alertDialogBuilder.setView(dialogView);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Connect",
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

                String passphrase = passphraseEditText.getText().toString();

                if(!Utils.validateEditTextValue(passphrase, passphraseEditLayout, "Passphrase not set!")) {
                    return;
                }

                fragment.getAdapter().connectToWifiNetwork(ssid, passphrase, new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                    @Override
                    public void onConnectToWifiNetwork(int status) {
                        if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_ERROR) {
                            Toast.makeText(appContext, "Error while connecting to wifi network", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                alertDialog.dismiss();
            }
        });
    }
}
