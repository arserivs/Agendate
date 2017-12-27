package com.arsoft.agendate.views;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.widget.TimePicker;

import com.arsoft.agendate.DrawerActivity;
import com.arsoft.agendate.R;
import com.arsoft.agendate.functions.Funciones;
import com.arsoft.agendate.json.DBApp;
import com.arsoft.agendate.json.UserInfo;
import com.arsoft.agendate.models.Paciente;
import com.arsoft.agendate.models.Turno;
import com.google.firebase.database.DataSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Usuario on 25/06/2017.
 */

public class RegistrarTurnoFragment extends Fragment {
    private static final int PICK_CONTACT=1;


    private TextView turnoNombre  ;
    private EditText turnoTelefono  ;
    private static TextView turnoFecha  ;
    private static TextView turnoHora ;
    private EditText turnoAnotacion ;
    private static String turnoPaciente ;


    private static Calendar c ;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private DateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private static String CAMPO_FECHA ;
    private UserInfo userInfoInt ;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View returnView = inflater.inflate(R.layout.registrar_turno, container, false);

        userInfoInt = ((DrawerActivity)getActivity()).getUserInfo() ;


        turnoNombre = (TextView) returnView.findViewById(R.id.registrar_turno_nombre) ;
        turnoTelefono = (EditText) returnView.findViewById(R.id.registrar_turno_telefono) ;
        turnoFecha = (TextView) returnView.findViewById(R.id.registrar_turno_fecha) ;
        turnoHora = (TextView) returnView.findViewById(R.id.registrar_turno_hora) ;
        turnoAnotacion = (EditText) returnView.findViewById(R.id.registrar_turno_anotaciones) ;


        // Use the current date as the default date in the picker
        c = Calendar.getInstance();

