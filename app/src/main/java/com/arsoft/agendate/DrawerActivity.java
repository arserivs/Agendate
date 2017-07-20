package com.arsoft.agendate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

//import com.bluelinelabs.logansquare.LoganSquare;
import com.arsoft.agendate.functions.Funciones;
import com.arsoft.agendate.json.User;
import com.arsoft.agendate.json.UserInfo;
import com.arsoft.agendate.json.typeconverter.StringToMenuStatus;
import com.arsoft.agendate.views.StaticHelpers;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by larcho on 8/1/16.
 */
public class DrawerActivity extends AppCompatActivity {



    private class DrawerMenuItem {
        private int imageID;
        private String label;
        private Class<? extends Fragment> fragmentClass;
        private boolean requiresLogin;
        private int showsLevel = 0;
        private Integer servicio;
        private StringToMenuStatus.MenuStatus menuStatus;

        public DrawerMenuItem(int imageID, String label, Class<? extends Fragment> fragmentClass, boolean requiresLogin, Integer servicio) {
            this.imageID = imageID;
            this.label = label;
            this.fragmentClass = fragmentClass;
            this.requiresLogin = requiresLogin;
            this.servicio = servicio;
        }

        public DrawerMenuItem(int imageID, String label, int showsLevel) {
            this.imageID = imageID;
            this.label = label;
            this.showsLevel = showsLevel;
        }

        public int getImageID() {
            return imageID;
        }

        public String getLabel() {
            return label;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return fragmentClass;
        }

        public boolean isRequiresLogin() {
            return requiresLogin;
        }

        public int getShowsLevel() {
            return showsLevel;
        }

        public Integer getServicio() {
            return servicio;
        }

        public StringToMenuStatus.MenuStatus getMenuStatus() {
            return menuStatus;
        }

        public void setMenuStatus(StringToMenuStatus.MenuStatus menuStatus) {
            this.menuStatus = menuStatus;
        }
    }

    private DrawerMenuItem[] menuLevel1 = {
            new DrawerMenuItem(R.drawable.menu_icon_logout, "Visión Online", null, false, null),
            new DrawerMenuItem(R.drawable.menu_icon_logout, "Encontrar locales", null, false, null),
            // new DrawerMenuItem(R.drawable.menu_icon_promociones, "Promociones y beneficios", null, false),
    };

    private DrawerMenuItem[] menuLevel2 = {
            new DrawerMenuItem(R.drawable.menu_icon_logout, "Inicio", DashboardFragment.class, true, null),
            new DrawerMenuItem(R.drawable.menu_icon_logout, "Pantalla 1", PantallaFragment.class, true, null),
            new DrawerMenuItem(R.drawable.menu_icon_logout, "Servicios", 3),
    };






    private DrawerMenuItem[] menuLevel3 = {
            new DrawerMenuItem(R.drawable.icon_arrow_white_back, "Servicios", 2),
            new DrawerMenuItem(R.drawable.menu_icon_logout, "Pantalla 2", PantallaFragment.class, true, null),
    };

    private ActionBarDrawerToggle drawerToggle;
    private BroadcastReceiver userBroadcastReceiver;


    private int notificationsCount = -1;
    private boolean loggedin = false;

