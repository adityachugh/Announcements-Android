package io.mindbend.android.announcements;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class Organization {
    //This class is used in order to populate a gridview of organizations/clubs.
    //this is used in the discover tab and on people's profiles.
    private String mObjectId;
    private String mTitle;
    private String mBannerDetail;

    public Organization(String objectId, String title, String bannerDetail){
        mObjectId = objectId;
        mTitle = title;
        mBannerDetail = bannerDetail;
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
