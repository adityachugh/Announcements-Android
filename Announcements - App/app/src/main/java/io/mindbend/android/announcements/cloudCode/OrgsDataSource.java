package io.mindbend.android.announcements.cloudCode;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.User;

/**
 * Created by Akshay Pall on 21/08/2015.
 */
public class OrgsDataSource {
    public final static String ORG_TITLE = "name";
    public final static String ORG_DESCRIPTION = "organizationDescription";
    public final static String ORG_FOLLOWER_COUNT = "followerCount";
    public final static String ORG_TYPE = "organizationType";
    public final static String ORG_REQUEST_CODE = "requestCode";
    public final static String ORG_TAG = "handle";
    public final static String ORG_IMAGE = "image";


    public final static String ORG_TYPES_PRIVATE = "PRI";
    public final static String ORG_TYPES_PUBLIC = "PUB";

    public final static String FOLLOW_STATE_ACCEPTED = "A";
    public final static String FOLLOW_STATE_NO_REQUEST_SENT = null;
    public final static String FOLLOW_STATE_PENDING = "P";
    public final static String FOLLOW_STATE_REJECTED = "R";
    public final static String FOLLOW_STATE_UNFOLLOWED = "N";


    public static boolean isNew(ParseObject org) {
        DateTime date = new DateTime(org.getCreatedAt());
        DateTime today = new DateTime();
        int daysBetween = Days.daysBetween(date, today).getDays();
        return (daysBetween <= 5);
    }

    public static void getChildOrganizationsInRange (final ProgressBar loading, String parentOrganizationObjectId, int startIndex, int numberOfOrganizations, final FunctionCallback<ArrayList<Organization>> callback){
        loading.setVisibility(View.VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("parentOrganizationObjectId", parentOrganizationObjectId);
        params.put("startIndex", startIndex);
        params.put("numberOfOrganizations", numberOfOrganizations);

        ParseCloud.callFunctionInBackground("getChildOrganizationsInRange", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ArrayList<Organization> orgs = new ArrayList<Organization>();
                if (e == null) {
                    for (ParseObject object : parseObjects) {
                        try {
                            object.fetchIfNeeded();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        orgs.add(new Organization(object));
                    }
                }
                loading.setVisibility(View.GONE);
                callback.done(orgs, e);
            }
        });
    }

    public static void getAllChildOrganizations(final ProgressBar loading, String parentOrganizationObjectId, final FunctionCallback<ArrayList<Organization>> callback) {
        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("parentOrganizationObjectId", parentOrganizationObjectId);

        ParseCloud.callFunctionInBackground("getAllChildOrganizations", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {

                ArrayList<Organization> orgs = new ArrayList<Organization>();
                if (e == null) {
                    for (ParseObject object : parseObjects) {
                        try {
                            object.fetchIfNeeded();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        orgs.add(new Organization(object));
                    }
                }
                loading.setVisibility(View.GONE);
                callback.done(orgs, e);
            }
        });
    }

    public static void getOrganizationsFollowedByUser(final ProgressBar loading, String userObjectId, final FunctionCallback<ArrayList<Organization>> callback) {
        loading.setVisibility(View.VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("userObjectId", userObjectId);

        ParseCloud.callFunctionInBackground("getOrganizationsFollowedByUser", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ArrayList<Organization> orgsFollowed = new ArrayList<Organization>();
                if (e == null) {
                    for (ParseObject object : parseObjects) {
                        ParseObject org = object.getParseObject("organization");
                        try {
                            org.fetchIfNeeded();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        orgsFollowed.add(new Organization(org));
                    }
                    loading.setVisibility(View.GONE);
                    callback.done(orgsFollowed, e);
                }

            }
        });
    }

    public static void getAllTopLevelOrganizations (final ProgressBar loading, final FunctionCallback<ArrayList<Organization>> callback){
        loading.setVisibility(View.VISIBLE);

        ParseCloud.callFunctionInBackground("getAllTopLevelOrganizations", null, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ArrayList<Organization> topOrgs = new ArrayList<Organization>();
                if (e == null) {
                    for (ParseObject object : parseObjects){
                        try {
                            object.fetchIfNeeded();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        topOrgs.add(new Organization(object));
                    }
                }
                loading.setVisibility(View.GONE);
                callback.done(topOrgs, e);
            }
        });
    }
}
