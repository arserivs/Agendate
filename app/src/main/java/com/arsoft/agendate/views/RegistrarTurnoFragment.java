package com.arsoft.agendate.views;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arsoft.agendate.R;
import com.arsoft.agendate.functions.Funciones;
import com.arsoft.agendate.json.DBApp;
import com.arsoft.agendate.models.Paciente;
import com.google.firebase.database.DataSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Usuario on 25/06/2017.
 */

public class RegistrarTurnoFragment extends Fragment {
    private static final int PICK_CONTACT=1;


    private EditText pctNombre  ;
    private EditText pctTelefono  ;
    private EditText pctDireccion  ;
    private EditText pctNick ;
    private static TextView pctFechaNacimiento  ;
    private static TextView pctFechaIngreso  ;

    private static Calendar c ;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static String CAMPO_FECHA ;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View returnView = inflater.inflate(R.layout.registrar_turno, container, false);

        pctTelefono = (EditText) returnView.findViewById(R.id.registrar_turno_telefono) ;


        // Use the current date as the default date in the picker
        c = Calendar.getInstance();

        final LinearLayout lytFecha = (LinearLayout) returnView.findViewById(R.id.registrar_turno_lytfecha) ;
        lytFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario(view) ;
            }
        });




        final ImageView contactos = (ImageView) returnView.findViewById(R.id.registrar_turno_contactos);
        contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(CommonDataKinds.Phone.CONTENT_TYPE);
                //Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        //TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        //Log.d("aa","numero : " + tm.getLine1Number());

        final Button btnEnviar = (Button) returnView.findViewById(R.id.registrar_turno_siguiente) ;

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Write a message to the database
                //final String key = pctTelefono.getText().toString() ;
                //Log.d("---------------","/paciente/"+key) ;
                //mDatabase.child("paciente").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                final List<String> p = new ArrayList<>() ;
                p.add("paciente/" + pctTelefono.getText().toString()) ;
                //p.add(pctTelefono.getText().toString()) ;

                DBApp.request(1, p, null, getActivity(), new DBApp.DBAppListener(){
                    @Override
                    public void respuesta(DataSnapshot datos, String error) {
                        if (error != null) {
                            Funciones.showErrorDialog(getActivity(), error);
                        } else {
                            Paciente post = datos.getValue(Paciente.class);
                            if (post != null) {
                                Log.d("agendate","nombre----" +  post.nombre);
                                Funciones.showErrorDialog(getActivity(), "Ya existe el paciente en el registro bajo el nombre " + post.nombre);
                            } else {
                                Funciones.showDialog(getActivity(), "Debe insertar");
                                Paciente newPaciente = new Paciente(pctNombre.getText().toString(), "","", "", pctNombre.getText().toString()) ;
                                //mDatabase.child("paciente").child(key).setValue(newPaciente);
                                DBApp.request(10, p, newPaciente, getActivity(), new DBApp.DBAppListener(){
                                    @Override
                                    public void respuesta(DataSnapshot datos, String error) {
                                        if (error != null) {
                                            Funciones.showErrorDialog(getActivity(), error);
                                        } else {

                                        }
                                    }
                                }) ;
                            }
                        }
                    }
                });

            }
        });

        final TextView btnCancelar = (TextView) returnView.findViewById(R.id.registrar_turno_cancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();

            }
        });


        return returnView ;
    }




    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    ContentResolver cr=getActivity().getApplicationContext().getContentResolver() ;


                    Uri uri = data.getData();
                    String[] projection = { CommonDataKinds.Phone.NUMBER, CommonDataKinds.Phone.DISPLAY_NAME };
                    Cursor cursor = cr.query(uri, projection, null, null, null);
                    String nro = "";
                    String nom = "";
                    while (cursor.moveToNext()) {
                        int numberColumnIndex = cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER);
                        int nameColumnIndex = cursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME);


                        if (cursor.getString(numberColumnIndex).length()>=9) {
                            nro = allTrim(cursor.getString(numberColumnIndex).trim()) ;
                            nom = cursor.getString(nameColumnIndex).trim() ;
                            if ( nro.substring(0,2).equals("09") && nro.length()==10) {
                                break;
                            }

                            if ( nro.substring(0,3).equals("021") && nro.length()==9) {
                                break;
                            }

                            if (nro.substring(0,4).equals("+595")) {
                                nro = "0" + nro.substring(4, nro.length()) ;
                                break;
                            }

                            nro="" ;
                        }
                    }
                    cursor.close();


                    pctTelefono.setText(nro);
                    pctNombre.setText(nom);

                    //Log.d("------nro", nro) ;


                }
                break;
        }
    }



    private String allTrim(String txt) {
        StringBuilder res= new StringBuilder() ;


        for (int j=0; j<txt.length(); j++) {
            if ("+0123456789".indexOf(txt.substring(j,j+1))>=0) {
                res.append(txt.substring(j,j+1)) ;
            }
        }



        return res.toString() ;
    }



    public void mostrarCalendario(View v) {
        try {
            if (CAMPO_FECHA.equals("NACIMIENTO")) {
                c.setTime(dateFormat.parse(pctFechaNacimiento.getText().toString()));// all done

            } else {
                c.setTime(dateFormat.parse(pctFechaIngreso.getText().toString()));// all done
            }
        } catch (Exception ex) {

        }


        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");


    }


    public static void setearCalendario(String fecha) {
        if (CAMPO_FECHA.equals("NACIMIENTO")) {
            pctFechaNacimiento.setText(fecha);
        } else {
            pctFechaIngreso.setText(fecha);
        }



    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int yearHoy = c.get(Calendar.YEAR);
            int monthHoy = c.get(Calendar.MONTH);
            int dayHoy = c.get(Calendar.DAY_OF_MONTH);


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, yearHoy, monthHoy, dayHoy);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Log.d("riverosa","elegi la fecha=" + day + "/" + (month+1) + "/" + year);
            setearCalendario((day<10?"0":"") + day + "/" + (month<9?"0":"") +  (month+1) + "/" + year) ;
        }
    }
}