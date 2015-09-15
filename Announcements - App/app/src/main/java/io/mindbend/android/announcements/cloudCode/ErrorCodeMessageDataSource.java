package io.mindbend.android.announcements.cloudCode;

import java.util.HashMap;

/**
 * Created by Akshay Pall on 14/09/2015.
 */
public class ErrorCodeMessageDataSource {
    public static String errorCodeMessage (String string) {
        HashMap<String, String> errorCodeMap = new HashMap<>();
        errorCodeMap.put("1", "Internal server error");
        errorCodeMap.put("100", "Connection to servers failed");
        errorCodeMap.put("101", "This does not exist");
        errorCodeMap.put("102", "Object queries is of an incorrect type");
        errorCodeMap.put("103", "Invalid class name. Report this to a Mindbend Studio employee");
        errorCodeMap.put("104", "Unspecified object ID.");
        errorCodeMap.put("105", "Invalid key name. Remember, they are case-sensitive.");
        errorCodeMap.put("106", "Error with pointer. Report this to a Mindbend Studio employee");
        errorCodeMap.put("107", "JSON error due to poor network connection");
        errorCodeMap.put("108", "This feature is only available internally for testing.");
        errorCodeMap.put("109", "Must initialize database connection. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("111", "Field set to an inconsistent type. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("112", "Invalid channel name. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("115", "Notifications misconfigured. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("116", "Object too large.");
        errorCodeMap.put("119", "Operation not allowed for users.");
        errorCodeMap.put("120", "Result not found in cache.");
        errorCodeMap.put("121", "Invalid key used in a nested JSON object. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("122", "Invalid file name in database. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("123", "Invalid ACL provided. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("124", "Request timed out. Please try again.");
        errorCodeMap.put("125", "Email address invalid");
        errorCodeMap.put("137", "Unique field gives a value that's already taken in the database");
        errorCodeMap.put("139", "Role's name invalid. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("140", "Server memory full. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("141", "Server logic failed. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("142", "Server verification failed. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("153", "Deletion failed. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("155", "App traffic too high. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("160", "Event name is invalid. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("200", "Username empty or missing.");
        errorCodeMap.put("201", "Password empty or missing");
        errorCodeMap.put("202", "Username already taken.");
        errorCodeMap.put("203", "Email already taken");
        errorCodeMap.put("204", "Email missing.");
        errorCodeMap.put("206", "User cannot be altered without a valid session.");
        errorCodeMap.put("207", "User only creatable through sign up.");
        errorCodeMap.put("208", "This account is already linked to another user");
        errorCodeMap.put("209", "Current session token invalid. Sign in again.");
        errorCodeMap.put("250", "Account ID could not be found. Report this to a Mindbend Studio employee.");
        errorCodeMap.put("251", "Linked account has an invalid session. Sign in again.");
        errorCodeMap.put("252", "The service being linked is unsupported.");

        if (errorCodeMap.get(string) != null)
            return errorCodeMap.get(string);
        else {
            return "Unknown Error. Report this to a Mindbend Studio employee.";
        }
    }
}
