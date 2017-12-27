package com.arsoft.agendate.functions ;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Usuario on 01/07/2017.
 */

public class Funciones {


    public static String cambiarFormatoFecha(String fecha, String fOrigen, String fDestino) {
        Calendar c = Calendar.getInstance();;
        DateFormat dateFormat = new SimpleDateFormat(fOrigen);

        try {
            c.setTime(dateFormat.parse(fecha));

            dateFormat = new SimpleDateFormat(fDestino);

            return dateFormat.format(c.getTime()) ;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "" ;

    }

    private static ProgressDialog progress=null ;
    public static void mostrarProgress(Activity act, String titulo, String mensaje) {
        if (progress != null) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
        }
        progress = ProgressDialog.show(act, ("".equals(titulo)?"Procesando":titulo), ("".equals(mensaje)?"Por favor aguarde ...":mensaje), true, false);
    }

    public static void ocultarProgress() {
        progress.dismiss();
    }

    //final ProgressDialog progress = ProgressDialog.show(getActivity(), "Procesando", "Por favor aguarde ...", true, false);

    //Esto son funciones helpers para las vistas que usan la mayor��a de los fragments
    public static void showErrorDialog(Context context, String message) {
        ocultarProgress();
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
        ocultarProgress();
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


    public static String limpiarTelefono(String txt) {
        StringBuilder res= new StringBuilder() ;

        for (int j=0; j<txt.length(); j++) {
            if ("+0123456789".indexOf(txt.substring(j,j+1))>=0) {
                res.append(txt.substring(j,j+1)) ;
            }
        }



        //Log.d("agendate","******" + res.toString()) ;
        if (res.toString().substring(0,4).equals("+595")) {
            //Log.d("agendate","------" + "0" + res.toString().substring(5, res.toString().length())) ;
            return "0" + res.toString().substring(5, res.toString().length()) ;
        }



        return res.toString() ;
    }

    public static String sumarFecha(String fecha, String formato, int seccion, int tiempo) {
        try {


            SimpleDateFormat df = new SimpleDateFormat(formato);
            Date date = df.parse(fecha);
            //Log.d("agendate", "sumarFecha fecha=" + fecha + "-seccion=" + seccion + "-tiempo=" + tiempo + "-date=" + date) ;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(seccion, tiempo);
            //Log.d("agendate", "sumarFecha calendar.getTime()=" + calendar.getTime()) ;
            fecha = df.format(calendar.getTime()) ;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fecha ;
    }

}
