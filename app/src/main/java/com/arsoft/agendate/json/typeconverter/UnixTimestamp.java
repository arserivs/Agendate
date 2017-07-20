package com.arsoft.agendate.json.typeconverter;

import com.bluelinelabs.logansquare.typeconverters.LongBasedTypeConverter;

import java.util.Date;

/**
 * Created by larcho on 7/25/16.
 */
public class UnixTimestamp extends LongBasedTypeConverter<Date> {
    @Override
    public Date getFromLong(long l) {
        return new Date(l * 1000);
    }

    @Override
    public long convertToLong(Date object) {
        if(object == null)
            return 0;

        return object.getTime();
    }
}
