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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    //final static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    final static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();


    public interface DBAppListener {
        void respuesta(DataSnapshot datos, String error);
        void respuesta(boolean actualizo);
    }


    public DBApp() {

    }


    public static void request(final int tipo,
                               final List<String> par,
                               final Object obj,
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

                    /*
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

*/
                    final ValueEventListener mListener=new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("agendate",tipo + " dataSnapshot=" + dataSnapshot.toString()) ;
                            listener.respuesta(dataSnapshot, null);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.d("agendate","The read failed: " + databaseError.getCode());
                            listener.respuesta(null, "The read failed: " + databaseError.getCode());
                        }
                    } ;

                            switch (tipo) {

                                case 1:
                                    //SELECT * FROM DB WHERE CAMPO=PAR.GET(0)
                                    Log.d("--case 1--","getReference("+par.get(0)+")") ;



                                    mDatabase.getReference(par.get(0)).addListenerForSingleValueEvent(mListener);

                                    break;


                                case 2:
                                    //SELECT * FROM DB WHERE CAMPO=PAR.GET(0) and CAMPO2=PAR.GET(3) and CAMPO4=PAR.GET(5)
                                    Log.d("--case 2--","getReference("+par.get(0)+").orderByChild("+par.get(1)+").equalTo("+par.get(2)+").orderByChild("+par.get(3)+").equalTo("+par.get(4)+")") ;
                                    mDatabase.getReference(par.get(0)).orderByChild(par.get(1)).equalTo(par.get(2)).orderByChild(par.get(3)).equalTo(par.get(4)).addListenerForSingleValueEvent(mListener);

                                    break ;


                                case 3:
                                    //SELECT * FROM DB WHERE CAMPO=PAR.GET(0) AND PAR.GET(1) BETWEEN PAR.GET(2) AND PAR.GET(3) ORDER BY PAR.GET(1)

                                    //mDatabase.child(par.get(0)).child(par.get(1)).orderByChild(par.get(2)).startAt(par.get(3)).endAt(par.get(4)).addValueEventListener(new ValueEventListener() {
                                    Log.d("--case 3--","getReference("+par.get(0)+").orderByChild("+par.get(1)+").startAt("+par.get(2)+").endAt("+par.get(3)+"))") ;
                                    mDatabase.getReference(par.get(0)).orderByChild(par.get(1)).startAt(par.get(2)).endAt(par.get(3)).addListenerForSingleValueEvent(mListener);

                                    break ;


                                case 4:
                                    //SELECT * FROM DB WHERE CAMPO=PAR.GET(0) AND PAR.GET(1) >= PAR.GET(2) ORDER BY PAR.GET(1)

                                    //mDatabase.child(par.get(0)).child(par.get(1)).orderByChild(par.get(2)).startAt(par.get(3)).limitToFirst(Integer.parseInt(par.get(4))).addChildEventListener(new ChildEventListener() {
                                    Log.d("--case 4--","getReference("+par.get(0)+").orderByChild("+par.get(1)+").startAt("+par.get(2)+").limitToFirst(Integer.parseInt("+par.get(3)+"))") ;
                                    mDatabase.getReference(par.get(0)).orderByChild(par.get(1)).startAt(par.get(2)).limitToFirst(Integer.parseInt(par.get(3))).addListenerForSingleValueEvent(mListener);
                                            /*
                                            .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            Log.d("agendate","4 dataSnapshot=" + dataSnapshot.toString()) ;
                                            listener.respuesta(dataSnapshot, null);
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                            Log.d("agendate","4 onChildChanged=" + dataSnapshot.toString()) ;

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                                            Log.d("agendate","4 onChildRemoved=" + dataSnapshot.toString()) ;

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                            Log.d("agendate","4 onChildMoved=" + dataSnapshot.toString()) ;

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.d("agendate","4 onCancelled=") ;

                                        }
                                    });
                                    */


                                    break;


                                case 5:
                                    //SELECT * FROM DB WHERE CAMPO=PAR.GET(0) and PAR.GET(1)=PAR.GET(2)
                                    Log.d("--case 5--","getReference("+par.get(0)+").orderByChild("+par.get(1)+").equalTo("+par.get(2)+")") ;
                                    mDatabase.getReference(par.get(0)).orderByChild(par.get(1)).equalTo(par.get(2)).addListenerForSingleValueEvent(mListener);

                                    break ;
                                case 6:
                                    //SELECT * FROM DB WHERE CLAVE BETWEEN PAR.GET(1) AND PAR.GET(2)
                                    Log.d("--case 6--","getReference("+par.get(0)+").orderByKey().startAt("+par.get(1)+").endAt("+par.get(2)+"))") ;
                                    mDatabase.getReference(par.get(0)).orderByKey().startAt(par.get(1)).endAt(par.get(2)).addListenerForSingleValueEvent(mListener);

                                    break ;
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
                       // }
                    //});
                } catch (Exception ex) {
                    listener.respuesta(null, "Error al intentar obtener datos");
                }

            }
        }).start();

    }





    public static void update(final int tipo,
                              final List<String> par,
                              final Object obj,
                              final Map<String, Object> childUpdates,
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

                    /*handler.post(new Runnable() {
                        @Override
                        public void run() {

*/

                            switch (tipo) {

                                case 10:

                                    //INSERTA O ACTUALIZA UN OBJETO A PARTIR DE LA REFERENCIA
                                    //mDatabase.child(par.get(0)).child(par.get(1)).setValue(obj) ;
                                    Log.d("--case 10--", "getReference(" + par.get(0) + ")");
                                    mDatabase.getReference(par.get(0)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            dataSnapshot.getRef().setValue(obj);


                                            listener.respuesta(dataSnapshot, null);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //Log.d("agendate","The read failed: " + databaseError.getCode());
                                            listener.respuesta(null, "The update failed: " + databaseError.getCode());
                                        }
                                    });


                                    break;

                                case 11:

                                    //ACTUALIZA UN VALOR A PARTIR DE LA REFERENCIA
                                    //mDatabase.child(par.get(0)).child(par.get(1)).setValue(obj) ;
                                    //mDatabase.getReference(par.get(0)).setValue(obj) ;
                                    //mDatabase.getReference(par.get(0)).orderByChild(par.get(1)).equalTo(par.get(2)).getRef().child(par.get(3)).setValue(par.get(4)) ;

                                    Log.d("--case 11--", "getReference(" + par.get(0) + ").orderByChild(" + par.get(1) + ").equalTo(" + par.get(2) + ")");
                                    mDatabase.getReference(par.get(0)).orderByChild(par.get(1)).equalTo(par.get(2)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                ds.getRef().child(par.get(3)).setValue(par.get(4));
                                            }
                                            listener.respuesta(dataSnapshot, null);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //Log.d("agendate","The read failed: " + databaseError.getCode());
                                            listener.respuesta(null, "The update failed: " + databaseError.getCode());
                                        }
                                    });


                                    break;
                                case 20:
                                    mDatabase.getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            listener.respuesta(task.isSuccessful());
                                        }
                                    });

                                    break ;

                                default:
                                    listener.respuesta(null, "Error al actualizar");
                            }

                        //}
                    //});
                } catch (Exception ex) {
                    listener.respuesta(false);
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