package com.arsoft.agendate.views;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arsoft.agendate.CustomListView;
import com.arsoft.agendate.DrawerActivity;
import com.arsoft.agendate.R;
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

public class AgendaTurnoFragment extends Fragment {







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View returnView = inflater.inflate(R.layout.agenda_turno, container, false);

        final Turno turnoSel = getArguments().getParcelable("turno");

        if (turnoSel != null) {
            Log.d("turnoSel.paciente", turnoSel.paciente) ;

            final TextView paciente = (TextView) returnView.findViewById(R.id.agenda_turno_paciente);
            paciente.setText(turnoSel.nombre);

            final TextView telefono = (TextView) returnView.findViewById(R.id.agenda_turno_telefono);
            telefono.setText(turnoSel.paciente);

            final TextView fecha = (TextView) returnView.findViewById(R.id.agenda_turno_fecha);
            fecha.setText(turnoSel.fecha);

            final TextView hora = (TextView) returnView.findViewById(R.id.agenda_turno_hora);
            hora.setText(turnoSel.hora);

        }

        return returnView ;
    }



}