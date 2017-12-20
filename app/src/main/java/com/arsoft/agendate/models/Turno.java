package com.arsoft.agendate.models;
import android.os.Parcelable;
import android.os.Parcel;
import android.support.annotation.Nullable;

import com.arsoft.agendate.functions.Funciones;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties

/**
 * Created by Usuario on 01/07/2017.
 */

public class Turno implements Parcelable {

    public String confirma ;
    public String paciente ;
    public String fecha ;
    public String hora ;
    public String nombre ;
    public String fecha_hora ;
    //public String key ;



    public Turno() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Turno(String confirma, String paciente, String fecha, String hora, String nombre) {
        this.confirma = confirma;
        this.paciente = paciente;
        this.fecha = fecha;
        this.hora = hora;
        this.nombre = nombre;

        this.fecha_hora = Funciones.cambiarFormatoFecha(fecha + " " + hora, "dd/MM/yyyy HH:mm", "yyyyMMddHHmm") ;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("confirma", confirma);
        result.put("paciente", paciente);
        result.put("fecha", fecha);
        result.put("hora", hora);
        result.put("nombre", nombre);
        //result.put("key", key);
        result.put("fecha_hora", fecha_hora);



        return result;
    }
    // [END post_to_map]


    protected Turno(Parcel in) {
        confirma = in.readString();
        paciente = in.readString();
        fecha = in.readString();
        hora = in.readString();
        nombre = in.readString();
        //key = in.readString();
        fecha_hora = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(confirma);
        dest.writeString(paciente);
        dest.writeString(fecha);
        dest.writeString(hora);
        dest.writeString(nombre);
        //dest.writeString(key);
        dest.writeString(fecha_hora);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Turno> CREATOR = new Creator<Turno>() {

        @Override
        public Turno createFromParcel(Parcel in) {
            return new Turno(in);
        }

        @Override
        public Turno[] newArray(int size) {
            return new Turno[size];
        }

    };
}
