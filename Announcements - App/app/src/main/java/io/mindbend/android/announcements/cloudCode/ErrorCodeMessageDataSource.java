package io.mindbend.android.announcements.cloudCode;

import java.util.HashMap;

/**
 * Created by Akshay Pall on 14/09/2015.
 */
public class ErrorCodeMessageDataSource {
    public static String errorCodeMessage (String string) {
        HashMap<String, String> errorCodeMap = new HashMap<>();

        if (errorCodeMap.get(string) != null)
            return errorCodeMap.get(string);
        else {
            return "Error";
        }
    }
}
