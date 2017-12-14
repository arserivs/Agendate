package com.arsoft.agendate;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.arsoft.agendate.functions.Funciones;
import com.arsoft.agendate.json.DBApp;
import com.arsoft.agendate.json.User;
import com.arsoft.agendate.json.UserInfo;
import com.arsoft.agendate.models.Doctor;
import com.arsoft.agendate.models.Turno;
import com.arsoft.agendate.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.firebase.iid.FirebaseInstanceId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class WelcomeActivity extends AppCompatActivity {

    private boolean didVerifyVersion = false;
    //private DatabaseReference mDatabase ;
    private EditText welcomeUsuario=null ;
    private EditText welcomeClave=null ;
    private String email;


    public static final String TAG = "NOTICIAS";

    private TextView infoTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
        setContentView(R.layout.welcome);

        //mDatabase = FirebaseDatabase.getInstance().getReference();

        //Funciones.showDialog(this, mPhoneNumber);

        if(savedInstanceState != null) {
            this.didVerifyVersion = savedInstanceState.getBoolean("didVerifyVersion");
        }

        final Typeface mbBold = Typeface.createFromAsset(getAssets(), "fonts/FSMillbank-Bold.ttf");
        final Typeface mbRegular = Typeface.createFromAsset(getAssets(), "fonts/FSMillbank-Regular.ttf");

        final LinearLayout layout = (LinearLayout) findViewById(R.id.welcome_linearLayout);
        welcomeUsuario = (EditText) findViewById(R.id.welcome_usuario) ;
        welcomeClave = (EditText) findViewById(R.id.welcome_clave) ;

        for(TextView tw : Funciones.getTextViewsByTag(layout, "bold")) {
            tw.setTypeface(mbBold);
        }

        for(TextView tw : Funciones.getTextViewsByTag(layout, "regular")) {
            tw.setTypeface(mbRegular);
        }



        final TextView topTextView = (TextView) findViewById(R.id.welcome_topTextView);

        /*
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Log.d("agendate", "----------------lenght-" + manager.getAccounts().length) ;
        final ArrayAdapter<String> cuentas = new ArrayAdapter<String>(this, android.R.layout.select_dialog_multichoice);
        if (manager.getAccounts().length>0) {
            for (Account list : manager.getAccounts()) {
                Log.d("agendate", "-----------------" + list.toString()) ;
                if (list.name.indexOf("@gmail.com")>0 || list.name.indexOf("@google.com")>0) {
                    cuentas.add(list.name) ;
                }
            }

            //if (cuentas.getCount()>1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                builder.setTitle("Seleccionà una cuenta")
                    .setAdapter(cuentas, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //obtenerCuenta(cuentas.getItem(which));
                            //login(cuentas.getItem(which)) ;
                            email=cuentas.getItem(which) ;

                        }
                    }
                ).create().show();

            //} else {
                //obtenerCuenta(cuentas.getItem(0));
                //login(cuentas.getItem(0)) ;
                //email=cuentas.getItem(0);
            //}

        } else {

        }
*/

        if(!didVerifyVersion) {
            User.verificarVersion(this);
            didVerifyVersion = true;
        }


        infoTextView = (TextView) findViewById(R.id.welcome_topTextView);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                infoTextView.append("\n" + key + ": " + value);
            }
        }

        String token = FirebaseInstanceId.getInstance().getToken() ;

        Log.d(TAG, "Token 1: " + token);

        final Button welcomeIngresar = (Button) findViewById(R.id.welcome_ingresar) ;
        welcomeIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });


    }

    private void registrarse() {


    }

/*
    private void obtenerCuenta(final String usuario) {

        final List<String> p = new ArrayList<>() ;
        p.add("usuario") ;

        DBApp.request(1, p, null, this, new DBApp.DBAppListener(){
            @Override
            public void respuesta(DataSnapshot datos, String error) {
                if (error != null) {
                    Funciones.showErrorDialog(WelcomeActivity.this, error);
                } else {
                    for (DataSnapshot postSnapshot: datos.getChildren()) {
                        final Usuario dbusuario = postSnapshot.getValue(Usuario.class);
                        if (dbusuario.email.equals(usuario)) {
                            //login(dbusuario.telefono);
                            return;
                        }
                    }

                    Funciones.showErrorDialog(WelcomeActivity.this, "La cuenta que seleccionaste no esta registrada en nuestro sistema");
                }
            }

        });


    }

*/


    private void login() {

        if (!"".equals(welcomeClave.getText().toString())) {
            Funciones.mostrarProgress(this, "", "");

            final List<String> p = new ArrayList<>();
            p.add("usuario/" + welcomeUsuario.getText().toString());
            /*
            p.add("email");
            p.add(cuenta);
            p.add("clave");
            p.add(welcomeClave.getText().toString());
            */

            DBApp.request(1, p, null, this, new DBApp.DBAppListener() {
                @Override
                public void respuesta(DataSnapshot datos, String error) {
                    if (error != null) {
                        Funciones.showErrorDialog(WelcomeActivity.this, error);
                    } else {
                        Log.d("agendate", "datos=" + datos.toString());
                        Usuario usu = datos.getValue(Usuario.class);
                        if (usu != null) {

                            if (usu.clave.equals(welcomeClave.getText().toString())) {
                                final List<String> d = new ArrayList<>();
                                d.add("doctor/" + datos.getKey());

                                DBApp.request(1, d, null, WelcomeActivity.this, new DBApp.DBAppListener() {
                                    @Override
                                    public void respuesta(DataSnapshot datosd, String error) {

                                        Funciones.ocultarProgress();


                                        if (error != null) {
                                            Funciones.showErrorDialog(WelcomeActivity.this, error);
                                        } else {
                                            Log.d("agendate", "datos=" + datosd.toString());
                                            Doctor doc = datosd.getValue(Doctor.class);
                                            if (doc != null) {

                                                final UserInfo userInfo = new UserInfo();
                                                userInfo.idUsuario = datosd.getKey();
                                                userInfo.nombre = doc.nombre;

                                                Intent intent = new Intent(WelcomeActivity.this, DrawerActivity.class);
                                                intent.putExtra("userInfo", userInfo);
                                                startActivity(intent);


                                            } else {
                                                Funciones.showErrorDialog(WelcomeActivity.this, "No está registrado para utilizar la app");
                                            }
                                        }
                                    }

                                });

                            } else {
                                Funciones.showErrorDialog(WelcomeActivity.this, "Login incorrecto");
                            }


                        } else {
                            Funciones.showErrorDialog(WelcomeActivity.this, "No está registrado para utilizar la app");
                        }
                    }
                }

            });
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("didVerifyVersion", didVerifyVersion);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                UserInfo userInfoTouch = intent.getParcelableExtra("userInfo");
                Intent drawerActivityIntent = new Intent(WelcomeActivity.this, DrawerActivity.class);
                drawerActivityIntent.putExtra("userInfo", userInfoTouch);
                startActivity(drawerActivityIntent);
            }
        }
    }






}
