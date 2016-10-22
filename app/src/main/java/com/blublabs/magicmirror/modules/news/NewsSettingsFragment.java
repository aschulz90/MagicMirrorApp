package com.blublabs.magicmirror.modules.news;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.MyCustomLayoutManager;
import com.blublabs.magicmirror.databinding.FragmentModuleSettingsHelloworldBinding;
import com.blublabs.magicmirror.databinding.FragmentModuleSettingsNewsBinding;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

/**
 * Created by andrs on 28.09.2016.
 */

public class NewsSettingsFragment extends ModuleSettingsFragment<NewsMagicMirrorModule> {

    private FragmentModuleSettingsNewsBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_module_settings_news, container, false);
        final View view = binding.getRoot();
        binding.setModule(getModule());

        // feeds list
        final RecyclerView feedListView = (RecyclerView) view.findViewById(R.id.recyclerViewFeeds);
        final FeedListAdapter feedsAdapter = new FeedListAdapter(getModule(), getActivity(), feedListView);
        feedListView.setLayoutManager(new MyCustomLayoutManager(getActivity().getApplicationContext()));
        feedListView.setItemAnimator(new DefaultItemAnimator());
        feedListView.setAdapter(feedsAdapter);

        // remove start tags
        final Spinner removeStartTagsSpinner = (Spinner) view.findViewById(R.id.spinnerRemoveStartTags);
        final ArrayAdapter<NewsMagicMirrorModule.RemoveTags> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, NewsMagicMirrorModule.RemoveTags.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        removeStartTagsSpinner.setAdapter(adapter);

        // remove start tags
        final Spinner removeEndTagsSpinner = (Spinner) view.findViewById(R.id.spinnerRemoveEndTagss);
        removeEndTagsSpinner.setAdapter(adapter);

        // start tags
        final RecyclerView startTagsRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewStartTags);
        final TagsListAdapter startTagsAdapter = new TagsListAdapter(getActivity(), startTagsRecyclerView, getModule(), getModule().getStartTags());
        startTagsRecyclerView.setLayoutManager(new MyCustomLayoutManager(getActivity().getApplicationContext()));
        startTagsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        startTagsRecyclerView.setAdapter(startTagsAdapter);

        // end tags
        final RecyclerView endTagsRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewEndTags);
        final TagsListAdapter endTagsAdapter = new TagsListAdapter(getActivity(), endTagsRecyclerView, getModule(), getModule().getStartTags());
        endTagsRecyclerView.setLayoutManager(new MyCustomLayoutManager(getActivity().getApplicationContext()));
        endTagsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        endTagsRecyclerView.setAdapter(endTagsAdapter);

        return view;
    }
}
