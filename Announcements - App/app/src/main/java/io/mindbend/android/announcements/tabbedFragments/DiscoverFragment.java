package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.User;
import io.mindbend.android.announcements.reusableFrags.OrgsGridAdapter;
import io.mindbend.android.announcements.reusableFrags.OrgsGridFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;
import io.mindbend.android.announcements.reusableFrags.ProfileFragment;
import io.mindbend.android.announcements.reusableFrags.SearchableFrag;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment implements Serializable, PostsFeedAdapter.PostInteractionListener, ProfileFragment.ProfileInteractionListener, SearchableFrag.SearchInterface {


    private static final String TAG = "TAG";
    private static final String ORG_PROFILE_FRAG = "ORG_PROFILE_FRAGMENT";
    private transient SearchableFrag mOrgsGridFrag;

    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_discover, container, false);
        setRetainInstance(true);
        //TODO: query discover_clubs data from Parse, then pass that data into an OrgsGridFragment that will be created using the OrgsGridFragment.NewInstance static method

        mOrgsGridFrag = SearchableFrag.newInstance(SearchableFrag.ORGS_TYPE, null, this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.discover_framelayout, mOrgsGridFrag).commit();
        return v;
    }

    public SearchableFrag getmOrgsGridFrag() {
        return mOrgsGridFrag;
    }


    @Override
    public void pressedPost(Post postPressed) {
        //TODO: do stuff, although switching to comments frag is already handled
        Log.d(TAG, "post pressed");
    }

    @Override
    public void userProfileToOrgProfile(Organization orgSelected) {
        pressedOrgFromProfile(orgSelected);
    }

    @Override
    public void pressedOrgFromProfile(Organization orgPressed) {
        //TODO: check if the user is an admin of this org
        //currently choosing randomly

        Random r = new Random();
        boolean isModifiable = r.nextBoolean();

        //replace the current profile frag with new org profile frag, while adding it to a backstack
        ProfileFragment orgProfile = ProfileFragment.newInstance(null, orgPressed, this, isModifiable);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.discover_framelayout, orgProfile).addToBackStack(null).commit();
        Log.d(TAG, "org has been pressed on discover page " + orgPressed.toString());
    }

    @Override
    public void pressedUserFromCommentOfOrgPost(User userPressed) {
        ProfileFragment userToVisit = ProfileFragment.newInstance(userPressed, null, this, false);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.discover_framelayout, userToVisit).addToBackStack(null).commit();
    }

    @Override
    public void searchUserPressed(User userPressed) {
        pressedUserFromCommentOfOrgPost(userPressed);
    }

    @Override
    public void searchOrgPressed(Organization orgPressed) {
        pressedOrgFromProfile(orgPressed);
    }
}