        final LinearLayout lytFecha = (LinearLayout) returnView.findViewById(R.id.registrar_turno_lytfecha) ;
        lytFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario(view) ;
            }
        });

        final LinearLayout lytHora = (LinearLayout) returnView.findViewById(R.id.registrar_turno_lythora) ;
        lytHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarHora(view);
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


        final Button btnRegistrar = (Button) returnView.findViewById(R.id.registrar_turno_siguiente) ;

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Funciones.mostrarProgress(getActivity(), "Dental Care", "Registrando turno");

                final Turno post = new Turno("S",
                        turnoPaciente,
                        turnoFecha.getText().toString(),
                        turnoHora.getText().toString(),
                        turnoNombre.getText().toString(),
                        turnoAnotacion.getText().toString()
                ) ;


                int duracion=userInfoInt.duracionmedia ;
                String desde = Funciones.sumarFecha(post.fecha_hora, "yyyyMMddHHmm", Calendar.MINUTE, (-1 * duracion)) ;
                String hasta = Funciones.sumarFecha(post.fecha_hora, "yyyyMMddHHmm", Calendar.MINUTE, duracion) ;

                Log.d("agendate", "fechahora " + desde + "<" + post.fecha_hora + "<" + hasta) ;
                final List<String> p = new ArrayList<>();
                p.add("turno/" + userInfoInt.idUsuario);
                p.add(desde);
                p.add(hasta);

                DBApp.request(6, p, null, getActivity(), new DBApp.DBAppListener() {
                    @Override
                    public void respuesta(DataSnapshot datos, String error) {
                        if(datos.getValue() != null) {
                            Funciones.ocultarProgress();
                            Turno turact=datos.getValue(Turno.class) ;
                            Funciones.showErrorDialog(getActivity(), "Tienes un turno en muy próximo en ese horario: " + turact.nombre + " a las " + turact.hora);
                        } else {
                            //Map<String, Object> postValues = post.toMap();
                            //Map<String, Object> childUpdates = new HashMap<>();
                            //childUpdates.put("/turno/" + userInfoInt.idUsuario + "/" + post.fecha_hora, postValues);
                            final List<String> t = new ArrayList<>() ;
                            t.add("turno/" + userInfoInt.idUsuario + "/" + post.fecha_hora) ;



                            DBApp.update(10, t, post, null, getActivity(), new DBApp.DBAppListener() {
                                @Override
                                public void respuesta(DataSnapshot datos, String error) {
                                    Funciones.ocultarProgress();
                                    if (error != null) {
                                        Funciones.showErrorDialog(getActivity(), error);
                                    } else {
                                        Funciones.showDialog(getActivity(), "Se registró el turno");
                                    }
                                }

                                @Override
                                public void respuesta(boolean actualizo) {
                                    /*
                                    Funciones.ocultarProgress();
                                    if (actualizo) {
                                        Funciones.showDialog(getActivity(), "Se registró el turno");
                                    }else {
                                        Funciones.showErrorDialog(getActivity(), "Ocurrió un error al intentar registrar el turno");
                                    }
                                    */

                                }
                            }) ;
                        }

                    }

                    @Override
                    public void respuesta(boolean actualizo) {}
                }) ;



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


                    //nro="0971159170" ;
                    final String pctnro=nro ;
                    final  String pctnom=nom ;
                    turnoTelefono.setText(pctnro);
                    final List<String> p = new ArrayList<>() ;
                    p.add("pacientetelefono/" + pctnro) ;


                    DBApp.request(1, p, null, getActivity(), new DBApp.DBAppListener(){
                        @Override
                        public void respuesta(DataSnapshot datos2, String error2) {
                            if (error2 != null) {
                                Funciones.showErrorDialog(getActivity(), error2);
                            } else {
                                Log.d("agendate", "datos=" + datos2.toString());
                                Turno post = datos2.getValue(Turno.class);
                                if (post != null) {

                                    turnoPaciente = post.paciente ;
                                    Log.d("agendate", "turnoPaciente=" + turnoPaciente);
                                    final List<String> p2 = new ArrayList<>() ;
                                    p2.add("paciente/" + turnoPaciente) ;

                                    DBApp.request(1, p2, null, getActivity(), new DBApp.DBAppListener(){
                                        @Override
                                        public void respuesta(DataSnapshot datos, String error) {
                                            if (error != null) {
                                                Funciones.showErrorDialog(getActivity(), error);
                                            } else {
                                                Paciente post2 = datos.getValue(Paciente.class);
                                                if (post2 != null) {
                                                    Log.d("agendate","nombre----" +  post2.nombre);
                                                    turnoNombre.setText(post2.nombre);
                                                } else {
                                                    //Funciones.showErrorDialog(getActivity(), "No está registrado como paciente");
                                                    registrarPaciente(pctnom, pctnro) ;

                                                }
                                            }
                                        }

                                        @Override
                                        public void respuesta(boolean actualizo) { }

                                    });
                                } else {
                                    //Funciones.showErrorDialog(getActivity(), "No está registrado como paciente");
                                    registrarPaciente(pctnom, pctnro);
                                }
                            }
                        }

                        @Override
                        public void respuesta(boolean actualizo) { }

                    });

                    /*
                    final List<String> p = new ArrayList<>() ;
                    p.add("paciente") ;
                    p.add("telefono") ;
                    p.add(nro) ;

                    DBApp.request(5, p, null, getActivity(), new DBApp.DBAppListener(){
                        @Override
                        public void respuesta(DataSnapshot datos2, String error2) {
                            if (error2 != null) {
                                Funciones.showErrorDialog(getActivity(), error2);
                            } else {
                                turnoPaciente = datos2.getKey() ;
                                Log.d("agendate","turnoPaciente----" +  turnoPaciente);
                                Paciente post2 = datos2.getValue(Paciente.class);
                                if (post2 != null) {
                                    Log.d("agendate","nombre----" +  post2.nombre);
                                    turnoNombre.setText(post2.nombre);
                                }
                            }
                        }
                    });
                    */
                }
                break;
        }
    }


    private  void registrarPaciente(final String pacienteNombre, final String pacienteTelefono) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Registro no encontrado")
                .setMessage("No está registrado como paciente ¿Quieres registrarlo ahora?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        Bundle bundle = new Bundle();
                        bundle.putString("pacienteNombre", pacienteNombre);
                        bundle.putString("pacienteTelefono", pacienteTelefono);

                        RegistrarPacienteFragment fragment = new RegistrarPacienteFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.loggedinbase_frameLayout, fragment);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                        ft.commit();

                    }
                })
                .show();
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
            if (!"".equals(turnoFecha.getText().toString())) {
                c.setTime(dateFormat.parse(turnoFecha.getText().toString()));// all done
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.d("agendate", "c antes de Fecha=" + c.getTime()) ;
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");


    }


    public static void setearCalendario(String fecha) {
            turnoFecha.setText(fecha);
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

    public void mostrarHora(View v) {
        try {
            if (!"".equals(turnoFecha.getText().toString()) & !"".equals(turnoHora.getText().toString())) {
                Log.d("agendate", "c entra setTime A Hora=" + turnoFecha.getText().toString() + " " + turnoHora.getText().toString()) ;
                c.setTime(timeFormat.parse(turnoFecha.getText().toString() + " " + turnoHora.getText().toString()));// all done
                Log.d("agendate", "c entra setTime D Hora=" + c.getTime()) ;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.d("agendate", "c antes de Hora=" + c.getTime()) ;
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public static void setearHora(String hora) {
        turnoHora.setText(hora);

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            Log.d("riverosa","elegi la hora=" + hourOfDay + ":" + minute);
            setearHora((hourOfDay<10?"0":"") + hourOfDay + ":" + (minute<10?"0":"") + minute); ;
        }
    }
}