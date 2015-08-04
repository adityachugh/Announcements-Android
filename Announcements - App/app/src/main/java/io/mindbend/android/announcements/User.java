package io.mindbend.android.announcements;

/**
 * Created by Avik Hasija on 8/3/2015.
 */
public class User {
    //Class contains details about a user, to be used in various places
    //Current content reflects what is needed for profile page found in "you" tab

    //Will be concatenated upon return
    private String mFirstName;
    private String mLastName;
    private String mInterestOne;
    private String mInterestTwo;

    //Displayed as a hashtag (such as #Grade10, #Teacher, #Administration, etc.)
    private String mUserCategory;

    private int mNumberOfOrganizationsFollowed;

    //TODO: add fields for profile photo, cover photo

    public User(String firstName, String lastName, String interestOne, String interestTwo, String userCategory, int numberOfOrganizationsFollowed){
        mFirstName = firstName;
        mLastName = lastName;
        mInterestOne = interestOne;
        mInterestTwo = interestTwo;
        mUserCategory = userCategory;

        mNumberOfOrganizationsFollowed = numberOfOrganizationsFollowed;
    }

    public String getName() {
        //Returns full name
        return (mFirstName + " " + mLastName);
    }

    public String getInterests() {
        //Returns both interests
        return ("Interested in " + mInterestOne + " and " + mInterestTwo);
    }

    public String getNumberOfOrganizationsFollowed() {
        //Returns String - only used on profile pages
        return (mNumberOfOrganizationsFollowed + " Groups");
    }

    public String getUserCategory() {
        return mUserCategory;
    }
}
