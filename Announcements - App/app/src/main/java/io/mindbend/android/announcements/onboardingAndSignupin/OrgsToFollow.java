package io.mindbend.android.announcements.onboardingAndSignupin;

import java.util.ArrayList;

/**
 * Created by Avik Hasija on 9/10/2015.
 */
public class OrgsToFollow extends ArrayList<String> {
    private static OrgsToFollow sInstance = null;

    private OrgsToFollow(){}

    public static OrgsToFollow getInstance() {
        if(sInstance == null){
            sInstance = new OrgsToFollow();
        }
        return sInstance;
    }

    //TO clear instance (reset selected orgs after user has signed up)
    public static void clearInstance(){
        sInstance = null;
    }
}
