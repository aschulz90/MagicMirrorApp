package com.blublabs.magicmirror;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blublabs.magicmirror.common.MagicMirrorFragment;

/**
 * Created by andrs on 21.09.2016.
 */

public class MainFragment extends MagicMirrorFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        return view;
    }

}
