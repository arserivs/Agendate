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

public class Usuario {

    public String email;
    public String telefono;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();


    public Usuario() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Usuario(String email, String telefono) {
        this.email = email;
        this.telefono = telefono;

    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("telefono", telefono);
        result.put("starCount", starCount);
        result.put("stars", stars);


        return result;
    }
    // [END post_to_map]
}
