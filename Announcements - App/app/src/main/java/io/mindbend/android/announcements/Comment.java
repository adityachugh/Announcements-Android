package io.mindbend.android.announcements;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

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

    public Comment(String userID, String text, String timeSince){
        mUserId = userID;
        mText = text;
        mTimeSince = timeSince;
    }

    //Constructor for parse object
    public Comment (Context context, ParseObject object){
        mText = object.getString(CommentsDataSource.COMMENT_TEXT);
        Log.wtf("CreateUser as String:", object.getString(CommentsDataSource.COMMENT_USER)); //ERROR: returns null :/ idk why
        mUser = new User(object.getParseUser(CommentsDataSource.COMMENT_USER));
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
