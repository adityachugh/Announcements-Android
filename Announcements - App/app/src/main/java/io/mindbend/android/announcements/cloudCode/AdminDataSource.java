package io.mindbend.android.announcements.cloudCode;

import android.app.ProgressDialog;
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

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;

/**
 * Created by Avik Hasija on 8/23/2015.
 */
public class AdminDataSource {

    public static void updateOrganizationProfilePhoto(final View view, final Context context, String organizationObjectId, byte[] photo, final FunctionCallback<Boolean> callback) {
        final ProgressDialog dialog = new ProgressDialog(context, R.style.DialogTheme);
        dialog.setMessage(context.getString(R.string.updating_org_photo_loading_dialog_message));

        if (!App.hasNetworkConnection(context)){
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            dialog.show();
            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationObjectId", organizationObjectId);
            params.put("photo", photo);

            ParseCloud.callFunctionInBackground("updateOrganizationProfilePhoto", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean isSuccessful, ParseException e) {
                    dialog.dismiss();
                    if (e == null) {
                        Snackbar.make(view, context.getString(R.string.succes_updated_org_photo), Snackbar.LENGTH_SHORT).show();
                    } else {
                        e.printStackTrace();
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                    if (callback != null)
                        callback.done(isSuccessful, e);
                }
            });
        }
    }

    public static void updateOrganizationCoverPhoto(final View view, final Context context, String organizationObjectId, byte[] photo, final FunctionCallback<Boolean> callback) {
        final ProgressDialog dialog = new ProgressDialog(context, R.style.DialogTheme);
        dialog.setMessage(context.getString(R.string.updating_org_photo_loading_dialog_message));
        if (!App.hasNetworkConnection(context)){
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        }

        else {
            dialog.show();
            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationObjectId", organizationObjectId);
            params.put("photo", photo);

            ParseCloud.callFunctionInBackground("updateOrganizationCoverPhoto", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean isSuccessful, ParseException e) {
                    dialog.dismiss();
                    if (e == null) {
                        Snackbar.make(view, context.getString(R.string.succes_updated_org_photo), Snackbar.LENGTH_SHORT).show();
                    } else {
                        e.printStackTrace();
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                    if (callback != null)
                        callback.done(isSuccessful, e);
                }
            });
        }
    }

    public static void addAdminToOrganization(Context context, final View view, final ProgressBar loading, String organizationObjectId,
                                              String selectedUserToBeAdminObjectId, final FunctionCallback<Boolean> callback) {
        loading.setVisibility(View.VISIBLE);

        if (!App.hasNetworkConnection(context)){
            loading.setVisibility(View.GONE);
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("organizationObjectId", organizationObjectId);
            params.put("selectedUserToBeAdminObjectId", selectedUserToBeAdminObjectId);

            ParseCloud.callFunctionInBackground("addAdminToOrganization", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean success, ParseException e) {
                    loading.setVisibility(View.GONE);
                    if (e != null) {
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    } else {
                        if (success)
                            callback.done(success, e);
                    }
                }
            });
        }


    }

    public static void createNewChildOrganization(final View view, final Context context, String organizationObjectId,
                                                  String configObjectId, String organizationName, String organizationHandle,
                                                  boolean isPrivate, String adminObjectId, boolean approvalRequired, Integer accessCode,
                                                  byte[] profilePhoto, byte[] coverPhoto, String description, String newOrgParentLevelConfigObjectId,
                                                  String newOrgLevelConfigObjectId,
                                                  final FunctionCallback<Boolean> callback) {

        if (!App.hasNetworkConnection(context)){
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationObjectId", organizationObjectId);
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
            params.put("newOrgParentLevelConfigObjectId", newOrgParentLevelConfigObjectId);
            params.put("newOrgLevelConfigObjectId", newOrgLevelConfigObjectId);

            ParseCloud.callFunctionInBackground("createNewChildOrganization", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean success, ParseException e) {
                    if (e != null || !success) {
                        String message = e == null ? "Unknown Error. Contact a Mindbend Studio Employee" : ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage());
                        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
                        if (e != null)
                            e.printStackTrace();
                    } else {
                        if (success)
                            callback.done(success, e);
                    }
                }
            });
        }


    }

    public static void getPostsToBeApprovedInRange(final View view, final ProgressBar loading,int viewIdToRemove, final Context context, String organizationObjectId, int startIndex, int numberOfPosts, final FunctionCallback<ArrayList<Post>> callback) {
        loading.setVisibility(View.VISIBLE);
        final View layoutView = view.findViewById(viewIdToRemove);
        layoutView.setVisibility(View.INVISIBLE);

        if (!App.hasNetworkConnection(context)){
            loading.setVisibility(View.GONE);
            layoutView.setVisibility(View.VISIBLE);
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationObjectId", organizationObjectId);
            params.put("startIndex", startIndex);
            params.put("numberOfPosts", numberOfPosts);

            ParseCloud.callFunctionInBackground("getPostsToBeApprovedInRange", params, new FunctionCallback<List<ParseObject>>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    loading.setVisibility(View.GONE);
                    layoutView.setVisibility(View.VISIBLE);
                    ArrayList<Post> pendingPosts = new ArrayList<Post>();
                    if (e == null) {
                        for (ParseObject object : parseObjects) {
                            pendingPosts.add(new Post(context, object));
                        }
                        callback.done(pendingPosts, e);
                    } else {
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }

                }
            });
        }


    }

    public static void getAllPostsForOrganizationForRange (final View view, final ProgressBar loading,int viewIdToRemove, final Context context,
                                                           String organizationObjectId, int startIndex,
                                                           int numberOfPosts, final FunctionCallback<ArrayList<Post>> callback){
        loading.setVisibility(View.VISIBLE);
        final View layoutView = view.findViewById(viewIdToRemove);
        layoutView.setVisibility(View.INVISIBLE);

        if (!App.hasNetworkConnection(context)){
            loading.setVisibility(View.GONE);
            layoutView.setVisibility(View.VISIBLE);
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationObjectId", organizationObjectId);
            params.put("startIndex", startIndex);
            params.put("numberOfPosts", numberOfPosts);

            ParseCloud.callFunctionInBackground("getAllPostsForOrganizationForRange", params, new FunctionCallback<List<ParseObject>>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    loading.setVisibility(View.GONE);
                    layoutView.setVisibility(View.VISIBLE);
                    ArrayList<Post> posts = new ArrayList<Post>();
                    if (e == null) {
                        for (ParseObject object : parseObjects) {
                            posts.add(new Post(context, object));
                        }
                        callback.done(posts, e);
                    } else {
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    public static void actOnFollowRequest(Context context, final View view, final ProgressBar loading, String organizationObjectId, String followObjectId, boolean isApproved, final FunctionCallback<Boolean> functionCallback) {
        loading.setVisibility(View.VISIBLE);

        if (!App.hasNetworkConnection(context)){
            loading.setVisibility(View.GONE);
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationObjectId", organizationObjectId);
            params.put("followObjectId", followObjectId);
            params.put("approvalState", isApproved);

            ParseCloud.callFunctionInBackground("actOnFollowRequest", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean success, ParseException e) {
                    loading.setVisibility(View.GONE);
                    if (e == null && success) {
                        Log.wtf("Act on follow request", "successfully completed");
                        functionCallback.done(success, e);
                    } else {
                        if (e!= null){
                            Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            });
        }


    }

    public static void updateOrganizationFields(Context context, final View view, String organizationObjectId,
                                                String accessCodeString, String description, String name,
                                                final FunctionCallback<Organization> orgModified) {

        if (!App.hasNetworkConnection(context)){
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
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
                    if (e == null && orgModified != null) {
                        Snackbar.make(view, "Successfully updated organization", Snackbar.LENGTH_SHORT).show();
                        orgModified.done(new Organization(orgReturned), e);
                    } else {
                        if (e != null){
                            Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            });
        }


    }

    public static void actOnApprovalRequest(Context context, final View view, String postObjectId, String organizationObjectId, final boolean approvalState, String rejectionReason, int priority, final FunctionCallback<Boolean> callback) {
        if (!App.hasNetworkConnection(context)){
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("postObjectId", postObjectId);
            params.put("organizationObjectId", organizationObjectId);
            params.put("approvalState", approvalState);
            params.put("rejectionReason", rejectionReason);
            params.put("priority", priority);

            ParseCloud.callFunctionInBackground("actOnApprovalRequest", params, new FunctionCallback<Boolean>() {

                @Override
                public void done(Boolean success, ParseException e) {
                    if (e == null && success) {
                        if (approvalState)
                            Snackbar.make(view, "Successfully approved post!", Snackbar.LENGTH_SHORT).show();
                        else
                            Snackbar.make(view, "Successfully declined post!", Snackbar.LENGTH_SHORT).show();
                    } else {
                        if (e!=null){
                            Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            });
        }


    }

    public static void deletePost (Context context, final View view, String organizationObjectId, String postObjectId, final FunctionCallback<Boolean> callback){

        if (!App.hasNetworkConnection(context)){
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationObjectId", organizationObjectId);
            params.put("postObjectId", postObjectId);

            ParseCloud.callFunctionInBackground("deletePost", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean done, ParseException e) {
                    Log.wtf("delete post", "did it work? " + done);
                    if (e == null){
                        if (done)
                            Snackbar.make(view, "Successfully deleted post!", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }


    }

    public static void removeAdminFromOrganization (Context context, final View view, String organizationObjectId, String selectedAdminToRemoveObjectId, final FunctionCallback<Boolean> callback){
        if (!App.hasNetworkConnection(context)){
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(context, R.style.DialogTheme);
            progressDialog.setMessage(context.getString(R.string.remove_admin_loading_message));
            progressDialog.setTitle("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationObjectId", organizationObjectId);
            params.put("selectedAdminToRemoveObjectId", selectedAdminToRemoveObjectId);

            ParseCloud.callFunctionInBackground("removeAdminFromOrganization", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean success, ParseException e) {
                    progressDialog.dismiss();
                    if (e == null){
                            Snackbar.make(view, "Successfully deleted admin!", Snackbar.LENGTH_SHORT).show();
                        Log.wtf("Deleted admin?", "SUCCESS");
                        callback.done(true, e);
                    } else {
                        Log.wtf("Deleted admin?", "FAILURE");
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
            //actually delete
        }
    }

    public static void removeFollowerFromOrganization (Context context, final View view, String organizationObjectId, String selectedFollowerToRemoveObjectId, final FunctionCallback<Boolean> callback){
        if (!App.hasNetworkConnection(context)){
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(context, R.style.DialogTheme);
            progressDialog.setMessage(context.getString(R.string.remove_follower_loading_message));
            progressDialog.setTitle("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationObjectId", organizationObjectId);
            params.put("selectedFollowerToRemoveObjectId", selectedFollowerToRemoveObjectId);

            ParseCloud.callFunctionInBackground("removeFollowerFromOrganization", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean success, ParseException e) {
                    progressDialog.dismiss();
                    if (e == null){
                        Snackbar.make(view, "Successfully deleted follower!", Snackbar.LENGTH_SHORT).show();
                        Log.wtf("Deleted follower?", "SUCCESS");
                        callback.done(true, e);
                    } else {
                        Log.wtf("Deleted follower?", "FAILURE");
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
            //actually delete
        }
    }
}

