package io.mindbend.android.announcements;

import java.io.Serializable;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class Organization implements Serializable{
    //This class is used in order to populate a gridview of organizations/clubs.
    //this is used in the discover tab and on people's profiles.
    private String mObjectId;
    private String mTitle;
    private String mDescription;
    private int mFollowers;
    private String mTag;
    private boolean mPrivateOrg;
    private boolean mNewOrg;

    public Organization(String objectId, String title, String description, int followers, String tag, boolean privateOrg, boolean newOrg){
        mObjectId = objectId;
        mTitle = title;
        mDescription = description;
        mFollowers = followers;
        mTag = tag;
        mPrivateOrg = privateOrg;
        mNewOrg = newOrg;
    }

    public String getmObjectId() {
        return mObjectId;
    }

    public String getmTitle() {
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
}
