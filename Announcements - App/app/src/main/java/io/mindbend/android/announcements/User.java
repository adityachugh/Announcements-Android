package io.mindbend.android.announcements;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.Serializable;

import io.mindbend.android.announcements.cloudCode.UserDataSource;

/**
 * Created by Avik Hasija on 8/3/2015.
 */
public class User implements Serializable, Parcelable{
    //Class contains details about a user, to be used in various places
    //Current content reflects what is needed for profile page found in "you" tab

    private String mObjectId;

    //Will be concatenated upon return
    private String mFirstName;
    private String mLastName;
    private String mInterestOne;
    private String mInterestTwo;
    private String mDescription;
    //Displayed as a hashtag (such as #Grade10, #Teacher, #Administration, etc.)
    private String mUserCategory;
    private int mNumberOfOrganizationsFollowed;
    private String mProfilePictureURL;
    private String mCoverPictureURL;

    //TODO: add fields for profile photo, cover photo

    public User(String firstName, String lastName, String interestOne, String interestTwo, String userCategory, int numberOfOrganizationsFollowed){
        mFirstName = firstName;
        mLastName = lastName;
        mInterestOne = interestOne;
        mInterestTwo = interestTwo;
        mUserCategory = userCategory;
        mDescription = "Interested in "+interestOne+" and "+interestTwo;
        mProfilePictureURL = "";
        mNumberOfOrganizationsFollowed = numberOfOrganizationsFollowed;
        mCoverPictureURL = "";
    }

    public User (ParseUser user){
        try {
            user.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mObjectId = user.getObjectId();
        mFirstName = user.getString(UserDataSource.FIRST_NAME);
        mLastName = user.getString(UserDataSource.LAST_NAME);
        mDescription = user.getString(UserDataSource.DESCRIPTION);
        mUserCategory = user.getUsername();
        mNumberOfOrganizationsFollowed = user.getInt(UserDataSource.ORG_FOLLOWED_COUNT);
        if (user.getParseFile(UserDataSource.PROFILE_PHOTO) != null)
            mProfilePictureURL = user.getParseFile(UserDataSource.PROFILE_PHOTO).getUrl();
        else
            mProfilePictureURL = "";

        if (user.getParseFile(UserDataSource.COVER_PHOTO) != null)
            mCoverPictureURL = user.getParseFile(UserDataSource.COVER_PHOTO).getUrl();
        else
            mCoverPictureURL = "";
    }

    public  User (Parcel in){
        mObjectId = in.readString();
        mFirstName = in.readString();
        mLastName = in.readString();
        mInterestOne = in.readString();
        mInterestTwo = in.readString();
        mDescription = in.readString();
        mUserCategory = in.readString();
        mNumberOfOrganizationsFollowed = in.readInt();
        mProfilePictureURL = in.readString();
        mCoverPictureURL = in.readString();
    }

    public String getName() {
        //Returns full name
        return (mFirstName + " " + mLastName);
    }

    public String getmObjectId(){
        return mObjectId;
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

    public String getmProfilePictureURL() {
        return mProfilePictureURL;
    }

    public String getmCoverPictureURL() {
        return mCoverPictureURL;
    }

    public String getmDescription() {
        return mDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mObjectId);
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeString(mInterestOne);
        dest.writeString(mInterestTwo);
        dest.writeString(mDescription);
        dest.writeString(mUserCategory);
        dest.writeInt(mNumberOfOrganizationsFollowed);
        dest.writeString(mProfilePictureURL);
        dest.writeString(mCoverPictureURL);
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
