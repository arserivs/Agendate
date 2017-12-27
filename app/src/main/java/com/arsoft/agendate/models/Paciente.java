package com.arsoft.agendate.models ;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties

/**
 * Created by Usuario on 01/07/2017.
 */

public class Paciente implements Parcelable {

    public String nombre;
    public String fechanacimiento;
    public String fechaingreso;
    public String direccion;
    public String nick;
    public String documento;


    public Paciente() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Paciente(String documento, String nombre, String fechanacimiento, String fechaingreso, String direccion, String nick) {
        this.documento = documento;
        this.nombre = nombre;
        this.fechanacimiento = fechanacimiento;
        this.fechaingreso = fechaingreso;
        this.direccion = direccion;
        this.nick = nick;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("documento", documento);
        result.put("nombre", nombre);
        result.put("fechanacimiento", fechanacimiento);
        result.put("fechaingreso", fechaingreso);
        result.put("direccion", direccion);
        result.put("nick", nick);


        return result;
    }
    // [END post_to_map]



    protected Paciente(Parcel in) {
        nombre = in.readString();
        documento = in.readString();
        fechanacimiento = in.readString();
        fechaingreso = in.readString();
        direccion = in.readString();
        nick = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(fechanacimiento);
        dest.writeString(fechaingreso);
        dest.writeString(direccion);
        dest.writeString(nick);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Paciente> CREATOR = new Creator<Paciente>() {

        @Override
        public Paciente createFromParcel(Parcel in) {
            return new Paciente(in);
        }

        @Override
        public Paciente[] newArray(int size) {
            return new Paciente[size];
        }

    };
}
