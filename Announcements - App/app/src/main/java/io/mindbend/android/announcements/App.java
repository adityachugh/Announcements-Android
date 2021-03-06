package io.mindbend.android.announcements;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.parse.Parse;

import java.util.HashMap;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class App extends Application {
    private static final String APP_STARTED = "APP STARTED";
    public static boolean isLollipopOrHigher = false;
    public static boolean isKitkatOrHigher = false;
    public static boolean isAPI22OrHigher = false;

    @Override
    public void onCreate() {
        super.onCreate();
//        this class is to initialize parse and setup anything else that is required for the app to run. This class is run FIRST before any activity.
        Log.i(APP_STARTED, "Application started succesfully");

        // Initialize Parse and Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "S1dL5D6QCSqsC1FyYTiyS5V4Yv2zcK47TeybVtEf", "llxS4kjhRdSv46DvGi9HyPapsRaFDoU9155Nh88V");
        Log.i(APP_STARTED, "Parse initialized");

//        set if the device is running lollipop or higher to decide whether to run animations or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isLollipopOrHigher = true;
            isKitkatOrHigher = true;
            Log.i(APP_STARTED, "device is running lollipop or higher");
        }else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            //Needed because backgroundTint method used to colour buttons is broken on API 21
            isAPI22OrHigher = true;
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isKitkatOrHigher = true;
            Log.i(APP_STARTED, "device is running kitkat or higher, animation framework turned on");
        }

    }

    public static boolean hasNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }
}

