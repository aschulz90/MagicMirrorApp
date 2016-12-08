package com.blublabs.magicmirror.settings.mirror.modules;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;

/**
 * Created by andrs on 21.09.2016.
 */

public class ModulesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_modules, container, false);

        final ModuleScrollView moduleScrollView = (ModuleScrollView) view.findViewById(R.id.card_recycler_view);
        moduleScrollView.setParentFragment(this);
        moduleScrollView.setModuleDataAdapter(MagicMirrorAdapterFactory.getAdapter(getActivity().getApplicationContext()));

        return view;
    }
}
