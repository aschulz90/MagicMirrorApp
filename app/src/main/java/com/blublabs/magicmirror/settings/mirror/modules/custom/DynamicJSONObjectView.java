package com.blublabs.magicmirror.settings.mirror.modules.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.common.Utils;
import com.blublabs.magicmirror.databinding.DialogJsonBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static android.text.InputType.*;

/**
 * Created by andrs on 23.10.2016.
 */

public class DynamicJSONObjectView extends LinearLayout implements Observable {

    public enum DataType {
        Boolean(java.lang.Boolean.class),
        String(java.lang.String.class),
        Integer(java.lang.Integer.class),
        Double(java.lang.Double.class),
        Array(org.json.JSONArray.class),
        Object(org.json.JSONObject.class);

        private final Class<?> typeClass;

        DataType(Class<?> typeClass) {
            this.typeClass = typeClass;
        }

        public Class<?> getTypeClass() {
            return typeClass;
        }

        public static DataType from(Class<?> typeClass) {

            if(typeClass == null) {
                return null;
            }

            for(DataType value : DataType.values()) {
                if(typeClass.equals(value.getTypeClass())) {
                    return value;
                }
            }

            return null;
        }
    }

    private JSONObject objectData;

    private transient PropertyChangeRegistry mCallbacks;

