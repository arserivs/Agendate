package com.arsoft.agendate.json;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by larcho on 7/28/16.
 */


@JsonObject
public class JsonBaseItem implements Parcelable {

    //public static String BASE_URL = "http://192.168.1.116/index.php/";
    // public static final String BASE_URL = "http://10.20.1.31/";
    //public static String BASE_URL = "http://10.118.1.36/";
    //public static String BASE_URL = "http://10.9.100.160/";
     //public static String BASE_URL = "http://10.9.100.164/";
    //public static String BASE_URL = "https://secure.visionbanco.com/app/test/";
    public static String BASE_URL = "https://secure.visionbanco.com/vonline/";
    //public static String BASE_URL = "http://10.9.100.166/";


    public interface JsonBaseListener {
        void onJsonBaseRequestFinished(@Nullable JsonBaseItem jsonItem, @Nullable String error);
    }

    private static String csrf_name = null;
    private static String csrf_hash = null;

    @JsonField
    public String error;
    @JsonField
    public String section_intro;
    @JsonField
    public String section_title;
    @JsonField
    public JsonBaseCSRF csrf;
    @JsonField
    public boolean session_inactive;

    public Thread hilo;

    protected JsonBaseItem(Parcel in) {
        error = in.readString();
        section_intro = in.readString();
        section_title = in.readString();
        csrf = in.readParcelable(JsonBaseCSRF.class.getClassLoader());
    }

    public JsonBaseItem() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(error);
        dest.writeString(section_intro);
        dest.writeString(section_title);
        dest.writeParcelable(csrf, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JsonBaseItem> CREATOR = new Creator<JsonBaseItem>() {
        @Override
        public JsonBaseItem createFromParcel(Parcel in) {
            return new JsonBaseItem(in);
        }

        @Override
        public JsonBaseItem[] newArray(int size) {
            return new JsonBaseItem[size];
        }
    };

    public static void request(@NonNull final String endpoint,
                               @NonNull final Class<? extends JsonBaseItem> jsonClass,
                               @Nullable final Map<String, String> post,
                               @Nullable final Activity activity,
                               @NonNull final JsonBaseListener listener) {

        final Handler handler = new Handler();

        if(activity!=null && !isNetworkAvailable(activity)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onJsonBaseRequestFinished(null, "Visión Móvil no está pudiendo obtener una conexión, compruebe la señal de su teléfono y si puede acceder a internet");
                }
            });
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                String _endpoint = null;

                if (endpoint.startsWith("http:")) _endpoint = endpoint.replace("http", "https");
                else _endpoint = endpoint;
                //_endpoint = endpoint;

                String finalURL = null;
                if (_endpoint.startsWith("http")) {
                    finalURL = _endpoint;
                } else {
                    finalURL = BASE_URL + _endpoint;
                }

                Log.d("@dparra", "finalURL: " + finalURL);

                HttpURLConnection con = null;
                BufferedReader in = null;
                BufferedWriter out = null;

