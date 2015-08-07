package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

public class OrgsGridFragment extends Fragment implements OrgsGridAdapter.OrgInteractionListener{

    private static final String TAG = "OrgsGridFragment";

    public static final String ORG_PROFILE_FRAG = "org_profile_frag";

    private Fragment mOrgProfile;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OrgsGridAdapter mOrgsAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrgsGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgsGridFragment newInstance(String param1, String param2) {
        OrgsGridFragment fragment = new OrgsGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public OrgsGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_orgs_grid, container, false);

        OrgsGridAdapter.setListener(this);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.orgs_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        //TODO: query posts from parse, pass into list, then set adapter
        List<Organization> orgs = new ArrayList<>();

        //ORG CONSTRUCTOR: String objectId, String title, String description, int followers, String tag, boolean privateOrg, boolean newOrg
        //FAKE ORGANIZATIONS TO TEST
        Organization testOrg1 = new Organization("test Id", "Software Dev Club", "Learn to make apps! Android! Fun!", 803, "#SoftwareDevClub", false, true); //TODO: change "NEW" to be a dynamically chosen banner
        orgs.add(testOrg1);

        Organization testOrg2 = new Organization("test Id", "Math Club", "We had that one meeting that one time", 11, "#MathClub", false, false); //TODO: change "NEW" to be a dynamically chosen banner
        orgs.add(testOrg2);

        Organization testOrg3 = new Organization("test Id", "Mindbend Studio", "The best dev firm hello@mindbend.io", 80, "#BendBoundaries", true, true); //TODO: change "NEW" to be a dynamically chosen banner
        orgs.add(testOrg3);

        //Initialize and set the adapter
        mOrgsAdapter = new OrgsGridAdapter (getActivity(), orgs);
        recyclerView.setAdapter(mOrgsAdapter);
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void pressedOrg(Organization orgSelected) {
        //replace the current profile frag with new org profile frag, while adding it to a backstack
        mOrgProfile = ProfileFragment.newInstance(null, orgSelected);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.orgs_grid_framelayout, mOrgProfile).addToBackStack(ORG_PROFILE_FRAG).commit();

        Log.d(TAG, "org has been pressed on discover page " + orgSelected.toString());
    }
}
