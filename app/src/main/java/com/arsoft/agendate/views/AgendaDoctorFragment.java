package com.arsoft.agendate.views;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.arsoft.agendate.CustomListView;
import com.arsoft.agendate.DrawerActivity;
import com.arsoft.agendate.R;
import com.arsoft.agendate.functions.Funciones;
import com.arsoft.agendate.json.UserInfo;

import com.arsoft.agendate.models.Turno;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Usuario on 25/06/2017.
 */

public class AgendaDoctorFragment extends Fragment {

    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private static Calendar c ;
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static TextView agendaFecha  ;
    private UserInfo userInfo ;
    private CustomListView listaAgendaDoctor ;
    private ProgressDialog progress ;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View returnView = inflater.inflate(R.layout.agenda_doctor, container, false);
        userInfo =  ((DrawerActivity)getActivity()).getUserInfo() ;
        listaAgendaDoctor = (CustomListView) returnView.findViewById(R.id.agenda_doctor_lista);


        // Use the current date as the default date in the picker
        c = Calendar.getInstance();
        agendaFecha = (TextView) returnView.findViewById(R.id.agenda_doctor_fecha) ;
        final LinearLayout agendaDoctorFechaLyt = (LinearLayout) returnView.findViewById(R.id.agenda_doctor_lytfecha) ;
        agendaDoctorFechaLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mostrarCalendario(view) ;
            }
        });



        agendaFecha.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("riverosa","********cambio texto desde=" + s.toString());
                cargarTurnos(s.toString()) ;
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        agendaFecha.setText(dateFormat.format(c.getTime()));
        //agendaFecha.setText("20-07-2017");

        //cargarTurnos("20-07-2017") ;



        return returnView ;
    }


    private void cargarTurnos(final String fecha) {

        //if ("".equals(agendaFecha.getText().toString())) {

        //}


        progress = ProgressDialog.show(getActivity(), "Procesando", "Por favor aguarde ...", true, false);
        //.orderByChild("hora")
        //("".equals(fecha)
        //        ? mDatabase.child("turno").child(userInfo.nroTelefono)
        //        : mDatabase.child("turno").child(userInfo.nroTelefono).startAt(fecha)
        //).addValueEventListener(new ValueEventListener() {
        //startAt(fecha).endAt(fecha)

        mDatabase.child("turno").child(userInfo.nroTelefono).orderByChild("fecha_hora").startAt(fecha+"_00:00").endAt(fecha+"_23:59").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progress.dismiss();

                List<Turno> listaTurnos = new ArrayList<Turno>() ;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    listaTurnos.add(postSnapshot.getValue(Turno.class));

                }

                AgendaDoctorAdapter adapter = new AgendaDoctorAdapter(getActivity(), listaTurnos);
                listaAgendaDoctor.setAdapter(adapter);
                listaAgendaDoctor.setVisibility(View.VISIBLE);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progress.dismiss();
                Log.d("agendate","The read failed: " + databaseError.getCode());
            }
        });

    }

    private class AgendaDoctorAdapter extends ArrayAdapter<Turno> {

        private Context context;


        public AgendaDoctorAdapter(Context context, List<Turno> list) {
            super(context, 0, list);
            this.context = context;

        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View returnView = inflater.inflate(R.layout.agenda_doctor_row, null);

            final TextView nombre = (TextView) returnView.findViewById(R.id.agenda_doctor_row_nombre);
            final TextView telefono = (TextView) returnView.findViewById(R.id.agenda_doctor_row_telefono);
            final TextView hora = (TextView) returnView.findViewById(R.id.agenda_doctor_row_hora);
            final TextView fecha = (TextView) returnView.findViewById(R.id.agenda_doctor_row_fecha);


            final Turno item = getItem(position);

            if (item != null) {
                nombre.setText(item.nombre);
                telefono.setText(item.paciente);
                hora.setText(item.hora);
                fecha.setText(item.fecha);

                returnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //cargarDesdeHistorico(item);

                    }
                }) ;

            }



            return returnView;

        }
    }



    public void mostrarCalendario(View v) {
        try {
                c.setTime(dateFormat.parse(agendaFecha.getText().toString()));// all done

        } catch (Exception ex) {

        }


        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");


    }


    public static void setearCalendario(String fecha) {

        Log.d("riverosa","elegi la fecha=" + fecha);
        if (!fecha.equals(agendaFecha.getText().toString())) {
            agendaFecha.setText(fecha);
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
            //Log.d("riverosa","elegi la fecha=" + day + "-" + (month+1) + "-" + year);
            setearCalendario((day<10?"0":"") + day + "-" + (month<9?"0":"") +  (month+1) + "-" + year) ;
        }
    }

}