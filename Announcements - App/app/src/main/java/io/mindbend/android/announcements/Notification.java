package io.mindbend.android.announcements;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akshay Pall on 06/08/2015.
 */
public class Notification implements Parcelable {


    Notification(){
        //TODO: make notif
    }

    public Notification (Parcel in){
        //TODO: make notif from parcel
    }

    public static final Parcelable.Creator<Notification> CREATOR
            = new Parcelable.Creator<Notification>() {
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
