package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
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

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

public class OrgsGridFragment extends Fragment implements OrgsGridAdapter.OrgInteractionListener{

    private static final String TAG = "OrgsGridFragment";

    public static final String ORG_PROFILE_FRAG = "org_profile_frag";
    private Fragment mOrgProfile;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LISTENER = "orgs_listener";
    private static final String ARG_ORGS_LIST = "orgs";

    // TODO: Rename and change types of parameters
    private OrgsGridAdapter.OrgInteractionListener mOrgsListener;
    private ArrayList<Organization> mOrgs;
    private OrgsGridAdapter mOrgsAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrgsGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgsGridFragment newInstance(ArrayList<Organization> orgs, OrgsGridAdapter.OrgInteractionListener orgListener) {
        OrgsGridFragment fragment = new OrgsGridFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ORGS_LIST, orgs);
        args.putSerializable(ARG_LISTENER, orgListener);
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
            mOrgs = getArguments().getParcelableArrayList(ARG_ORGS_LIST);
            mOrgsListener = (OrgsGridAdapter.OrgInteractionListener)getArguments().getSerializable(ARG_LISTENER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_orgs_grid, container, false);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.orgs_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        //Initialize and set the adapter
        mOrgsAdapter = new OrgsGridAdapter (getActivity(), mOrgs, mOrgsListener);
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
