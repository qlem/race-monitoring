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

    /**
     * This constructor is used for retrieve the values that originally wrote into the Parcel.
     * @param in the Parcel
     */
    private Altitude(Parcel in) {
        altitude = in.readFloat();
    }

    /**
     * Interface that must be implemented and provided as a public CREATOR field that generates
     * instances of the Parcelable class from a Parcel.
     */
    public static final Creator<Altitude> CREATOR = new Creator<Altitude>() {

        /**
         * Create a new instance of the Parcelable class, instantiating it from the given Parcel
         * whose data had previously been written by Parcelable.writeToParcel().
         * @param in the Parcel to read the object's data from
         * @return a new instance of the Parcelable class
         */
        @Override
        public Altitude createFromParcel(Parcel in) {
            return new Altitude(in);
        }

        /**
         * Create a new array of the Parcelable class.
         * @param size size of the array
         * @return an array of the Parcelable class, with every entry initialized to null
         */
        @Override
        public Altitude[] newArray(int size) {
            return new Altitude[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable instance's marshaled
     * representation.
     * @return a bitmask indicating the set of special object types marshaled by this
     * Parcelable object instance
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     * @param dest the Parcel in which the object should be written
     * @param flags additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(altitude);
    }
}
