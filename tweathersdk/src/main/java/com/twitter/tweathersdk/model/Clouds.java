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

public class Clouds implements Parcelable {
    @SerializedName("cloudiness")
    @Expose
    private Integer cloudiness;
    public final static Creator<Clouds> CREATOR = new Creator<Clouds>() {
        @SuppressWarnings({
            "unchecked"
        })
        public Clouds createFromParcel(Parcel in) {
            return new Clouds(in);
        }

        public Clouds[] newArray(int size) {
            return (new Clouds[size]);
        }
    };

    protected Clouds(Parcel in) {
        this.cloudiness = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public Clouds() {
    }

    public Integer getCloudiness() {
        return cloudiness;
    }

    public void setCloudiness(Integer cloudiness) {
        this.cloudiness = cloudiness;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(cloudiness);
    }

    public int describeContents() {
        return  0;
    }
}
