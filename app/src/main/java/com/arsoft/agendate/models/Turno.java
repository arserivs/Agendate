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

public class Turno {

    public String confirma ;
    public String paciente ;
    public String fecha ;
    public String hora ;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();


    public Turno() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Turno(String confirma, String paciente, String fecha, String hora) {
        this.confirma = confirma;
        this.paciente = paciente;
        this.fecha = fecha;
        this.hora = hora;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("confirma", confirma);
        result.put("paciente", paciente);
        result.put("fecha", fecha);
        result.put("hora", hora);
        result.put("starCount", starCount);
        result.put("stars", stars);


        return result;
    }
    // [END post_to_map]
}
