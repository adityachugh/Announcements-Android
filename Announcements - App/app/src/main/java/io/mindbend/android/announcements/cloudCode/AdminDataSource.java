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

import java.util.HashMap;

import io.mindbend.android.announcements.Organization;

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
}
