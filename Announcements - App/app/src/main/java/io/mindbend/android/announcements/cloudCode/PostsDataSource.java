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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;

/**
 * Created by Akshay Pall on 21/08/2015.
 */
public class PostsDataSource {

    public static final String POST_TITLE = "title";
    public static final String POST_BODY = "body";
    public static final String POST_IMAGE = "image";
    public static final String POST_START_DATE = "postStartDate";
    public static final String POST_END_DATE = "postEndDate";
    public static final String POST_PRIORITY = "priority";
    public static final String POST_ORGANIZATION = "organization";
    public static final String POST_STATUS = "status";
    public static final String POST_REJECTION_REASON = "rejectionReason";

    //levels of priority
    public static final int LOW_PRIORITY = 3;
    public static final int MEDIUM_PRIORITY = 2;
    public static final int HIGH_PRIORITY = 1;


    public static void getRangeOfPostsForDay (final View view, final ProgressBar loader, boolean isGettingPostsForFirstTime, int layoutToDisappearId, final Context context, int startIndex, int numberOfPosts, Date date, final FunctionCallback<ArrayList<Post>> callback){
        loader.setVisibility(View.VISIBLE);
        final View layoutView = view.findViewById(layoutToDisappearId);
        if (isGettingPostsForFirstTime)
            layoutView.setVisibility(View.INVISIBLE);

        if (!App.hasNetworkConnection(context)){
            loader.setVisibility(View.GONE);
            layoutView.setVisibility(View.VISIBLE);
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("startIndex", startIndex);
            params.put("numberOfPosts", numberOfPosts);
            params.put("date", date);
            ParseCloud.callFunctionInBackground("getRangeOfPostsForDay", params, new FunctionCallback<List<ParseObject>>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    loader.setVisibility(View.GONE);
                    layoutView.setVisibility(View.VISIBLE);

                    //convert all parseobjects to posts
                    ArrayList<Post> posts = new ArrayList<Post>();
                    if (e == null){
                        for (ParseObject object : parseObjects){
                            posts.add(new Post(context, object));
                        }
                        //to allow each frag to do it specifically
                        callback.done(posts, e);
                    } else {
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    public static void getPostsOfOrganizationInRange (final View view, final ProgressBar loading, final boolean removeLayout, int layoutToRemoveId, final Context context, String OrganizationObjectId,
                                                      int startIndex, int numberOfPosts, final FunctionCallback<ArrayList<Post>> callback){
        loading.setVisibility(View.VISIBLE);
        final View layoutView = view.findViewById(layoutToRemoveId);
        if (removeLayout){
            layoutView.setVisibility(View.INVISIBLE);
        }

        if (!App.hasNetworkConnection(context)){
            loading.setVisibility(View.GONE);
            if (removeLayout)
                layoutView.setVisibility(View.VISIBLE);
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationObjectId", OrganizationObjectId);
            params.put("startIndex", startIndex);
            params.put("numberOfPosts", numberOfPosts);

            ParseCloud.callFunctionInBackground("getPostsOfOrganizationInRange", params, new FunctionCallback<List<ParseObject>>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    ArrayList<Post> orgPosts = new ArrayList<Post>();
                    loading.setVisibility(View.GONE);
                    if(removeLayout)
                        layoutView.setVisibility(View.VISIBLE);
                    if (e == null){
                        for (ParseObject object : parseObjects){
                            orgPosts.add(new Post(context, object));
                        }
                        callback.done(orgPosts, e);
                    } else {
                        Snackbar.make(view, ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    public static void uploadPostForOrganization (Context context, final View view, final ProgressBar loading, int layoutToRemoveId,
                                                  String organizationObjectId, String title, String body, byte[] photo, Date startDate, Date endDate,
                                                  int priority, boolean notifyParent, final FunctionCallback<Boolean> callback){
        loading.setVisibility(View.VISIBLE);
        final View layoutView = view.findViewById(layoutToRemoveId);
        layoutView.setVisibility(View.INVISIBLE);

        if (!App.hasNetworkConnection(context)){
            loading.setVisibility(View.GONE);
            layoutView.setVisibility(View.VISIBLE);
            Snackbar.make(view, context.getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("organizationObjectId", organizationObjectId);
            params.put("title", title);
            params.put("body", body);
            params.put("photo", photo);
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            params.put("priority", priority);
            params.put("notifyParent", notifyParent);

            ParseCloud.callFunctionInBackground("uploadPostForOrganization", params, new FunctionCallback<Boolean>() {
                @Override
                public void done(Boolean successful, ParseException e) {
                    loading.setVisibility(View.GONE);
                    layoutView.setVisibility(View.VISIBLE);
                    String message = "Successfully uploaded announcement";
                    if (e == null) {
                        callback.done(successful, e);
                    } else {
                        e.printStackTrace();
                        message = ErrorCodeMessageDataSource.errorCodeMessage(e.getMessage());
                    }

                    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
                }
            });
        }

    }
}
