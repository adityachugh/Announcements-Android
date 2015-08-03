package io.mindbend.android.announcements.tabbedFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.mindbend.android.announcements.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class YouFragment extends Fragment {

    //NOTE: Image within Ratiolayout must be contained in a parent fragment (like this)
    //Frame layout added inside the RatioLayout
    //Grandchild fragments are reusable


    public YouFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_you, container, false);
    }


}
