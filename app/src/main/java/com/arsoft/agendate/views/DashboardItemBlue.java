package com.arsoft.agendate.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by larcho on 8/4/16.
 */
public class DashboardItemBlue extends LinearLayout {

    private float graphTotal;
    private float graphValue;
    private Paint paintStroke = new Paint() {
        {
            setStyle(Style.STROKE);
            setAntiAlias(true);
            setColor(Color.argb(255, 255, 255, 255));
            setStrokeWidth(getContext().getResources().getDisplayMetrics().density);
        }
    };

    private Paint paintBG = new Paint() {
        {
            setStyle(Style.STROKE);
            setAntiAlias(true);
            setColor(Color.argb(51, 255, 255, 255));
            setStrokeWidth(getContext().getResources().getDisplayMetrics().density);
        }
    };

    public DashboardItemBlue(Context context) {
        super(context);
    }

    public DashboardItemBlue(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DashboardItemBlue(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setGraphTotal(float graphTotal) {
        this.graphTotal = graphTotal;
    }

    public void setGraphValue(float graphValue) {
        this.graphValue = graphValue;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = (float)getWidth();
        float height = (float)getHeight();

        if(graphTotal > 0 && graphValue > 0 && width > 0 && height > 0) {
            Path strokePath = new Path();
            Path bgPath = new Path();

            float density = getContext().getResources().getDisplayMetrics().density;
            float y = (density * 25.0f);

            strokePath.moveTo(0, height - y);
            bgPath.moveTo(0, height - y);

            bgPath.lineTo(width, height - y);

            float x = (graphValue / graphTotal) * width;
            strokePath.lineTo(x, height - y);

            canvas.drawPath(bgPath, paintBG);
            canvas.drawPath(strokePath, paintStroke);
        }
    }
}
