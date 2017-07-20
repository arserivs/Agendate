package com.arsoft.agendate.json;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Base64;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.arsoft.agendate.json.typeconverter.StringToMenuStatus;
import com.arsoft.agendate.views.StaticHelpers;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * Created by larcho on 7/28/16.
 */

@JsonObject
public class User extends JsonBaseItem {
    //public class User extends JsonBaseItem {

    public interface UserEntornoListener {
        void onModificarEntorno(boolean modificar);
    }

    public interface UserMenuStatusListener {
        void onMenuStatuses(Map<Integer, StringToMenuStatus.MenuStatus> statusMap);
    }

    public static final String BROADCAST_USER = "BROADCAST_USER";
    public static final String EXTRA_USER_LOGGEDIN = "EXTRA_USER_LOGGEDIN";
    public static final String EXTRA_USER_NOTIFICATIONS_COUNT = "EXTRA_USER_NOTIFICATIONS_COUNT";
    public static final String GCM_REGISTER_TOKEN_PREF = "GCM_REGISTER_TOKEN_PREF";
    public static final String EXTRA_SESSION_INACTIVE = "EXTRA_SESSION_INACTIVE";

    @JsonField(name = "user_info")
    public UserInfo userInfo;
    @JsonField
    public String hashTouch;



    public static void login(final String documento, final String tipo, final String password, String documento_operador, String td_operador, boolean is_operador, String hashTouch, final Context context, final Location location, final JsonBaseListener listener) {

        HashMap<String, String> post = new HashMap<>();

        if(hashTouch==null) {
            String encryptedPassword = encryptPassword(password, context);

            if (encryptedPassword == null) {
                listener.onJsonBaseRequestFinished(null, "Verifique que esté conetado a internet e intente de nuevo..");
                return;
            }

            post.put("password", encryptedPassword);

        } else {
            try {
                post.put("hashTouch", URLEncoder.encode(hashTouch, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        post.putAll(getDeviceInformation(context));
        if(location != null) {
            post.put("latitude", String.valueOf(location.getLatitude()));
            post.put("longitude", String.valueOf(location.getLongitude()));
        }

        if(is_operador){
            post.put("documento", documento_operador);
            post.put("tipo", td_operador);
            post.put("operador_documento", documento);
            post.put("operador_tipo", tipo);
        } else {
            post.put("documento", documento);
            post.put("tipo", tipo);
        }

        final ProgressDialog progress = ProgressDialog.show(context, "Procesando", "Por favor aguarde ...", true, false);

        JsonBaseItem.request("login_mobile", User.class, post, null, new JsonBaseListener() {
            @Override
            public void onJsonBaseRequestFinished(@Nullable JsonBaseItem jsonItem, @Nullable String error) {
                progress.dismiss();
                if (error == null) {
                    Intent intent = new Intent(BROADCAST_USER);
                    intent.putExtra(EXTRA_USER_LOGGEDIN, true);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                listener.onJsonBaseRequestFinished(jsonItem, error);
            }
        });
    }

    public static Map<String, String> getDeviceInformation(final Context context) {
        Map<String, String> returnMap = new HashMap<>();

        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        final String token = pref.getString(GCM_REGISTER_TOKEN_PREF, "");

        returnMap.put("uuid", android_id);
        returnMap.put("imei", token);
        returnMap.put("sistemaOperativo", "Android");
        returnMap.put("versionSistOperativo", android.os.Build.VERSION.RELEASE);
        returnMap.put("modeloTelefono", android.os.Build.MODEL);

        String nroBuildApp = "0";

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int verCode = pInfo.versionCode;
            nroBuildApp = String.valueOf(verCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        returnMap.put("nroBuildApp", nroBuildApp);

        return returnMap;
    }

    public static void downloadPublicKey(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File pemFile = new File(context.getCacheDir(), "public_key.pem");
                try {

                    URL url = new URL(JsonBaseItem.BASE_URL + "login_mobile/public_key/");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream is = connection.getInputStream();
                    FileOutputStream fo = new FileOutputStream(pemFile);

                    byte[] buffer = new byte[1024];
                    int read = is.read(buffer);
                    while (read > 0) {
                        fo.write(buffer, 0, read);
                        read = is.read(buffer);
                    }
                    is.close();
                    fo.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    pemFile.delete();
                }
            }
        }).start();
    }

    public static String encryptPassword(final String password, final Context context) {

        downloadPublicKey(context);
        File pemFile = new File(context.getCacheDir(), "public_key.pem");

        if (!pemFile.exists()) {
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(pemFile);
            DataInputStream dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int) pemFile.length()];
            dis.readFully(keyBytes);
            dis.close();
            fis.close();

            String keyBytesString = new String(keyBytes);
            keyBytesString = keyBytesString.replace("-----BEGIN PUBLIC KEY-----\n", "");
            keyBytesString = keyBytesString.replace("-----END PUBLIC KEY-----", "");

            X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(keyBytesString, Base64.DEFAULT));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            Key publicKey = keyFactory.generatePublic(spec);

            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(password.getBytes());

            return URLEncoder.encode(Base64.encodeToString(encryptedBytes, Base64.DEFAULT).replace("\n", ""), "UTF-8");

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }



    public static void logout(final Context context, final DrawerLayout drawerLayout) {
        JsonBaseItem.request("login_mobile/logout", JsonBaseItem.class, null, null, new JsonBaseListener() {
            @Override
            public void onJsonBaseRequestFinished(@Nullable JsonBaseItem jsonItem, @Nullable String error) {

                Intent intent = new Intent(BROADCAST_USER);
                intent.putExtra(EXTRA_USER_LOGGEDIN, false);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                drawerLayout.closeDrawers();
            }
        });
    }

    public static void logout(final Context context) {
        JsonBaseItem.request("login_mobile/logout", JsonBaseItem.class, null, null, new JsonBaseListener() {
            @Override
            public void onJsonBaseRequestFinished(@Nullable JsonBaseItem jsonItem, @Nullable String error) {

                Intent intent = new Intent(BROADCAST_USER);
                intent.putExtra(EXTRA_USER_LOGGEDIN, false);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            }
        });
    }


    public static void verificarVersion(final Activity activity) {



    }

    public static void getMenuStatus(final Integer[] ids, final UserMenuStatusListener listener) {
        Map<String, String> post = new HashMap<>();
        post.put("ids", TextUtils.join(",",ids));
        String url = "login_mobile/menu_status/android/";

    }

    public static void modificarEntorno(final String documento, final String tipo, Activity activity, final UserEntornoListener listener) {
        Map<String, String> post = new HashMap<>();
        post.put("tipo", tipo);
        post.put("documento", documento);


    }



}
