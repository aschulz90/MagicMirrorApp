package com.blublabs.magicmirror.common;

import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by andrs on 04.10.2016.
 */

public class ExpandAndScrollAnimation extends Animation {

    private final int startHeight;
    private final float startRotation;
    private final int deltaHeight;
    private final View collapsableView;
    private final NestedScrollView scrollView;
    private final View item;
    private final ImageView expandIndicator;

    public ExpandAndScrollAnimation(int startHeight, int endHeight, final View collapsableView, final NestedScrollView scrollView, final View item, ImageView expandIndicator) {
        this.startHeight = startHeight;
        this.deltaHeight = endHeight - startHeight;
        this.collapsableView = collapsableView;
        this.scrollView = scrollView;
        this.item = item;
        this.expandIndicator = expandIndicator;
        this.startRotation = expandIndicator.getRotation();
    }

    @Override
    protected void applyTransformation(float interpolatedTime,  Transformation t) {
        final int newHeight = (int) (startHeight + deltaHeight * interpolatedTime);
        if(interpolatedTime == 1) {
            collapsableView.getLayoutParams().height = WRAP_CONTENT;
        }
        else {
            collapsableView.getLayoutParams().height = newHeight;
        }

        final float newRotation = startRotation + (180 * interpolatedTime * Math.signum(deltaHeight));
        expandIndicator.setRotation(newRotation);

        if (newHeight <= 0) {
            collapsableView.setVisibility(View.GONE);
        } else {
            collapsableView.setVisibility(View.VISIBLE);
        }
        collapsableView.requestLayout();

        final int bottom = item.getBottom();
        final int top = item.getTop();
        final int listViewHeight = scrollView.getHeight();
        if (bottom > listViewHeight) {
            scrollView.smoothScrollTo(0, Math.min(bottom - listViewHeight + scrollView.getPaddingBottom(), top));
        }
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