                try {

                    String jsonString = "";

                    URL url = new URL(finalURL);
                    con = (HttpURLConnection) url.openConnection();

                    con.setReadTimeout(60000);
                    con.setConnectTimeout(10000);
                    con.setRequestProperty("Accept", "application/json");


/*
                    CookieHandler cookieManager = CookieHandler.getDefault();
                    Map<String, List<String>> cookies = cookieManager.get(url.toURI(), con.getRequestProperties());
                    for(String key : cookies.keySet()) {
                        for(String cookieValue : cookies.get(key)) {
                            Log.d("larcho", "COOKKIE " + cookieValue);
                        }
                    }
*/
                    if (post != null) {

                        con.setDoOutput(true);
                        con.setRequestMethod("POST");

                        String postString = "";
                        for (String key : post.keySet()) {
                            postString += "&" + key + "=" + post.get(key);
                        }

                        if (csrf_name != null && csrf_hash != null) {
                            postString += "&" + csrf_name + "=" + csrf_hash;
                        }

                        Log.d("fpareden", "enviando (" + finalURL + "): " + postString);

                        out = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(con.getOutputStream()), "UTF-8"));
                        out.write(postString);
                        out.flush();
                        out.close();

                    } else {

                        Log.d("fpareden", "enviando (" + finalURL + ")");

                    }

                    in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String line = in.readLine();
                    while (line != null) {
                        jsonString += line;
                        line = in.readLine();
                    }

                    int maxLogSize = 1000;
                    for (int i = 0; i <= jsonString.length() / maxLogSize; i++) {
                        int start = i * maxLogSize;
                        int end = (i + 1) * maxLogSize;
                        end = end > jsonString.length() ? jsonString.length() : end;
                        Log.d("diegobonnin", "recibido (" + finalURL + "): " + jsonString.substring(start, end));
                    }


                    if (jsonString.trim().equals("<script>document.location.href='http://10.118.1.57/login/index?ref=ef';</script>")
                            || jsonString.trim().equals("<script>document.location.href='http://10.118.1.57/login/index?ref=notID';</script>")
                            || jsonString.trim().indexOf("Sesion inactiva") > 0) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //listener.onJsonBaseRequestFinished(null, "SESSION_OVER");
                                if (activity != null) {
                                    Toast.makeText(activity, "Sesion inválida", Toast.LENGTH_LONG).show();
                                    activity.finish();
                                }
                            }
                        });

                    } else {

                        final JsonBaseItem jsonItem = LoganSquare.parse(jsonString, jsonClass);


                        if (jsonItem != null) {

                            if (activity != null && jsonItem.session_inactive) {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //listener.onJsonBaseRequestFinished(null, "SESSION_OVER");
                                        if (activity != null) {
                                            Toast.makeText(activity, "Sesion inválida", Toast.LENGTH_LONG).show();
                                            activity.finish();
                                        }
                                    }
                                });

                            }

                            if (jsonItem.csrf != null) {
                                csrf_name = jsonItem.csrf.name;
                                csrf_hash = jsonItem.csrf.hash;
                            }


                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (jsonItem != null && jsonItem.error != null && jsonItem.error.length() > 0) {
                                        listener.onJsonBaseRequestFinished(null, jsonItem.error);
                                    } else {
                                        listener.onJsonBaseRequestFinished(jsonItem, null);
                                    }
                                }
                            });

                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onJsonBaseRequestFinished(null, "No se recuperaron datos");
                                }
                            });
                        }
                    }

                } catch (final IOException ioe) {

                    ioe.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onJsonBaseRequestFinished(null, "Hubo un error de conexión: " + ioe.getMessage());
                        }
                    });

                } catch (final Exception e) {

                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onJsonBaseRequestFinished(null, "Hubo un error no catalogado: " + e.getMessage());
                        }
                    });

                } finally {
                    if (out != null) try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (in != null) try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (con != null) con.disconnect();
                }

            }
        }).start();

    }

    public static String unescapeJavaString(String st) {

        StringBuilder sb = new StringBuilder(st.length());

        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);
                // Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                && st.charAt(i + 1) <= '7') {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    // Hex Unicode: u????
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(i + 2) + st.charAt(i + 3)
                                        + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public String llamarAServicioHttp(Map<String, String> post, String endpoint) {

        String _endpoint = null;

        if (endpoint.startsWith("http:"))
            _endpoint = endpoint.replace("http", "https");
        else _endpoint = endpoint;

        Log.d("diegobonnin", "URL Completa: " + _endpoint.startsWith("http"));

        String finalURL = null;
        if (_endpoint.startsWith("http")) {
            finalURL = _endpoint;
        } else {
            finalURL = BASE_URL + _endpoint;
        }

        Log.d("@dparra", "finalURL: " + finalURL);

        HttpURLConnection con = null;
        BufferedReader in = null;
        BufferedWriter out = null;

        try {

            String jsonString = "";

            URL url = new URL(finalURL);
            con = (HttpURLConnection) url.openConnection();

            con.setReadTimeout(60000);
            con.setConnectTimeout(10000);
            con.setRequestProperty("Accept", "application/json");

            if (post != null) {

                con.setDoOutput(true);
                con.setRequestMethod("POST");

                String postString = "";
                for (String key : post.keySet()) {
                    postString += "&" + key + "=" + post.get(key);
                }

                if (csrf_name != null && csrf_hash != null) {
                    postString += "&" + csrf_name + "=" + csrf_hash;
                }

                Log.d("fpareden", "enviando (" + finalURL + "): " + postString);

                out = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(con.getOutputStream()), "UTF-8"));
                out.write(postString);
                out.flush();
                out.close();

            } else {
                Log.d("fpareden", "enviando (" + finalURL + ")");
            }

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line = in.readLine();
            while (line != null) {
                jsonString += line;
                line = in.readLine();
            }

            int maxLogSize = 1000;
            for (int i = 0; i <= jsonString.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > jsonString.length() ? jsonString.length() : end;
                Log.d("diegobonnin", "recibido (" + finalURL + "): " + jsonString.substring(start, end));
            }
            return jsonString;

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (in != null) try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (con != null) con.disconnect();
        }
        return null;
    }

    private static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}