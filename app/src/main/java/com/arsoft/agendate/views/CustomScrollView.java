package com.arsoft.agendate.views;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by ivankoop on 2/1/17.
 */

public class CustomScrollView extends ScrollView {


    public boolean is_bottom = false;

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public final static String REQUEST_RECEIVER = "REQUEST_RECEIVER";

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt)
    {

        View view = (View) getChildAt(getChildCount()-1);

        int diff = (view.getBottom()-(getHeight()+getScrollY()));

        if( diff == 0 )
        {
            this.is_bottom = true;
       
            Log.e("ivankoop", "Scroll llego al final");
            Intent intent = new Intent(REQUEST_RECEIVER);
            intent.putExtra("Request", true);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    public boolean isBottom(){
        return is_bottom;
    }



}