    private UserInfo userInfo;
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }



    //Limpia cache por cuestiones de seguridad
    public static boolean deleteDir(File dir) {
        if(dir != null){
            if (dir.isDirectory()) {
                String[] children = dir.list();
                if(children != null){
                    for (int i = 0; i < children.length; i++) {
                        Log.e("DIR",children[i].toString());
                        boolean success = deleteDir(new File(dir, children[i]));
                        if (!success) {
                            return false;
                        }
                    }
                }

            }
            return dir.delete();
        }

        return false;
    }

    public static DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);
        deleteDir(this.getExternalCacheDir());

        drawerLayout = (DrawerLayout) findViewById(R.id.drawermain_drawerLayout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //this.userInfo = (UserInfo)extras.get("userInfo") ;
            this.userInfo = (UserInfo) extras.get("userInfo") ;
        }

        //Funciones.showDialog(this, " drawer telefono" + this.userInfo.nroTelefono);



        /*
        if (savedInstanceState != null) {
            this.userInfo = savedInstanceState.getParcelable("userInfo");
            this.notificationsCount = savedInstanceState.getInt("notificationsCount");
            this.loggedin = savedInstanceState.getBoolean("loggedin");

        } else {
            Funciones.showDialog(this, "no entra a userInfo");
        }
        */


        final ListView menuList = (ListView) findViewById(R.id.drawermain_listView);
        setDrawerMenu(menuList);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DrawerMenuItem item = (DrawerMenuItem) adapterView.getItemAtPosition(i);

                if(item.getShowsLevel() > 0) {
                    DrawerMenuItem[] menuItems;

                    switch (item.getShowsLevel()) {
                        case 1:
                            menuItems = menuLevel1;
                            break;
                        case 2:
                            menuItems = menuLevel2;
                            break;
                        case 3:
                            menuItems = menuLevel3;
                            break;
                        default:
                            menuItems = menuLevel1;
                            break;
                    }

                    List<DrawerMenuItem> menuItemList = new ArrayList<>();
                    for(DrawerMenuItem menuItem : menuItems) {
                        if(menuItem != null){
                            if(menuItem.getMenuStatus() != StringToMenuStatus.MenuStatus.HIDDEN) {
                                menuItemList.add(menuItem);
                            }
                        }
                    }

                    menuList.setAdapter(new DrawerMenuAdapter(DrawerActivity.this, menuItemList));

                    return;
                }

                if (item.getFragmentClass() != null) {

                    drawerLayout.closeDrawers();

                    Fragment fragment = null;
                    String tag = null;
                    boolean addToBackStack = false;

                    if(item.getFragmentClass() == DashboardFragment.class) {
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return;
                    }else if (item.requiresLogin) {
                        tag = "loggedInBase";
                        fragment = LoggedinBaseFragment.getFragment(item.getFragmentClass(), null);
                    } else {
                        try {
                            fragment = item.getFragmentClass().newInstance();
                            addToBackStack = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (fragment != null) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.drawermain_mainContent, fragment, tag);
                        if(addToBackStack)
                            ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            }
        });


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setCustomView(R.layout.actionbar_notification);

        getSupportActionBar().getCustomView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(DrawerActivity.this, NotificationsActivity.class);
                //startActivity(intent);
                StaticHelpers.showDialog(getApplicationContext(), "notificaciones");
            }
        });

        final ImageButton buttonLogout = (ImageButton) findViewById(R.id.drawermain_buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.logout(DrawerActivity.this, drawerLayout);
                finish();
            }
        });

        final TextView salir = (TextView) findViewById(R.id.drawermenuitem_textLabel);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawermain_drawerLayout);
                drawerLayout.setScrimColor(Color.TRANSPARENT);
                //User.logout(DrawerActivity.this, drawerLayout);
                finish();
            }
        });



            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.drawermain_mainContent, LoggedinBaseFragment.getFragment(DashboardFragment.class, null), "loggedInBase");
            ft.commit();


        userBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DrawerActivity.this.loggedin = intent.getBooleanExtra(User.EXTRA_USER_LOGGEDIN, false);
                DrawerActivity.this.notificationsCount = intent.getIntExtra(User.EXTRA_USER_NOTIFICATIONS_COUNT, -1);

                handleLoggedInUser();
            }
        };


        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );




    }


    //creado por ARIVEROS
    private Map<String, String> opcionesMenu = new HashMap<>();

    public Map<String, String> getOpcionesMenu() {
        return opcionesMenu;
    }

    public void setOpcionesMenu(Map<String, String> opcionesMenu) {
        this.opcionesMenu = opcionesMenu;
    }

    public String getOpcionMenu(String opcion) {

        return opcionesMenu.get(opcion);
    }
    public void setOpcionMenu(String opcion, String valor) {

        this.opcionesMenu.put(opcion, valor) ;
    }

    private void setDrawerMenu(final ListView menuList) {
        DrawerMenuItem[] items;
        /*
        if(this.userInfo == null) {
            items = menuLevel1;
        } else {
            items = menuLevel2;
        }
        */

        items = menuLevel2;
        menuList.setAdapter(new DrawerMenuAdapter(DrawerActivity.this, items));

        List<Integer> ids = new ArrayList<>();

        for(int i = 1; i <= 3; i++) {
            DrawerMenuItem[] lItems;
            switch (i) {
                case 1:
                    lItems = menuLevel1;
                    break;
                case 2:
                    lItems = menuLevel2;
                    break;
                case 3:
                    lItems = menuLevel3;
                    break;
                default:
                    lItems = menuLevel1;
                    break;
            }

            for(DrawerMenuItem item : lItems) {
                if(item!=null) {
                    if (item.getServicio() != null) {
                        ids.add(item.getServicio());
                    }
                }
            }
        }

        final DrawerMenuItem[] finalItems = items;

        User.getMenuStatus(ids.toArray(new Integer[ids.size()]), new User.UserMenuStatusListener() {
            @Override
            public void onMenuStatuses(Map<Integer, StringToMenuStatus.MenuStatus> statusMap) {

                if(statusMap.size() > 0) {
                    for(int i = 1; i <= 3; i++) {
                        DrawerMenuItem[] lItems;
                        switch (i) {
                            case 1:
                                lItems = menuLevel1;
                                break;
                            case 2:
                                lItems = menuLevel2;
                                break;
                            case 3:
                                lItems = menuLevel3;
                                break;
                            default:
                                lItems = menuLevel1;
                                break;
                        }

                        for(DrawerMenuItem item : lItems) {
                            if (item != null){
                                if (item.getServicio() != null) {
                                    StringToMenuStatus.MenuStatus status = statusMap.get(item.getServicio());
                                    if (status != null) {
                                        item.setMenuStatus(status);
                                    }
                                }
                            }
                        }
                    }
                }


                List<DrawerMenuItem> menuItemList = new ArrayList<>();
                for(DrawerMenuItem item : finalItems) {
                    if(item!=null) {
                        StringToMenuStatus.MenuStatus status = statusMap.get(item.getServicio());
                        if (status == null || status != StringToMenuStatus.MenuStatus.HIDDEN) {
                            item.setMenuStatus(status);
                            menuItemList.add(item);
                        }
                    }
                }
                menuList.setAdapter(new DrawerMenuAdapter(DrawerActivity.this, menuItemList));
            }
        });
    }

    public void handleLoggedInUser()
    {
        getSupportActionBar().setCustomView(R.layout.actionbar_notification);
        final ListView menuList = (ListView) findViewById(R.id.drawermain_listView);
        final ImageButton buttonLogout = (ImageButton) findViewById(R.id.drawermain_buttonLogout);
        final View ab_notification = getSupportActionBar().getCustomView();
        final TextView actionbar_search = (TextView) ab_notification.findViewById(R.id.actionbar_search);

        if (notificationsCount > -1) {

            TextView tvNotifications = (TextView) ab_notification.findViewById(R.id.actionbarnotification_textView);
            if (notificationsCount > 99) {
                tvNotifications.setText("99+");
            } else if (notificationsCount > 0) {
                tvNotifications.setText(String.valueOf(notificationsCount));
            } else {
                tvNotifications.setText("0");
            }
            getSupportActionBar().setDisplayShowCustomEnabled(true);

            tvNotifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = new Intent(DrawerActivity.this, NotificationsActivity.class);
                    //startActivityForResult(intent, 0);
                    StaticHelpers.showDialog(getApplicationContext(), "tvNotificaciones");
                }
            });
        }

        actionbar_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticHelpers.showDialog(getApplicationContext(), "actionbar_search");
                PantallaFragment fragment = new PantallaFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.drawermain_mainContent, fragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                ft.commit();
            }
        });

        setDrawerMenu(menuList);

        if (loggedin) {
            buttonLogout.setVisibility(View.VISIBLE);

        } else {
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            buttonLogout.setVisibility(View.INVISIBLE);

            DrawerActivity.this.userInfo = null;
            StaticHelpers.showDialog(getApplicationContext(), "LoginFragment");

            //LoginFragment loginFragment = new LoginFragment();
            //getFragmentManager().beginTransaction().replace(R.id.drawermain_mainContent, loginFragment).commit();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        StaticHelpers.ocultarTeclado(this);
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(userBroadcastReceiver, new IntentFilter(User.BROADCAST_USER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userBroadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("userInfo", this.userInfo);
        outState.putInt("notificationsCount", this.notificationsCount);
        outState.putBoolean("loggedin", this.loggedin);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.userInfo != null) {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Cerrar sesión")
                        .setMessage("Desea cerrar su sesión?")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                User.logout(DrawerActivity.this);
                            }
                        })
                        .show();


                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    private class DrawerMenuAdapter extends ArrayAdapter<DrawerMenuItem> {

        public DrawerMenuAdapter(Context context, DrawerMenuItem[] items) {
            super(context, 0, items);
        }

        public DrawerMenuAdapter(Context context, List<DrawerMenuItem> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View returnView = null;
            Typeface mbBold = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                returnView = inflater.inflate(R.layout.drawer_menu_item, parent, false);
                mbBold = Typeface.createFromAsset(getAssets(), "fonts/FSMillbank-Bold.ttf");
            } else {
                returnView = convertView;
            }

            final ImageView imageView = (ImageView) returnView.findViewById(R.id.drawermenuitem_imageView);
            final TextView label = (TextView) returnView.findViewById(R.id.drawermenuitem_textLabel);

            if (mbBold != null) {
                label.setTypeface(mbBold);
            }

            final DrawerMenuItem item = getItem(position);
            if (item != null) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), item.getImageID()));
                label.setText(item.getLabel());
                if(item.getMenuStatus() == StringToMenuStatus.MenuStatus.DISABLED) {
                    imageView.setAlpha(0.3f);
                    label.setAlpha(0.3f);
                } else {
                    imageView.setAlpha(1.0f);
                    label.setAlpha(1.0f);
                }
            }

            return returnView;
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data){
        int DASHBOARD_REFRESH = 99;
        if(requestCode == 0) {
            if (data != null) {
                Bundle bundle = data.getExtras();

                if (resultCode == Activity.RESULT_CANCELED) {
                    Intent intent = null;
                    switch (bundle.getString("PantallaSolicitada").toString()) {
                        case "VerCentroAprobaciones":
                            //intent = new Intent(DrawerActivity.this, CentroAprobacionesActivity.class);
                            //intent.putExtra("cantidadAprobaciones", bundle.get("cantidadAprobaciones").toString());

                            Bundle bundleFinal = new Bundle();
                            bundleFinal.putString("cantidadAprobaciones", bundle.get("cantidadAprobaciones").toString());
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            PantallaFragment fragment = new PantallaFragment();
                            fragment.setArguments(bundleFinal);
                            ft.replace(R.id.loggedinbase_frameLayout, fragment);
                            ft.addToBackStack(null);
                            ft.commit();


                            break;
                        default:
                    }


                    if(intent != null) {
                        startActivityForResult(intent, 0);
                    }

                } else {
                    if (resultCode == Activity.RESULT_OK) {
                        StaticHelpers.showDialog(getApplicationContext(), "resultCode==OK");
                    }else if(resultCode == DASHBOARD_REFRESH){
                        DashboardFragment fragment = new DashboardFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.loggedinbase_frameLayout, fragment);
                        ft.commit();
                    }
                }
            }
        }
    }



}
