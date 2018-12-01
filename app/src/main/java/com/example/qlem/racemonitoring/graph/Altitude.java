package com.example.qlem.racemonitoring.graph;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents a parcelable object that contains an altitude value.
 */
public class Altitude implements Parcelable {

    /**
     * The altitude value.
     */
    public float altitude;

    /**
     * Default constructor of the class.
     * @param altitude the altitude value.
     */
    public Altitude(float altitude) {
        this.altitude = altitude;
    }

    // TODO add headers

    private Altitude(Parcel in) {
        altitude = in.readFloat();
    }

    public static final Creator<Altitude> CREATOR = new Creator<Altitude>() {
        @Override
        public Altitude createFromParcel(Parcel in) {
            return new Altitude(in);
        }

        @Override
        public Altitude[] newArray(int size) {
            return new Altitude[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(altitude);
    }
}
