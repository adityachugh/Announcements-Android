package io.mindbend.android.announcements;

import java.io.Serializable;

/**
 * Created by Akshay Pall on 02/08/2015.
 */
public class Comment implements Serializable {
    private String mUserId;
    private User mUser;
    private String mText;
    private String mTimeSince;
    //TODO: setup passing in image for the comment

    public Comment(String userID, User user, String text, String timeSince){
        mUserId = userID;
        mUser = user;
        mText = text;
        mTimeSince = timeSince;
    }

    //To fetch user from comment
    public User getUser(){
        return mUser;
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
