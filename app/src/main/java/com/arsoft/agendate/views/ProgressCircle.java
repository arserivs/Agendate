package com.arsoft.agendate.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.arsoft.agendate.R;

/**
 * Created by larcho on 4/1/16.
 */
public class ProgressCircle extends TextView {

    private Paint strokeBg;
    private Paint strokeFill;
    private int totalSteps = 0;

    public ProgressCircle(Context context) {
        super(context);
        setup();
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressCircle, 0, 0);
        try {
            this.totalSteps = ta.getInt(R.styleable.ProgressCircle_stepsTotal, 0);
        } finally {
            ta.recycle();
        }
        setup();
    }

    public ProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        float scale = getContext().getResources().getDisplayMetrics().density;

        strokeBg = new Paint();
        strokeBg.setStyle(Paint.Style.STROKE);
        strokeBg.setStrokeWidth(1 * scale + 0.5f);
        strokeBg.setColor(0xFFE1E1E1);
        strokeBg.setAntiAlias(true);

        strokeFill = new Paint();
        strokeFill.setStyle(Paint.Style.STROKE);
        strokeFill.setStrokeWidth(1 * scale + 0.5f);
        strokeFill.setColor(0xFFE62E78);
        strokeFill.setAntiAlias(true);

        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/FSMillbank-Bold.ttf"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int arc = 90;
        int number = 0;
        try {
            number = Integer.parseInt(getText().toString());
        } catch (Exception ex) {
            number = 0;
        }

        if(number > 0 && totalSteps > 0) {
            int arcItem = (int)(360.0 / (float)totalSteps);
            arc = number * arcItem;
        }

        float scale = getContext().getResources().getDisplayMetrics().density;
        RectF rect = new RectF(scale, scale, getWidth() - (scale * 2), getHeight() - (scale * 2));


        canvas.drawArc(rect, 0, 360, false, strokeBg);
        canvas.drawArc(rect, -90, arc, false, strokeFill);

        super.onDraw(canvas);
    }
}
