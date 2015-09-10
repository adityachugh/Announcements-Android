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
                    Snackbar.make(view, context.getString(R.string.succes_updated_org_photo), Snackbar.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                    Snackbar.make(view, context.getString(R.string.could_not_upload_org_photo), Snackbar.LENGTH_SHORT).show();
                }
                if (callback != null)
                    callback.done(isSuccessful, e);
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
                    Snackbar.make(view, context.getString(R.string.succes_updated_org_photo), Snackbar.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                    Snackbar.make(view, context.getString(R.string.could_not_upload_org_photo), Snackbar.LENGTH_SHORT).show();
                }
                if (callback != null)
                    callback.done(isSuccessful, e);
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
                                                  byte[] profilePhoto, byte[] coverPhoto, String description, String parentLevelConfigObjectId,
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
        params.put("parentLevelConfigObjectId", parentLevelConfigObjectId);

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
                if (e == null&& success) {
                    Log.wtf("Act on follow request", "successfully completed");
                    functionCallback.done(success, e);
                } else {
                    Snackbar.make(view, "Could not act on follow request", Snackbar.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public static void changeOrganizationType (final View view, final ProgressBar loading, String organizationObjectId,
                                               String type, String nullableAccessCode, final FunctionCallback<Boolean> callback){
        loading.setVisibility(View.VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("type", type);
        if ((nullableAccessCode != null && !nullableAccessCode.equals("")))
            params.put("accessCode", Integer.parseInt(nullableAccessCode));

        ParseCloud.callFunctionInBackground("changeOrganizationType", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null && success){
                    Snackbar.make(view, "Organization type changed", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(view, "Failed to change organization type", Snackbar.LENGTH_SHORT).show();
                    if (e!= null)
                        e.printStackTrace();
                }
                callback.done(success, e);
            }
        });
    }

    public static void updateOrganizationName (final View view, final ProgressBar loading,
                                                String organizationObjectId, String name,
                                                final FunctionCallback<Boolean> callback){
        loading.setVisibility(View.VISIBLE);

        HashMap<String, String> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("name", name);

        ParseCloud.callFunctionInBackground("updateOrganizationName", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null && success){
                    Snackbar.make(view, "Updated organization name", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (e!=null)
                        e.printStackTrace();
                    Snackbar.make(view, "Could not update organization name", Snackbar.LENGTH_SHORT).show();
                }
                callback.done(success, e);
            }
        });
    }

    public static void updateOrganizationDescription (final View view, final ProgressBar loading,
                                               String organizationObjectId, String description,
                                               final FunctionCallback<Boolean> callback){
        loading.setVisibility(View.VISIBLE);

        HashMap<String, String> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("description", description);

        ParseCloud.callFunctionInBackground("updateOrganizationDescription", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null&& success){
                    Snackbar.make(view, "Updated organization description", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (e!=null)
                        e.printStackTrace();
                    Snackbar.make(view, "Could not update organization description", Snackbar.LENGTH_SHORT).show();
                }
                callback.done(success, e);
            }
        });
    }

    public static void updateOrganizationAccessCode (final View view, final ProgressBar loading,
                                                      String organizationObjectId, String nullableAccessCode,
                                                      final FunctionCallback<Boolean> callback){
        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        Integer accessCode = (nullableAccessCode == null || nullableAccessCode.equals("")) ? null : Integer.parseInt(nullableAccessCode);
        params.put("accessCode", accessCode);

        ParseCloud.callFunctionInBackground("updateOrganizationAccessCode", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null&& success){
                    Snackbar.make(view, "Updated organization access code", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (e!=null)
                        e.printStackTrace();
                    Snackbar.make(view, "Could not update organization access code", Snackbar.LENGTH_SHORT).show();
                }
                callback.done(success, e);
            }
        });
    }

    public static void updateOrganizationFields (final View view, final ProgressBar loading, String organizationObjectId,
                                                 String accessCodeString, String description, String name,
                                                 final FunctionCallback<Organization> orgModified){

        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        Integer accessCode = (accessCodeString == null || accessCodeString.equals("")) ? null : Integer.parseInt(accessCodeString);
        params.put("accessCode", accessCode);
        params.put("description", description);
        params.put("name", name);
        
        ParseCloud.callFunctionInBackground("updateOrganizationFields", params, new FunctionCallback<ParseObject>() {
            @Override
            public void done(ParseObject orgReturned, ParseException e) {
//                int i = 0;
                loading.setVisibility(View.GONE);
                if (e == null && orgModified != null){
                    Snackbar.make(view, "Successfully updated organization", Snackbar.LENGTH_SHORT).show();
                    orgModified.done(new Organization(orgReturned), e);
                }
                else {
                    Snackbar.make(view, "Failed to update organization", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}
