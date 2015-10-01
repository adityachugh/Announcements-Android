package io.mindbend.android.announcements.cloudCode;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.zip.Deflater;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.Organization;
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
    public final static String FOLLOWER_USER_FIELD = "user";


    //for the user types of a follower object: admin, follower, and penging
    public final static String FOLLOWER_NORMAL = "FOL";
    public final static String FOLLOWER_ADMIN = "ADM";
    public final static String FOLLOWER_PENDING = "PEN";
    public final static String FOLLOWER_NOT_FOLLOWING = "NFO";
    public final static String FOLLOWER_REJECTED = "REJ";

    public final static String FOLLOWER_USER_TYPE_FIELD = "type";

    public static void getCurrentUserWithInfo(final View view, Context context, final ProgressBar loading, final FunctionCallback<User> callback) {
        loading.setVisibility(View.VISIBLE);

        if (!App.hasNetworkConnection(context)){
            loading.setVisibility(View.GONE);
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    loading.setVisibility(View.GONE);
                    if (e != null) {
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    } else {
                        User user = new User(ParseUser.getCurrentUser());
                        callback.done(user, e);
                    }
                }
            });
        }
    }

    public static void updateUserProfileImages (final View layout, final Context context, final byte[] image, final FunctionCallback<Boolean> callback, final Boolean profilePhoto) {
        final ProgressDialog dialog = new ProgressDialog(context, R.style.DialogTheme);
        dialog.setMessage(context.getString(R.string.updating_profile_dialog_message));

        if (!App.hasNetworkConnection(context)){
            Snackbar.make(layout, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            loginDialog(layout, context, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("userObjectId", ParseUser.getCurrentUser().getObjectId());
                        params.put("photo", image);

                        String funcName = profilePhoto ? "updateUserProfilePhoto" : "updateUserCoverPhoto";
                        dialog.show();
                        ParseCloud.callFunctionInBackground(funcName, params, new FunctionCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                dialog.dismiss();
                                if (e != null){
                                    Snackbar.make(layout, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                                } else {
                                    callback.done(e == null, e);
                                }
                            }
                        });
                    } else {
                        //did not login
                        Snackbar.make(layout, context.getResources().getString(R.string.incorrect_login_credentials), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }



    public static void updateUserDescription (final View layout, final Context context, final String description, final FunctionCallback<Boolean> callback) {
        final ProgressDialog dialog = new ProgressDialog(context, R.style.DialogTheme);
        dialog.setMessage(context.getString(R.string.updating_profile_dialog_message));

        if (!App.hasNetworkConnection(context)){
            Snackbar.make(layout, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            loginDialog(layout, context, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("userObjectId", ParseUser.getCurrentUser().getObjectId());
                        params.put("description", description);
                        dialog.show();
                        ParseCloud.callFunctionInBackground("updateUserDescription", params, new FunctionCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                dialog.dismiss();
                                callback.done(e == null, e);
                            }
                        });
                    } else {
                        //did not login
                        Snackbar.make(layout, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    public static void updateFollowStateForUser (Context context, final boolean isPrivate, final View layout, final Boolean isFollowing, String organizationObjectId, final FunctionCallback<Boolean> callback){
        if (!App.hasNetworkConnection(context)){
            Snackbar.make(layout, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            final ProgressDialog dialog = new ProgressDialog(context, R.style.DialogTheme);
            dialog.setMessage(context.getString(R.string.updating_follow_state_dialog_message));
            HashMap<String, Object> params = new HashMap<>();
            params.put("isFollowing", isFollowing);
            params.put("organizationObjectId", organizationObjectId);
            final String toastText = isFollowing ? "" : "un";

            dialog.show();
            ParseCloud.callFunctionInBackground("updateFollowStateForUser", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean isSuccessful, ParseException e) {
                    String message = "Failure";
                    dialog.dismiss();
                    if (e == null && isSuccessful) {
                        message = (isPrivate && isFollowing) ? "Sent follow request to organization" : "Successfully " + toastText + "followed organization";
                        callback.done(isSuccessful, e);
                    }
                    if (e != null) {
                        e.printStackTrace();
                        message = ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage());
                    }
                    Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
                }
            });
        }


    }

    public static void followOrganizations (final View v, final Context context, ArrayList<String> orgs){
        final ProgressDialog dialog = new ProgressDialog(context, R.style.DialogTheme);
        dialog.setMessage(context.getString(R.string.following_orgs_signup_dialog_message));

        if (!App.hasNetworkConnection(context)){
            Snackbar.make(v, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap <String, Object> params = new HashMap<>();
            params.put("organizationObjectIds", orgs);

            dialog.show();
            ParseCloud.callFunctionInBackground("followOrganizations", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean success, ParseException e) {
                    dialog.dismiss();
                    if (e == null) {
                        if (success) {
                            new AlertDialog.Builder(context)
                                    .setMessage("You can find and follow organizations at any time in the 'Discover' tab")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(context, TabbedActivity.class);
                                            context.startActivity(i);
                                        }
                                    })
                                    .show();
                        }

                    } else {
                        e.printStackTrace();
                        Snackbar.make(v, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                        Intent i = new Intent(context, TabbedActivity.class);
                        context.startActivity(i);
                    }
                }
            });
        }


    }

    private static void loginDialog(View v, final Context context, final LogInCallback logInCallback) {
        if (!App.hasNetworkConnection(context)){
            Snackbar.make(v, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
            View view = ((TabbedActivity)context).getLayoutInflater().inflate(R.layout.login_user_dialog, null);
            builder.setView(view);
            final EditText password = (EditText)view.findViewById(R.id.login_dialog_password);
            Button login = (Button)view.findViewById(R.id.login_dialog_button);

            final AlertDialog dialog = builder.create();
            dialog.show();

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String usernameT = ParseUser.getCurrentUser().getUsername();
                    String passwordT = password.getText().toString();

                    dialog.dismiss();

                    if (passwordT.equals("")) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.incorrect_login_credentials))
                                .setPositiveButton("OK", null)
                                .show();
                    } else {
                        ParseUser.logInInBackground(usernameT, passwordT, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (e!= null){
                                    Snackbar.make(v, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                                } else {
                                    logInCallback.done(parseUser, e);
                                }
                            }
                        });
                    }
                }
            });
        }

    }

    public static void getOrganizationsThatUserIsAdminOf (Context context, final View layout, final ProgressBar loading, String userObjectId, final FunctionCallback<ArrayList<Organization>> callback){
        loading.setVisibility(View.VISIBLE);

        if (!App.hasNetworkConnection(context)){
            loading.setVisibility(View.GONE);
            Snackbar.make(layout, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("userObjectId", userObjectId);

            ParseCloud.callFunctionInBackground("getOrganizationsThatUserIsAdminOf", params, new FunctionCallback<List<ParseObject>>() {
                @Override
                public void done(List<ParseObject> followFields, ParseException e) {
                    loading.setVisibility(View.GONE);
//                int i = 0; //for debugging
                    if (e == null) {
                        ArrayList<Organization> orgs = new ArrayList<Organization>();
                        for (ParseObject followField : followFields) {
                            ParseObject orgObject = followField.getParseObject("organization");
                            Organization org = new Organization(orgObject);
                            org.setmLevel(Organization.getLevel(ConfigDataSource.ORG_LEVEL_CONFIG, orgObject));
                            if (Organization.getLevel(ConfigDataSource.ORG_CHILD_LEVEL_CONFIG, orgObject) != null)
                                org.setmChildLevel(Organization.getLevel(ConfigDataSource.ORG_CHILD_LEVEL_CONFIG, orgObject));
                            if (Organization.getLevel(ConfigDataSource.ORG_PARENT_LEVEL_CONFIG, orgObject) != null)
                                org.setmParentLevel(Organization.getLevel(ConfigDataSource.ORG_PARENT_LEVEL_CONFIG, orgObject));
                            orgs.add(org);
                        }

                        callback.done(orgs, e);
                    } else {
                        e.printStackTrace();
                        Snackbar.make(layout, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    public static void searchForUsersInRange (final Context context, final View v, final ProgressBar loading,
                                                      String searchString, int startIndex,
                                                      final FunctionCallback<ArrayList<User>> callback){
        loading.setVisibility(View.VISIBLE);

        if (!App.hasNetworkConnection(context)){
            loading.setVisibility(View.GONE);
            Snackbar.make(v, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("searchString", searchString);
            params.put("startIndex", startIndex);

            ParseCloud.callFunctionInBackground("searchForUsersInRange", params, new FunctionCallback<List<ParseUser>>() {
                @Override
                public void done(List<ParseUser> usersReturned, ParseException e) {
                    loading.setVisibility(View.GONE);
                    if (e == null) {
                        ArrayList<User> users = new ArrayList<User>();
                        for (ParseUser user : usersReturned) {
                            users.add(new User(user));
                        }

                        callback.done(users, e);
                    } else {
                        Snackbar.make(v, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
}