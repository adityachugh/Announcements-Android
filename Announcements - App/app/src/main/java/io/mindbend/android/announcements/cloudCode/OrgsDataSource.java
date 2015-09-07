package io.mindbend.android.announcements.cloudCode;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.reusableFrags.UserListAdapter;

/**
 * Created by Akshay Pall on 21/08/2015.
 */
public class OrgsDataSource {
    public final static String ORG_TITLE = "name";
    public final static String ORG_DESCRIPTION = "organizationDescription";
    public final static String ORG_FOLLOWER_COUNT = "followerCount";
    public final static String ORG_TYPE = "organizationType";
    public final static String ORG_ACCESS_CODE = "accessCode";
    public final static String ORG_TAG = "handle";
    public final static String ORG_PROFILE_IMAGE = "image";
    public static final String ORG_COVER_IMAGE = "coverPhoto";


    public final static String ORG_TYPES_PRIVATE = "PRI";
    public final static String ORG_TYPES_PUBLIC = "PUB";

    public final static String FOLLOW_STATE_ACCEPTED = "A";
    public final static String FOLLOW_STATE_NO_REQUEST_SENT = null;
    public final static String FOLLOW_STATE_PENDING = "P";
    public final static String FOLLOW_STATE_REJECTED = "R";


    //for the map callback given in getFollowersFollowRequestsAndAdminsForOrganizationInRange function
    public final static Boolean MAP_USER_TYPES_KEY = true;
    public final static Boolean MAP_USER_LIST_KEY= false;


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

    public static void getOrganizationsFollowedByUserInRange (final ProgressBar loading, String userObjectId, final FunctionCallback<ArrayList<Organization>> callback) {
        //only for user in profile frag tab
        loading.setVisibility(View.VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("userObjectId", userObjectId);
        params.put("startIndex", 0);
        params.put("numberOfOrganizations", 30);

        ParseCloud.callFunctionInBackground("getOrganizationsFollowedByUserInRange", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                loading.setVisibility(View.GONE);
//                int i = 0;
                ArrayList<Organization> orgsFollowed = new ArrayList<Organization>();
                if (e == null) {
                    for (ParseObject object : parseObjects) {
                        ParseObject org = object.getParseObject("organization");
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

    public static void isFollowingOrganization (final View layout, final ProgressBar loading, String userObjectId, String organizationObjectId, final FunctionCallback<String> callback){
        loading.setVisibility(View.VISIBLE);

        HashMap<String, String> params = new HashMap<>();
        params.put("userObjectId", userObjectId);
        params.put("organizationObjectId", organizationObjectId);

        ParseCloud.callFunctionInBackground("isFollowingOrganization", params, new FunctionCallback<String>() {
            @Override
            public void done(String followStatus, ParseException e) {
                //int i = 0;
                loading.setVisibility(View.GONE);
                callback.done(followStatus, e);
            }
        });
    }

    public static void privateOrganizationAccessCodeEntered (final View layout, final ProgressBar loading, String organizationObjectId, String enteredAccessCode, final FunctionCallback<Boolean> callback){
        loading.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<>();

        Integer accessCode = null;
        if (!enteredAccessCode.equals(""))
            accessCode = Integer.parseInt(enteredAccessCode);
        Log.wtf("attempt to follow PRI org", "Access code: "+accessCode);

        params.put("enteredAccessCode", accessCode);
        params.put("organizationObjectId", organizationObjectId);

        ParseCloud.callFunctionInBackground("privateOrganizationAccessCodeEntered", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean correctCodeEntered, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null){
                    callback.done(correctCodeEntered, e);
                } else {
                    Snackbar.make(layout, "Error", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void getOrganizationsForDiscoverTabInRange (final View view, final Context context, final ProgressBar loading, String userObjectId, int startIndex, int numberOfOrganizations, final FunctionCallback<ArrayList<Organization>> callback) {
        loading.setVisibility(View.VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("userObjectId", userObjectId);
        params.put("startIndex", startIndex);
        params.put("numberOfOrganizations", numberOfOrganizations);

        ParseCloud.callFunctionInBackground("getOrganizationsForDiscoverTabInRange", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseOrgs, ParseException e) {
                loading.setVisibility(View.GONE);
                if (e == null){
                    ArrayList<Organization> orgs = new ArrayList<Organization>();
                    for (ParseObject org : parseOrgs){
                        orgs.add(new Organization(org));
                    }
                    callback.done(orgs, e);
                } else {
                    e.printStackTrace();
                    Snackbar.make(view, context.getString(R.string.error_getting_discover_orgs), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void getFollowersFollowRequestsAndAdminsForOrganizationInRange (final View view, final Context context, final ProgressBar loading,
                                                                                  String organizationObjectId, int startIndex, int numberOfUsers, boolean isAdmin,
                                                                                  final FunctionCallback<HashMap<Boolean, Object>> callback) {
        loading.setVisibility(View.VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        params.put("organizationObjectId", organizationObjectId);
        params.put("startIndex", startIndex);
        params.put("numberOfUsers", numberOfUsers);
        params.put("isAdmin", isAdmin);

        ParseCloud.callFunctionInBackground("getFollowersFollowRequestsAndAdminsForOrganizationInRange", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> followObjects, ParseException e) {
                int i = 0;
                loading.setVisibility(View.GONE);
                if (e == null){
                    ArrayList<User> users = new ArrayList<User>();
                    HashMap<User, Integer> userTypes = new HashMap<User, Integer>();
                    for (ParseObject follow : followObjects){
                        User user = new User(follow.getParseUser(UserDataSource.FOLLOWER_USER_FIELD));
                        user.setmFollowObjectId(follow.getObjectId());
                        users.add(user);
                        userTypes.put(user, getTypeOfFollower(follow.getString(UserDataSource.FOLLOWER_USER_TYPE_FIELD)));
                    }

                    HashMap<Boolean, Object> toReturnMap = new HashMap<Boolean, Object>();
                    toReturnMap.put(MAP_USER_LIST_KEY, users);
                    toReturnMap.put(MAP_USER_TYPES_KEY, userTypes);

                    callback.done(toReturnMap, e);
                } else {
                    e.printStackTrace();
                    Snackbar.make(view, context.getString(R.string.error_loading_org_members), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static Integer getTypeOfFollower(String userTypeFromFollowObject){
        switch (userTypeFromFollowObject){
            case UserDataSource.FOLLOWER_NORMAL:
                return UserListAdapter.USERS_MEMBERS;
            case UserDataSource.FOLLOWER_ADMIN:
                return UserListAdapter.USERS_ADMINS;
            default: //is pending
                return UserListAdapter.USERS_PENDING;
        }
    }
}
