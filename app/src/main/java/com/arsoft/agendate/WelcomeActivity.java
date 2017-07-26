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


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class WelcomeActivity extends AppCompatActivity {

    private boolean didVerifyVersion = false;
    //private DatabaseReference mDatabase ;



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

        for(TextView tw : Funciones.getTextViewsByTag(layout, "bold")) {
            tw.setTypeface(mbBold);
        }

        for(TextView tw : Funciones.getTextViewsByTag(layout, "regular")) {
            tw.setTypeface(mbRegular);
        }

        final TextView topTextView = (TextView) findViewById(R.id.welcome_topTextView);
        /*
        final TextView versionTextView = (TextView) findViewById(R.id.welcome_textViewVersion);


        String titleString = "";
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(hour >= 6 && hour < 12) {
            titleString = "Buenos días";
        } else if(hour >= 12 && hour < 18) {
            titleString = "Buenas tardes";
        } else {
            titleString = "Buenas noches";
        }

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            versionTextView.setText("Build " + verCode + "." + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        topTextView.setText(titleString);
        */


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

            if (cuentas.getCount()>1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                builder.setTitle("Seleccionà una cuenta")
                    .setAdapter(cuentas, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            obtenerCuenta(cuentas.getItem(which));

                        }
                    }
                ).create().show();

            } else {
                obtenerCuenta(cuentas.getItem(0));
            }

        } else {

        }


        if(!didVerifyVersion) {
            User.verificarVersion(this);
            didVerifyVersion = true;
        }
    }

    private void registrarse() {


    }


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
                            login(dbusuario.telefono);
                            return;
                        }
                    }

                    Funciones.showErrorDialog(WelcomeActivity.this, "La cuenta que seleccionaste no esta registrada en nuestro sistema");
                }
            }

        });


    }




    private void login(final String telefono) {

        final List<String> p = new ArrayList<>() ;
        p.add("doctor/"+telefono) ;
        //p.add(telefono) ;

        DBApp.request(1, p, null, this, new DBApp.DBAppListener(){
            @Override
            public void respuesta(DataSnapshot datos, String error) {
                if (error != null) {
                    Funciones.showErrorDialog(WelcomeActivity.this, error);
                } else {
                    Doctor post = datos.getValue(Doctor.class);
                    if (post != null) {
                        Log.d("agendate", "nombre----" + post.nombre);
                        //Funciones.showDialog(WelcomeActivity.this, "Encontro doctor " + post.nombre);

                        final UserInfo userInfo = new UserInfo();
                        userInfo.nroTelefono = telefono;
                        userInfo.nombre = post.nombre;

                        Intent intent = new Intent(WelcomeActivity.this, DrawerActivity.class);
                        intent.putExtra("userInfo", userInfo);
                        startActivity(intent);


                    } else {
                        Funciones.showErrorDialog(WelcomeActivity.this, "No esta registrado como Doctor en la app");
                    }
                }

            }

        });


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
