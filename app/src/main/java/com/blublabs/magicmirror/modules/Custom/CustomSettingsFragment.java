package com.blublabs.magicmirror.modules.custom;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.modules.ModuleSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import static com.blublabs.magicmirror.modules.MagicMirrorModule.KEY_DATA_CONFIG;

/**
 * Created by andrs on 28.09.2016.
 */

public class CustomSettingsFragment extends ModuleSettingsFragment<CustomMagicMirrorModule> {

    private final Observable.OnPropertyChangedCallback propertyChangedCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            getModule().notifyPropertyChanged(i);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_module_settings_custom, container, false);

        final DynamicJSONObjectView dynamicJSONObjectView = (DynamicJSONObjectView) view.findViewById(R.id.dynamicConfigView);
        dynamicJSONObjectView.addOnPropertyChangedCallback(propertyChangedCallback);

        try {
            if(!getModule().getData().has(KEY_DATA_CONFIG)) {

                getModule().getData().put(KEY_DATA_CONFIG, new JSONObject());
            }

            dynamicJSONObjectView.setObjectData(getModule().getData().getJSONObject(KEY_DATA_CONFIG));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
