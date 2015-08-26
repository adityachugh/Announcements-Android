package io.mindbend.android.announcements.cloudCode;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.zip.Deflater;

import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;
import io.mindbend.android.announcements.User;

/**
 * Created by Avik Hasija on 8/23/2015.
 */
public class UserDataSource {

    public final static String USER_NAME = VerificationDataSource.USER_USERNAME;
    public final static String FIRST_NAME = VerificationDataSource.USER_FIRST_NAME;
    public final static String LAST_NAME = VerificationDataSource.USER_LAST_NAME;
    public final static String DESCRIPTION = "userDescription";
    public final static String ORG_FOLLOWED_COUNT = "organizationsFollowedCount";
    public final static String PROFILE_PHOTO = "profilePhoto";
    public final static String COVER_PHOTO = "coverPhoto";

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

    public static void updateUserProfileImages (final Context context, final ProgressBar loading, final byte[] image, final FunctionCallback<Boolean> callback, final Boolean profilePhoto) {
        loading.setVisibility(View.VISIBLE);

        loginDialog(context, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null) {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("userObjectId", ParseUser.getCurrentUser().getObjectId());
                    params.put("photo", image);
                    loading.setVisibility(View.VISIBLE);

                    String funcName = profilePhoto ? "updateUserProfilePhoto" : "updateUserCoverPhoto";

                    ParseCloud.callFunctionInBackground(funcName, params, new FunctionCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            loading.setVisibility(View.GONE);
                            callback.done(e == null, e);
                        }
                    });
                } else {
                    //did not login
                    Toast.makeText(context, context.getResources().getString(R.string.incorrect_login_credentials), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public static void updateUserDescription (final Context context, final ProgressBar loading, final String description, final FunctionCallback<Boolean> callback) {
        loading.setVisibility(View.VISIBLE);

        loginDialog(context, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null) {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("userObjectId", ParseUser.getCurrentUser().getObjectId());
                    params.put("description", description);
                    loading.setVisibility(View.VISIBLE);
                    ParseCloud.callFunctionInBackground("updateUserDescription", params, new FunctionCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            loading.setVisibility(View.GONE);
                            callback.done(e == null, e);
                        }
                    });
                } else {
                    //did not login
                    Toast.makeText(context, context.getResources().getString(R.string.incorrect_login_credentials), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static void loginDialog(final Context context, final LogInCallback logInCallback) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        View view = ((TabbedActivity)context).getLayoutInflater().inflate(R.layout.login_user_dialog, null);
        builder.setView(view);
        final EditText username = (EditText)view.findViewById(R.id.login_dialog_username);
        final EditText password = (EditText)view.findViewById(R.id.login_dialog_password);
        Button login = (Button)view.findViewById(R.id.login_dialog_button);

        final AlertDialog dialog = builder.create();
        dialog.show();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameT = username.getText().toString();
                String passwordT = password.getText().toString();

                dialog.dismiss();

                if (usernameT.equals("") || passwordT.equals("")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.incorrect_login_credentials))
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    ParseUser.logInInBackground(usernameT, passwordT, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            logInCallback.done(parseUser, e);
                        }
                    });
                }
            }
        });
    }
}