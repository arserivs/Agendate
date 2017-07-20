package com.arsoft.agendate;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.arsoft.agendate.functions.Funciones;
import com.arsoft.agendate.json.JsonBaseItem;
import com.arsoft.agendate.json.User;
import com.arsoft.agendate.json.UserInfo;
import com.arsoft.agendate.models.Doctor;
import com.arsoft.agendate.views.StaticHelpers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Calendar;


public class WelcomeActivity extends AppCompatActivity {

    private boolean didVerifyVersion = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
        setContentView(R.layout.welcome);

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

        final View ingresarAgenda = findViewById(R.id.welcome_layoutIngresarAgenda);
        //final View locales = findViewById(R.id.welcome_layoutLocales);
        //final View pagoMiContacto = findViewById(R.id.welcome_layoutPagoMiContacto);
        final View registrarPaciente = findViewById(R.id.welcome_layoutRegistrarPaciente);

        ingresarAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, DrawerActivity.class);
                startActivity(intent);
            }
        });

        /*
        locales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, DrawerActivity.class);
                startActivity(intent);
            }
        });

        pagoMiContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, DrawerActivity.class);
                intent.putExtra("PagoMiContacto", "S");
                startActivity(intent);
            }
        });
        */


        registrarPaciente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                registrarse();
            }

        });

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);


        final String key = Funciones.limpiarTelefono(tMgr.getLine1Number()) ;
        System.out.println("-------"+key);
        mDatabase.child("doctor").child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Doctor post = dataSnapshot.getValue(Doctor.class);
                if (post != null) {
                    System.out.println("nombre----" +  post.nombre);
                    Funciones.showDialog(WelcomeActivity.this, "Encontro doctor " + post.nombre);

                    final UserInfo userInfo = new UserInfo() ;
                    userInfo.nroTelefono = key;
                    userInfo.nombre = post.nombre ;

                    Intent intent = new Intent(WelcomeActivity.this, DrawerActivity.class);
                    intent.putExtra("userInfo", userInfo) ;
                    startActivity(intent);


                } else {
                    Funciones.showErrorDialog(WelcomeActivity.this, "No esta registrado como Doctor en la app");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });



        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(WelcomeActivity.this, DrawerActivity.class);
                intent.putExtra("userInfo", userInfo) ;
                startActivity(intent);
            }
        }, 2000);
        */

        if(!didVerifyVersion) {
            User.verificarVersion(this);
            didVerifyVersion = true;
        }
    }

    private void registrarse() {


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
