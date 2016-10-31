package com.blublabs.magicmirror.modules.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.databinding.DialogJsonBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

/**
 * Created by andrs on 24.10.2016.
 */

public class DynamicJSONArrayView extends DynamicJSONObjectView {

    private JSONArray arrayData;

    public DynamicJSONArrayView(Context context) {
        super(context);
    }

    public DynamicJSONArrayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicJSONArrayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setArrayData(JSONArray arrayData) {
        this.arrayData = arrayData;
        initConfig();
        requestLayout();
    }

    private void initConfig() {

        removeAllViews();

        TextView addConfigView = (TextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, this, false);
        addConfigView.setText("Add Value");
        addView(addConfigView);

        if(arrayData != null) {
            for(int i = 0; i < arrayData.length(); i++) {
                try {
                    addConfigValue(i, arrayData.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void addConfigValue(int index, @NonNull Object value) {

        final DataType type = DataType.from(value.getClass());

        switch (type) {
            case Boolean:
                addConfigValue(index, (Boolean) value);
                break;
            case String:
                addConfigValue(index, (String) value);
                break;
            case Integer:
                addConfigValue(index, (Integer) value);
                break;
            case Double:
                addConfigValue(index, (Double) value);
                break;
            case Array:
                addConfigValue((JSONArray) value);
                break;
            case Object:
                addConfigValue((JSONObject) value);
                break;
            default:
                break;
        }
    }

    protected void addConfigValue(final int index, @NonNull Boolean value) {
        View rowBoolean = LayoutInflater.from(getContext()).inflate(R.layout.row_json_boolean, this, false);

        TextView title = (TextView) rowBoolean.findViewById(R.id.textViewBoolean);
        title.setVisibility(GONE);

        SwitchCompat switchView = (SwitchCompat) rowBoolean.findViewById(R.id.switchBoolean);
        switchView.setChecked(value);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    arrayData.put(index, isChecked);
                    notifyChange();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        addView(rowBoolean, index);
    }

    private EditText createTextRow(@NonNull Object value) {
        View rowText = LayoutInflater.from(getContext()).inflate(R.layout.row_json_text, this, false);

        TextView title = (TextView) rowText.findViewById(R.id.textViewText);
        title.setVisibility(GONE);

        EditText editText = (EditText) rowText.findViewById(R.id.editText);
        editText.setText(value.toString());

        addView(rowText, getChildCount() - 1);

        return editText;
    }

    protected void addConfigValue(final int index, @NonNull String value) {
        EditText editText = createTextRow(value);
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
                    arrayData.put(index, s.toString());
                    notifyChange();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void addConfigValue(final int index, @NonNull Integer value) {
        EditText editText = createTextRow(value);
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
                    arrayData.put(index, Integer.parseInt(s.toString()));
                    notifyChange();
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void addConfigValue(final int index, @NonNull Double value) {
        EditText editText = createTextRow(value);
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
                    arrayData.put(index, Double.parseDouble(s.toString()));
                    notifyChange();
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void addConfigValue(@NonNull JSONArray value) {
        View rowObject = LayoutInflater.from(getContext()).inflate(R.layout.row_json_array, this, false);

        TextView title = (TextView) rowObject.findViewById(R.id.textViewText);
        title.setVisibility(GONE);

        DynamicJSONArrayView dynamicConfigView = (DynamicJSONArrayView) rowObject.findViewById(R.id.dynamicConfigView);
        dynamicConfigView.setArrayData(value);
        dynamicConfigView.addOnPropertyChangedCallback(propertyChangedCallback);

        addView(rowObject, getChildCount() - 1);
    }

    protected void addConfigValue(@NonNull JSONObject value) {
        View rowObject = LayoutInflater.from(getContext()).inflate(R.layout.row_json_object, this, false);

        TextView title = (TextView) rowObject.findViewById(R.id.textViewText);
        title.setVisibility(GONE);

        DynamicJSONObjectView dynamicJSONObjectView = (DynamicJSONObjectView) rowObject.findViewById(R.id.dynamicConfigView);
        dynamicJSONObjectView.setObjectData(value);
        dynamicJSONObjectView.addOnPropertyChangedCallback(propertyChangedCallback);

        addView(rowObject, getChildCount() - 1);
    }

    private void showAddValueDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        DialogJsonBinding dialogViewBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_json, null, false);
        View dialogView = dialogViewBinding.getRoot();
        final TextInputLayout nameEditLayout = (TextInputLayout) dialogView.findViewById(R.id.inputLayoutName);
        nameEditLayout.setVisibility(GONE);

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

                try {
                    switch ((DataType) dataTypeSpinner.getSelectedItem()) {
                        case Boolean:
                            arrayData.put(false);
                            addConfigValue(arrayData.length() - 1, false);
                            break;
                        case String:
                            arrayData.put("");
                            addConfigValue(arrayData.length() - 1, "");
                            break;
                        case Integer:
                            arrayData.put(0);
                            addConfigValue(arrayData.length() - 1, 0);
                            break;
                        case Double:
                            arrayData.put(0.0);
                            addConfigValue(arrayData.length() - 1, 0.0);
                            break;
                        case Array:
                            arrayData.put(new JSONArray());
                            addConfigValue(new JSONArray());
                            break;
                        case Object:
                            arrayData.put(new JSONObject());
                            addConfigValue(new JSONObject());
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
