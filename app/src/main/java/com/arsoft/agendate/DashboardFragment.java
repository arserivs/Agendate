package com.arsoft.agendate;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.arsoft.agendate.json.Dashboard;
//import com.arsoft.agendate.json.DashboardProducto;
import com.arsoft.agendate.functions.Funciones;
import com.arsoft.agendate.json.DBApp;
import com.arsoft.agendate.json.UserInfo;
import com.arsoft.agendate.models.Paciente;
import com.arsoft.agendate.models.Turno;
import com.arsoft.agendate.views.AgendaDoctorFragment;
import com.arsoft.agendate.views.DashboardItemBlue;
import com.arsoft.agendate.views.DashboardItemSpline;
import com.arsoft.agendate.views.RegistrarPacienteFragment;
import com.arsoft.agendate.views.StaticHelpers;
import com.google.firebase.database.ChildEventListener;
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


public class DashboardFragment extends Fragment {

    //private Dashboard dashboard;
    //final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    //private DatabaseReference mDatabase ;
    private UserInfo userInfo ;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View returnView = inflater.inflate(R.layout.dashboard, container, false);
        final View progressBar = returnView.findViewById(R.id.dashboard_progressBar);

        final LinearLayout linearLayout = (LinearLayout) returnView.findViewById(R.id.dashboard_linearLayout);
        final Typeface mbBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FSMillbank-Bold.ttf");
        final Typeface mbLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FSMillbank-Light.ttf");

        userInfo =  ((DrawerActivity)getActivity()).getUserInfo() ;
        //mDatabase = ((DrawerActivity)getActivity()).getMDatabase() ;

        LoggedinBaseFragment.setTitle(getActivity(), "Hola " + userInfo.nombre, null);
        try {

            final DashboardItemSpline view = (DashboardItemSpline) inflater.inflate(R.layout.dashboard_item_spline, linearLayout, false);
            final TextView title = (TextView) view.findViewById(R.id.dashboarditemspline_title);
            final TextView subtitle = (TextView) view.findViewById(R.id.dashboarditemspline_subtitle);

            title.setTypeface(mbLight);
            subtitle.setTypeface(mbBold);


            title.setText("Tu próximo paciente es ");
            subtitle.setText("");
            //view.setGraficoValues(producto.graficoValues);

            //Funciones.showDialog(getActivity(), "dashboard telefono " + ((DrawerActivity)getActivity()).getUserInfo().nroTelefono);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //StaticHelpers.showDialog(getActivity(), "view.setOnClickListener");
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    AgendaDoctorFragment fragment = new AgendaDoctorFragment();
                    ft.replace(R.id.loggedinbase_frameLayout, fragment);
                    ft.addToBackStack(null);
                    ft.commit();

                }
            });

            linearLayout.addView(view);



            final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            final Calendar c = Calendar.getInstance();
            final String fecha=dateFormat.format(c.getTime()) ;
            Log.d("agendate", "fecha=" + fecha+"_00:00") ;


            final List<String> p = new ArrayList<>() ;
            p.add("turno") ;
            p.add(userInfo.nroTelefono) ;
            p.add("fecha_hora") ;
            p.add(fecha+"_00:00") ;
            p.add("1") ;

            DBApp.request(4, p, getActivity(), new DBApp.DBAppListener(){
                @Override
                public void respuesta(DataSnapshot datos, String error) {
                    if (error != null) {
                        Funciones.showErrorDialog(getActivity(), error);
                    } else {
                        Turno t = datos.getValue(Turno.class);
                        subtitle.setText(t.nombre + ", el " + t.fecha + " a las " + t.hora);
                        view.setVisibility(View.VISIBLE);
                    }
                }

            });

            /*
            mDatabase.child("turno").child(userInfo.nroTelefono).orderByChild("fecha_hora").startAt(fecha+"_00:00").limitToFirst(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Turno t = dataSnapshot.getValue(Turno.class);
                    subtitle.setText(t.nombre + ", el " + t.fecha + " a las " + t.hora);
                    view.setVisibility(View.VISIBLE);

                    //cargarProximoTurno(dataSnapshot.getValue(Turno.class), subtitle, view);

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
            */


            DashboardItemBlue view2 = (DashboardItemBlue) inflater.inflate(R.layout.dashboard_item_blue, linearLayout, false);
            TextView title2 = (TextView) view2.findViewById(R.id.dashboarditemblue_title);
            TextView subtitle2 = (TextView) view2.findViewById(R.id.dashboarditemblue_subtitle);
            TextView dato2 = (TextView) view2.findViewById(R.id.dashboarditemblue_dato);

            title2.setTypeface(mbLight);
            subtitle2.setTypeface(mbBold);
            dato2.setTypeface(mbLight);

            title2.setText("Item DashboardItemBlue");
            subtitle2.setText("Subtitulo DashboardItemBlue");
            dato2.setText("Dato DashboardItemBlue");

            view2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StaticHelpers.showDialog(getActivity(), "view.setOnClickListener 2");
                }
            });

            linearLayout.addView(view2);




            LinearLayout view3 = (LinearLayout) inflater.inflate(R.layout.dashboard_item_yellow, linearLayout, false);
            TextView credito_title = (TextView) view3.findViewById(R.id.credito_title);
            TextView credito_subtitle = (TextView) view3.findViewById(R.id.credito_subtitle);
            TextView credito_btn_text = (TextView) view3.findViewById(R.id.credito_btn_text);
            credito_btn_text.setTypeface(mbBold);
            credito_subtitle.setTypeface(mbBold);
            credito_title.setTypeface(mbBold);

            credito_btn_text.setText("credito_btn_text");
            credito_subtitle.setText("credito_subtitle");
            credito_title.setText("credito_title");

            view3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StaticHelpers.showDialog(getActivity(), "view.setOnClickListener 3");
                }
            });


            linearLayout.addView(view3);



            LinearLayout view4 = (LinearLayout) inflater.inflate(R.layout.dashboard_item_pink, linearLayout, false);
            TextView pink_title = (TextView) view4.findViewById(R.id.pink_title);
            pink_title.setTypeface(mbBold);
            pink_title.setText("pink_title");

            view4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StaticHelpers.showDialog(getActivity(), "view.setOnClickListener 4");
                }
            });


            linearLayout.addView(view4);





        } catch (Exception ex) {
            ex.printStackTrace();
        }


        progressBar.setVisibility(View.GONE);





        return returnView;

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        // outState.putParcelable("dashboard", dashboard);
        super.onSaveInstanceState(outState);
    }

}
