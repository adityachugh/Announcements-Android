package io.mindbend.android.announcements;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;

import io.mindbend.android.announcements.cloudCode.UserDataSource;
import io.mindbend.android.announcements.cloudCode.VerificationDataSource;

/**
 * Created by Avik Hasija on 8/3/2015.
 */
public class User implements Serializable, Parcelable {
    //Class contains details about a user, to be used in various places
    //Current content reflects what is needed for profile page found in "you" tab

    private String mUsername;

    //Will be concatenated upon return
    private String mFirstName;
    private String mLastName;
    private String mInterestOne;
    private String mInterestTwo;

    //Displayed as a hashtag (such as #Grade10, #Teacher, #Administration, etc.)
    private String mUserCategory;

    private int mNumberOfOrganizationsFollowed;

    //TODO: add fields for profile photo, cover photo

    public User(String firstName, String lastName, String interestOne, String interestTwo, String userCategory, int numberOfOrganizationsFollowed) {
        mFirstName = firstName;
        mLastName = lastName;
        mInterestOne = interestOne;
        mInterestTwo = interestTwo;
        mUserCategory = userCategory;

        mNumberOfOrganizationsFollowed = numberOfOrganizationsFollowed;
    }

    public User(ParseUser parseUser) {
        if (parseUser != null) {
            mUsername = parseUser.getUsername();
            mFirstName = parseUser.getString(VerificationDataSource.USER_FIRST_NAME);
            mLastName = parseUser.getString(VerificationDataSource.USER_LAST_NAME);
        } else {
            mUsername = "Error loading Username";
            mFirstName = "";
            mLastName = "";
        }
    }

    public User(Parcel in) {
        mFirstName = in.readString();
        mLastName = in.readString();
        mInterestOne = in.readString();
        mInterestTwo = in.readString();
        mUserCategory = in.readString();
        mNumberOfOrganizationsFollowed = in.readInt();
    }

    public String getName() {
        //Returns full name
        return (mFirstName + " " + mLastName);
    }

    public String getInterestOne() {
        return mInterestOne;
    }

    public void setInterestOne(String mInterestOne) {
        this.mInterestOne = mInterestOne;
    }

    public String getInterestTwo() {
        return mInterestTwo;
    }

    public void setInterestTwo(String mInterestTwo) {
        this.mInterestTwo = mInterestTwo;
    }

    public String getInterests() {
        //Returns both interests
        return ("Interested in " + mInterestOne + " and " + mInterestTwo);
    }

    public void setUserCategory(String mUserCategory) {
        this.mUserCategory = mUserCategory;
    }

    public String getNumberOfOrganizationsFollowed() {
        //Returns String - only used on profile pages
        return (mNumberOfOrganizationsFollowed + " Groups");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeString(mInterestOne);
        dest.writeString(mInterestTwo);
        dest.writeString(mUserCategory);
        dest.writeInt(mNumberOfOrganizationsFollowed);
    }

    public String getUserCategory() {
        return mUserCategory;
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
