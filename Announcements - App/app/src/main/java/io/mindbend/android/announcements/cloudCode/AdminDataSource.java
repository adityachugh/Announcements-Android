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

    public static void checkIfUserIsAdminOfOrganization (final Context context, Organization organization, ParseUser user, final FunctionCallback<String> callback){
        HashMap<String, Object> params = new HashMap<>();
        params.put("Organization", organization);
        //params.put("user", user);

        ParseCloud.callFunctionInBackground("checkIfUserIsAdminOfOrganization", params, new FunctionCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                String isAdmin = "";
                if (e == null){
                    isAdmin = parseObject.toString();
                    Log.w("AdminDataSource", "AdminDataSource e is null! " +isAdmin);
                }
                callback.done(isAdmin, e);
            }
        });
    }
}
