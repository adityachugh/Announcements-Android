package io.mindbend.android.announcements;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

import java.io.Serializable;

import io.mindbend.android.announcements.cloudCode.OrgsDataSource;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class Organization implements Serializable, Parcelable {
    //This class is used in order to populate a gridview of organizations/clubs.
    //this is used in the discover tab and on people's profiles.
    private String mObjectId;
    private String mTitle;
    private String mDescription;
    private int mFollowers;
    private String mTag;
    private boolean mPrivateOrg;
    private boolean mNewOrg;
    private String mProfileImageURL;

    private int mRequestCode;

    public Organization(String objectId, String title, String description, int followers, String tag, boolean privateOrg, boolean newOrg){
        mObjectId = objectId;
        mTitle = title;
        mDescription = description;
        mFollowers = followers;
        mTag = tag;
        mPrivateOrg = privateOrg;
        mNewOrg = newOrg;
    }

    public Organization (ParseObject object){
        mObjectId = object.getObjectId();
        mTitle = object.getString(OrgsDataSource.ORG_TITLE);
        mDescription = object.getString(OrgsDataSource.ORG_DESCRIPTION);
        mFollowers = object.getInt(OrgsDataSource.ORG_FOLLOWER_COUNT);
        mPrivateOrg = object.getString(OrgsDataSource.ORG_TYPE).equals(OrgsDataSource.ORG_TYPES_PRIVATE);
        if(mPrivateOrg)
            mRequestCode = object.getInt(OrgsDataSource.ORG_REQUEST_CODE);
        mNewOrg = OrgsDataSource.isNew(object);
        if (object.getParseFile(OrgsDataSource.ORG_IMAGE) != null)
            mProfileImageURL = object.getParseFile(OrgsDataSource.ORG_IMAGE).getUrl();
        else
            mProfileImageURL = "";
    }

    public Organization(Parcel in){
        mObjectId = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
        mFollowers = in.readInt();
        mTag = in.readString();

        if (in.readInt() == 0) mPrivateOrg = false;
        else mPrivateOrg = true;

        if (in.readInt() == 0) mNewOrg = false;
        else mNewOrg = true;

        mProfileImageURL = in.readString();
    }

    public String getmObjectId() {
        return mObjectId;
    }

    public String getmProfileImageURL() {
        return mProfileImageURL;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getFollowers() {
        return mFollowers;
    }

    public String getTag() {
        return mTag;
    }

    public boolean isPrivateOrg(){
        return mPrivateOrg;
    }

    public boolean isNewOrg(){
        return mNewOrg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mObjectId);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeInt(mFollowers);
        dest.writeString(mTag);
        //a boolean cannot be written and read by a parcel
        //thus an int has to be used
        //0 = false, 1 = true
        if (mPrivateOrg) dest.writeInt(1);
        else dest.writeInt(0);

        if (mNewOrg) dest.writeInt(1);
        else dest.writeInt(0);

        dest.writeString(mProfileImageURL);
    }

    public static final Parcelable.Creator<Organization> CREATOR
            = new Parcelable.Creator<Organization>() {
        public Organization createFromParcel(Parcel in) {
            return new Organization(in);
        }

        public Organization[] newArray(int size) {
            return new Organization[size];
        }
    };
}
