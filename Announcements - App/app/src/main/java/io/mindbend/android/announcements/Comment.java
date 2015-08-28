package io.mindbend.android.announcements;

import android.content.Context;
import android.text.format.DateUtils;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;

import io.mindbend.android.announcements.cloudCode.CommentsDataSource;

/**
 * Created by Akshay Pall on 02/08/2015.
 */
public class Comment implements Serializable {
    private String mUserId;
    private String mText;
    private String mTimeSince;

    private User mUser;
    //TODO: setup passing in image for the comment

    public Comment(String userID, String text, String timeSince){
        mUserId = userID;
        mText = text;
        mTimeSince = timeSince;
    }

    public Comment (Context context, ParseObject object){
        mText = object.getString(CommentsDataSource.COMMENT_TEXT);
        ParseUser user = (ParseUser)object.get(CommentsDataSource.COMMENT_USER); //ERROR: returns null :/ idk why
        mUser = new User(user);
        mTimeSince = DateUtils.getRelativeDateTimeString(context, object.getCreatedAt().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0).toString();
    }

    public User getmUser() {
        return mUser;
    }

    public String getmText() {
        return mText;
    }

    public String getmTimeSince() {
        return mTimeSince;
    }
}
