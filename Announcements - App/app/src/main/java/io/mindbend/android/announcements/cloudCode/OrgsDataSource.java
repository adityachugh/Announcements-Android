package io.mindbend.android.announcements.cloudCode;

import com.parse.ParseObject;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Created by Akshay Pall on 21/08/2015.
 */
public class OrgsDataSource {
    public final static String ORG_TITLE = "name";
    public final static String ORG_DESCRIPTION = "organizationDescription";
    public final static String ORG_FOLLOWER_COUNT = "followerCount";
    public final static String ORG_TYPE = "organizationType";
    public final static String ORG_REQUEST_CODE = "requestCode";
    public final static String ORG_TAG = "handle";

    public final static String ORG_TYPES_PRIVATE = "Private";
    public final static String ORG_TYPES_PUBLIC = "Public";

    public static boolean isNew (ParseObject org) {
        DateTime date = new DateTime(org.getCreatedAt());
        DateTime today = new DateTime();
        int daysBetween = Days.daysBetween(date, today).getDays();
        return (daysBetween <= 5);
    }
}
