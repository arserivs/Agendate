package com.arsoft.agendate.json;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class DBApp  {
    final static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();



    public interface DBAppListener {
        void respuesta(DataSnapshot datos, String error);
    }


    public DBApp() {

    }


    public static void request(final int tipo,
                               final List<String> par,
                               final Activity activity,
                               final DBAppListener listener) {

        final Handler handler = new Handler();

        if(activity!=null && !isNetworkAvailable(activity)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.respuesta(null, "Dental Care no está pudiendo obtener una conexión, compruebe la señal de su teléfono y si puede acceder a internet");
                }
            });
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            switch (tipo) {

                                case 1:
                                    mDatabase.child(par.get(0)).addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.d("agendate","dataSnapshot=" + dataSnapshot.toString()) ;
                                            listener.respuesta(dataSnapshot, null);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //Log.d("agendate","The read failed: " + databaseError.getCode());
                                            listener.respuesta(null, "The read failed: " + databaseError.getCode());
                                        }
                                    });

                                    break;

                                case 2:
                                    mDatabase.child(par.get(0)).child(par.get(1)).addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            listener.respuesta(dataSnapshot, null);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //Log.d("agendate","The read failed: " + databaseError.getCode());
                                            listener.respuesta(null, "The read failed: " + databaseError.getCode());
                                        }
                                    });

                                    break ;


                                case 3:

                                    break ;


                                case 4:
                                    mDatabase.child(par.get(0)).child(par.get(1)).orderByChild(par.get(2)).startAt(par.get(3)).limitToFirst(Integer.parseInt(par.get(4))).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            listener.respuesta(dataSnapshot, null);
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                    break;

                                default:
                                    listener.respuesta(null, "Error en el tipo de consulta");
                            }
                            /*
                            if (jsonItem != null && jsonItem.error != null && jsonItem.error.length() > 0) {
                                listener.respuesta(null, jsonItem.error);
                            } else {
                                listener.respuesta(jsonItem, null);
                            }
                            */
                        }
                    });
                } catch (Exception ex) {
                    listener.respuesta(null, "Error al intentar obtener datos");
                }

            }
        }).start();

    }


    private static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}