package com.blublabs.magicmirror.settings.mirror.remotemodules;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.adapter.MagicMirrorAdapterFactory;
import com.blublabs.magicmirror.settings.mirror.modules.ModuleScrollView;
import com.blublabs.magicmirror.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.blublabs.magicmirror.adapter.IMagicMirrorAdapter.KEY_CUSTOM_MODULES;

/**
 * Created by andrs on 21.09.2016.
 */

public class RemoteModulesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_remote_modules, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        final View progress = view.findViewById(R.id.progressBarHolder);

        init(recyclerView, progress);

        return view;
    }

    private void init(final RecyclerView recyclerView, final View progress) {
        final IMagicMirrorAdapter moduleAdapter = MagicMirrorAdapterFactory.getAdapter(getContext().getApplicationContext());

        moduleAdapter.getInstalledModuleList(new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
            @Override
            public void onGetInstalledModuleList(int status, Map<String, List<String>> installedModules) {

                try {
                    if (status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {
                        String jsonString = Utils.loadJsonFromAsset(getContext(), "remote_modules.json");


                        JSONArray remoteModuleArray = new JSONArray(jsonString);
                        List<RemoteModule> remoteModuleList = new ArrayList<>();

                        for (int i = 0; i < remoteModuleArray.length(); i++) {
                            JSONObject remoteModule = remoteModuleArray.getJSONObject(i);
                            RemoteModule module = new RemoteModule(remoteModule);
                            if(checkInstalled(installedModules.get(KEY_CUSTOM_MODULES), module)) {
                                module.setInstalled(true);
                            }
                            remoteModuleList.add(module);
                        }

                        RemoteModuleListAdapter adapter = new RemoteModuleListAdapter(getContext(), recyclerView, remoteModuleList, progress);
                        recyclerView.setAdapter(adapter);

                    } else {
                        showRetry(recyclerView, progress);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showRetry(recyclerView, progress);
                }

                progress.setVisibility(View.GONE);
            }
        });
    }

    private boolean checkInstalled(List<String> installed, RemoteModule module) {

        for(String folder : installed) {
            String idLower = module.getId().toLowerCase();
            String folderLower = folder.toLowerCase();

            if(idLower.endsWith(folderLower)) {
                return true;
            }
        }

        return false;
    }

    private void showRetry(final RecyclerView recyclerView, final View progress) {
        Snackbar.make(recyclerView, "Error getting installed module list!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progress.setVisibility(View.VISIBLE);
                        init(recyclerView, progress);
                    }
                })
                .show();
    }
}
