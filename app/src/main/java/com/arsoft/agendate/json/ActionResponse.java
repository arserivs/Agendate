package com.arsoft.agendate.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by ivankoop on 11/30/16.
 */

@JsonObject
public class ActionResponse extends JsonBaseItem implements Parcelable {

    @JsonField
    public String codRes;

    @JsonField
    public String menRes;

    public ActionResponse(){}

    protected ActionResponse(Parcel in){
        codRes = in.readString();
        menRes = in.readString();
    }

    public static final Creator<ActionResponse> CREATOR = new Creator<ActionResponse>() {
        @Override
        public ActionResponse createFromParcel(Parcel in) {
            return new ActionResponse(in);
        }

        @Override
        public ActionResponse[] newArray(int size) {
            return new ActionResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(codRes);
        dest.writeString(menRes);
    }
}
