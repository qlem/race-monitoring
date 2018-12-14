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

    /**
     * This constructor is used for retrieve the values that originally wrote into the Parcel.
     * @param in the Parcel
     */
    private Speed(Parcel in) {
        speed = in.readFloat();
    }

    /**
     * Interface that must be implemented and provided as a public CREATOR field that generates
     * instances of the Parcelable class from a Parcel.
     */
    public static final Creator<Speed> CREATOR = new Creator<Speed>() {

        /**
         * Create a new instance of the Parcelable class, instantiating it from the given Parcel
         * whose data had previously been written by Parcelable.writeToParcel().
         * @param in the Parcel to read the object's data from
         * @return a new instance of the Parcelable class
         */
        @Override
        public Speed createFromParcel(Parcel in) {
            return new Speed(in);
        }

        /**
         * Create a new array of the Parcelable class.
         * @param size size of the array
         * @return an array of the Parcelable class, with every entry initialized to null
         */
        @Override
        public Speed[] newArray(int size) {
            return new Speed[size];
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
        dest.writeFloat(speed);
    }
}
