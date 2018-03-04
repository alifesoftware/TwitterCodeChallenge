package com.twitter.tweathersdk.model;

/**
 * Auto generated from http://www.jsonschema2pojo.org/
 * to be used by Gson to convert JSON to POJO
 *
 */
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rain implements Parcelable {
    @SerializedName("3h")
    @Expose
    private Integer _3h;
    public final static Creator<Rain> CREATOR = new Creator<Rain>() {
        @SuppressWarnings({
            "unchecked"
        })
        public Rain createFromParcel(Parcel in) {
            return new Rain(in);
        }

        public Rain[] newArray(int size) {
            return (new Rain[size]);
        }
    };

    protected Rain(Parcel in) {
        this._3h = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public Rain() {
    }

    public Integer get3h() {
        return _3h;
    }

    public void set3h(Integer _3h) {
        this._3h = _3h;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(_3h);
    }

    public int describeContents() {
        return  0;
    }
}
