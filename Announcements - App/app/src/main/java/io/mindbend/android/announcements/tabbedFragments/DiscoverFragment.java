package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.reusableFrags.OrgsGridFragment;
import io.mindbend.android.announcements.reusableFrags.PostsCardsFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment {


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
        Fragment orgsGridFrag = OrgsGridFragment.newInstance("test", "test");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.discover_framelayout, orgsGridFrag).commit();
        return v;
    }


}
