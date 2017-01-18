package com.blublabs.magicmirror.settings.mirror.modules;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.ExpandAndScrollAnimation;
import com.blublabs.magicmirror.adapter.IMagicMirrorAdapter;
import com.blublabs.magicmirror.databinding.CardModulesBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by andrs on 22.10.2016.
 */

public class ModuleScrollView extends CoordinatorLayout {

    private final ArrayList<MagicMirrorModule> moduleList = new ArrayList<>();
    private IMagicMirrorAdapter moduleDataAdapter = null;
    private Fragment parentFragment = null;
    private LinearLayout linearLayout = null;
    private NestedScrollView nestedScrollView = null;
    private View progressBar = null;

    private Map<String, List<String>> installedModules = null;

    private final Handler delayHandler = new Handler();
    private static final int UPDATE_DELAY = 1000;

    private final Observable.OnPropertyChangedCallback moduleChangedCallback =  new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, final int i) {

            if(!moduleDataAdapter.isConnectedToMirror()) {
                return;
            }

            if(observable instanceof MagicMirrorModule) {
                final MagicMirrorModule module = (MagicMirrorModule) observable;
                final int index = getModuleList().indexOf(module);

                if(!module.isInitialized() || index == -1) {
                    return;
                }

                if(module.getUpdateHandler() == null) {
                    module.setUpdateHandler(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                moduleDataAdapter.setModuleData(index, module.toJson(), new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                                    @Override
                                    public void onSetModuleData(int status) {
                                        if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {

                                            if(module.parameterRequiresRefresh(i)) {
                                                showRefreshMagicMirrorBar();
                                            }
                                        }
                                        else {
                                            Toast.makeText(getContext(), "Error while setting module '" + module.getName() + "' data!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            finally {
                                module.setUpdateHandler(null);
                            }
                        }
                    });
                }
                else {
                    delayHandler.removeCallbacks(module.getUpdateHandler());
                }

                delayHandler.postDelayed(module.getUpdateHandler(), UPDATE_DELAY);
            }
        }
    };

    public ModuleScrollView(Context context) {
        super(context);
    }

    public ModuleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModuleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {

        nestedScrollView = new NestedScrollView(getContext());
        nestedScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        super.addView(nestedScrollView);

        FloatingActionButton fab = new FloatingActionButton(getContext());
        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.END;
        lp.setMargins(16,16,16,16);
        fab.setLayoutParams(lp);
        fab.setImageResource(R.drawable.ic_action_add_grey);
        fab.setScaleType(ImageView.ScaleType.CENTER);
        super.addView(fab);

        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(installedModules == null) {
                    showProgressBar(true);
                    moduleDataAdapter.getInstalledModuleList(new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                        @Override
                        public void onGetInstalledModuleList(int status, Map<String, List<String>> installedModules) {
                            if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {
                                ModuleScrollView.this.installedModules = installedModules;
                                showInstalledModuleList();
                                showProgressBar(false);
                            }
                        }
                    });
                }
                else {
                    showInstalledModuleList();
                }
            }
        });

        linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        nestedScrollView.addView(linearLayout);

        progressBar = LayoutInflater.from(getContext()).inflate(R.layout.module_list_progressbar, this, false);
        progressBar.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, 1));
        super.addView(progressBar);
    }

    private void showRefreshMagicMirrorBar() {

        Snackbar.make(this, "MagicMirror requires a refresh", Snackbar.LENGTH_INDEFINITE)
                .setAction("Refresh now", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moduleDataAdapter.executeQuery("REFRESH", new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                            @Override
                            public void onExecuteQuery(int status) {
                                if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {
                                    Snackbar.make(ModuleScrollView.this, "MagicMirror is being refreshed!!", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .show();
    }

    private void showProgressBar(boolean show) {

        AlphaAnimation anim;

        if(show) {
            anim = new AlphaAnimation(0f, 1f);
            anim.setDuration(200);
            progressBar.setAnimation(anim);
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            anim = new AlphaAnimation(1f, 0f);
            anim.setDuration(200);
            progressBar.setAnimation(anim);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showInstalledModuleList() {

        final List<String> modules = new ArrayList<>(installedModules.get(IMagicMirrorAdapter.KEY_DEFAULT_MODULES));
        modules.addAll(installedModules.get(IMagicMirrorAdapter.KEY_CUSTOM_MODULES));

        final CharSequence moduleNames[] = new CharSequence[modules.size()];

        for(int i = 0; i < modules.size(); i++) {
            moduleNames[i] = modules.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add a module");
        builder.setItems(moduleNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final MagicMirrorModule module = MagicMirrorModule.getModuleForName(moduleNames[which].toString());

                if(module != null) {
                    try {
                        JSONObject data = new JSONObject();
                        data.put(MagicMirrorModule.KEY_DATA_NAME, module.getName());

                        moduleDataAdapter.addModule(data, new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                            @Override
                            public void onAddModule(int status) {
                                if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {
                                    addModule(module);
                                }
                                else {
                                    Toast.makeText(getContext(), "Error while adding module '" + module.getName() + "'", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error while adding module '" + module.getName() + "'", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        builder.show();
    }

    public void setParentFragment(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void setModuleDataAdapter(IMagicMirrorAdapter moduleDataAdapter) {
        this.moduleDataAdapter = moduleDataAdapter;

        if(moduleDataAdapter != null && moduleList.isEmpty() && moduleDataAdapter.isConnectedToMirror()) {
            showProgressBar(true);
            moduleDataAdapter.getModuleList(new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                @Override
                public void onGetModuleList(int status, final List<MagicMirrorModule> modules) {
                if(status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(final MagicMirrorModule module : modules) {
                                    addModule(module);
                                }
                                showProgressBar(false);
                            }
                    });
                }
                }
            });
        }
    }

    @Override
    public void addView(View child) {
        if(child instanceof Snackbar.SnackbarLayout) {
            super.addView(child);
        }
        else {
            linearLayout.addView(child);
        }
    }

    @Override
    public void removeView(View view) {
        if(view instanceof Snackbar.SnackbarLayout) {
            super.removeView(view);
        }
        else {
            linearLayout.removeView(view);
        }
    }

    private void addModule(final MagicMirrorModule module) {
        if(!moduleList.contains(module)) {
            moduleList.add(module);

            module.addOnPropertyChangedCallback(moduleChangedCallback);

            final CardModulesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.card_modules, this, false);
            binding.setModule(module);
            addView(binding.getRoot());
            binding.getRoot().setTag(module);

            final LinearLayout settingsContent = (LinearLayout) binding.getRoot().findViewById(R.id.module_settings_content);
            final Spinner positionSpinner = (Spinner) binding.getRoot().findViewById(R.id.position_spinner);
            final ImageView expandIndicator = (ImageView) binding.getRoot().findViewById(R.id.indicatorExpand);
            final View expandBar = binding.getRoot().findViewById(R.id.expandBar);
            final View titleBar = binding.getRoot().findViewById(R.id.titleBar);

            // Position Spinner
            ArrayAdapter<MagicMirrorModule.PositionRegion> spinnerAdapter = new ArrayAdapter<>(parentFragment.getActivity(), android.R.layout.simple_spinner_item, MagicMirrorModule.PositionRegion.values());
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            positionSpinner.setAdapter(spinnerAdapter);

            settingsContent.setVisibility(module.isActive() ? View.VISIBLE : View.GONE);
            expandIndicator.setRotation(module.isActive() ? 180 : 0);

            View removeText = binding.getRoot().findViewById(R.id.removeText);
            removeText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setMessage("Do you really want to remove this value?")
                            .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    moduleDataAdapter.removeModule(moduleList.indexOf(module), new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {

                                        public void onRemoveModule(int status) {
                                            if (status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {
                                                parentFragment.getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        removeModule(module);
                                                    }
                                                });
                                            }
                                            else {
                                                Toast.makeText(getContext(), "Error while removing module '" + module.getName() + "'", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
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
            });

            OnClickListener expandClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    module.setActive(!module.isActive());

                    if (module.isInitialized()) {
                        if (expandBar.getAnimation() == null) {
                            settingsContent.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            Animation a;
                            if (!module.isActive()) {
                                a = new ExpandAndScrollAnimation(settingsContent.getHeight(), 0, settingsContent, nestedScrollView, binding.getRoot(), expandIndicator);
                            } else {
                                a = new ExpandAndScrollAnimation(settingsContent.getHeight(), settingsContent.getMeasuredHeight(), settingsContent, nestedScrollView, binding.getRoot(), expandIndicator);
                            }
                            a.setDuration(200);

                            expandBar.startAnimation(a);
                        }
                    } else {
                        module.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                            @Override
                            public void onPropertyChanged(Observable observable, int i) {

                                module.setInitialized();
                                module.removeOnPropertyChangedCallback(this);

                                parentFragment.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.getRoot().findViewById(R.id.progressBar1).setVisibility(GONE);
                                        settingsContent.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        Animation a;
                                        if (!module.isActive()) {
                                            a = new ExpandAndScrollAnimation(settingsContent.getHeight(), 0, settingsContent, nestedScrollView, binding.getRoot(), expandIndicator);
                                        } else {
                                            a = new ExpandAndScrollAnimation(settingsContent.getHeight(), settingsContent.getMeasuredHeight(), settingsContent, nestedScrollView, binding.getRoot(), expandIndicator);
                                        }
                                        a.setDuration(200);

                                        expandBar.startAnimation(a);
                                    }
                                });
                            }
                        });
                        moduleDataAdapter.getModuleData(moduleList.indexOf(module), new IMagicMirrorAdapter.MagicMirrorAdapterCallback() {
                            public void onGetModuleData(int status, JSONObject data) {
                                if (status == IMagicMirrorAdapter.MagicMirrorAdapterCallback.STATUS_SUCCESS) {
                                    try {
                                        module.setData(data);
                                    } catch (JSONException e) {
                                        Toast.makeText(getContext(), "Error while getting data for module '" + module.getName() + "'", Toast.LENGTH_LONG).show();
                                        Log.e("ModuleScrollView", "Error while getting data for module '" + module.getName() + "'", e);
                                        binding.getRoot().findViewById(R.id.progressBar1).setVisibility(GONE);
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Error while getting data for module '" + module.getName() + "'", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        binding.getRoot().findViewById(R.id.progressBar1).setVisibility(VISIBLE);
                        binding.getRoot().requestLayout();
                    }
                }
            };

            titleBar.setOnClickListener(expandClickListener);
            expandBar.setOnClickListener(expandClickListener);

            // add custom settings fragment
            final ModuleSettingsFragment settingsFragment = module.getAdditionalSettingsFragment();

            if(parentFragment != null && settingsFragment != null) {
                FrameLayout frameLayout = new FrameLayout(getContext());
                frameLayout.setId(Math.abs(module.hashCode()));
                settingsContent.addView(frameLayout);
                FragmentManager fragmentManager = parentFragment.getChildFragmentManager();
                fragmentManager.beginTransaction().replace(frameLayout.getId(), settingsFragment).commit();
            }

        }
    }

    private void removeModule(final MagicMirrorModule module) {
        if(moduleList.contains(module)) {
            moduleList.remove(module);
            removeView(findViewWithTag(module));
        }
    }

    private ArrayList<MagicMirrorModule> getModuleList() {
        return moduleList;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState ss = new SavedState(parcelable);

        ss.moduleList = this.moduleList;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.moduleList.addAll(ss.moduleList);

        for(MagicMirrorModule module : moduleList) {
            addModule(module);
        }
    }

    static class SavedState extends BaseSavedState {

        ArrayList<MagicMirrorModule> moduleList;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            MagicMirrorModule[] modules = (MagicMirrorModule[]) in.readParcelableArray(MagicMirrorModule.class.getClassLoader());
            this.moduleList = new ArrayList<>();
            Collections.addAll(this.moduleList, modules);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            Parcelable[] list = new Parcelable[moduleList.size()];
            for(int i = 0; i < moduleList.size(); i++) {
                list[i] = moduleList.get(i);
            }
            out.writeParcelableArray(list, 0);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
