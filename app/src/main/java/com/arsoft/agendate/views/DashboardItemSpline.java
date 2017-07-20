package com.arsoft.agendate.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by larcho on 8/3/16.
 */
public class DashboardItemSpline extends LinearLayout {

    private List<Long> graficoValues;
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
            setStyle(Style.FILL);
            setAntiAlias(true);
            setColor(Color.argb(51, 255, 255, 255));
        }
    };

    public DashboardItemSpline(Context context) {
        super(context);
    }

    public DashboardItemSpline(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DashboardItemSpline(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setGraficoValues(List<List<Long>> graficoValues) {
        this.graficoValues = new ArrayList<>();
        for(List<Long> list : graficoValues) {
            if(graficoValues.size() >= 2) {
                this.graficoValues.add(list.get(1));
            }
        }
    }

    public void setGraficoValuesSimple(List<Long> graficoValues) {
        this.graficoValues = graficoValues;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float width = (float)getWidth();
        float height = (float)getHeight();

        if(this.graficoValues != null && this.graficoValues.size() > 0 && width > 0 && height > 0) {

            Long max = Collections.max(graficoValues);
            Long min = Collections.min(graficoValues);

            long diff = max - min;
            float gHeight = (float) (height / 2.0);

            Path strokePath = new Path();
            Path bgPath = new Path();
            bgPath.moveTo(0, height);

            try {
                for (float i = 0; i < graficoValues.size(); i++) {

                    float x = (width / (float) (graficoValues.size() - 1)) * i;
                    float y = (height - (((graficoValues.get((int) i) - (float)min) / (float)diff) * gHeight)) - (gHeight / 8.0f);

                    if (i == 0) {
                        strokePath.moveTo(x, y);
                    } else {
                        strokePath.lineTo(x, y);
                    }
                    bgPath.lineTo(x, y);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            bgPath.lineTo(width, height);
            bgPath.close();

            canvas.drawPath(bgPath, paintBG);
            canvas.drawPath(strokePath, paintStroke);
        }
    }
}
