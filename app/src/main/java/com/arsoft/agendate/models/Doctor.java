package com.arsoft.agendate.models;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties

/**
 * Created by Usuario on 01/07/2017.
 */

public class Doctor {

    public String nombre;
    public String descripcion;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();


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
        result.put("starCount", starCount);
        result.put("stars", stars);


        return result;
    }
    // [END post_to_map]
}
