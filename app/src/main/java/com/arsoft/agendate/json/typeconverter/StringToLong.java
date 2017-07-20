package com.arsoft.agendate.json.typeconverter;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

/**
 * Created by larcho on 8/8/16.
 */
public class StringToLong extends StringBasedTypeConverter<Long> {

    @Override
    public Long getFromString(String string) {
        return Long.parseLong(string);
    }

    @Override
    public String convertToString(Long object) {
        return object.toString();
    }
}
