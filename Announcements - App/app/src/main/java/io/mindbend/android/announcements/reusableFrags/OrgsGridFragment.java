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
import android.widget.ProgressBar;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.parse.FunctionCallback;
import com.parse.ParseException;

import java.io.Serializable;
import java.util.ArrayList;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.cloudCode.OrgsDataSource;
import io.mindbend.android.announcements.tabbedFragments.TodayFragment;

public class OrgsGridFragment extends Fragment implements OrgsGridAdapter.OrgInteractionListener, Serializable, SwipyRefreshLayout.OnRefreshListener {

    private static final String TAG = "OrgsGridFragment";

    public static final String ORG_PROFILE_FRAG = "org_profile_frag";

    private static final String ARG_ORGS_LISTENER = "orgs_listener";
    private static final String ARG_ORGS_LIST = "orgs";
    private static final String ARG_GRID_LISTENER = "grid_listener";
    private static final String ARG_SEARCH_QUERY_TEXT = "search_org_text"; //in order to recall searchOrg method with same text
    private static final String ARG_SIGN_UP = "sign_up";

    private OrgsGridAdapter.OrgInteractionListener mOrgsListener;
    private ArrayList<Organization> mOrgs;
    private OrgsGridAdapter mOrgsAdapter;
    private OrgsGridInteractionListener mListener;

    private String mQueryText;
    private View mView;
    private ProgressBar mLoading;
    private transient SwipyRefreshLayout mLoadMoreSearchItems;
    private boolean mSignUp;

    public static OrgsGridFragment newInstance(ArrayList<Organization> orgs, OrgsGridAdapter.OrgInteractionListener orgListener,
                                               OrgsGridInteractionListener gridListener, String nullableSearchQuery, boolean signUp) {


        OrgsGridFragment fragment = new OrgsGridFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ORGS_LIST, orgs);
        args.putSerializable(ARG_ORGS_LISTENER, orgListener);
        args.putSerializable(ARG_GRID_LISTENER, gridListener);
        args.putBoolean(ARG_SIGN_UP, signUp);
        if (nullableSearchQuery != null)
            args.putString(ARG_SEARCH_QUERY_TEXT, nullableSearchQuery);

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
            mSignUp = getArguments().getBoolean(ARG_SIGN_UP);
            if (getArguments().getString(ARG_SEARCH_QUERY_TEXT) != null)
                mQueryText = getArguments().getString(ARG_SEARCH_QUERY_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_orgs_grid, container, false);

        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.orgs_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        //Initialize and set the adapter
        mOrgsAdapter = new OrgsGridAdapter (getActivity(), mOrgs, mOrgsListener, mSignUp);
        recyclerView.setAdapter(mOrgsAdapter);

        mLoading = (ProgressBar)mView.findViewById(R.id.orgs_grid_progressbar);

        //in order to setup scroll-to-bottom-to-load-more
        mLoadMoreSearchItems = (SwipyRefreshLayout) mView.findViewById(R.id.orgs_refresher);
        mLoadMoreSearchItems.setColorSchemeResources(R.color.accent, R.color.primary);
        if (mQueryText != null){
            mLoadMoreSearchItems.setOnRefreshListener(this);
        }
        else {
            mLoadMoreSearchItems.setEnabled(false);
        }

        return mView;
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

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
        OrgsDataSource.searchForOrganizationsInRange(getActivity(), mView, mLoading,
                mQueryText, mOrgs.size(), new FunctionCallback<ArrayList<Organization>>() {
                    @Override
                    public void done(ArrayList<Organization> organizations, ParseException e) {
                        if (e == null){
                            mOrgs.addAll(organizations);
                            mOrgsAdapter.notifyDataSetChanged();
                            mLoadMoreSearchItems.setRefreshing(false);
                        }
                    }
                });
    }

    public interface OrgsGridInteractionListener extends Serializable{
        void pressedOrgFromGrid (Organization orgPressed);
    }
}
