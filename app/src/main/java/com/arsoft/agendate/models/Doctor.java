package com.arsoft.agendate.models;
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

public class Doctor implements Parcelable{

    public String nombre;
    public String descripcion;


    public Doctor() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Doctor(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;

    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nombre", nombre);
        result.put("descripcion", descripcion);


        return result;
    }
    // [END post_to_map]


    protected Doctor(Parcel in) {
        nombre = in.readString();
        descripcion = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(descripcion);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Doctor> CREATOR = new Parcelable.Creator<Doctor>() {

        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }

    };
}
