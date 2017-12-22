package com.arsoft.agendate.json;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.arsoft.agendate.json.typeconverter.Base64;


@JsonObject
public class UserInfo implements Parcelable {
    @JsonField
    public String idUsuario;
    @JsonField
    public String nombre;
    @JsonField
    public String apellido;
    @JsonField
    public int duracionmedia;
    @JsonField
    public String mensajeCalif;
    @JsonField(typeConverter = Base64.class)
    public byte[] base64imageFotoPortada;
    @JsonField(typeConverter = Base64.class)
    public byte[] base64imageFotoPerfil;
    @JsonField
    public boolean logueoDuo;
    @JsonField
    public String cambiarPass;
    //@JsonField
    //public String nroTelefono;

    public UserInfo() {

    }

    public Bitmap getFotoPortada() {
        if(base64imageFotoPortada!=null && base64imageFotoPortada.length > 0) {
            return BitmapFactory.decodeByteArray(base64imageFotoPortada, 0, base64imageFotoPortada.length);
        } else {
            return null;
        }
    }

    public Bitmap getFotoPerfil() {
        if(base64imageFotoPerfil!=null && base64imageFotoPerfil.length > 0) {
            return BitmapFactory.decodeByteArray(base64imageFotoPerfil, 0, base64imageFotoPerfil.length);
        } else {
            return null;
        }
    }

    protected UserInfo(Parcel in) {
        idUsuario = in.readString();
        nombre = in.readString();
        apellido = in.readString();
        duracionmedia = in.readInt();
        mensajeCalif = in.readString();
        base64imageFotoPortada = in.createByteArray();
        base64imageFotoPerfil = in.createByteArray();
        logueoDuo = in.readByte() != 0;
        cambiarPass = in.readString();
        //nroTelefono = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idUsuario);
        dest.writeString(nombre);
        dest.writeString(apellido);
        dest.writeInt(duracionmedia);
        dest.writeString(mensajeCalif);
        dest.writeByteArray(base64imageFotoPortada);
        dest.writeByteArray(base64imageFotoPerfil);
        dest.writeByte((byte) (logueoDuo ? 1 : 0));
        dest.writeString(cambiarPass);
        //dest.writeString(nroTelefono);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

}
