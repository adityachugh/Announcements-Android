package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.Post;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.reusableFrags.OrgsGridAdapter;
import io.mindbend.android.announcements.reusableFrags.OrgsGridFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;
import io.mindbend.android.announcements.reusableFrags.PostsFeedAdapter;
import io.mindbend.android.announcements.reusableFrags.ProfileFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment implements OrgsGridAdapter.OrgInteractionListener, PostsFeedAdapter.PostInteractionListener, ProfileFragment.ProfileInteractionListener, OrgsGridFragment.OrgsGridInteractionListener{


    private static final String TAG = "TAG";
    private static final String ORG_PROFILE_FRAG = "ORG_PROFILE_FRAGMENT";
    private OrgsGridFragment mOrgsGridFrag;

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

        ArrayList<Organization> orgs = new ArrayList<>();

        //ORG CONSTRUCTOR: String objectId, String title, String description, int followers, String tag, boolean privateOrg, boolean newOrg
        //FAKE ORGANIZATIONS TO TEST
        Organization testOrg1 = new Organization("test Id", "Software Dev Club", "Learn to make apps! Android! Fun!", 803, "#SoftwareDevClub", false, true); //TODO: change "NEW" to be a dynamically chosen banner
        orgs.add(testOrg1);

        Organization testOrg2 = new Organization("test Id", "Math Club", "We had that one meeting that one time", 11, "#MathClub", false, false); //TODO: change "NEW" to be a dynamically chosen banner
        orgs.add(testOrg2);

        Organization testOrg3 = new Organization("test Id", "Mindbend Studio", "The best dev firm hello@mindbend.io", 80, "#BendBoundaries", true, true); //TODO: change "NEW" to be a dynamically chosen banner
        orgs.add(testOrg3);

        mOrgsGridFrag = OrgsGridFragment.newInstance(orgs, this, this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.discover_framelayout, mOrgsGridFrag).commit();
        return v;
    }

    public OrgsGridFragment getmOrgsGridFrag() {
        return mOrgsGridFrag;
    }

    @Override
    public void pressedOrg(Organization orgSelected) {
        //replace the current profile frag with new org profile frag, while adding it to a backstack
        ProfileFragment orgProfile = ProfileFragment.newInstance(null, orgSelected, this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.discover_framelayout, orgProfile).addToBackStack(null).commit();
        Log.d(TAG, "org has been pressed on discover page " + orgSelected.toString());
    }

    @Override
    public void pressedPost(Post postPressed) {
        //TODO: do stuff, although switching to comments frag is already handled
        Log.d(TAG, "post pressed");
    }

    @Override
    public void userProfileToOrgProfile(Organization orgSelected) {
        pressedOrg(orgSelected);
    }

    @Override
    public void pressedOrgFromGrid(Organization orgPressed) {
        pressedOrg(orgPressed);
    }

    @Override
    public void pressedOrgFromProfile(Organization orgPressed) {
        pressedOrgFromGrid(orgPressed);
    }
}
