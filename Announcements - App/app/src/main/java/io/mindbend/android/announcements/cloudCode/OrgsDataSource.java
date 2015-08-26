package io.mindbend.android.announcements.cloudCode;

import android.content.Context;
import android.util.Log;

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
    public final static String ORG_DESCRIPTION = "description";
    public final static String ORG_FOLLOWER_COUNT = "followerCount";
    public final static String ORG_TYPE = "organizationType";
    public final static String ORG_REQUEST_CODE = "requestCode";
    public final static String ORG_TAG = "handle";
    public final static String ORG_IMAGE = "image";


    public final static String ORG_TYPES_PRIVATE = "Private";
    public final static String ORG_TYPES_PUBLIC = "Public";

    public static boolean isNew (ParseObject org) {
        DateTime date = new DateTime(org.getCreatedAt());
        DateTime today = new DateTime();
        int daysBetween = Days.daysBetween(date, today).getDays();
        return (daysBetween <= 5);
    }

    public static void getAllChildOrganizations (String parentOrganizationObjectId, final FunctionCallback<ArrayList<Organization>> callback){
        HashMap<String, Object> params = new HashMap<>();
        params.put("parentOrganizationObjectId", parentOrganizationObjectId);

        ParseCloud.callFunctionInBackground("getAllChildOrganizations", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {

                ArrayList<Organization> orgs = new ArrayList<Organization>();
                if (e == null){
                    for (ParseObject object : parseObjects){
                        orgs.add(new Organization(object));
                    }
                }
                callback.done(orgs, e);
            }
        });
    }

    public static void getOrganizationsFollowedByUser (String userObjectId , final FunctionCallback<ArrayList<Organization>> callback){
        HashMap<String, Object> params = new HashMap<>();
        params.put("userObjectId", userObjectId);

        ParseCloud.callFunctionInBackground("getOrganizationsFollowedByUser", params, new FunctionCallback<List<ParseObject>>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {

                ArrayList<Organization> orgsFollowed = new ArrayList<Organization>();
                if (e == null){
                    for (ParseObject object : parseObjects){
                        try {
                            object.fetch();
                            orgsFollowed.add(new Organization(object.fetch()));
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    Log.wtf("OrgsDataSource", "FUNCTION WORKS");
                }
                else
                    Log.wtf("OrgsDataSource", "FUNCTION broken");


                callback.done(orgsFollowed, e);
            }
        });
    }
}
