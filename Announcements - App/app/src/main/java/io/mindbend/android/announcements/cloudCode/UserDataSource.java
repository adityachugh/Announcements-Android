package io.mindbend.android.announcements.cloudCode;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

import io.mindbend.android.announcements.User;

/**
 * Created by Avik Hasija on 8/23/2015.
 */
public class UserDataSource {

    public final static String USER_NAME = VerificationDataSource.USER_USERNAME;
    public final static String FIRST_NAME = VerificationDataSource.USER_FIRST_NAME;
    public final static String LAST_NAME = VerificationDataSource.USER_LAST_NAME;
    public final static String DESCRIPTION = "description";
    public final static String ORG_FOLLOWED_COUNT = "organizationsFollowedCount";
    public final static String PHOTO = "profilePhoto";

    //TODO: profile photo, cover photo

    public static User getCurrentUserWithInfo(ProgressBar loading) {
        loading.setVisibility(View.VISIBLE);
        try {
            ParseUser.getCurrentUser().fetch();
            loading.setVisibility(View.GONE);
            return new User(ParseUser.getCurrentUser());
        } catch (ParseException e) {
            e.printStackTrace();
            loading.setVisibility(View.GONE);
            return null;
        }
    }

}