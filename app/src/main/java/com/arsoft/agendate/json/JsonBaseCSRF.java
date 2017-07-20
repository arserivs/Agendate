package com.arsoft.agendate.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by larcho on 10/6/16.
 */

@JsonObject
public class JsonBaseCSRF implements Parcelable {

    @JsonField
    public String name;
    @JsonField
    public String hash;

    public JsonBaseCSRF() {

    }

    protected JsonBaseCSRF(Parcel in) {
        name = in.readString();
        hash = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(hash);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JsonBaseCSRF> CREATOR = new Creator<JsonBaseCSRF>() {
        @Override
        public JsonBaseCSRF createFromParcel(Parcel in) {
            return new JsonBaseCSRF(in);
        }

        @Override
        public JsonBaseCSRF[] newArray(int size) {
            return new JsonBaseCSRF[size];
        }
    };
}
