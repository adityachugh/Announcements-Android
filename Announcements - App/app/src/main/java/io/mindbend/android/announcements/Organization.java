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
    private String mBannerDetail;
    private String mDescription;
    private int mFollowers;
    private String mTag;
    private boolean mPrivateOrg;

    public Organization(String objectId, String title, String bannerDetail, String description, int followers, String tag, boolean privateOrg){
        mObjectId = objectId;
        mTitle = title;
        mBannerDetail = bannerDetail;
        description = mDescription;
        followers = mFollowers;
        tag = mTag;
        privateOrg = mPrivateOrg;
    }

    public String getmObjectId() {
        return mObjectId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmBannerDetail() {
        return mBannerDetail;
    }
}
