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
}
