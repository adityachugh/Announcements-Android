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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;

/**
 * Created by Avik Hasija on 8/23/2015.
 */
public class AdminDataSource {
    public static void checkIfUserIsAdminOfOrganization (final ProgressBar loading, final Context context, String organizationObjectId, String userObjectId, final FunctionCallback<Boolean> callback){
        loading.setVisibility(View.VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("userObjectId", userObjectId);

        ParseCloud.callFunctionInBackground("checkIfUserIsAdminOfOrganization", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean bool, ParseException e) {
                Boolean isAdmin = false;
                if (e == null){
                    isAdmin = bool;
                }
                loading.setVisibility(View.GONE);
                callback.done(isAdmin, e);
            }
        });
    }

    public static void updateOrganizationProfilePhoto (final View view, final Context context, final ProgressBar loading, String organizationObjectId, byte[] photo, final FunctionCallback<Boolean> callback){
        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("photo", photo);

        ParseCloud.callFunctionInBackground("updateOrganizationProfilePhoto", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean isSuccessful, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null){
                    if (callback != null)
                        callback.done(isSuccessful, e);
                    Snackbar.make(view, context.getString(R.string.succes_updated_org_photo), Snackbar.LENGTH_SHORT).show();
                }
                else {
                    e.printStackTrace();
                    Snackbar.make(view, context.getString(R.string.could_not_upload_org_photo), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void updateOrganizationCoverPhoto (final View view, final Context context, final ProgressBar loading, String organizationObjectId, byte[] photo, final FunctionCallback<Boolean> callback){
        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("photo", photo);

        ParseCloud.callFunctionInBackground("updateOrganizationCoverPhoto", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean isSuccessful, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null){
                    if (callback != null)
                        callback.done(isSuccessful, e);
                    Snackbar.make(view, context.getString(R.string.succes_updated_org_photo), Snackbar.LENGTH_SHORT).show();
                }
                else {
                    e.printStackTrace();
                    Snackbar.make(view, context.getString(R.string.could_not_upload_org_photo), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void addAdminToOrganization (final View view, final ProgressBar loading, String organizationObjectId,
                                               String selectedUserToBeAdminObjectId, final FunctionCallback<Boolean> callback){
        loading.setVisibility(View.VISIBLE);

        HashMap<String, String> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("selectedUserToBeAdminObjectId", selectedUserToBeAdminObjectId);

        ParseCloud.callFunctionInBackground("addAdminToOrganization", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e != null || !success){
                    Snackbar.make(view, "Error adding user as an admin", Snackbar.LENGTH_SHORT).show();
                } else {
                    callback.done(success, e);
                }
            }
        });
    }

    public static void getPostsToBeApprovedInRange (final Context context, String organizationObjectId, int startIndex, int numberOfPosts, final FunctionCallback<ArrayList<Post>> callback){
        //loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("startIndex", startIndex);
        params.put("numberOfPosts", numberOfPosts);

        ParseCloud.callFunctionInBackground("getPostsToBeApprovedInRange", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ArrayList<Post> pendingPosts = new ArrayList<Post>();
                if (e == null){
                    for (ParseObject object : parseObjects){
                        pendingPosts.add(new Post(context, object));
                    }
                }
                //loading.setVisibility(View.GONE);
                callback.done(pendingPosts, e);
            }
        });
    }

    public static void actOnApprovalRequest (final View view, String postObjectId, String organizationObjectId, boolean approvalState, String rejectionReason, int priority, final FunctionCallback<Boolean> callback){

        HashMap<String, Object> params = new HashMap<>();
        params.put("postObjectId", postObjectId);
        params.put("organizationObjectId", organizationObjectId);
        params.put("approvalState", approvalState);
        params.put("rejectionReason", rejectionReason);
        params.put("priority", priority);

        ParseCloud.callFunctionInBackground("actOnApprovalRequest", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean success, ParseException e) {
                if (e == null){
                    if (success)
                        Snackbar.make(view, "Successfully approved post!", Snackbar.LENGTH_SHORT).show();
                    else
                        Snackbar.make(view, "Successfully declined post!", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    Snackbar.make(view, "Error approving/declining post", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}
