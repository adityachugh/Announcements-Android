package io.mindbend.android.announcements;

import android.app.Application;
import android.os.Build;
import android.util.Log;

/**
 * Created by Akshay Pall on 01/08/2015.
 */
public class App extends Application {
    private static final String APP_STARTED = "APP STARTED";
    public static boolean isLollipopOrHigher = false;

    @Override
    public void onCreate() {
        super.onCreate();
//        TODO: this class is to initialize parse and setup anything else that is required for the app to run. This class is run FIRST before any activity.
        Log.d(APP_STARTED, "Application started succesfully");
//        set if the device is running lollipop or higher to decide whether to run animations or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isLollipopOrHigher = true;
            Log.d(APP_STARTED, "device is running lollipop or higher, animations turned on");
        }

    }
}
