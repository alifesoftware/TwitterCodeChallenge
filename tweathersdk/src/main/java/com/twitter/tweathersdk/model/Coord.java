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

public class Coord implements Parcelable {
    @SerializedName("lon")
    @Expose
    private Float lon;
    @SerializedName("lat")
    @Expose
    private Float lat;
    public final static Creator<Coord> CREATOR = new Creator<Coord>() {
        @SuppressWarnings({
            "unchecked"
        })
        public Coord createFromParcel(Parcel in) {
            return new Coord(in);
        }

        public Coord[] newArray(int size) {
            return (new Coord[size]);
        }
    };

    protected Coord(Parcel in) {
        this.lon = ((Float) in.readValue((Float.class.getClassLoader())));
        this.lat = ((Float) in.readValue((Float.class.getClassLoader())));
    }

    public Coord() {
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(lon);
        dest.writeValue(lat);
    }

    public int describeContents() {
        return  0;
    }
}
