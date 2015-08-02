package io.mindbend.android.announcements;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class Post {
    private String mObjectId;
    private String mPostTitle;
    private String mPostTimeSince;
    private String mPostDetail;
    private String mPostClubUsername;
    //TODO: setup passing in club image for the post
    //private String mUrlToPicture;

    public Post(String objectId, String title, String timeSinceString, String details, String clubUsername){
        mPostTitle = title;
        mPostTimeSince = timeSinceString;
        mPostDetail = details;
        mPostClubUsername = clubUsername;
    }

    public String getmObjectId() {
        return mObjectId;
    }

    public String getmPostTitle() {
        return mPostTitle;
    }

    public String getmPostTimeSince() {
        return mPostTimeSince;
    }

    public String getmPostDetail() {
        return mPostDetail;
    }

    public String getmPostClubUsername() {
        return mPostClubUsername;
    }
}
