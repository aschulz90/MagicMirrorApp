package com.blublabs.magicmirror.common;

import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.widget.Toast;

import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;

/**
 * Created by Andreas Schulz on 14.01.2017.
 */

public class QueryPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if(positiveResult) {
            getAdapter().executeQuery(getPreference().getKey(), new IMagicMirrorAdapter.MagicMirrorAdapterCallback(){
                @Override
                public void onExecuteQuery(int status) {
                    if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_ERROR) {
                        Toast.makeText(getContext(), "Error while sexecuting query!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private IMagicMirrorAdapter getAdapter() {
        return MagicMirrorAdapterFactory.getAdapter(getActivity().getApplicationContext());
    }
}
