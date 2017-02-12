package com.blublabs.magicmirror.utils;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.blublabs.magicmirror.common.BindableRangeBar;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by andrs on 08.10.2016.
 */

public final class Utils {

    private Utils() {}

    public static boolean validateEditTextValue(String value, TextInputLayout textInputLayout, String errorMessage) {
        if (value == null || value.isEmpty()) {
            textInputLayout.setError(errorMessage);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public static String loadJsonFromAsset(Context context, String file) {
        String json = null;

        try {
            InputStream is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotEmtpy(String value) {
        return !isEmpty(value);
    }

    public static boolean objectsEqual(Object a, Object b){
        return (a == b) || (a != null && a.equals(b));
    }

    public static class TextIntegerBinder {
        @BindingAdapter("android:text")
        public static void setText(TextView view, Integer oldValue, Integer newValue) {

            if (!objectsEqual(oldValue, newValue)) {
                view.setText(newValue == null ? "" : String.valueOf(newValue));
            }
        }

        @InverseBindingAdapter(attribute = "android:text")
        public static Integer getText(TextView view) {
            try {
                return Integer.parseInt(view.getText().toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public static class TextFloatBinder {
        @BindingAdapter("android:text")
        public static void setText(TextView view, Double oldValue, Double newValue) {

            if (!objectsEqual(oldValue, newValue)) {
                view.setText(newValue == null ? "" : String.valueOf(newValue));
            }
        }

        @InverseBindingAdapter(attribute = "android:text")
        public static Double getText(TextView view) {
            try {
                return Double.parseDouble(view.getText().toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    @InverseBindingMethods({
            @InverseBindingMethod(type = AppCompatSpinner.class, attribute = "app:selectedItem"),
    })
    public static class SpinnerEnumBinder {
        @BindingAdapter("app:selectedItem")
        public static void setSelectedItem(AppCompatSpinner view, Enum oldValue, Enum newValue) {

            if (!objectsEqual(oldValue, newValue)) {
                view.setSelection((newValue).ordinal());
            }
        }

        @BindingAdapter("selectedItemAttrChanged")
        public static void setSelectedItemListener(AppCompatSpinner view, final InverseBindingListener selectedItemChange) {
            if (selectedItemChange == null) {
                view.setOnItemSelectedListener(null);
            } else {
                view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedItemChange.onChange();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }
    }
}
