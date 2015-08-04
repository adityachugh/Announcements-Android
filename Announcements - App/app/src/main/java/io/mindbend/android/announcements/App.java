package io.mindbend.android.announcements;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.parse.Parse;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class App extends Application {
    private static final String APP_STARTED = "APP STARTED";
    public static boolean isLollipopOrHigher = false;
    public static boolean isKitkatOrHigher = false;

    @Override
    public void onCreate() {
        super.onCreate();
//        TODO: this class is to initialize parse and setup anything else that is required for the app to run. This class is run FIRST before any activity.
        Log.d(APP_STARTED, "Application started succesfully");

        // Initialize Parse and Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "S1dL5D6QCSqsC1FyYTiyS5V4Yv2zcK47TeybVtEf", "llxS4kjhRdSv46DvGi9HyPapsRaFDoU9155Nh88V");
        Log.d(APP_STARTED, "Parse initialized");

//        set if the device is running lollipop or higher to decide whether to run animations or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isLollipopOrHigher = true;
            isKitkatOrHigher = true;
            Log.d(APP_STARTED, "device is running lollipop or higher");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isKitkatOrHigher = true;
            Log.d(APP_STARTED, "device is running kitkat or higher, animation framework turned on");
        }

    }
}