    protected final Observable.OnPropertyChangedCallback propertyChangedCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            DynamicJSONObjectView.this.notifyChange();
        }
    };

    public DynamicJSONObjectView(Context context) {
        super(context);
    }

    public DynamicJSONObjectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicJSONObjectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setObjectData(JSONObject objectData) {
        this.objectData = objectData;
        initConfig();
        requestLayout();
    }

    private void initConfig() {

        removeAllViews();

        TextView addConfigView = (TextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, this, false);
        addConfigView.setText("Add Value");
        addView(addConfigView);

        addConfigView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddValueDialog();
            }
        });

        if(objectData != null) {

            Iterator<String> nameItr = objectData.keys();
            while (nameItr.hasNext()) {
                try {
                    String name = nameItr.next();
                    addConfigValue(name, objectData.get(name));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void addConfigValue(@NonNull String name, @NonNull Object value) {

        final DataType type = DataType.from(value.getClass());

        switch (type) {
            case Boolean:
                addConfigValue(name, (Boolean) value);
                break;
            case String:
                addConfigValue(name, (String) value);
                break;
            case Integer:
                addConfigValue(name, (Integer) value);
                break;
            case Double:
                addConfigValue(name, (Double) value);
                break;
            case Array:
                addConfigValue(name, (JSONArray) value);
                break;
            case Object:
                addConfigValue(name, (JSONObject) value);
                break;
            default:
                break;
        }
    }

    private void removeValue(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Do you really want to remove this value?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        objectData.remove(key);
                        removeView(findViewWithTag(key));
                        notifyChange();
                        requestLayout();
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

    protected void addConfigValue(@NonNull final String name, @NonNull Boolean value) {
        View rowBoolean = LayoutInflater.from(getContext()).inflate(R.layout.row_json_boolean, this, false);
        rowBoolean.setTag(name);

        TextView title = (TextView) rowBoolean.findViewById(R.id.textViewBoolean);
        title.setText(name);

        SwitchCompat switchView = (SwitchCompat) rowBoolean.findViewById(R.id.switchBoolean);
        switchView.setChecked(value);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    objectData.put(name, isChecked);
                    notifyChange();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageView removeIcon = (ImageView) rowBoolean.findViewById(R.id.deleteIcon);
        removeIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeValue(name);
            }
        });

        addView(rowBoolean, getChildCount() - 1);
    }

    private EditText createTextRow(@NonNull final String name, @NonNull Object value) {
        final View rowText = LayoutInflater.from(getContext()).inflate(R.layout.row_json_text, this, false);
        rowText.setTag(name);

        TextView title = (TextView) rowText.findViewById(R.id.textViewText);
        title.setText(name);

        EditText editText = (EditText) rowText.findViewById(R.id.editText);
        editText.setText(value.toString());

        ImageView removeIcon = (ImageView) rowText.findViewById(R.id.deleteIcon);
        removeIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeValue(name);
            }
        });

        addView(rowText, getChildCount() - 1);

        return editText;
    }

    protected void addConfigValue(@NonNull final String name, @NonNull String value) {
        EditText editText = createTextRow(name, value);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    objectData.put(name, s.toString());
                    notifyChange();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void addConfigValue(@NonNull final String name, @NonNull Integer value) {
        EditText editText = createTextRow(name, value);
        editText.setInputType(TYPE_CLASS_NUMBER);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    objectData.put(name, Integer.parseInt(s.toString()));
                    notifyChange();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (NumberFormatException e) {
                    //NOP
                }
            }
        });
    }

    protected void addConfigValue(@NonNull final String name, @NonNull Double value) {
        EditText editText = createTextRow(name, value);
        editText.setInputType(TYPE_NUMBER_FLAG_DECIMAL);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    objectData.put(name, Double.parseDouble(s.toString()));
                    notifyChange();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (NumberFormatException e) {
                    //NOP
                }
            }
        });
    }

    protected void addConfigValue(@NonNull final String name, @NonNull JSONArray value) {
        View rowArray = LayoutInflater.from(getContext()).inflate(R.layout.row_json_array, this, false);
        rowArray.setTag(name);

        TextView title = (TextView) rowArray.findViewById(R.id.textViewText);
        title.setText(name);

        DynamicJSONArrayView dynamicConfigView = (DynamicJSONArrayView) rowArray.findViewById(R.id.dynamicConfigView);
        dynamicConfigView.setArrayData(value);
        dynamicConfigView.addOnPropertyChangedCallback(propertyChangedCallback);

        ImageView removeIcon = (ImageView) rowArray.findViewById(R.id.deleteIcon);
        removeIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeValue(name);
            }
        });

        addView(rowArray, getChildCount() - 1);
    }

    protected void addConfigValue(@NonNull final String name, @NonNull JSONObject value) {
        View rowObject = LayoutInflater.from(getContext()).inflate(R.layout.row_json_object, this, false);
        rowObject.setTag(name);

        TextView title = (TextView) rowObject.findViewById(R.id.textViewText);
        title.setText(name);

        DynamicJSONObjectView dynamicJSONObjectView = (DynamicJSONObjectView) rowObject.findViewById(R.id.dynamicConfigView);
        dynamicJSONObjectView.setObjectData(value);
        dynamicJSONObjectView.addOnPropertyChangedCallback(propertyChangedCallback);

        ImageView removeIcon = (ImageView) rowObject.findViewById(R.id.deleteIcon);
        removeIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeValue(name);
            }
        });

        addView(rowObject, getChildCount() - 1);
    }

    @Override
    public synchronized void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (mCallbacks == null) {
            mCallbacks = new PropertyChangeRegistry();
        }
        mCallbacks.add(callback);
    }

    @Override
    public synchronized void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    public synchronized void notifyChange() {
        if (mCallbacks != null) {
            mCallbacks.notifyCallbacks(this, 0, null);
        }
    }

    private void showAddValueDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        DialogJsonBinding dialogViewBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_json, null, false);
        View dialogView = dialogViewBinding.getRoot();
        final TextInputLayout nameEditLayout = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutName);
        final EditText nameEditText = (EditText) dialogView.findViewById(R.id.editTextName);

        // data type
        final Spinner dataTypeSpinner = (Spinner) dialogView.findViewById(R.id.spinnerType);
        final ArrayAdapter<DataType> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, DataType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataTypeSpinner.setAdapter(adapter);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(dialogView);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
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

                final String name = nameEditText.getText().toString();

                if(objectData.has(name)) {
                    Toast.makeText(getContext(), "Value '" + name + "' already exists!", Toast.LENGTH_LONG).show();
                    return;
                }
                else  if(!Utils.validateEditTextValue(name, nameEditLayout, "Value name not set!")) {
                    return;
                }
                try {
                    switch ((DataType) dataTypeSpinner.getSelectedItem()) {
                        case Boolean:
                            objectData.put(name, false);
                            addConfigValue(name, false);
                            break;
                        case String:
                            objectData.put(name, "");
                            addConfigValue(name, "");
                            break;
                        case Integer:
                            objectData.put(name, 0);
                            addConfigValue(name, 0);
                            break;
                        case Double:
                            objectData.put(name, 0.0);
                            addConfigValue(name, 0.0);
                            break;
                        case Array:
                            objectData.put(name, new JSONArray());
                            addConfigValue(name, new JSONArray());
                            break;
                        case Object:
                            objectData.put(name, new JSONObject());
                            addConfigValue(name, new JSONObject());
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                requestLayout();
                notifyChange();

                alertDialog.dismiss();
            }
        });
    }
}
