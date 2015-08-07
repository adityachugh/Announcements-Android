package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class YouFragment extends Fragment implements OrgsGridAdapter.OrgInteractionListener {
    private static final String TAG = "TAG";

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

        Fragment profileFragment = ProfileFragment.newInstance(testUser, this, null, null);

        //inflate profileFrag in framelayout
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.you_framelayout, profileFragment).commit();

        return v;
    }

    @Override
    public void pressedOrg(Organization orgSelected) {
        //TODO: do stuff
        Log.d(TAG, "Org selected");
    }
}
