package com.blublabs.magicmirror.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blublabs.magicmirror.R;
import com.blublabs.magicmirror.utils.Utils;

import static android.widget.LinearLayout.HORIZONTAL;

/**
 * Created by Andreas Schulz on 11.12.2016.
 */

public class InfoIcon extends FrameLayout {

    final String description;
    final String defaultVal;
    final CharSequence[] possibleValues;
    final String notes;
    final String title;

    public InfoIcon(final Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InfoIcon, 0, 0);

        description = ta.getString(R.styleable.InfoIcon_description);
        defaultVal = ta.getString(R.styleable.InfoIcon_defaultVal);
        possibleValues = ta.getTextArray(R.styleable.InfoIcon_possibleValues);
        notes = ta.getString(R.styleable.InfoIcon_notes);
        title = ta.getString(R.styleable.InfoIcon_title);

        ta.recycle();

        init(context, attrs);
    }

    public InfoIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InfoIcon, 0, 0);

        description = ta.getString(R.styleable.InfoIcon_description);
        defaultVal = ta.getString(R.styleable.InfoIcon_defaultVal);
        possibleValues = ta.getTextArray(R.styleable.InfoIcon_possibleValues);
        notes = ta.getString(R.styleable.InfoIcon_notes);
        title = ta.getString(R.styleable.InfoIcon_title);

        ta.recycle();

        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        final LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        layout.setOrientation(HORIZONTAL);
        addView(layout);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InfoIcon, 0, 0);

        if(Utils.isNotEmtpy(title)) {
            final TextView textView = new TextView(context);
            textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            textView.setText(title);
            textView.setTextSize(20);
            layout.addView(textView);
        }

        ta.recycle();

        boolean showTooltip = Utils.isNotEmtpy(description) || Utils.isNotEmtpy(defaultVal) || Utils.isNotEmtpy(notes) || (possibleValues != null && possibleValues.length > 0);

        if(showTooltip) {
            final AppCompatImageView image = new AppCompatImageView(context);
            image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            image.setImageResource(R.drawable.ic_action_info);
            layout.addView(image);

            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(createTooltipView(context));
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            //just close
                                        }
                                    }).create().show();
                }
            });
        }
    }

    private View createTooltipView(Context context) {
        final View tooltipView = LayoutInflater.from(context).inflate(R.layout.tooltip_value_description, null);

        if(Utils.isNotEmtpy(title)) {
            ((TextView) tooltipView.findViewById(R.id.tooltipTitle)).setText(title);
        }
        else {
            tooltipView.findViewById(R.id.tooltipTitle).setVisibility(GONE);
        }

        if(Utils.isNotEmtpy(description)) {
            ((TextView) tooltipView.findViewById(R.id.tooltipDescription)).setText(description);
        }

        if(Utils.isNotEmtpy(defaultVal)) {
            ((TextView) tooltipView.findViewById(R.id.tooltipDefaultValue)).setText(defaultVal);
        }
        else {
            tooltipView.findViewById(R.id.tooltipDefaultValueFrame).setVisibility(GONE);
        }

        if(possibleValues != null && possibleValues.length > 0) {

            String txt = possibleValues[0].toString();

            for(int i = 1; i < possibleValues.length; i++) {
                txt += ", " + possibleValues[i];
            }

            ((TextView) tooltipView.findViewById(R.id.tooltipPossibleValues)).setText(txt);
        }
        else {
            tooltipView.findViewById(R.id.tooltipPossibleValuesFrame).setVisibility(GONE);
        }

        if(Utils.isNotEmtpy(notes)) {
            ((TextView) tooltipView.findViewById(R.id.tooltipNotes)).setText(notes);
        }
        else {
            tooltipView.findViewById(R.id.tooltipNotesFrame).setVisibility(GONE);
        }

        return tooltipView;
    }

}
