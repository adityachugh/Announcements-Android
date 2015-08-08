package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

public class OrgsGridFragment extends Fragment implements OrgsGridAdapter.OrgInteractionListener{

    private static final String TAG = "OrgsGridFragment";

    public static final String ORG_PROFILE_FRAG = "org_profile_frag";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ORGS_LISTENER = "orgs_listener";
    private static final String ARG_ORGS_LIST = "orgs";
    private static final String ARG_GRID_LISTENER = "grid_listener";

    // TODO: Rename and change types of parameters
    private OrgsGridAdapter.OrgInteractionListener mOrgsListener;
    private ArrayList<Organization> mOrgs;
    private OrgsGridAdapter mOrgsAdapter;
    private OrgsGridInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrgsGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgsGridFragment newInstance(ArrayList<Organization> orgs, OrgsGridAdapter.OrgInteractionListener orgListener, OrgsGridInteractionListener gridListener) {
        OrgsGridFragment fragment = new OrgsGridFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ORGS_LIST, orgs);
        args.putSerializable(ARG_ORGS_LISTENER, orgListener);
        args.putSerializable(ARG_GRID_LISTENER, gridListener);
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
            mOrgsListener = (OrgsGridAdapter.OrgInteractionListener)getArguments().getSerializable(ARG_ORGS_LISTENER);
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
        mListener.pressedOrgFromGrid(orgSelected);
    }

    public interface OrgsGridInteractionListener extends Serializable{
        void pressedOrgFromGrid (Organization orgPressed);
    }
}
