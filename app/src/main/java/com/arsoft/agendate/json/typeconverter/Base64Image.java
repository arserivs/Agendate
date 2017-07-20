package com.arsoft.agendate.json.typeconverter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import java.io.ByteArrayOutputStream;

/**
 * Created by larcho on 7/25/16.
 */
public class Base64Image extends StringBasedTypeConverter<Bitmap> {
    @Override
    public Bitmap getFromString(String string) {

        if(string.length() <= 0)
            return null;

        byte[] imageData = Base64.decode(string, Base64.DEFAULT);

        if(imageData.length <= 0)
            return null;

        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }

    @Override
    public String convertToString(Bitmap object) {
        if(object == null)
            return null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        object.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }
}
