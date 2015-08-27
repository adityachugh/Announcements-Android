package io.mindbend.android.announcements.cloudCode;

import android.content.Context;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.HashMap;

import io.mindbend.android.announcements.Organization;

/**
 * Created by Avik Hasija on 8/23/2015.
 */
public class AdminDataSource {

    public static void checkIfUserIsAdminOfOrganization (final Context context, Organization organization, String userObjectId, final FunctionCallback<Boolean> callback){
        //TODO: unfinished! Aditya needs to update function to accept userId instead of user object!
        HashMap<String, Object> params = new HashMap<>();
        params.put("Organization", organization);
        //params.put("userObjectId", userObjectId);

        ParseCloud.callFunctionInBackground("checkIfUserIsAdminOfOrganization", params, new FunctionCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                Boolean isAdmin = false;
                if (e == null){
                    //UPDATE ISADMIN
                }
                callback.done(isAdmin, e);
            }
        });
    }
}
