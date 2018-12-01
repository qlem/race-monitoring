package com.example.qlem.racemonitoring.graph;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents a parcelable object that contains a speed value.
 */
public class Speed implements Parcelable {

    /**
     * The speed value.
     */
    public float speed;

    /**
     * Default constructor of the class.
     * @param speed the speed value.
     */
    public Speed(float speed) {
        this.speed = speed;
    }

    // TODO add headers

    private Speed(Parcel in) {
        speed = in.readFloat();
    }

    public static final Creator<Speed> CREATOR = new Creator<Speed>() {
        @Override
        public Speed createFromParcel(Parcel in) {
            return new Speed(in);
        }

        @Override
        public Speed[] newArray(int size) {
            return new Speed[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(speed);
    }
}
