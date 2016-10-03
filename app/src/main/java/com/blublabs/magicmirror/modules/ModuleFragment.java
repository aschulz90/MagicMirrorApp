package com.blublabs.magicmirror.modules;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.MagicMirrorFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrs on 21.09.2016.
 */

public class ModuleFragment extends MagicMirrorFragment {

    private List<MagicMirrorModule> moduleList = new ArrayList<>();
    private MagicMirrorModuleListAdapter moduleListAdapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_modules, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view);

        moduleListAdapter = new MagicMirrorModuleListAdapter(moduleList, getActivity().getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));*/

        recyclerView.setAdapter(moduleListAdapter);

        moduleList.add(new MagicMirrorModule("Module1", true, MagicMirrorModule.PositionRegion.top_left));
        moduleList.add(new MagicMirrorModule("Module2", true, MagicMirrorModule.PositionRegion.top_left));
        moduleList.add(new MagicMirrorModule("Module3", true, MagicMirrorModule.PositionRegion.top_left));
        moduleList.add(new MagicMirrorModule("Module4", true, MagicMirrorModule.PositionRegion.top_left));

        return view;
    }

}
