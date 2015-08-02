package io.mindbend.android.announcements;

/**
 * Created by Akshay Pall on 02/08/2015.
 */
public class Comment {
    private String mUserId;
    private String mText;
    private String mTimeSince;
    //TODO: setup passing in image for the comment

    public Comment(String userID, String text, String timeSince){
        mUserId = userID;
        mText = text;
        mTimeSince = timeSince;
    }

    public String getmUserId() {
        return mUserId;
    }

    public String getmText() {
        return mText;
    }

    public String getmTimeSince() {
        return mTimeSince;
    }
}
