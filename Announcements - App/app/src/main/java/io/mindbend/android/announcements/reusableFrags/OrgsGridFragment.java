package io.mindbend.android.announcements.reusableFrags;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

public class OrgsGridFragment extends Fragment implements OrgsGridAdapter.OrgInteractionListener, Serializable{

    private static final String TAG = "OrgsGridFragment";

    public static final String ORG_PROFILE_FRAG = "org_profile_frag";

    private static final String ARG_ORGS_LISTENER = "orgs_listener";
    private static final String ARG_ORGS_LIST = "orgs";
    private static final String ARG_GRID_LISTENER = "grid_listener";

    private OrgsGridAdapter.OrgInteractionListener mOrgsListener;
    private ArrayList<Organization> mOrgs;
    private OrgsGridAdapter mOrgsAdapter;
    private OrgsGridInteractionListener mListener;

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
            mListener = (OrgsGridInteractionListener)getArguments().getSerializable(ARG_GRID_LISTENER);
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
        mListener = null;
        mOrgsListener = null;
    }

    @Override
    public void pressedOrg(Organization orgSelected) {
        mListener.pressedOrgFromGrid(orgSelected);
    }

    public void updateSingleOrg(Organization updatedOrg) {
        if (mOrgs != null && mOrgs.size() > 0){
            for (int i = 0; i < mOrgs.size() ; i++){
                Organization org = mOrgs.get(i);
                if (org.getmObjectId().equals(updatedOrg.getmObjectId())){
                    Log.wtf("ORG FRAG", "Org updated");
                    mOrgs.remove(i);
                    //update org with properties of updated org
                    org.setmTitle(updatedOrg.getTitle());
                    org.setmDescription(updatedOrg.getDescription());
                    org.setmFollowers(updatedOrg.getFollowers());
                    org.setmPrivateOrg(updatedOrg.isPrivateOrg());
                    org.setmHasAccessCode(updatedOrg.hasAccessCode());

                    mOrgs.add(i, org);
                    mOrgsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public interface OrgsGridInteractionListener extends Serializable{
        void pressedOrgFromGrid (Organization orgPressed);
    }
}
