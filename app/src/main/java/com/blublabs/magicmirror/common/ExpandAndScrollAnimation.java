package com.blublabs.magicmirror.common;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by andrs on 04.10.2016.
 */

public class ExpandAndScrollAnimation extends Animation {

    private final int startHeight;
    private final int deltaHeight;
    private final View collapsableView;
    private final RecyclerView recyclerView;
    private final View item;

    public ExpandAndScrollAnimation(int startHeight, int endHeight, final View collapsableView, final RecyclerView recyclerView, final View item) {
        this.startHeight = startHeight;
        this.deltaHeight = endHeight - startHeight;
        this.collapsableView = collapsableView;
        this.recyclerView = recyclerView;
        this.item = item;
    }

    @Override
    protected void applyTransformation(float interpolatedTime,  Transformation t) {
        final int newHeight = (int) (startHeight + deltaHeight * interpolatedTime);
        collapsableView.getLayoutParams().height = newHeight;

        if (newHeight <= 0) {
            collapsableView.setVisibility(View.GONE);
        } else {
            collapsableView.setVisibility(View.VISIBLE);
        }
        collapsableView.requestLayout();

        final int bottom = item.getBottom();
        final int listViewHeight = recyclerView.getHeight();
        if (bottom > listViewHeight) {
            final int top = item.getTop();
            if (top > 0) {
                recyclerView.smoothScrollBy(0, Math.min(bottom - listViewHeight + recyclerView.getPaddingBottom(), top));
            }
        }
        else {
            final int top = item.getTop();
            if(top < 0) {
                recyclerView.smoothScrollBy(0, top - recyclerView.getPaddingTop());
            }
        }
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
