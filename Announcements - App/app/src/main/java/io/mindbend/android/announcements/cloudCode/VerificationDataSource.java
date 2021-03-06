package io.mindbend.android.announcements.cloudCode;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;
import io.mindbend.android.announcements.onboardingAndSignupin.SignUpOrgsActivity;

/**
 * Created by Akshay Pall on 21/08/2015.
 */
public class VerificationDataSource {
    //USER class and its properties
    public static final String USER_CLASS = "User";
    public static final String USER_FIRST_NAME = "firstName";
    public static final String USER_LAST_NAME = "lastName";
    public static final String USER_EMAIL = "email";
    public static final String USER_USERNAME = "username";
    public static final String USER_PASSWORD = "password";

    public static void signupUser (final View layout, final Context context, final String firsttName, final String lastName, final String password, final String username, final String email){
        final ProgressDialog dialog = new ProgressDialog(context, R.style.DialogTheme);
        dialog.setMessage(context.getString(R.string.sign_up_new_user_dialog_message));

        if (!App.hasNetworkConnection(context)){
            Snackbar.make(layout, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
            builder.setTitle("Field(s) in use.");
            builder.setPositiveButton("OK", null);
            dialog.show();
            checkFieldInUse(layout, context, USER_CLASS, USER_EMAIL, email, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean aBoolean, ParseException e) {
                    dialog.dismiss();
                    if (e == null) {
                        if (aBoolean) {
                            builder.show();
                        } else {
                            dialog.show();
                            checkFieldInUse(layout, context, USER_CLASS, USER_USERNAME, username, new FunctionCallback<Boolean>() {
                                @Override
                                public void done(Boolean aBoolean, ParseException e) {
                                    dialog.dismiss();
                                    if (e == null) {
                                        if (aBoolean) {
                                            builder.show();
                                        } else {
                                            ParseUser user = new ParseUser();
                                            user.setUsername(username);
                                            user.setPassword(password);
                                            user.setEmail(email);
                                            user.put(USER_FIRST_NAME, firsttName);
                                            user.put(USER_LAST_NAME, lastName);
                                            user.put(UserDataSource.DESCRIPTION, context.getString(R.string.user_default_description));

                                            dialog.show();
                                            user.signUpInBackground(new SignUpCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    dialog.dismiss();
                                                    Intent i = new Intent(context, SignUpOrgsActivity.class);
                                                    context.startActivity(i);
                                                }
                                            });
                                        }
                                    } else {
                                        Snackbar.make(layout, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else {
                        Snackbar.make(layout, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public static void checkFieldInUse (View view, Context context, String className, String key, String value, final FunctionCallback<Boolean> callback){
        if (!App.hasNetworkConnection(context)){
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("className", className);
            params.put("key", key);
            params.put("value", value);
            ParseCloud.callFunctionInBackground("isFieldValueInUse", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean aBoolean, ParseException e) {
                    callback.done(aBoolean, e);
                }
            });
        }


    }

}
