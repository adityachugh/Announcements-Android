package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.reusableFrags.OrgsGridAdapter;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;
import io.mindbend.android.announcements.reusableFrags.ProfileFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class YouFragment extends Fragment implements Serializable, ProfileFragment.ProfileInteractionListener {
    private static final String TAG = "TAG";
    private static final String DEFAULT = "default_frag";
    private Fragment mProfileFragment;

    //NOTE: Opens child ProfileFragment, which has a grandchild for user followed organizations/ organization announcements
    //Makes Profile fragment generic (useable by both users and organizations)


    public YouFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        View v = inflater.inflate(R.layout.fragment_you, container, false);

        //FAKE USER FOR TESTING
        User testUser = new User("Aditya", "Chugh", "getting paper", "node.js", "#Grade12", 9);

        //ProfileFragment newInstance(User user, Organization org, ProfileInteractionListener profileListener, boolean onTodayTab, boolean onDiscoverTab, boolean onYouTab
        mProfileFragment = ProfileFragment.newInstance(testUser, null, this, false, false, true);

        //inflate profileFrag in framelayout
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.you_framelayout, mProfileFragment).addToBackStack(DEFAULT).commit();

        return v;
    }
    public Fragment getmProfileFragment() {
        return mProfileFragment;
    }

    @Override
    public void userProfileToOrgProfile(Organization orgSelected) {
//        replace the current profile frag with new org profile frag, while adding it to a backstack
        ProfileFragment orgProfile = ProfileFragment.newInstance(null, orgSelected, this, false, false, true);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.you_framelayout, orgProfile).addToBackStack(null).commit();

        Log.d(TAG, "org has been pressed on profile page " + orgSelected.toString());
    }

    @Override
    public void pressedOrgFromProfile(Organization orgPressed) {
        userProfileToOrgProfile(orgPressed);
    }

    @Override
    public void pressedUserFromCommentOfOrgPost(User userPressed) {
        ProfileFragment userToVisit = ProfileFragment.newInstance(userPressed, null, this, false, false, true);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.you_framelayout, userToVisit).addToBackStack(null).commit();
    }
}
