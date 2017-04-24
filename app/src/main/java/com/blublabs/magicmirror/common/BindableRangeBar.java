package com.blublabs.magicmirror.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.appyvet.rangebar.RangeBar;
import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Andreas Schulz on 20.01.2017.
 */

@InverseBindingMethods(value = {
        @InverseBindingMethod(type = BindableRangeBar.class, attribute = "app:currentValue")
})
public class BindableRangeBar extends RangeBar {

    public BindableRangeBar(Context context) {
        this(context,null);
    }

    public BindableRangeBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BindableRangeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setRangeBarEnabled(false);
        setSelectorColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        setPinColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        setConnectingLineColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        setTickHeight(0);
        setTemporaryPins(false);

        try {
            // WORKAROUND: the "active" colors don't get set in their respective setters
            Field field = RangeBar.class.getDeclaredField("mActiveConnectingLineColor");
            field.setAccessible(true);
            field.setInt(this, ContextCompat.getColor(getContext(), R.color.colorPrimary));

            field = RangeBar.class.getDeclaredField("mActiveCircleColor");
            field.setAccessible(true);
            field.setInt(this, ContextCompat.getColor(getContext(), R.color.colorPrimary));

            // WORKAROUND: there is no setter for this value, except in xml
            field = RangeBar.class.getDeclaredField("mMaxPinFont");
            field.setAccessible(true);
            field.setFloat(this, 18 * getResources().getDisplayMetrics().density);
            Method method = RangeBar.class.getDeclaredMethod("createPins");
            method.setAccessible(true);
            method.invoke(this);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BindableRangeBar, 0, 0);

        final float defaultValue = ta.getFloat(R.styleable.BindableRangeBar_defaultRangeValue, getTickStart());
        setRangePinsByValue(getTickStart(), defaultValue);

        ta.recycle();
    }

    public Number getCurrentValue() {
        return Double.parseDouble(getRightPinValue());
    }

    public void setCurrentValue(Number newValue) {
        if(newValue != null && !Double.valueOf(getRightPinValue()).equals(newValue)) {
            setRangePinsByValue(getTickStart(), newValue.floatValue());
        }
    }

    @BindingAdapter("app:currentValue")
    public static void setCurrentValue(BindableRangeBar view, Number oldValue, Number newValue) {

        if (!Utils.objectsEqual(oldValue, newValue)) {
            view.setCurrentValue(newValue.doubleValue());
        }
    }

    @InverseBindingAdapter(attribute = "app:currentValue")
    public static Number getCurrentValue(BindableRangeBar view) {
        return view.getCurrentValue().intValue();
    }

    @BindingAdapter("currentValueAttrChanged")
    public static void setCurrentValueListener(BindableRangeBar rangeBar, final InverseBindingListener currentValueChange) {

        if (currentValueChange == null) {
            rangeBar.setOnRangeBarChangeListener(null);
        } else {
            rangeBar.setOnRangeBarChangeListener(new OnRangeBarChangeListener() {
                @Override
                public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                    currentValueChange.onChange();
                }
            });
        }
    }
}
