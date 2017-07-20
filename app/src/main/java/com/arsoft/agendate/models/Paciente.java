package com.arsoft.agendate.models ;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties

/**
 * Created by Usuario on 01/07/2017.
 */

public class Paciente {

    public String nombre;
    public String fechanacimiento;
    public String fechaingreso;
    public String direccion;
    public String nick;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();


    public Paciente() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Paciente(String nombre, String fechanacimiento, String fechaingreso, String direccion, String nick) {
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
        result.put("nombre", nombre);
        result.put("fechanacimiento", fechanacimiento);
        result.put("fechaingreso", fechaingreso);
        result.put("direccion", direccion);
        result.put("nick", nick);
        result.put("starCount", starCount);
        result.put("stars", stars);


        return result;
    }
    // [END post_to_map]
}
