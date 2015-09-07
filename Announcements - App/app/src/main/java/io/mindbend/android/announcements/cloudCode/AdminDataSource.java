package io.mindbend.android.announcements.cloudCode;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.HashMap;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

/**
 * Created by Avik Hasija on 8/23/2015.
 */
public class AdminDataSource {

    public static void updateOrganizationProfilePhoto(final View view, final Context context, final ProgressBar loading, String organizationObjectId, byte[] photo, final FunctionCallback<Boolean> callback) {
        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("photo", photo);

        ParseCloud.callFunctionInBackground("updateOrganizationProfilePhoto", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean isSuccessful, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null) {
                    if (callback != null)
                        callback.done(isSuccessful, e);
                    Snackbar.make(view, context.getString(R.string.succes_updated_org_photo), Snackbar.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                    Snackbar.make(view, context.getString(R.string.could_not_upload_org_photo), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void updateOrganizationCoverPhoto(final View view, final Context context, final ProgressBar loading, String organizationObjectId, byte[] photo, final FunctionCallback<Boolean> callback) {
        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("photo", photo);

        ParseCloud.callFunctionInBackground("updateOrganizationCoverPhoto", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean isSuccessful, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null) {
                    if (callback != null)
                        callback.done(isSuccessful, e);
                    Snackbar.make(view, context.getString(R.string.succes_updated_org_photo), Snackbar.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                    Snackbar.make(view, context.getString(R.string.could_not_upload_org_photo), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void addAdminToOrganization(final View view, final ProgressBar loading, String organizationObjectId,
                                              String selectedUserToBeAdminObjectId, final FunctionCallback<Boolean> callback) {
        loading.setVisibility(View.VISIBLE);

        HashMap<String, String> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("selectedUserToBeAdminObjectId", selectedUserToBeAdminObjectId);

        ParseCloud.callFunctionInBackground("addAdminToOrganization", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e != null || !success) {
                    Snackbar.make(view, "Error adding user as an admin", Snackbar.LENGTH_SHORT).show();
                } else {
                    callback.done(success, e);
                }
            }
        });
    }

    public static void createNewChildOrganization(final View view, final Context context, final ProgressBar loading, String organizationObjectId,
                                                  String levelConfigObjectId, String configObjectId, String organizationName, String organizationHandle,
                                                  boolean isPrivate, String adminObjectId, boolean approvalRequired, Integer accessCode,
                                                  byte[] profilePhoto, byte[] coverPhoto, String description,
                                                  final FunctionCallback<Boolean> callback) {
        //levelConfigObjectId is the child level config of the org that's calling this function
        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("levelConfigObjectId", levelConfigObjectId);
        params.put("configObjectId", configObjectId);
        params.put("organizationName", organizationName);
        String organizationType = isPrivate ? OrgsDataSource.ORG_TYPES_PRIVATE : OrgsDataSource.ORG_TYPES_PUBLIC;
        params.put("organizationType", organizationType);
        params.put("adminObjectId", adminObjectId);
        params.put("organizationHandle", organizationHandle);
        params.put("approvalRequired", approvalRequired);
        params.put("accessCode", accessCode);
        params.put("profilePhoto", profilePhoto);
        params.put("coverPhoto", coverPhoto);
        params.put("description", description);

        ParseCloud.callFunctionInBackground("createNewChildOrganization", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e != null || !success) {
                    Snackbar.make(view, context.getString(R.string.error_creating_org_message), Snackbar.LENGTH_SHORT).show();
                    e.printStackTrace();
                } else {
                    callback.done(success, e);
                }
            }
        });
    }

    public static void actOnFollowRequest(final View view, final ProgressBar loading, String organizationObjectId, String followObjectId, boolean isApproved, final FunctionCallback<Boolean> functionCallback) {
        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("followObjectId", followObjectId);
        params.put("approvalState", isApproved);

        ParseCloud.callFunctionInBackground("actOnFollowRequest", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                loading.setVisibility(View.GONE);
                if (success && e == null) {
                    Log.wtf("Act on follow request", "successfully completed");
                    functionCallback.done(success, e);
                } else {
                    Snackbar.make(view, "Could not act on follow request", Snackbar.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
