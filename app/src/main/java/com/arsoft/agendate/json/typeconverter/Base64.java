package com.arsoft.agendate.json.typeconverter;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

/**
 * Created by larcho on 12/28/16.
 */

public class Base64 extends StringBasedTypeConverter<byte[]> {
    @Override
    public byte[] getFromString(String string) {
        if(string.length() <= 0)
            return new byte[0];

        return android.util.Base64.decode(string, android.util.Base64.DEFAULT);
    }

    @Override
    public String convertToString(byte[] object) {
        if(object == null)
            return null;

        return android.util.Base64.encodeToString(object, android.util.Base64.DEFAULT);
    }
}
