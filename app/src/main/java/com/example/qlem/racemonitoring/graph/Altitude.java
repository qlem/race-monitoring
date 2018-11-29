package com.example.qlem.racemonitoring.graph;

import android.os.Parcel;
import android.os.Parcelable;

public class Altitude implements Parcelable {

    public float altitude;

    public Altitude(float altitude) {
        this.altitude = altitude;
    }

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
