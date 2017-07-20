package com.arsoft.agendate.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arsoft.agendate.R;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by larcho on 10/7/16.
 */

public class StaticHelpers {

    //Esto son funciones helpers para las vistas que usan la mayoría de los fragments
    public static void showErrorDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public static void showDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("")
                .setMessage(message)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public static List<TextView> getTextViewsByTag(ViewGroup root, String tag) {
        List<TextView> views = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getTextViewsByTag((ViewGroup) child, tag));
            } else if (child instanceof TextView) {
                final Object tagObj = child.getTag();
                if (tagObj != null && tagObj.equals(tag)) {
                    views.add((TextView) child);
                }
            }

        }
        return views;
    }

    public static void ocultarTeclado(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isEmptyEditText(EditText editText) {
        String aux = editText.getText().toString();
        return aux.isEmpty();
    }

    public static byte[] obtImagen(String url){

        if(url.startsWith("http:"))  url=url.replace("http", "https");
        byte[] bytes=null;

        HttpURLConnection con = null;
        InputStream in=null;
        try{

            con=(HttpsURLConnection) new URL(url).openConnection();
            con.setReadTimeout(30000);
            con.setConnectTimeout(10000);
            in=con.getInputStream();
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            bytes=byteBuffer.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(con!=null) con.disconnect();
        }

        return bytes;

    }

}