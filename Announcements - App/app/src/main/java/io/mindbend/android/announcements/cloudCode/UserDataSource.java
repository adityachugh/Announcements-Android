package io.mindbend.android.announcements.cloudCode;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.zip.Deflater;

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

    public static void getCurrentUserWithInfo(final ProgressBar loading, final FunctionCallback<User> callback) {
        loading.setVisibility(View.VISIBLE);

        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                loading.setVisibility(View.GONE);
                User user = new User(ParseUser.getCurrentUser());
                callback.done(user, e);
            }
        });

    }

    public static void updateUserProfilePhoto (final ProgressBar loading, byte[] image, final FunctionCallback<Boolean> callback) {
        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("userObjectId", ParseUser.getCurrentUser().getObjectId());
        params.put("photo", image);
        ParseCloud.callFunctionInBackground("updateUserProfilePhoto", params, new FunctionCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                loading.setVisibility(View.GONE);
                callback.done(e==null ,e);
            }
        });
    }

    public static void updateUserDescription (final ProgressBar loading, String description, final FunctionCallback<Boolean> callback) {
        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("userObjectId", ParseUser.getCurrentUser().getObjectId());
        params.put("description", description);
        ParseCloud.callFunctionInBackground("updateUserDescription", params, new FunctionCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                loading.setVisibility(View.GONE);
                callback.done(e==null, e);
            }
        });
    }
}