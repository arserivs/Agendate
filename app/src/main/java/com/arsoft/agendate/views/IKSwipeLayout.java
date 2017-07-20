package com.arsoft.agendate.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


/**
 * Created by ivankoop on 3/22/17.
 */

public class IKSwipeLayout extends LinearLayout {

    private float max_width;
    private float mid_width;
    private IKSwipeLayout me;
    private boolean state;

    private Animation animation;

    public IKSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        me = this;


    }

    public void openAnimation() {
        FrameLayout parent = (FrameLayout) this.getParent();
        max_width = parent.getWidth();
        mid_width = (max_width / 2);
        final float percentage_width = ((35 * max_width) / 100);
        animation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                me.animate().translationX(- percentage_width);
            }


        };

        animation.setDuration(200);
        me.startAnimation(animation);
    }

    public void closeAnimation(){
        animation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                me.animate().translationX(0);
            }


        };

        animation.setDuration(200);
        me.startAnimation(animation);

    }


    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}



