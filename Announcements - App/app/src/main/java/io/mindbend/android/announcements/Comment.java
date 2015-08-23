package io.mindbend.android.announcements;

import com.parse.ParseObject;

import java.io.Serializable;
import java.util.Date;

import io.mindbend.android.announcements.cloudCode.CommentsDataSource;

/**
 * Created by Akshay Pall on 02/08/2015.
 */
public class Comment implements Serializable {
    private String mUserId;
    private String mText;
    private String mTimeSince;

    private User mUser;
    private Date mDate;
    //TODO: setup passing in image for the comment

    public Comment(String userID, String text, String timeSince){
        mUserId = userID;
        mText = text;
        mTimeSince = timeSince;
    }

    //Constructor for parse object
    public Comment (ParseObject object){
        mText = object.getString(CommentsDataSource.COMMENT_TEXT);
        mUser = new User(object.getParseObject(CommentsDataSource.COMMENT_USER));
        mTimeSince = object.getCreatedAt().toString();
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
