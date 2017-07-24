package com.arsoft.agendate.models;
import android.os.Parcelable;
import android.os.Parcel;

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
    //public int starCount = 0;
    //public Map<String, Boolean> stars = new HashMap<>();


    public Turno() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Turno(String confirma, String paciente, String fecha, String hora, String nombre) {
        this.confirma = confirma;
        this.paciente = paciente;
        this.fecha = fecha;
        this.hora = hora;
        this.nombre = nombre;
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
        //result.put("starCount", starCount);
        //result.put("stars", stars);


        return result;
    }
    // [END post_to_map]


    protected Turno(Parcel in) {
        confirma = in.readString();
        paciente = in.readString();
        fecha = in.readString();
        hora = in.readString();
        nombre = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(confirma);
        dest.writeString(paciente);
        dest.writeString(fecha);
        dest.writeString(hora);
        dest.writeString(nombre);
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
